package com.CodeLab.DB_Service.responseDTO;

import com.CodeLab.DB_Service.model.Contest;
import com.CodeLab.DB_Service.model.ContestProblem;
import com.CodeLab.DB_Service.model.Problem;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString

public class ContestStartResponseDTO {
    private Contest contest;
    private List<ContestProblem> problemList;
    private long remainingTimeInSeconds;
}
