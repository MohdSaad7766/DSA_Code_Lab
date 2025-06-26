package com.CodeLab.DB_Service.controller;

import com.CodeLab.DB_Service.model.*;
import com.CodeLab.DB_Service.requestDTO.ContestRequestDTO;
import com.CodeLab.DB_Service.requestDTO.PartialContestSubmissionRequestDTO;
import com.CodeLab.DB_Service.requestDTO.UpdatePartialContestSubmissionRequestDTO;
import com.CodeLab.DB_Service.responseDTO.*;
import com.CodeLab.DB_Service.service.ContestService;
import com.CodeLab.DB_Service.service.ProblemService;
import com.CodeLab.DB_Service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/db/contest")
public class ContestController {
    @Autowired
    ContestService contestService;

    @Autowired
    ProblemService problemService;

    @Autowired
    UserService userService;

    @PostMapping("/add")
    public ContestAddedResponseDTO addContest(@RequestBody ContestRequestDTO requestDTO){
        return contestService.addContest(requestDTO);
    }

    @GetMapping("/get/{contestId}")
    public Contest getContestById(@PathVariable UUID contestId){
        return contestService.getContestById(contestId);
    }

    @GetMapping("/get-upcoming-contests")
    public List<UpcomingContestResponseDTO> getUpcomingContests(@RequestParam UUID userId){
        return contestService.getUpcomingContests(userId);
    }

    @GetMapping("/get-upcoming-contests/{pageNo}")
    public List<UpcomingContestResponseDTO> getUpcomingContestsByPage(@PathVariable int pageNo,@RequestParam UUID userId){
        return contestService.getUpcomingContests(pageNo,userId);
    }

    @PostMapping("/register")
    public ContestRegistrationResponseDTO registerForContest(@RequestParam UUID userId, @RequestParam UUID contestId){
        ContestRegistrationResponseDTO responseDTO = new ContestRegistrationResponseDTO();
        try {
            ContestUser contestUser = contestService.registerForContest(userId,contestId);
            responseDTO.setContest(contestUser.getContest());
            responseDTO.setUser(contestUser.getUser());
            responseDTO.setMessage("User with id-"+userId+" has been successfully registered:)");
            responseDTO.setRegistered(true);
        } catch (RuntimeException e) {
            responseDTO.setContest(null);
            responseDTO.setUser(null);
            responseDTO.setMessage(e.getMessage());
            responseDTO.setRegistered(false);
        }

        return responseDTO;
    }

    @GetMapping("/get-live-contests")
    public List<LiveContestResponseDTO> getLiveContests(@RequestParam UUID userId){
        return contestService.getLiveContests(userId);
    }

    @GetMapping("/get-live-contests/{pageNo}")
    public List<LiveContestResponseDTO> getLiveContestsByPage(@PathVariable int pageNo, @RequestParam UUID userId){
        return contestService.getLiveContests(userId,pageNo);
    }

    @PostMapping("/user-start-contest")
    public ContestStartResponseDTO userStartsContest(@RequestParam UUID userId, @RequestParam UUID contestId){
        return contestService.userStartsContest(userId,contestId);
    }

    @GetMapping("/problem/{problemId}")
    public Problem getContestProblem(@PathVariable UUID problemId){
        return contestService.getContestProblem(problemId);
    }

    @PostMapping("/partial-submit")
    public PartialContestSubmission submitPartialContest(@RequestBody PartialContestSubmissionRequestDTO requestDTO){
        Problem problem = problemService.getProblem(requestDTO.getProblemId());
        User user = userService.getUserById(requestDTO.getUserId());
        Contest contest = contestService.getContestById(requestDTO.getContestId());

        return contestService.addPartialContestSubmission(requestDTO,problem,user,contest);
    }

    @PutMapping("/update-status")
    public PartialContestSubmission updatePartialContestsSubmission(@RequestBody UpdatePartialContestSubmissionRequestDTO requestDTO){
        return contestService.updatePartialContestSubmission(requestDTO);
    }

    @GetMapping("/get-partial-submission")
    public PartialContestSubmission getPartialContestsSubmission(@RequestParam UUID submissionId){
        return contestService.getPartialContestSubmission(submissionId);
    }

    @PostMapping("/submit")
    public FullContestSubmission submitContest(@RequestParam UUID userId,@RequestParam UUID contestId){
        return contestService.submitContest(userId,contestId);
    }


    @GetMapping("/get-past-contests")
    public List<PastContestResponseListDTO> getPastContests(@RequestParam UUID userId){
        return contestService.getPastContests(userId);
    }

    @GetMapping("/get-past-contests/{pageNo}")
    public List<PastContestResponseListDTO> getPastContestsByPage(@PathVariable int pageNo, @RequestParam UUID userId){
        return contestService.getPastContestsByPage(userId,pageNo);
    }

    @GetMapping("/get-past-contest-detail/{contestId}")
    public PastContestResponseDTO getPastContestDetails(@RequestParam UUID userId,@PathVariable UUID contestId){
        return contestService.getPastContestDetails(userId,contestId);
    }

    //contest problem number
    //problem response dto

}
