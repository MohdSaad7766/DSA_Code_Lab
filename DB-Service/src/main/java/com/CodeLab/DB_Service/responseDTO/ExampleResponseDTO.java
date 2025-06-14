package com.CodeLab.DB_Service.responseDTO;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ExampleResponseDTO {

    private String exampleInput;

    private String exampleOutput;

    private String exampleExplanation;

}
