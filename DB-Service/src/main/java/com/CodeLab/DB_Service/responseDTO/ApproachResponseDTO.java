package com.CodeLab.DB_Service.responseDTO;

import com.CodeLab.DB_Service.enums.ApproachType;
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
