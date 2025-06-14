package com.CodeLab.Central_Service.service;

import com.CodeLab.Central_Service.exception.ErrorException;
import com.CodeLab.Central_Service.exception.NotFoundException;
import com.CodeLab.Central_Service.integration.DBService;
import com.CodeLab.Central_Service.integration.ExecutionService;
import com.CodeLab.Central_Service.integration.RabbitMQIntegration;
import com.CodeLab.Central_Service.model.*;
import com.CodeLab.Central_Service.requestDTO.CodeRequestDTO;
import com.CodeLab.Central_Service.requestDTO.PartialContestSubmissionRequestDTO;
import com.CodeLab.Central_Service.requestDTO.RunCodeRequestDTO;
import com.CodeLab.Central_Service.requestDTO.SubmissionRequestDTO;
import com.CodeLab.Central_Service.responseDTO.CentralServiceRunCodeResponse;
import com.CodeLab.Central_Service.responseDTO.RunCodeResponseDTO;
import com.CodeLab.Central_Service.responseDTO.RunTimeErrorResponseDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
public class CodeExecutionService {

    @Autowired
    ExecutionService executionService;

    @Autowired
    DBService dbService;


    @Autowired
    RabbitMQIntegration rabbitMQIntegration;

    //Synchronous
//    public CodeExecutionResult runCode(CodeRequestDTO requestDTO) {
//        UUID problemId = requestDTO.getProblemId();
//        String language = requestDTO.getLanguage().toString();
//
//        Set<String> visibleFirstLanguages = Set.of("PYTHON", "JAVA_SCRIPT");
//        String baseCode = visibleFirstLanguages.contains(language)
//                ? requestDTO.getVisibleCode() + "\n" + requestDTO.getInvisibleCode()
//                : requestDTO.getInvisibleCode() + "\n" + requestDTO.getVisibleCode();
//
//        Problem problem = dbService.callGetProblem(problemId);
//        if (problem == null) {
//            throw new ErrorException("Problem Not Found!!!");
//        }
//
//        List<TestCase> testCases = problem.getAllVisibleTestCases();
//        if (testCases == null || testCases.isEmpty()) {
//            throw new ErrorException("No visible test cases found for the problem.");
//        }
//
//        String versionIndex = getVersionIndex(language);
//        String finalLang = normalizeLanguage(language);
//        ExecutorService executor = Executors.newFixedThreadPool(Math.min(testCases.size(), 5));
//
//        List<CentralServiceRunCodeResponse> resultList = new ArrayList<>();
//        CodeExecutionResult finalResult = new CodeExecutionResult();
//
//        for (TestCase testCase : testCases) {
//            RunCodeRequestDTO runCodeRequestDTO = new RunCodeRequestDTO();
//            runCodeRequestDTO.setCode(baseCode);
//            runCodeRequestDTO.setLanguage(finalLang);
//            runCodeRequestDTO.setVersionIndex(versionIndex);
//            runCodeRequestDTO.setInput(testCase.getTestCaseInput());
//            runCodeRequestDTO.setExpectedOutput(testCase.getTestCaseOutput());
//
//            RunCodeResponseDTO responseDTO = executionService.callRunCode(runCodeRequestDTO);
//
//            if (responseDTO.isError()) {
//                if (responseDTO.hasRuntimeError()) {
//                    RunTimeErrorResponseDTO errorDto = new RunTimeErrorResponseDTO();
//                    errorDto.setRuntimeError(responseDTO.getRuntimeError());
//                    errorDto.setLastExecutedTestcase(testCase);
//
//                    finalResult.setSuccess(false);
//                    finalResult.setRuntimeError(errorDto);
//                    executor.shutdown();
//                    return finalResult;
//                }
//
//                finalResult.setSuccess(false);
//                finalResult.setErrorMessage("Compilation/Error: " + responseDTO.getOutput());
//                executor.shutdown();
//                return finalResult;
//            }
//
//            String actualOutput = responseDTO.getOutput().trim();
//            String expectedOutput = testCase.getTestCaseOutput().trim();
//            boolean isMatched = actualOutput.equals(expectedOutput);
//
//            if (!isMatched) {
//                finalResult.setSuccess(false);
//                finalResult.setErrorMessage("Wrong Answer:\nInput: " + testCase.getTestCaseInput()
//                        + "\nExpected: " + expectedOutput + "\nActual: " + actualOutput);
//                executor.shutdown();
//                return finalResult;
//            }
//
//            CentralServiceRunCodeResponse response = new CentralServiceRunCodeResponse();
//            response.setInput(testCase.getTestCaseInput());
//            response.setOutput(actualOutput);
//            response.setExpected(expectedOutput);
//            response.setOutputMatched(true);
//
//            resultList.add(response);
//        }
//
//        executor.shutdown();
//        finalResult.setSuccess(true);
//        finalResult.setResponses(resultList);
//        return finalResult;
//    }

