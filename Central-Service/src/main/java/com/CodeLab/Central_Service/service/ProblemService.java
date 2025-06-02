package com.CodeLab.Central_Service.service;

import com.CodeLab.Central_Service.enums.Difficulty;
import com.CodeLab.Central_Service.integration.DBService;
import com.CodeLab.Central_Service.model.Problem;
import com.CodeLab.Central_Service.requestDTO.ProblemRequestDTO;
import com.CodeLab.Central_Service.responseDTO.GeneralResponseDTO;
import com.CodeLab.Central_Service.responseDTO.ProblemAddedResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProblemService {
    @Autowired
    DBService dbService;

    public ProblemAddedResponseDTO addProblem(ProblemRequestDTO problemRequestDTO){
        return dbService.callAddProblem(problemRequestDTO);
    }

    public List<Problem> getProblems(){
        return dbService.callGetProblems();
    }

    public List<Problem> getProblemsByPage(int pageNo){
        return dbService.callGetProblemsByPage(pageNo);
    }

    public Problem getProblemById(UUID problemId){
       return dbService.callGetProblem(problemId);
    }

    public List<Problem> getProblemsTopicWise(String topicName){
        return dbService.callGetProblemsTopicWise(topicName.trim().replaceAll(" ","_"));
    }

    public List<Problem> getProblemsCompanyWise(String companyName){
        String company = companyName.trim().split(" ")[0];
        return dbService.callGetProblemsCompanyWise(company);
    }

    public long getProblemsCountTopicWise(String topicName){
        return dbService.callGetProblemsCountTopicWise(topicName.trim().replaceAll(" ","_"));
    }

    public long getProblemsCountCompanyWise(String companyName){
        String company = companyName.trim().split(" ")[0];
        return dbService.callGetProblemsCountCompanyWise(company);
    }

    public List<Problem> searchProblem(String keyword){

        return dbService.callSearchProblem(keyword.trim().replaceAll(" ","_"));
    }


    public GeneralResponseDTO deleteById(UUID problemId){
       return dbService.callDeleteById(problemId);
    }


    public GeneralResponseDTO deleteAll(){
       return dbService.callDeleteAll();
    }

    public List<Problem> getProblemByDifficulty(Difficulty difficulty){
        return dbService.callGetProblemByDifficulty(difficulty);
    }

}
