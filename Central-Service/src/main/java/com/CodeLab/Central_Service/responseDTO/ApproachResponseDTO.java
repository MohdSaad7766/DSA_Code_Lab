package com.CodeLab.Central_Service.responseDTO;

import com.CodeLab.Central_Service.enums.ApproachType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString

public class ApproachResponseDTO {

    private ApproachType approachType;

    private String approachDescription;
}
