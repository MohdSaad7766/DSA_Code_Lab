package com.CodeLab.DB_Service.requestDTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString

public class ContestStartRequestDTO {

    private String userEmail;
    private String userName;
    private String contestName;
    private String contestStartTime;
    private String contestEndTime;
    private String contestDuration;

}