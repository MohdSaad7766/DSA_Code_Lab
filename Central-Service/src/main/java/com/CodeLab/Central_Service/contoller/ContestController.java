package com.CodeLab.Central_Service.contoller;

import com.CodeLab.Central_Service.model.FullContestSubmission;
import com.CodeLab.Central_Service.model.PartialContestSubmission;
import com.CodeLab.Central_Service.model.Problem;
import com.CodeLab.Central_Service.requestDTO.CodeRequestDTO;
import com.CodeLab.Central_Service.requestDTO.ContestRequestDTO;
import com.CodeLab.Central_Service.responseDTO.*;
import com.CodeLab.Central_Service.service.AuthenticationService;
import com.CodeLab.Central_Service.service.CodeExecutionService;
import com.CodeLab.Central_Service.service.ContestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/central/contest")
public class ContestController {

    @Autowired
    ContestService contestService;

    @Autowired
    CodeExecutionService codeExecutionService;

    @Autowired
    AuthenticationService authenticationService;

    @PostMapping("/add")
    public ResponseEntity<?> addContest(@RequestBody ContestRequestDTO requestDTO, @RequestHeader(value = "Authorization", required = false) String header){
        TokenValidationResponseDTO authResponseDTO = authenticationService.validateAdminToken(header);
        System.out.println(authResponseDTO.getMessage());

        if (!authResponseDTO.isValid()) {
            return new ResponseEntity<>(authResponseDTO, HttpStatus.UNAUTHORIZED);
        }
        ContestAddedResponseDTO responseDTO =  contestService.addContest(requestDTO);
        return new ResponseEntity<>(responseDTO,HttpStatus.OK);
    }

    @GetMapping("/get-upcoming-contests")
    public ResponseEntity<?> getUpcomingContests(@RequestHeader(value = "Authorization", required = false) String header){
        TokenValidationResponseDTO authResponseDTO = authenticationService.validateUserToken(header);
        System.out.println(authResponseDTO.getMessage());

        if (!authResponseDTO.isValid()) {
            return new ResponseEntity<>(authResponseDTO, HttpStatus.UNAUTHORIZED);
        }

        UUID userId = authResponseDTO.getUserId();

        List<UpcomingContestResponseDTO> responseDTO =  contestService.getUpcomingContests(userId);
        return new ResponseEntity<>(responseDTO,HttpStatus.OK);
    }

