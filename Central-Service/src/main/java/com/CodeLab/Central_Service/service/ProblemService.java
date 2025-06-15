package com.CodeLab.Central_Service.service;

import com.CodeLab.Central_Service.enums.Difficulty;
import com.CodeLab.Central_Service.enums.UserProblemStatus;
import com.CodeLab.Central_Service.integration.DBService;
import com.CodeLab.Central_Service.model.Problem;
import com.CodeLab.Central_Service.requestDTO.ProblemRequestDTO;
import com.CodeLab.Central_Service.responseDTO.GeneralResponseDTO;
import com.CodeLab.Central_Service.responseDTO.ProblemAddedResponseDTO;
import com.CodeLab.Central_Service.responseDTO.ProblemResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProblemService {
    @Autowired
    DBService dbService;

    public ProblemAddedResponseDTO addProblem(ProblemRequestDTO problemRequestDTO) {
        return dbService.callAddProblem(problemRequestDTO);
    }

    public List<ProblemResponseDTO> getProblemsByPage(int pageNo) {
        return dbService.callGetProblemsByPage(pageNo);
    }

    public List<ProblemResponseDTO> getProblemsByPage(int pageNo, UUID userId) {
        return dbService.callGetProblemsByPage(pageNo, userId);
    }

    public ProblemResponseDTO getProblemByIdForUser(UUID problemId) {
        return dbService.callGetProblemForUser(problemId);
    }

    public Problem getProblemByIdForSystem(UUID problemId) {
        return dbService.callGetProblemForSystem(problemId);
    }

    public List<ProblemResponseDTO> getProblemsTopicWise(int pageNo,String topicName) {
        return dbService.callGetProblemsTopicWise(pageNo,topicName.trim());
    }

    public List<ProblemResponseDTO> getProblemsTopicWise(int pageNo,String topicName, UUID userId) {
        return dbService.callGetProblemsTopicWise(pageNo,topicName.trim(), userId);
    }

    public List<ProblemResponseDTO> getProblemsCompanyWise(int pageNo,String companyName) {
        return dbService.callGetProblemsCompanyWise(pageNo,companyName.trim());
    }

    public List<ProblemResponseDTO> getProblemsCompanyWise(int pageNo,String companyName, UUID userId) {
        return dbService.callGetProblemsCompanyWise(pageNo,companyName.trim(), userId);
    }

    public List<ProblemResponseDTO> getProblemsStatusWise(int pageNo,UserProblemStatus status, UUID userId) {
        return dbService.callGetProblemsStatusWise(pageNo,status,userId);
    }

    public List<ProblemResponseDTO> getProblemsStatusWise(int pageNo,UserProblemStatus status) {
        return dbService.callGetProblemsStatusWise(pageNo,status);
    }

    public long getProblemsCountTopicWise(String topicName) {
        return dbService.callGetProblemsCountTopicWise(topicName.trim());
    }

    public long getProblemsCountCompanyWise(String companyName) {
        return dbService.callGetProblemsCountCompanyWise(companyName.trim());
    }


    public List<ProblemResponseDTO> searchProblem(String keyword, int pageNo) {
        return dbService.callSearchProblem(keyword.trim(), pageNo);
    }

    public List<ProblemResponseDTO> searchProblem(String keyword, int pageNo, UUID userId) {
        return dbService.callSearchProblem(keyword.trim(), pageNo, userId);
    }

    public GeneralResponseDTO deleteById(UUID problemId) {
        return dbService.callDeleteById(problemId);
    }

    public GeneralResponseDTO deleteAll() {
        return dbService.callDeleteAll();
    }

    public List<ProblemResponseDTO> getProblemByDifficulty(int pageNo,Difficulty difficulty) {
        return dbService.callGetProblemByDifficulty(pageNo,difficulty);
    }

    public List<ProblemResponseDTO> getProblemByDifficulty(int pageNo,Difficulty difficulty, UUID userId) {
        return dbService.callGetProblemByDifficulty(pageNo,difficulty, userId);
    }
}
