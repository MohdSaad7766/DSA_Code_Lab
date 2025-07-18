package com.CodeLab.DB_Service.responseDTO;

import com.CodeLab.DB_Service.enums.UserProblemStatus;
import com.CodeLab.DB_Service.enums.Difficulty;

import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString


public class ProblemResponseDTO {

    private UUID problemId;

    private int problemNo;

    private String problemTitle;

    private Difficulty problemDifficulty;

    private List<String> topicList;

    private List<String> companyList;

    private String problemDescription;

    private List<ExampleResponseDTO> exampleList = new ArrayList<>();

    private List<String> problemConstraints;

    private List<TestCaseResponseDTO> testCasesList = new ArrayList<>();

    private SolutionResponseDTO solution;

    private List<CodeTemplateResponseDTO> codeTemplateList = new ArrayList<>();

    private String note;

    private UserProblemStatus userProblemStatus;

}
