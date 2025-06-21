package com.CodeLab.Central_Service.contoller;

import com.CodeLab.Central_Service.enums.Difficulty;
import com.CodeLab.Central_Service.enums.UserProblemStatus;
import com.CodeLab.Central_Service.model.Problem;
import com.CodeLab.Central_Service.requestDTO.ProblemRequestDTO;
import com.CodeLab.Central_Service.responseDTO.*;
import com.CodeLab.Central_Service.service.AuthenticationService;
import com.CodeLab.Central_Service.service.ProblemService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

@RestController
@RequestMapping("/central/problem") // http://localhost:8080/central/problem
public class ProblemController {

    private static final Logger logger = LoggerFactory.getLogger(ProblemController.class);

    @Autowired
    private ProblemService problemService;

    @Autowired
    private AuthenticationService authenticationService;

    // Helper: Page number validation
    private boolean isInvalidPage(int pageNo) {
        return pageNo <= 0;
    }

    // Helper: Create generic response
    private ResponseEntity<GeneralResponseDTO> badRequest(String message) {
        GeneralResponseDTO response = new GeneralResponseDTO();
        response.setMessage(message);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Helper: Admin token validation
    private ResponseEntity<?> validateAdmin(String header) {
        TokenValidationResponseDTO token = authenticationService.validateAdminToken(header);
        logger.info("Admin Token Check: " + token.getMessage());
        if (!token.isValid()) {
            return new ResponseEntity<>(token, HttpStatus.UNAUTHORIZED);
        }
        return null;
    }

    // Helper: User token validation
    private TokenValidationResponseDTO validateUser(String header) {
        TokenValidationResponseDTO token = authenticationService.validateUserToken(header);
        logger.info("User Token Check: " + token.getMessage());
        return token;
    }

    // ==================== Add Problem ====================

    @PostMapping("/add")
    public ResponseEntity<?> addProblem(@RequestBody ProblemRequestDTO problemRequestDTO,
                                        @RequestHeader(value = "Authorization", required = false) String header) {
        ResponseEntity<?> authCheck = validateAdmin(header);
        if (authCheck != null) return authCheck;

        ProblemAddedResponseDTO response = problemService.addProblem(problemRequestDTO);
        response.setMessage(response.getProblem() != null ? "Problem Added Successfully :)" : "Problem Not Added!!!");
        return new ResponseEntity<>(response, response.getProblem() != null ? HttpStatus.CREATED : HttpStatus.OK);
    }

    // ==================== Get All Problems ====================

    @GetMapping("/get/{pageNo}")
    public ResponseEntity<?> getProblemsByPage(@PathVariable int pageNo,
                                               @RequestHeader(value = "Authorization", required = false) String header) {
        if (isInvalidPage(pageNo)) return badRequest("Invalid Page No. " + pageNo);

        TokenValidationResponseDTO token = validateUser(header);

        return new ResponseEntity<>(
                token.isValid() ? problemService.getProblemsByPage(pageNo, token.getUserId()) :
                        problemService.getProblemsByPage(pageNo),
                HttpStatus.OK
        );
    }

    @GetMapping("/get/")
    public ResponseEntity<?> getProblems(@RequestHeader(value = "Authorization", required = false) String header) {
        TokenValidationResponseDTO token = validateUser(header);
        return new ResponseEntity<>(
                token.isValid() ? problemService.getProblemsByPage(0, token.getUserId()) :
                        problemService.getProblemsByPage(0),
                HttpStatus.OK
        );
    }

    @GetMapping("/get-a-problem/{problemId}")
    public ResponseEntity<?> getProblemForUser(@PathVariable UUID problemId,@RequestHeader(value = "Authorization", required = false) String header) {
        TokenValidationResponseDTO token = validateUser(header);

        ProblemResponseDTO problem = token.isValid() ? problemService.getProblemByIdForUser(problemId,token.getUserId()) :  problemService.getProblemByIdForUser(problemId);
        if (problem == null)
            return new ResponseEntity<>(new GeneralResponseDTO("Problem with Id-" + problemId + " not Found!!!"), HttpStatus.OK);
        return new ResponseEntity<>(problem, HttpStatus.OK);
    }

    @GetMapping("/get-for-system/{problemId}")
    public ResponseEntity<?> getProblemForSystem(@PathVariable UUID problemId,
                                                 @RequestHeader(value = "Authorization", required = false) String header) {
        ResponseEntity<?> authCheck = validateAdmin(header);
        if (authCheck != null) return authCheck;

        Problem problem = problemService.getProblemByIdForSystem(problemId);
        if (problem == null)
            return new ResponseEntity<>(new GeneralResponseDTO("Problem with Id-" + problemId + " not Found!!!"), HttpStatus.OK);

        return new ResponseEntity<>(problem, HttpStatus.OK);
    }

    // ==================== Filter by Topic ====================

    @GetMapping("/get-by-topic/{pageNo}")
    public ResponseEntity<?> getProblemsTopicWise(@PathVariable int pageNo,
                                                  @RequestParam String topicName,
                                                  @RequestHeader(value = "Authorization", required = false) String header) {
        if (isInvalidPage(pageNo)) return badRequest("Invalid Page No. " + pageNo);

        TokenValidationResponseDTO token = validateUser(header);
        return new ResponseEntity<>(
                token.isValid() ? problemService.getProblemsTopicWise(pageNo, topicName, token.getUserId()) :
                        problemService.getProblemsTopicWise(pageNo, topicName),
                HttpStatus.OK
        );
    }

    @GetMapping("/get-by-topic/")
    public ResponseEntity<?> getProblemsTopicWise(@RequestParam String topicName,
                                                  @RequestHeader(value = "Authorization", required = false) String header) {
        TokenValidationResponseDTO token = validateUser(header);
        return new ResponseEntity<>(
                token.isValid() ? problemService.getProblemsTopicWise(0, topicName, token.getUserId()) :
                        problemService.getProblemsTopicWise(0, topicName),
                HttpStatus.OK
        );
    }

    // ==================== Filter by Company ====================

    @GetMapping("/get-by-company/{pageNo}")
    public ResponseEntity<?> getProblemsCompanyWise(@PathVariable int pageNo,
                                                    @RequestParam String companyName,
                                                    @RequestHeader(value = "Authorization", required = false) String header) {
        if (isInvalidPage(pageNo)) return badRequest("Invalid Page No. " + pageNo);

        TokenValidationResponseDTO token = validateUser(header);
        return new ResponseEntity<>(
                token.isValid() ? problemService.getProblemsCompanyWise(pageNo, companyName, token.getUserId()) :
                        problemService.getProblemsCompanyWise(pageNo, companyName),
                HttpStatus.OK
        );
    }

    @GetMapping("/get-by-company/")
    public ResponseEntity<?> getProblemsCompanyWise(@RequestParam String companyName,
                                                    @RequestHeader(value = "Authorization", required = false) String header) {
        TokenValidationResponseDTO token = validateUser(header);
        return new ResponseEntity<>(
                token.isValid() ? problemService.getProblemsCompanyWise(0, companyName, token.getUserId()) :
                        problemService.getProblemsCompanyWise(0, companyName),
                HttpStatus.OK
        );
    }

    // ==================== Filter by Status ====================

    @GetMapping("/get-by-status/{pageNo}")
    public ResponseEntity<?> getProblemsStatusWise(@PathVariable int pageNo,
                                                   @RequestParam UserProblemStatus status,
                                                   @RequestHeader(value = "Authorization", required = false) String header) {
        if (isInvalidPage(pageNo)) return badRequest("Invalid Page No. " + pageNo);

        TokenValidationResponseDTO token = validateUser(header);
        return new ResponseEntity<>(
                token.isValid() ? problemService.getProblemsStatusWise(pageNo, status, token.getUserId()) :
                        problemService.getProblemsStatusWise(pageNo, status),
                HttpStatus.OK
        );
    }

    @GetMapping("/get-by-status/")
    public ResponseEntity<?> getProblemsStatusWise(@RequestParam UserProblemStatus status,
                                                   @RequestHeader(value = "Authorization", required = false) String header) {
        TokenValidationResponseDTO token = validateUser(header);
        return new ResponseEntity<>(
                token.isValid() ? problemService.getProblemsStatusWise(0, status, token.getUserId()) :
                        problemService.getProblemsStatusWise(0, status),
                HttpStatus.OK
        );
    }

    // ==================== Filter by Difficulty ====================

    @GetMapping("/get-by-difficulty/{pageNo}")
    public ResponseEntity<?> getByDifficulty(@PathVariable int pageNo,
                                             @RequestParam Difficulty difficulty,
                                             @RequestHeader(value = "Authorization", required = false) String header) {
        if (isInvalidPage(pageNo)) return badRequest("Invalid Page No. " + pageNo);

        TokenValidationResponseDTO token = validateUser(header);
        return new ResponseEntity<>(
                token.isValid() ? problemService.getProblemByDifficulty(pageNo, difficulty, token.getUserId()) :
                        problemService.getProblemByDifficulty(pageNo, difficulty),
                HttpStatus.OK
        );
    }

    @GetMapping("/get-by-difficulty/")
    public ResponseEntity<?> getByDifficulty(@RequestParam Difficulty difficulty,
                                             @RequestHeader(value = "Authorization", required = false) String header) {
        TokenValidationResponseDTO token = validateUser(header);
        return new ResponseEntity<>(
                token.isValid() ? problemService.getProblemByDifficulty(0, difficulty, token.getUserId()) :
                        problemService.getProblemByDifficulty(0, difficulty),
                HttpStatus.OK
        );
    }

    // ==================== Search ====================

    @GetMapping("/search/{pageNo}")
    public ResponseEntity<?> searchProblem(@PathVariable int pageNo,
                                           @RequestParam String keyword,
                                           @RequestHeader(value = "Authorization", required = false) String header) {
        if (isInvalidPage(pageNo)) return badRequest("Invalid Page No. " + pageNo);

        TokenValidationResponseDTO token = validateUser(header);
        return new ResponseEntity<>(
                token.isValid() ? problemService.searchProblem(keyword, pageNo, token.getUserId()) :
                        problemService.searchProblem(keyword, pageNo),
                HttpStatus.OK
        );
    }

    @GetMapping("/search/")
    public ResponseEntity<?> searchProblem(@RequestParam String keyword,
                                           @RequestHeader(value = "Authorization", required = false) String header) {
        TokenValidationResponseDTO token = validateUser(header);
        return new ResponseEntity<>(
                token.isValid() ? problemService.searchProblem(keyword, 0, token.getUserId()) :
                        problemService.searchProblem(keyword, 0),
                HttpStatus.OK
        );
    }

    // ==================== Counts ====================

    @GetMapping("/get-count-by-topic")
    public ResponseEntity<?> getProblemsCountTopicWise(@RequestParam String topicName) {
        return new ResponseEntity<>(problemService.getProblemsCountTopicWise(topicName), HttpStatus.OK);
    }

    @GetMapping("/get-count-by-company")
    public ResponseEntity<?> getProblemsCountCompanyWise(@RequestParam String companyName) {
        return new ResponseEntity<>(problemService.getProblemsCountCompanyWise(companyName), HttpStatus.OK);
    }

    // ==================== Delete ====================

    @DeleteMapping("/delete/{problemId}")
    public ResponseEntity<?> deleteById(@PathVariable UUID problemId,
                                        @RequestHeader(value = "Authorization", required = false) String header) {
        ResponseEntity<?> authCheck = validateAdmin(header);
        if (authCheck != null) return authCheck;

        return new ResponseEntity<>(problemService.deleteById(problemId), HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteAll(@RequestHeader(value = "Authorization", required = false) String header) {
        ResponseEntity<?> authCheck = validateAdmin(header);
        if (authCheck != null) return authCheck;

        return new ResponseEntity<>(problemService.deleteAll(), HttpStatus.OK);
    }
}
