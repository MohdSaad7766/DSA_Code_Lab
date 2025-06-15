package com.CodeLab.DB_Service.requestdto_converter;

import com.CodeLab.DB_Service.enums.UserProblemStatus;
import com.CodeLab.DB_Service.model.*;
import com.CodeLab.DB_Service.requestDTO.*;
import com.CodeLab.DB_Service.responseDTO.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProblemConverter {

    public static Problem requestDTO_problemConverter(ProblemRequestDTO problemRequestDTO, boolean isVisible) {
        String problemTopics = processString(problemRequestDTO.getTopicList(),true);
        String problemCompanies = processString(problemRequestDTO.getCompanyList(),true);
        String problemConstraints = processString(problemRequestDTO.getProblemConstraintsList(),false);

        Problem problem = new Problem();

        problem.setProblemTitle(problemRequestDTO.getProblemTitle());
        problem.setProblemDifficulty(problemRequestDTO.getProblemDifficulty());
        problem.setProblemDescription(problemRequestDTO.getProblemDescription());

        problem.setProblemConstraints(problemConstraints);
        problem.setNote(problemRequestDTO.getNote());
        problem.setTopicList(problemTopics);
        problem.setCompanyList(problemCompanies);
        problem.setVisible(isVisible);

        List<Example> exampleList = ExampleConverter.exampleConverter(problemRequestDTO.getExampleRequestDTOList(), problem);
        problem.setExampleList(exampleList);

        List<TestCase> testCaseList = TestCaseConverter.testCaseConverter(problemRequestDTO.getTestCaseRequestDTOList(), problem);
        problem.setTestCasesList(testCaseList);

        Solution solution = SolutionConverter.solutionConverter(problemRequestDTO.getSolutionRequestDTO(), problem);
        problem.setSolution(solution);

        List<CodeTemplate> codeTemplateList = CodeTemplateConverter.codeTemplateConverter(problemRequestDTO.getCodeTemplateRequestDTOList(), problem);
        problem.setCodeTemplateList(codeTemplateList);

        return problem;
    }


    public static List<ProblemResponseDTO> problem_responseDTOConverter(List<Problem> problemList){
        List<ProblemResponseDTO> responseDTOList = new ArrayList<>();

        for (Problem problem : problemList){
            ProblemResponseDTO responseDTO = ProblemConverter.problem_responseDTOConverter(problem);
            responseDTOList.add(responseDTO);
        }
        return responseDTOList;
    }

    public static ProblemResponseDTO problem_responseDTOConverter(Problem problem){
        ProblemResponseDTO responseDTO = new ProblemResponseDTO();
        responseDTO.setProblemId(problem.getProblemId());
        responseDTO.setProblemNo(problem.getProblemNo());
        responseDTO.setNote(problem.getNote());
        responseDTO.setProblemTitle(problem.getProblemTitle());


        List<String> problemContraintsList = processList(problem.getProblemConstraints());
        responseDTO.setProblemConstraints(problemContraintsList);

        responseDTO.setProblemDifficulty(problem.getProblemDifficulty());
        responseDTO.setProblemDescription(problem.getProblemDescription());

        List<String> problemTopicList = processList(problem.getTopicList());
        responseDTO.setTopicList(problemTopicList);

        List<String> problemCompanyList = processList(problem.getCompanyList());
        responseDTO.setCompanyList(problemCompanyList);

        responseDTO.setExampleList(ProblemConverter.exampleConverter(problem.getExampleList()));
        responseDTO.setTestCasesList(ProblemConverter.testCaseConverter(problem.getTestCasesList()));
        responseDTO.setSolution(ProblemConverter.solutionConverter(problem.getSolution()));
        responseDTO.setCodeTemplateList(ProblemConverter.codeTemplateConverter(problem.getCodeTemplateList()));
        responseDTO.setUserProblemStatus(UserProblemStatus.UNATTEMPTED);

        return responseDTO;
    }

    public static List<CodeTemplateResponseDTO> codeTemplateConverter(List<CodeTemplate> codeTemplateList){
        List<CodeTemplateResponseDTO> responseDTOList = new ArrayList<>();

        for(CodeTemplate codeTemplate: codeTemplateList){
            CodeTemplateResponseDTO responseDTO = new CodeTemplateResponseDTO();
            responseDTO.setInvisibleTemplateCode(codeTemplate.getInvisibleTemplateCode());
            responseDTO.setVisibleTemplateCode(codeTemplate.getVisibleTemplateCode());
            responseDTO.setLanguage(codeTemplate.getLanguage());

            responseDTOList.add(responseDTO);
        }

        return responseDTOList;
    }


    public static SolutionResponseDTO solutionConverter(Solution solution){
        SolutionResponseDTO responseDTO = new SolutionResponseDTO();

        List<ApproachResponseDTO> approachResponseDTOList = ProblemConverter.approachConverter(solution.getApproachList());
        responseDTO.setApproachList(approachResponseDTOList);
        return responseDTO;
    }

    public static List<ApproachResponseDTO> approachConverter(List<Approach> approachList){
        List<ApproachResponseDTO> responseDTOList = new ArrayList<>();

        for(Approach approach: approachList){
            ApproachResponseDTO responseDTO = new ApproachResponseDTO();
            responseDTO.setApproachType(approach.getApproachType());
            responseDTO.setApproachDescription(approach.getApproachDescription());

            responseDTOList.add(responseDTO);
        }

        return responseDTOList;
    }
    public static List<TestCaseResponseDTO> testCaseConverter(List<TestCase> testCaseList){
        List<TestCaseResponseDTO> responseDTOList = new ArrayList<>();

        for(TestCase testCase: testCaseList){
            TestCaseResponseDTO responseDTO = new TestCaseResponseDTO();
            responseDTO.setTestCaseInput(testCase.getTestCaseInput());
            responseDTO.setTestCaseOutput(testCase.getTestCaseOutput());
            responseDTO.setVisible(testCase.isVisible());

            responseDTOList.add(responseDTO);
        }

        return responseDTOList;
    }

    public static List<ExampleResponseDTO> exampleConverter(List<Example> exampleList){
        List<ExampleResponseDTO> responseDTOList = new ArrayList<>();

        for(Example example: exampleList){
            ExampleResponseDTO responseDTO = new ExampleResponseDTO();
            responseDTO.setExampleInput(example.getExampleInput());
            responseDTO.setExampleOutput(example.getExampleOutput());
            responseDTO.setExampleExplanation(example.getExampleExplanation());

            responseDTOList.add(responseDTO);
        }

        return responseDTOList;
    }

    public static String processString(List<String> list,boolean isUppercase){
        StringBuilder finalList = new StringBuilder();
        int i = list.size();
        for(String li : list){
            String str = (isUppercase) ?li.trim().toUpperCase() : li.trim();
            finalList.append(str);
            if(i > 1 ){
                finalList.append(", ");
            }
            i--;
        }

        return finalList.toString();
    }

    public static List<String> processList(String str) {

        String[] list = str.split(", ");
        return Arrays.asList(list);
    }

}
