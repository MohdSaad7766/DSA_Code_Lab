package com.CodeLab.Central_Service.responseDTO;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SolutionResponseDTO {
    private List<ApproachResponseDTO> approachList  = new ArrayList<>();
}
