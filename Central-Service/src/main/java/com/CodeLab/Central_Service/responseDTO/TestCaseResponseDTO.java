package com.CodeLab.Central_Service.responseDTO;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString

public class TestCaseResponseDTO {

    private String testCaseInput;

    private String testCaseOutput;

    private boolean isVisible;
}
