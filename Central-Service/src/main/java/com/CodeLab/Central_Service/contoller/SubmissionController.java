package com.CodeLab.Central_Service.contoller;

import com.CodeLab.Central_Service.model.Submission;
import com.CodeLab.Central_Service.responseDTO.GeneralResponseDTO;
import com.CodeLab.Central_Service.responseDTO.TokenValidationResponseDTO;
import com.CodeLab.Central_Service.service.AuthenticationService;
import com.CodeLab.Central_Service.service.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/central/submission")
public class SubmissionController {
    @Autowired
    SubmissionService submissionService;

    @Autowired
    AuthenticationService authenticationService;

    @GetMapping("/get-by-id/{submissionId}")
    public ResponseEntity<?> getSubmissionById(@PathVariable UUID submissionId,@RequestHeader(value = "Authorization", required = false) String header){

        TokenValidationResponseDTO responseDTO = authenticationService.validateUserToken(header);
        System.out.println(responseDTO.getMessage());

        if (!responseDTO.isValid()) {
            return new ResponseEntity<>(responseDTO, HttpStatus.UNAUTHORIZED);
        }

        Submission submission = submissionService.getSubmissionById(submissionId);

        if(submission == null){
            GeneralResponseDTO generalResponseDTO = new GeneralResponseDTO();
            generalResponseDTO.setMessage("Submission with id-"+submissionId+" not Found!!!");
            return new ResponseEntity<>(generalResponseDTO, HttpStatus.OK);
        }
//        System.out.println("Submission: "+submission);
        return new ResponseEntity<>(submission, HttpStatus.OK);
    }

    @GetMapping("/get-by-user-id")
    public ResponseEntity<?> getSubmissionsByUserId(@RequestHeader(value = "Authorization", required = false) String header){
        TokenValidationResponseDTO responseDTO = authenticationService.validateUserToken(header);
        System.out.println(responseDTO.getMessage());

        if (!responseDTO.isValid()) {
            return new ResponseEntity<>(responseDTO, HttpStatus.UNAUTHORIZED);
        }

        UUID userId = responseDTO.getUserId();

        List<Submission> submissions = submissionService.getSubmissionsByUserId(userId);
        return new ResponseEntity<>(submissions, HttpStatus.OK);
    }
    @GetMapping("/get-by-user-id-and-problem-id")
    public ResponseEntity<?> getSubmissionsByUserId(@RequestParam UUID problemId,@RequestHeader(value = "Authorization", required = false) String header){
        TokenValidationResponseDTO responseDTO = authenticationService.validateUserToken(header);
        System.out.println(responseDTO.getMessage());

        if (!responseDTO.isValid()) {
            return new ResponseEntity<>(responseDTO, HttpStatus.UNAUTHORIZED);
        }

        UUID userId = responseDTO.getUserId();

        List<Submission> submissions = submissionService.getSubmissionsByUserIdAndProblemId(userId,problemId);
        return new ResponseEntity<>(submissions, HttpStatus.OK);
    }

    @GetMapping("/get-by-problem-id")
    public ResponseEntity<?> getSubmissionsByProblemId(@RequestParam UUID problemId,@RequestHeader(value = "Authorization", required = false) String header){
        TokenValidationResponseDTO responseDTO = authenticationService.validateAdminToken(header);
        System.out.println(responseDTO.getMessage());

        if (!responseDTO.isValid()) {
            return new ResponseEntity<>(responseDTO, HttpStatus.UNAUTHORIZED);
        }


        List<Submission> submissions = submissionService.getSubmissionsByProblemId(problemId);
        return new ResponseEntity<>(submissions, HttpStatus.OK);
    }


    @GetMapping("/get-all")
    public ResponseEntity<?> getAllSubmissions(@RequestHeader(value = "Authorization", required = false) String header){
        TokenValidationResponseDTO responseDTO = authenticationService.validateAdminToken(header);
        System.out.println(responseDTO.getMessage());

        if (!responseDTO.isValid()) {
            return new ResponseEntity<>(responseDTO, HttpStatus.UNAUTHORIZED);
        }


        List<Submission> submissions = submissionService.getAllSubmissions();
        return new ResponseEntity<>(submissions, HttpStatus.OK);
    }

    @GetMapping("/get-latest-of-user")
    public ResponseEntity<?> getLatestSubmissionByUserId(@RequestHeader(value = "Authorization", required = false) String header){

        TokenValidationResponseDTO responseDTO = authenticationService.validateUserToken(header);
        System.out.println(responseDTO.getMessage());

        if (!responseDTO.isValid()) {
            return new ResponseEntity<>(responseDTO, HttpStatus.UNAUTHORIZED);
        }

        UUID userID = responseDTO.getUserId();

        Submission submission = submissionService.getLatestSubmissionByUserId(userID);
        if(submission == null){
            GeneralResponseDTO generalResponseDTO = new GeneralResponseDTO();
            generalResponseDTO.setMessage("No submission found by User with id-"+userID);
            return new ResponseEntity<>(generalResponseDTO, HttpStatus.OK);
        }
        return new ResponseEntity<>(submission, HttpStatus.OK);
    }
}