    @GetMapping("/get-upcoming-contests/{pageNo}")
    public ResponseEntity<?> getUpcomingContestsByPage(@PathVariable int pageNo, @RequestHeader(value = "Authorization", required = false) String header){
        TokenValidationResponseDTO authResponseDTO = authenticationService.validateUserToken(header);
        System.out.println(authResponseDTO.getMessage());

        if (!authResponseDTO.isValid()) {
            return new ResponseEntity<>(authResponseDTO, HttpStatus.UNAUTHORIZED);
        }

        if(pageNo <= 0){
            GeneralResponseDTO generalResponseDTO = new GeneralResponseDTO();
            generalResponseDTO.setMessage("Invalid Page No."+pageNo);
            return new ResponseEntity<>(generalResponseDTO, HttpStatus.BAD_REQUEST);
        }

        UUID userId = authResponseDTO.getUserId();

        List<UpcomingContestResponseDTO> responseDTO =  contestService.getUpcomingContestsByPage(pageNo,userId);
        return new ResponseEntity<>(responseDTO,HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerForContest(@RequestParam UUID contestId,@RequestHeader(value = "Authorization", required = false) String header){
        TokenValidationResponseDTO authResponseDTO = authenticationService.validateUserToken(header);
        System.out.println(authResponseDTO.getMessage());

        if (!authResponseDTO.isValid()) {
            return new ResponseEntity<>(authResponseDTO, HttpStatus.UNAUTHORIZED);
        }

        UUID userId = authResponseDTO.getUserId();
        ContestRegistrationResponseDTO responseDTO = contestService.registerForContest(userId,contestId);

        return new ResponseEntity<>(responseDTO,HttpStatus.OK);

    }


    @GetMapping("/get-live-contests")
    public ResponseEntity<?> getLiveContests(@RequestHeader(value = "Authorization", required = false) String header){
        TokenValidationResponseDTO authResponseDTO = authenticationService.validateUserToken(header);
        System.out.println(authResponseDTO.getMessage());

        if (!authResponseDTO.isValid()) {
            return new ResponseEntity<>(authResponseDTO, HttpStatus.UNAUTHORIZED);
        }

        UUID userId = authResponseDTO.getUserId();

        List<LiveContestResponseDTO> responseDTO =  contestService.getLiveContests(userId);
        return new ResponseEntity<>(responseDTO,HttpStatus.OK);
    }

    @GetMapping("/get-live-contests/{pageNo}")
    public ResponseEntity<?> getLiveContests(@PathVariable int pageNo,@RequestHeader(value = "Authorization", required = false) String header){
        TokenValidationResponseDTO authResponseDTO = authenticationService.validateUserToken(header);
        System.out.println(authResponseDTO.getMessage());

        if (!authResponseDTO.isValid()) {
            return new ResponseEntity<>(authResponseDTO, HttpStatus.UNAUTHORIZED);
        }

        if(pageNo <= 0){
            GeneralResponseDTO generalResponseDTO = new GeneralResponseDTO();
            generalResponseDTO.setMessage("Invalid Page No."+pageNo);
            return new ResponseEntity<>(generalResponseDTO, HttpStatus.BAD_REQUEST);
        }
        UUID userId = authResponseDTO.getUserId();

        List<LiveContestResponseDTO> responseDTO =  contestService.getLiveContestsByPage(pageNo,userId);
        return new ResponseEntity<>(responseDTO,HttpStatus.OK);
    }

    @PostMapping("/user-start-contest")
    public ResponseEntity<?> userStartsContest(@RequestParam UUID contestId,@RequestHeader(value = "Authorization", required = false) String header){
        TokenValidationResponseDTO authResponseDTO = authenticationService.validateUserToken(header);
        System.out.println(authResponseDTO.getMessage());

        if (!authResponseDTO.isValid()) {
            return new ResponseEntity<>(authResponseDTO, HttpStatus.UNAUTHORIZED);
        }

        UUID userId = authResponseDTO.getUserId();
        System.out.println(userId);
        ContestStartResponseDTO responseDTO =  contestService.userStartsContest(userId,contestId);
        return new ResponseEntity<>(responseDTO,HttpStatus.OK);
    }

    @GetMapping("/problem/{problemId}")
    public ResponseEntity<?> getContestProblem(@PathVariable UUID problemId,@RequestHeader(value = "Authorization", required = false) String header){
        TokenValidationResponseDTO authResponseDTO = authenticationService.validateUserToken(header);
        System.out.println(authResponseDTO.getMessage());

        if (!authResponseDTO.isValid()) {
            return new ResponseEntity<>(authResponseDTO, HttpStatus.UNAUTHORIZED);
        }

        Problem responseDTO =  contestService.getContestProblem(problemId);
        return new ResponseEntity<>(responseDTO,HttpStatus.OK);
    }

    @PostMapping("/partial-contest-submission")
    public ResponseEntity<?> partialSubmitCode(@RequestBody CodeRequestDTO requestDTO,@RequestParam UUID contestId, @RequestHeader(value = "Authorization", required = false) String header) {
        TokenValidationResponseDTO responseDTO = authenticationService.validateUserToken(header);
        System.out.println(responseDTO.getMessage());

        if (!responseDTO.isValid()) {
            return new ResponseEntity<>(responseDTO, HttpStatus.UNAUTHORIZED);
        }

        PartialContestSubmission submission = codeExecutionService.contestPartialSubmit(requestDTO,responseDTO.getUserId(),contestId);
        SubmissionResponseDTO submissionResponseDTO = new SubmissionResponseDTO();
        submissionResponseDTO.setSubmissionId(submission.getSubmissionId());
        submissionResponseDTO.setMessage("All Testcases are inserted into the messaging queue");

        return new ResponseEntity<>(submissionResponseDTO, HttpStatus.OK);
    }
    @PostMapping("/submit-contest")
    public ResponseEntity<?> submitContest(@RequestParam UUID contestId, @RequestHeader(value = "Authorization", required = false) String header) {
        TokenValidationResponseDTO validationResponseDTO = authenticationService.validateUserToken(header);
        System.out.println(validationResponseDTO.getMessage());

        if (!validationResponseDTO.isValid()) {
            return new ResponseEntity<>(validationResponseDTO, HttpStatus.UNAUTHORIZED);
        }

        UUID userId = validationResponseDTO.getUserId();

        FullContestSubmission responseDTO = contestService.submitContest(userId,contestId);

        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @GetMapping("/get-past-contests")
    public ResponseEntity<?> getPastContests(@RequestHeader(value = "Authorization", required = false) String header){
        TokenValidationResponseDTO authResponseDTO = authenticationService.validateUserToken(header);
        System.out.println(authResponseDTO.getMessage());

        if (!authResponseDTO.isValid()) {
            return new ResponseEntity<>(authResponseDTO, HttpStatus.UNAUTHORIZED);
        }

        UUID userId = authResponseDTO.getUserId();

        List<PastContestResponseListDTO> responseDTO =  contestService.getPastContests(userId);
        return new ResponseEntity<>(responseDTO,HttpStatus.OK);
    }

    @GetMapping("/get-past-contests/{pageNo}")
    public ResponseEntity<?> getPastContests(@PathVariable int pageNo,@RequestHeader(value = "Authorization", required = false) String header){
        TokenValidationResponseDTO authResponseDTO = authenticationService.validateUserToken(header);
        System.out.println(authResponseDTO.getMessage());

        if (!authResponseDTO.isValid()) {
            return new ResponseEntity<>(authResponseDTO, HttpStatus.UNAUTHORIZED);
        }

        if(pageNo <= 0){
            GeneralResponseDTO generalResponseDTO = new GeneralResponseDTO();
            generalResponseDTO.setMessage("Invalid Page No."+pageNo);
            return new ResponseEntity<>(generalResponseDTO, HttpStatus.BAD_REQUEST);
        }

        UUID userId = authResponseDTO.getUserId();


        List<PastContestResponseListDTO> responseDTO =  contestService.getPastContestsByPage(pageNo,userId);
        return new ResponseEntity<>(responseDTO,HttpStatus.OK);
    }

    @GetMapping("/get-past-contest-detail/{contestId}")
    public ResponseEntity<?> getPastContestDetails(@PathVariable UUID contestId,@RequestHeader(value = "Authorization", required = false) String header){
        TokenValidationResponseDTO authResponseDTO = authenticationService.validateUserToken(header);
        System.out.println(authResponseDTO.getMessage());

        if (!authResponseDTO.isValid()) {
            return new ResponseEntity<>(authResponseDTO, HttpStatus.UNAUTHORIZED);
        }

        UUID userId = authResponseDTO.getUserId();

        PastContestResponseDTO responseDTO = contestService.getPastContestDetails(userId,contestId);;
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }


    @GetMapping("/get-partial-submission")
    public ResponseEntity<?> getPartialContestSubmission(@RequestParam UUID submissionId,@RequestHeader(value = "Authorization", required = false) String header){
        TokenValidationResponseDTO authResponseDTO = authenticationService.validateUserToken(header);
        System.out.println(authResponseDTO.getMessage());

        if (!authResponseDTO.isValid()) {
            return new ResponseEntity<>(authResponseDTO, HttpStatus.UNAUTHORIZED);
        }

        PartialContestSubmission partialContestSubmission = contestService.getPartialContestSubmission(submissionId);
        return new ResponseEntity<>(partialContestSubmission, HttpStatus.OK);

    }

}
