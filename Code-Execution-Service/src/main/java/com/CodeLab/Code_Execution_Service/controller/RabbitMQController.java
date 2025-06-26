package com.CodeLab.Code_Execution_Service.controller;

import com.CodeLab.Code_Execution_Service.enums.SubmissionStatus;
import com.CodeLab.Code_Execution_Service.integration.DBService;
import com.CodeLab.Code_Execution_Service.requestDTO.RunCodeRequestDTO;
import com.CodeLab.Code_Execution_Service.requestDTO.UpdatePartialContestSubmissionRequestDTO;
import com.CodeLab.Code_Execution_Service.requestDTO.UpdateSubmissionRequestDTO;
import com.CodeLab.Code_Execution_Service.responseDTO.RunCodeResponseDTO;
import com.CodeLab.Code_Execution_Service.service.DetectError;
import com.CodeLab.Code_Execution_Service.service.ExecuteCodeService;
import com.CodeLab.Code_Execution_Service.service.GeminiService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@RestController
public class RabbitMQController {

    @Autowired
    private ExecuteCodeService executeCodeService;

    @Autowired
    private DetectError detectError;

    @Autowired
    private DBService dbService;

    @Autowired
    private GeminiService geminiService;

    @RabbitListener(queues = "normal-submission-queue")
    public void handleNormalSubmissions(@Payload List<RunCodeRequestDTO> requestList) {
        System.out.println("Normal Submission");
        NormalSubmission(requestList);
    }

    @RabbitListener(queues = "contest-submission-queue")
    public void handleContestSubmissions(@Payload List<RunCodeRequestDTO> requestList) {
        System.out.println("Contest Submission");
//        System.out.println(requestList.get(0).getInvisibleCode());
//        System.out.println(requestList.get(0).getVisibleCode());
        ContestSubmission(requestList);

    }

