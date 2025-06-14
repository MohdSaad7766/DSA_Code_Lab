package com.CodeLab.DB_Service.service;

import com.CodeLab.DB_Service.enums.Difficulty;
import com.CodeLab.DB_Service.exceptions.NotFoundException;
import com.CodeLab.DB_Service.model.Company;
import com.CodeLab.DB_Service.model.Problem;
import com.CodeLab.DB_Service.model.TestCase;
import com.CodeLab.DB_Service.model.Topic;
import com.CodeLab.DB_Service.repository.CompanyRepo;
import com.CodeLab.DB_Service.repository.ProblemRepo;
import com.CodeLab.DB_Service.repository.TopicRepo;
import com.CodeLab.DB_Service.requestDTO.CompanyRequestDTO;
import com.CodeLab.DB_Service.requestDTO.ProblemRequestDTO;
import com.CodeLab.DB_Service.requestDTO.TestCaseRequestDTO;
import com.CodeLab.DB_Service.requestDTO.TopicRequestDTO;
import com.CodeLab.DB_Service.requestdto_converter.CompanyConverter;
import com.CodeLab.DB_Service.requestdto_converter.ProblemConverter;
import com.CodeLab.DB_Service.requestdto_converter.TestCaseConverter;
import com.CodeLab.DB_Service.requestdto_converter.TopicConverter;
import com.CodeLab.DB_Service.responseDTO.ProblemResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ProblemService {

    @Autowired
    private ProblemRepo problemRepo;

    @Autowired
    private CompanyRepo companyRepo;

    @Autowired
    private TopicRepo topicRepo;

    public long getProblemCount() {
        return problemRepo.count();
    }

    public Problem addProblem(ProblemRequestDTO problemRequestDTO,boolean isVisible) {

        Problem problem = ProblemConverter.requestDTO_problemConverter(problemRequestDTO,isVisible);

        int totalProblems = (int) this.getProblemCount();
        problem.setProblemNo(totalProblems + 1);

        String[] topicList = problem.getTopicList().split(",");
        for (String rawTopic : topicList) {
            final String topicName = rawTopic.trim();
            topicRepo.findByTopicName(topicName).orElseGet(() -> {
                Topic topic = TopicConverter.topicConverter(new TopicRequestDTO(topicName));
                return topicRepo.save(topic);
            });
        }

        String[] companyList = problem.getCompanyList().split(",");
        for (String rawCompany : companyList) {
            final String companyName = rawCompany.trim();
            companyRepo.findByCompanyName(companyName).orElseGet(() -> {
                Company company = CompanyConverter.companyConverter(new CompanyRequestDTO(companyName));
                return companyRepo.save(company);
            });
        }

        return problemRepo.save(problem);
    }


    public List<ProblemResponseDTO> getProblems() {

        List<Problem>  problemList = problemRepo.getAllVisibleProblems();
        List<ProblemResponseDTO> responseDTOList = ProblemConverter.problem_responseDTOConverter(problemList);

        return responseDTOList;
    }

    public List<ProblemResponseDTO> getProblems(int pageNo) {
        int pageSize = 10;
        Pageable pageable = PageRequest.of(pageNo-1, pageSize, Sort.by("problemNo").ascending());
        List<Problem>  problemList =  problemRepo.findAllByIsVisibleTrue(pageable).getContent();
        List<ProblemResponseDTO> responseDTOList = ProblemConverter.problem_responseDTOConverter(problemList);

        return responseDTOList;
    }

    public Problem getProblem(UUID problemId) {
        return problemRepo.findById(problemId).orElse(null);
    }

    public ProblemResponseDTO getProblemForUser(UUID problemId) {

        Problem problem =  problemRepo.findById(problemId).orElse(null);
        if(problem == null) {
            return null;
        }

        ProblemResponseDTO responseDTO = ProblemConverter.problem_responseDTOConverter(problem);
        return responseDTO;
    }

    public List<ProblemResponseDTO> getProblemsTopicWise(String topicName) {
        List<Problem>  problemList =  problemRepo.findByTopicName(normalize(topicName));
        List<ProblemResponseDTO> responseDTOList = ProblemConverter.problem_responseDTOConverter(problemList);

        return responseDTOList;
    }

    public List<ProblemResponseDTO> getProblemsCompanyWise(String companyName) {
        List<Problem>  problemList =  problemRepo.findByCompanyName(normalize(companyName));
        List<ProblemResponseDTO> responseDTOList = ProblemConverter.problem_responseDTOConverter(problemList);

        return responseDTOList;
    }

    public long getProblemsCountTopicWise(String topicName) {
        return problemRepo.countByTopicName(normalize(topicName));
    }

    public long getProblemsCountCompanyWise(String companyName) {
        return problemRepo.countByCompanyName(normalize(companyName));
    }

    public List<ProblemResponseDTO> searchProblem(String keyword) {
        String[] keywords = keyword.trim().toLowerCase().split("[_\\s]+");
        Set<Problem> problemSet = new HashSet<>();

        for (String key : keywords) {
            problemSet.addAll(problemRepo.searchVisibleProblems(key));
        }

        return ProblemConverter.problem_responseDTOConverter(new ArrayList<>(problemSet));
    }

    public List<ProblemResponseDTO> searchVisibleProblems(String keyword, int pageNo) {
        int pageSize = 10;
        pageNo--;
        String[] keywords = keyword.trim().toLowerCase().split("[_\\s]+");
        Set<Problem> problemSet = new HashSet<>();

        for (String key : keywords) {
            List<Problem> results = problemRepo.searchVisibleProblems(key);
            System.out.println("Key: " + key + " -> " + results.size() + " results");
            problemSet.addAll(results);
        }

        List<Problem> problemList = new ArrayList<>(problemSet);
        problemList.sort(Comparator.comparing(Problem::getProblemNo));

        int start = pageNo * pageSize;
        int end = Math.min(start + pageSize, problemList.size());

        System.out.println("Total results: " + problemList.size() + ", Showing from: " + start + " to: " + end);

        if (start >= problemList.size()) {
            return Collections.emptyList();
        }

        List<Problem> pagedList = problemList.subList(start, end);
        return ProblemConverter.problem_responseDTOConverter(pagedList);
    }


    public void deleteProblem(UUID problemId) {
        if (getProblem(problemId) == null) {
            throw new NotFoundException("Problem with id-" + problemId + " not found!!!");
        }
        problemRepo.deleteById(problemId);
    }

    public void deleteAllProblems() {
        if (this.getProblemCount() == 0) {
            throw new NotFoundException("There is not any problem present in the Database");
        }
        problemRepo.deleteAll();
    }

    public void addTestCases(List<TestCaseRequestDTO> testCaseRequestDTOList, UUID problemId) {
        Problem problem = this.getProblem(problemId);

        if (problem == null) {
            throw new NotFoundException("Problem with id-" + problemId + " not found!!!");
        }

        List<TestCase> testCases = TestCaseConverter.testCaseConverter(testCaseRequestDTOList, problem);

        for (TestCase testCase : testCases) {
            problem.getTestCasesList().add(testCase);
        }
        problemRepo.save(problem);
    }

    public List<ProblemResponseDTO> getProblemByDifficulty(Difficulty difficulty) {
        List<Problem>  problemList =  problemRepo.findProblemByDifficulty(difficulty.toString());
        List<ProblemResponseDTO> responseDTOList = ProblemConverter.problem_responseDTOConverter(problemList);

        return responseDTOList;
    }

    private String normalize(String s) {
        return s.toUpperCase().trim().replaceAll("_", " ");
    }

}
