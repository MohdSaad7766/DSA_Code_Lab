package com.CodeLab.DB_Service.requestDTO;


import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString

public class ContestRequestDTO {

    private String contestName;

    private String contestDescription;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private long duration; //in seconds

    List<ProblemRequestDTO> newProblemList;

    List<UUID> existingProblemList;

}
