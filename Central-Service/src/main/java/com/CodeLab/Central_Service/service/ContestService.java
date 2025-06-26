package com.CodeLab.Central_Service.service;

import com.CodeLab.Central_Service.integration.DBService;
import com.CodeLab.Central_Service.model.FullContestSubmission;
import com.CodeLab.Central_Service.model.PartialContestSubmission;
import com.CodeLab.Central_Service.model.Problem;
import com.CodeLab.Central_Service.requestDTO.ContestRequestDTO;
import com.CodeLab.Central_Service.responseDTO.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



import java.util.List;
import java.util.UUID;


@Service
public class ContestService {
    @Autowired
    DBService dbService;

    public ContestAddedResponseDTO addContest(ContestRequestDTO requestDTO){
        return dbService.callAddContest(requestDTO);
    }

    public List<UpcomingContestResponseDTO> getUpcomingContests(UUID userId){
       return dbService.callGetUpcomingContests(userId);
    }

    public List<UpcomingContestResponseDTO> getUpcomingContestsByPage(int pageNo, UUID userId){
        return dbService.callGetUpcomingContestsByPage(pageNo,userId);
    }

    public ContestRegistrationResponseDTO registerForContest(UUID userId,UUID contestId){
        return dbService.callRegisterForContest(userId,contestId);
    }

    public List<LiveContestResponseDTO> getLiveContests(UUID userId){
        return dbService.callGetLiveContests(userId);
    }

    public List<LiveContestResponseDTO> getLiveContestsByPage(int pageNo,UUID userId){
        return dbService.callGetLiveContestsByPage(pageNo,userId);
    }

    public ContestStartResponseDTO userStartsContest(UUID userId,UUID contestId){
        return dbService.callUserStartsContest(userId,contestId);
    }

    public Problem getContestProblem(UUID problemId){
        return dbService.callGetContestProblem(problemId);
    }

    public FullContestSubmission submitContest(UUID userId,UUID contestId){
        return dbService.callSubmitContest(userId,contestId);
    }

    public List<PastContestResponseListDTO> getPastContests(UUID userId){
        return dbService.callGetPastContests(userId);
    }

    public List<PastContestResponseListDTO> getPastContestsByPage(int pageNo, UUID userId){
        return dbService.callGetPastContestsByPage(pageNo,userId);
    }

    public PastContestResponseDTO getPastContestDetails(UUID userId,UUID contestId){
        return dbService.callGetPastContestDetails(userId,contestId);
    }

    public PartialContestSubmission getPartialContestSubmission(UUID submissionId){
        return dbService.callGetPartialContestSubmission(submissionId);
    }

}
