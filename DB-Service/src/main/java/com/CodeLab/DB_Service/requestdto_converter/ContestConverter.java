package com.CodeLab.DB_Service.requestdto_converter;

import com.CodeLab.DB_Service.model.Contest;
import com.CodeLab.DB_Service.model.ContestProblem;
import com.CodeLab.DB_Service.model.Problem;
import com.CodeLab.DB_Service.requestDTO.ContestRequestDTO;

public class ContestConverter {
    public static Contest contestConverter(ContestRequestDTO requestDTO){
        Contest contest = new Contest();
        contest.setContestName(requestDTO.getContestName());
        contest.setContestDescription(requestDTO.getContestDescription());
        contest.setStartTime(requestDTO.getStartTime());
        contest.setEndTime(requestDTO.getEndTime());
        contest.setDuration(requestDTO.getDuration());

        return contest;
    }

    public static ContestProblem contestProblemConverter(Problem problem,Contest contest){
        ContestProblem contestProblem = new ContestProblem();
        contestProblem.setProblem(problem);
        contestProblem.setContest(contest);
        contestProblem.setContestQuestionNo(0);
        return contestProblem;
    }
}