    //Asynchronous
    public CodeExecutionResult runCode(CodeRequestDTO requestDTO) {
        UUID problemId = requestDTO.getProblemId();
        String language = requestDTO.getLanguage().toString();

        Set<String> visibleFirstLanguages = Set.of("PYTHON", "JAVA_SCRIPT");
        String baseCode = visibleFirstLanguages.contains(language)
                ? requestDTO.getVisibleCode() + "\n" + requestDTO.getInvisibleCode()
                : requestDTO.getInvisibleCode() + "\n" + requestDTO.getVisibleCode();

        Problem problem = dbService.callGetProblemForSystem(problemId);
        if (problem == null) {
            throw new ErrorException("Problem Not Found!!!");
        }

        List<TestCase> testCases = problem.getAllVisibleTestCases();
        if (testCases == null || testCases.isEmpty()) {
            throw new ErrorException("No visible test cases found for the problem.");
        }

        String versionIndex = getVersionIndex(language);
        String finalLang = normalizeLanguage(language);
        String invisibleCode = requestDTO.getInvisibleCode();
        String visibleCode = requestDTO.getVisibleCode();

        ExecutorService executor = Executors.newFixedThreadPool(Math.min(testCases.size(), 5));
        List<CompletableFuture<Optional<CentralServiceRunCodeResponse>>> futures = new ArrayList<>();

        CodeExecutionResult finalResult = new CodeExecutionResult();
        List<CentralServiceRunCodeResponse> resultList = Collections.synchronizedList(new ArrayList<>());

        for (TestCase testCase : testCases) {
            CompletableFuture<Optional<CentralServiceRunCodeResponse>> future = CompletableFuture.supplyAsync(() -> {
                RunCodeRequestDTO runCodeRequestDTO = new RunCodeRequestDTO();
                runCodeRequestDTO.setVisibleCode(visibleCode);
                runCodeRequestDTO.setInvisibleCode(invisibleCode);
                runCodeRequestDTO.setLanguage(finalLang);
                runCodeRequestDTO.setVersionIndex(versionIndex);
                runCodeRequestDTO.setInput(testCase.getTestCaseInput());
                runCodeRequestDTO.setExpectedOutput(testCase.getTestCaseOutput());
                runCodeRequestDTO.setPartialContestSubmission(false);

                RunCodeResponseDTO responseDTO = executionService.callRunCode(runCodeRequestDTO);

                if (responseDTO.isError()) {
                    if (responseDTO.hasRuntimeError()) {
                        RunTimeErrorResponseDTO errorDto = new RunTimeErrorResponseDTO();
                        errorDto.setRuntimeError(responseDTO.getRuntimeError());
//                        System.out.println(testCase);
                        errorDto.setLastExecutedTestcase(testCase);
//                        System.out.println(errorDto.getLastExecutedTestcase());
                        throw new RuntimeException("RUNTIME:" + serializeRuntimeError(errorDto));
                    }

                    throw new RuntimeException(responseDTO.getOutput());
                }

                String actualOutput = responseDTO.getOutput().trim();
                String expectedOutput = testCase.getTestCaseOutput().trim();
                boolean isMatched = actualOutput.equals(expectedOutput);

                CentralServiceRunCodeResponse response = new CentralServiceRunCodeResponse();
                response.setInput(testCase.getTestCaseInput());
                response.setOutput(actualOutput);
                response.setExpected(expectedOutput);
                response.setOutputMatched(isMatched);


                return Optional.of(response);
            }, executor).exceptionally(ex -> {
                String message = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();

                if (message.startsWith("RUNTIME:")) {
                    finalResult.setSuccess(false);
                    finalResult.setRuntimeError(deserializeRuntimeError(message.substring(8)));

                } else {
                    finalResult.setSuccess(false);
                    finalResult.setErrorMessage(message);
                }
                return Optional.empty(); // we won't add a response
            });

            futures.add(future);
        }

        // Wait for all futures to complete
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        // Collect successful responses
        for (CompletableFuture<Optional<CentralServiceRunCodeResponse>> future : futures) {
            future.join().ifPresent(resultList::add);
        }

        executor.shutdown();

        if (finalResult.getRuntimeError() == null && finalResult.getErrorMessage() == null) {
            finalResult.setSuccess(true);
            finalResult.setResponses(resultList);
        }

        return finalResult;
    }