    public void NormalSubmission(List<RunCodeRequestDTO> requestList){
        int totalTestcasesPassed = 0;
        try {
            long totalStart = System.nanoTime();
            RunCodeRequestDTO firstRequest = requestList.get(0);

            // Step 1: Execute test cases
            long execStart = System.nanoTime();
            List<CompletableFuture<RunCodeResponseDTO>> futures = requestList.stream()
                    .map(executeCodeService::executeAsync)
                    .collect(Collectors.toList());

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            List<RunCodeResponseDTO> results = futures.stream()
                    .map(CompletableFuture::join)
                    .collect(Collectors.toList());

            long execEnd = System.nanoTime();
            double execTimeInSec = (execEnd - execStart) / 1_000_000_000.0;

            // Step 2: Check for errors or wrong answers

            for (int i = 0; i < results.size(); i++) {
                RunCodeResponseDTO result = results.get(i);
                RunCodeRequestDTO request = requestList.get(i);

                if (result.isError()) {
                    if (result.hasRuntimeError()) {
                        dbService.callUpdateSubmission(new UpdateSubmissionRequestDTO(
                                request.getSubmissionId(),
                                SubmissionStatus.RUNTIME_ERROR,
                                null, null,
                                result.getRuntimeError(),
                                request.getVisibleCode(),
                                request.getInput(),
                                result.getOutput(),
                                request.getExpectedOutput(),requestList.size(), totalTestcasesPassed
                        ));
                        System.out.println("Runtime Error:\n" + result.getRuntimeError());
                    } else {
                        dbService.callUpdateSubmission(new UpdateSubmissionRequestDTO(
                                request.getSubmissionId(),
                                SubmissionStatus.COMPILE_ERROR,
                                null, null,
                                result.getOutput(),
                                request.getVisibleCode(),
                                null,
                                null,
                                null,requestList.size(), totalTestcasesPassed
                        ));
                        System.out.println("Compile Error:\n" + result.getOutput());
                    }
                    return;
                }

                if (!detectError.isOutputMatched(result, request)) {
                    String wrong = "Input:\n" + request.getInput() +
                            "\nYour Output:\n" + result.getOutput() +
                            "\nExpected Output:\n" + request.getExpectedOutput();

                    dbService.callUpdateSubmission(new UpdateSubmissionRequestDTO(
                            request.getSubmissionId(),
                            SubmissionStatus.WRONG_ANSWER,
                            null, null,
                            null,
                            request.getVisibleCode(),
                            request.getInput(),
                            result.getOutput(),
                            request.getExpectedOutput(),requestList.size(), totalTestcasesPassed
                    ));
                    System.out.println("Wrong Answer:\n" + wrong);
                    return;
                }

                System.out.println("Test Case " + (i + 1));
                System.out.println("Output: " + result.getOutput());
                System.out.println("CPU Time: " + result.getCpuTime());
                System.out.println("-------------------------");
                totalTestcasesPassed++;
            }

            System.out.printf("Executed %d test cases in %.3f seconds%n", results.size(), execTimeInSec);

            // Step 3: Run Gemini complexity analysis
            long analysisStart = System.nanoTime();
            CompletableFuture<Map<String, String>> analysisFuture =
                    CompletableFuture.supplyAsync(() -> geminiService.analyzeCodeComplexity(firstRequest.getVisibleCode()));
            Map<String, String> map = analysisFuture.join();
            long analysisEnd = System.nanoTime();
            double analysisTimeInSec = (analysisEnd - analysisStart) / 1_000_000_000.0;

            System.out.println("TC: " + map.get("TC"));
            System.out.println("SC: " + map.get("SC"));
            System.out.printf("Time taken for complexity analysis: %.3f seconds%n", analysisTimeInSec);

            // Step 4: Final update (ACCEPTED)
            dbService.callUpdateSubmission(new UpdateSubmissionRequestDTO(
                    firstRequest.getSubmissionId(),
                    SubmissionStatus.ACCEPTED,
                    map.get("TC"),
                    map.get("SC"),
                    null,
                    firstRequest.getVisibleCode(),
                    null,
                    null,
                    null,requestList.size(),requestList.size()
            ));

            long totalEnd = System.nanoTime();
            double totalTimeInSec = (totalEnd - totalStart) / 1_000_000_000.0;
            System.out.printf("Total time taken (execution + analysis): %.3f seconds%n", totalTimeInSec);

        } catch (Exception e) {
            dbService.callUpdateSubmission(new UpdateSubmissionRequestDTO(
                    requestList.get(0).getSubmissionId(),
                    SubmissionStatus.INTERNAL_ERROR,
                    null, null,
                    "Error during execution: " + e.getMessage(),
                    requestList.get(0).getVisibleCode(),
                    null, null, null,requestList.size(),totalTestcasesPassed
            ));
            System.err.println("Error during execution: " + e.getMessage());
        }

    }
    public void ContestSubmission(List<RunCodeRequestDTO> requestList) {
        int totalTestcasesPassed = 0;
        try {
            long totalStart = System.nanoTime();
            RunCodeRequestDTO firstRequest = requestList.get(0); // Assuming requestList is never empty

            // Step 1: Execute test cases
            long execStart = System.nanoTime();
            List<CompletableFuture<RunCodeResponseDTO>> futures = requestList.stream()
                    .map(executeCodeService::executeAsync)
                    .collect(Collectors.toList());

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            List<RunCodeResponseDTO> results = futures.stream()
                    .map(CompletableFuture::join)
                    .collect(Collectors.toList());

            long execEnd = System.nanoTime();
            double execTimeInSec = (execEnd - execStart) / 1_000_000_000.0;

            // Step 2: Check for errors or wrong answers
            for (int i = 0; i < results.size(); i++) {
                RunCodeResponseDTO result = results.get(i);
                RunCodeRequestDTO request = requestList.get(i);

                if (result.isError()) {
                    double percentage = ((double) totalTestcasesPassed / requestList.size()) * 100;
                    percentage = Math.round(percentage * 100.0) / 100.0;

                    if (result.hasRuntimeError()) {
                        // Update for RUNTIME_ERROR
                        dbService.callUpdatePartialContestSubmission(new UpdatePartialContestSubmissionRequestDTO(
                                request.getSubmissionId(),
                                SubmissionStatus.RUNTIME_ERROR,
                                result.getRuntimeError(), // Error message for runtime error
                                request.getVisibleCode(),
                                requestList.size(),
                                totalTestcasesPassed, // Will be 0 if an error occurs early
                                percentage // Percentage for errors
                        ));
                        System.out.println("Runtime Error:\n" + result.getRuntimeError());
                    } else {
                        // Update for COMPILE_ERROR
                        dbService.callUpdatePartialContestSubmission(new UpdatePartialContestSubmissionRequestDTO(
                                request.getSubmissionId(),
                                SubmissionStatus.COMPILE_ERROR,
                                result.getOutput(), // Compile error message is often in output
                                request.getVisibleCode(),
                                requestList.size(),
                                totalTestcasesPassed, // Will be 0 if an error occurs early
                                0.0 // Percentage for errors
                        ));
                        System.out.println("Compile Error:\n" + result.getOutput());
                    }
                    return; // Exit on first error
                }

                if (!detectError.isOutputMatched(result, request)) {
                    // Update for WRONG_ANSWER
                    String wrongDetails = "Input: " + request.getInput() +
                            "\nYour Output: " + result.getOutput() +
                            "\nExpected Output: " + request.getExpectedOutput();
                    double percentage = ((double) totalTestcasesPassed / requestList.size()) * 100;
                    percentage = Math.round(percentage * 100.0) / 100.0;

                    dbService.callUpdatePartialContestSubmission(new UpdatePartialContestSubmissionRequestDTO(
                            request.getSubmissionId(),
                            SubmissionStatus.WRONG_ANSWER,
                            wrongDetails, // Detailed wrong answer message
                            request.getVisibleCode(),
                            requestList.size(),
                            totalTestcasesPassed, // Will be 0 if an error occurs early
                            percentage // Percentage for errors
                    ));
                    System.out.println("Wrong Answer:\n" + wrongDetails);
                    return; // Exit on first wrong answer
                }

                System.out.println("Test Case " + (i + 1));
                System.out.println("Output: " + result.getOutput());
                System.out.println("CPU Time: " + result.getCpuTime());
                System.out.println("-------------------------");
                totalTestcasesPassed++; // Increment only if test case passes
            }

            System.out.printf("Executed %d test cases in %.3f seconds%n", results.size(), execTimeInSec);

            // Step 3: Final update (ACCEPTED) - no complexity analysis
            dbService.callUpdatePartialContestSubmission(new UpdatePartialContestSubmissionRequestDTO(
                    firstRequest.getSubmissionId(),
                    SubmissionStatus.ACCEPTED,
                    null, // No error for ACCEPTED
                    firstRequest.getVisibleCode(),
                    requestList.size(),
                    requestList.size(), // All test cases passed for ACCEPTED
                    100.0 // 100% for accepted
            ));

            long totalEnd = System.nanoTime();
            double totalTimeInSec = (totalEnd - totalStart) / 1_000_000_000.0;
            System.out.printf("Total time taken (execution): %.3f seconds%n", totalTimeInSec);

        } catch (Exception e) {
            // Update for INTERNAL_ERROR
            dbService.callUpdatePartialContestSubmission(new UpdatePartialContestSubmissionRequestDTO(
                    requestList.get(0).getSubmissionId(), // Assuming requestList is not empty even on error
                    SubmissionStatus.INTERNAL_ERROR,
                    "Error during execution: " + e.getMessage(), // Error message
                    requestList.get(0).getVisibleCode(),
                    requestList.size(),
                    totalTestcasesPassed, // Will be 0 in case of an unexpected exception before test cases are run or completed
                    100.0 // Percentage for errors
            ));
            System.err.println("Error during execution: " + e.getMessage());
        }
    }
}
