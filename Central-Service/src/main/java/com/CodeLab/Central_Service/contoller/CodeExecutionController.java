package com.CodeLab.Central_Service.contoller;

import com.CodeLab.Central_Service.model.CodeExecutionResult;
import com.CodeLab.Central_Service.model.Submission;
import com.CodeLab.Central_Service.requestDTO.CodeRequestDTO;
import com.CodeLab.Central_Service.responseDTO.*;
import com.CodeLab.Central_Service.service.AuthenticationService;
import com.CodeLab.Central_Service.service.CodeExecutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/central/execute")
public class CodeExecutionController {
    @Autowired
    CodeExecutionService codeExecutionService;

    @Autowired
    AuthenticationService authenticationService;

    @PostMapping("/run")
    public ResponseEntity<?> runCode(@RequestBody CodeRequestDTO requestDTO, @RequestHeader(value = "Authorization", required = false) String header) {
        TokenValidationResponseDTO responseDTO = authenticationService.validateUserToken(header);
        System.out.println(requestDTO.getVisibleCode());
        System.out.println(requestDTO.getVisibleCode());

        if (!responseDTO.isValid()) {
            return new ResponseEntity<>(responseDTO, HttpStatus.UNAUTHORIZED);
        }
        try {
            CodeExecutionResult responseDTOS = codeExecutionService.runCode(requestDTO);
            return new ResponseEntity<>(responseDTOS, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e, HttpStatus.OK);
        }
    }


    @PostMapping("/submit")
    public ResponseEntity<?> submitCode(@RequestBody CodeRequestDTO requestDTO, @RequestHeader(value = "Authorization", required = false) String header) {
        TokenValidationResponseDTO responseDTO = authenticationService.validateUserToken(header);
        System.out.println("Problem id: "+requestDTO.getProblemId());
        System.out.println("Language: "+requestDTO.getLanguage());
        System.out.println("Main Code: \n"+requestDTO.getInvisibleCode());
        System.out.println("User Code: \n"+requestDTO.getVisibleCode());

        if (!responseDTO.isValid()) {
            return new ResponseEntity<>(responseDTO, HttpStatus.UNAUTHORIZED);
        }

        Submission submission = codeExecutionService.submitCode(requestDTO,responseDTO.getUserId());
        SubmissionResponseDTO submissionResponseDTO = new SubmissionResponseDTO();
        submissionResponseDTO.setSubmissionId(submission.getSubmissionId());
        submissionResponseDTO.setMessage("All Testcases are inserted into the messaging queue");


        return new ResponseEntity<>(submissionResponseDTO, HttpStatus.OK);
    }
}
