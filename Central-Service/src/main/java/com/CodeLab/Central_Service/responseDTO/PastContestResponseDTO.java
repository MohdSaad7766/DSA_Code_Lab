package com.CodeLab.Central_Service.responseDTO;

import com.CodeLab.Central_Service.model.Contest;
import com.CodeLab.Central_Service.model.ContestProblem;
import com.CodeLab.Central_Service.model.Problem;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString

public class PastContestResponseDTO {
    private Contest contest;
    private boolean userParticipated;
    private Integer userRank;
    List<LeaderboardEntryResponseDTO> leaderboard = new ArrayList<>();
    List<ContestProblem> problemList = new ArrayList<>();
//    List<String> percentageGotByUser;
    private int totalParticipants;
}
