package com.CodeLab.Central_Service.requestDTO;

import com.CodeLab.Central_Service.enums.Difficulty;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString

public class ProblemRequestDTO {

    private String problemTitle;//

    private Difficulty problemDifficulty;//

    private String problemDescription;//

    private List<ExampleRequestDTO> exampleRequestDTOList = new ArrayList<>();//

    private List<String> problemConstraintsList;


    private List<String> topicList;


    private List<String> companyList;

    private List<TestCaseRequestDTO> testCaseRequestDTOList = new ArrayList<>();

    private SolutionRequestDTO solutionRequestDTO;

    private List<CodeTemplateRequestDTO> codeTemplateRequestDTOList = new ArrayList<>();

    private String note;
}
