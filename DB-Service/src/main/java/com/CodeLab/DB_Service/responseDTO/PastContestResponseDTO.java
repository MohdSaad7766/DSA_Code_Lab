package com.CodeLab.DB_Service.responseDTO;

import com.CodeLab.DB_Service.model.Contest;
import com.CodeLab.DB_Service.model.ContestProblem;
import com.CodeLab.DB_Service.model.LeaderboardEntry;
import com.CodeLab.DB_Service.model.Problem;
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
    private int totalParticipants;
}
