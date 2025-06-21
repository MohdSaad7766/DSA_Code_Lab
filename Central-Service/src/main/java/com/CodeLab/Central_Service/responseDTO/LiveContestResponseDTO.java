package com.CodeLab.Central_Service.responseDTO;

import com.CodeLab.Central_Service.model.Contest;
import lombok.*;

import java.time.LocalDateTime;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString

public class LiveContestResponseDTO {
    private Contest contest;
    private boolean userRegistered;
    private boolean userRejoin;
    private long remainingTimeInSeconds;
    private boolean contestSubmitted;
}