    private String serializeRuntimeError(RunTimeErrorResponseDTO dto) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(dto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize runtime error", e);
        }
    }


    private RunTimeErrorResponseDTO deserializeRuntimeError(String str) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(str, RunTimeErrorResponseDTO.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to deserialize runtime error", e);
        }
    }






    public Submission submitCode(CodeRequestDTO requestDTO, UUID userId) {
        UUID problemId = requestDTO.getProblemId();

        // Fetch problem and validate
        Problem problem = dbService.callGetProblemForSystem(problemId);
        if (problem == null) {
            throw new NotFoundException("Problem with id-" + userId + " not Found!!!");
        }


        List<TestCase> testCases = problem.getTestCasesList();
//        List<TestCase> testCases = problem.getAllVisibleTestCases();


        if (testCases == null || testCases.isEmpty()) {
            throw new ErrorException("No test cases available for submission.");
        }

        // Create submission
        Submission submission = dbService.callAddSubmission(
                new SubmissionRequestDTO(requestDTO.getLanguage(),userId,problemId)
        );

        // Precompute shared fields
        RunCodeRequestDTO baseRequest = convertToRunCodeRequest(requestDTO,false);
        baseRequest.setSubmissionId(submission.getSubmissionId());

        // Prepare execution requests
        List<RunCodeRequestDTO> executionRequests = testCases.stream()
                .map(testCase -> {
                    RunCodeRequestDTO request = new RunCodeRequestDTO(baseRequest);
                    request.setInput(testCase.getTestCaseInput());
                    request.setExpectedOutput(testCase.getTestCaseOutput());
                    return request;
                })
                .collect(Collectors.toList());

        // Send to execution queue
        rabbitMQIntegration.sendNormalCodeExecutionRequest(executionRequests);
        return submission;
    }


    public PartialContestSubmission contestPartialSubmit(CodeRequestDTO requestDTO, UUID userId,UUID contestId) {
        UUID problemId = requestDTO.getProblemId();

        // Fetch problem and validate
        Problem problem = dbService.callGetProblemForSystem(problemId);
        if (problem == null) {
            throw new NotFoundException("Problem with id-" + userId + " not Found!!!");
        }

        Contest contest = dbService.callGetContestById(contestId);
        if (contest == null) {
            throw new NotFoundException("Contest with id-" + contestId + " not Found!!!");
        }


        List<TestCase> testCases = problem.getTestCasesList();
//        List<TestCase> testCases = problem.getAllVisibleTestCases();


        if (testCases == null || testCases.isEmpty()) {
            throw new ErrorException("No test cases available for submission.");
        }

        // Create submission

        PartialContestSubmission submission = dbService.callSubmitPartialContest(new PartialContestSubmissionRequestDTO(requestDTO.getLanguage(),userId,problemId,contestId));

        // Precompute shared fields
        RunCodeRequestDTO baseRequest = convertToRunCodeRequest(requestDTO,true);
        baseRequest.setSubmissionId(submission.getSubmissionId());

        // Prepare execution requests
        List<RunCodeRequestDTO> executionRequests = testCases.stream()
                .map(testCase -> {
                    RunCodeRequestDTO request = new RunCodeRequestDTO(baseRequest);
                    request.setInput(testCase.getTestCaseInput());
                    request.setExpectedOutput(testCase.getTestCaseOutput());
                    return request;
                })
                .collect(Collectors.toList());

        // Send to execution queue
        rabbitMQIntegration.sendContestCodeExecutionRequest(executionRequests);
        return submission;
    }

    public RunCodeRequestDTO convertToRunCodeRequest(CodeRequestDTO requestDTO,boolean isPartialContestSubmission) {
        String invisibleCode = requestDTO.getInvisibleCode();
        String visibleCode = requestDTO.getVisibleCode();
        String language = requestDTO.getLanguage().toString();

        RunCodeRequestDTO runCodeRequestDTO = new RunCodeRequestDTO();
        runCodeRequestDTO.setInvisibleCode(invisibleCode);
        runCodeRequestDTO.setVisibleCode(visibleCode);
        runCodeRequestDTO.setLanguage(normalizeLanguage(language));
        runCodeRequestDTO.setVersionIndex(getVersionIndex(language));
        runCodeRequestDTO.setPartialContestSubmission(isPartialContestSubmission);
        return runCodeRequestDTO;
    }

    private String getVersionIndex(String language) {
        return switch (language.toUpperCase()) {
            case "JAVA" -> "4";
            case "CPP", "C" -> "5";
            case "JAVA_SCRIPT" -> "3";
            default -> "4"; // Python or fallback
        };
    }

    private String normalizeLanguage(String language) {
        return switch (language.toUpperCase()) {
            case "JAVA" -> "java";
            case "CPP" -> "cpp";
            case "JAVA_SCRIPT" -> "nodejs";
            case "C" -> "c";
            default -> "python3";
        };
    }
}
