package com.CodeLab.DB_Service.service;

import com.CodeLab.DB_Service.enums.Difficulty;
import com.CodeLab.DB_Service.enums.SubmissionStatus;
import com.CodeLab.DB_Service.enums.UserProblemStatus;
import com.CodeLab.DB_Service.exceptions.NotFoundException;
import com.CodeLab.DB_Service.model.*;
import com.CodeLab.DB_Service.repository.CompanyRepo;
import com.CodeLab.DB_Service.repository.ProblemRepo;
import com.CodeLab.DB_Service.repository.SubmissionRepo;
import com.CodeLab.DB_Service.repository.TopicRepo;
import com.CodeLab.DB_Service.requestDTO.CompanyRequestDTO;
import com.CodeLab.DB_Service.requestDTO.ProblemRequestDTO;
import com.CodeLab.DB_Service.requestDTO.TestCaseRequestDTO;
import com.CodeLab.DB_Service.requestDTO.TopicRequestDTO;
import com.CodeLab.DB_Service.requestdto_converter.CompanyConverter;
import com.CodeLab.DB_Service.requestdto_converter.ProblemConverter;
import com.CodeLab.DB_Service.requestdto_converter.TestCaseConverter;
import com.CodeLab.DB_Service.requestdto_converter.TopicConverter;
import com.CodeLab.DB_Service.responseDTO.PaginatedResponse;
import com.CodeLab.DB_Service.responseDTO.ProblemResponseDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProblemService {

    private static final int PAGE_SIZE = 3;
    private static final Sort SORT_BY_PROBLEM_NO = Sort.by("problemNo").ascending();

    @Autowired
    private ProblemRepo problemRepo;

    @Autowired
    private CompanyRepo companyRepo;

    @Autowired
    private TopicRepo topicRepo;

    @Autowired
    private UserService userService;

    @Autowired
    private SubmissionRepo submissionRepo;

    // --------------------- CRUD Operations ---------------------

    /**
     * Fetch a single problem by ID.
     */
    public Problem getProblem(UUID problemId) {
        return problemRepo.findById(problemId).orElse(null);
    }

    /**
     * Fetch a single problem and return a response DTO.
     */
    public ProblemResponseDTO getProblemForUser(UUID problemId) {
        Problem problem = problemRepo.findById(problemId).orElse(null);
        return (problem == null) ? null : ProblemConverter.problem_responseDTOConverter(problem);
    }

    public ProblemResponseDTO getProblemForUser(UUID problemId, UUID userId) {
        Problem problem = problemRepo.findById(problemId).orElse(null);
        if (problem == null) return null;

        // Convert problem to response DTO
        ProblemResponseDTO dto = ProblemConverter.problem_responseDTOConverter(problem);

        // Set user problem status
        UserProblemStatus status = determineUserProblemStatus(userId, problemId);
        dto.setUserProblemStatus(status);

        return dto;
    }
    /**
     * Add a new problem with specified visibility.
     */
    public Problem addProblem(ProblemRequestDTO problemRequestDTO, boolean isVisible) {
        Problem problem = ProblemConverter.requestDTO_problemConverter(problemRequestDTO, isVisible);
        problem.setProblemNo((int) this.getProblemCount() + 1);

        Arrays.stream(problem.getTopicList().split(","))
                .map(String::trim)
                .forEach(topic -> topicRepo.findByTopicName(topic)
                        .orElseGet(() -> topicRepo.save(TopicConverter.topicConverter(new TopicRequestDTO(topic)))));

        Arrays.stream(problem.getCompanyList().split(","))
                .map(String::trim)
                .forEach(company -> companyRepo.findByCompanyName(company)
                        .orElseGet(() -> companyRepo.save(CompanyConverter.companyConverter(new CompanyRequestDTO(company)))));

        return problemRepo.save(problem);
    }

    /**
     * Delete a problem by ID.
     */
    public void deleteProblem(UUID problemId) {
        if (getProblem(problemId) == null)
            throw new NotFoundException("Problem with id-" + problemId + " not found!!!");
        problemRepo.deleteById(problemId);
    }

    /**
     * Delete all problems.
     */
    public void deleteAllProblems() {
        if (this.getProblemCount() == 0)
            throw new NotFoundException("There is not any problem present in the Database");
        problemRepo.deleteAll();
    }

    // --------------------- Problem Retrieval ---------------------

    /**
     * Get total count of problems.
     */
    public long getProblemCount() {
        return problemRepo.count();
    }

    /**
     * Get problems paginated for anonymous users.
     */
    public PaginatedResponse<ProblemResponseDTO> getProblems(int pageNo) {
        // Handle first page (custom logic for pageNo == 0)
        if (pageNo == 0) {
            List<Problem> problemList = problemRepo.getAllVisibleProblems();
            List<ProblemResponseDTO> content = ProblemConverter.problem_responseDTOConverter(problemList);

            return new PaginatedResponse<>(
                    content,
                    1,
                    1,
                    content.size()
            );
        }

        // Handle paginated response from DB
        Page<Problem> problemPage = problemRepo.findAllByIsVisibleTrue(createPageRequest(pageNo));
        List<ProblemResponseDTO> content = ProblemConverter.problem_responseDTOConverter(problemPage.getContent());

        return new PaginatedResponse<>(
                content,
                pageNo,
                problemPage.getTotalPages(),
                problemPage.getTotalElements()
        );
    }


    /**
     * Get problems paginated for a specific user with user status.
     */
    public PaginatedResponse<ProblemResponseDTO> getProblems(int pageNo, UUID userId) {
        validateUser(userId);

        if (pageNo == 0) {
            List<Problem> problemList = problemRepo.getAllVisibleProblems();
            List<ProblemResponseDTO> content = convertWithUserStatus(problemList, userId);

            return new PaginatedResponse<>(
                    content,
                    1,
                    1,
                    content.size()
            );
        }

        Page<Problem> problemPage = problemRepo.findAllByIsVisibleTrue(createPageRequest(pageNo));
        List<ProblemResponseDTO> content = convertWithUserStatus(problemPage.getContent(), userId);

        return new PaginatedResponse<>(
                content,
                pageNo,
                problemPage.getTotalPages(),
                problemPage.getTotalElements()
        );
    }


    // --------------------- Filter by Topic ---------------------

    /**
     * Get problems by topic name (for all users).
     */
    public PaginatedResponse<ProblemResponseDTO> getProblemsTopicWise(String topicName, int pageNo) {
        List<Problem> problemList = problemRepo.findByTopicName(normalize(topicName));
        List<ProblemResponseDTO> responseList = ProblemConverter.problem_responseDTOConverter(problemList);
        return toPaginatedResponse(responseList, pageNo);
    }

    /**
     * Get problems by topic for a specific user.
     */
    public PaginatedResponse<ProblemResponseDTO> getProblemsTopicWise(String topicName, UUID userId, int pageNo) {
        validateUser(userId);
        List<Problem> problemList = problemRepo.findByTopicName(normalize(topicName));
        List<ProblemResponseDTO> responseList = convertWithUserStatus(problemList, userId);
        return toPaginatedResponse(responseList, pageNo);
    }


    /**
     * Get count of problems associated with a topic.
     */
    public long getProblemsCountTopicWise(String topicName) {
        return problemRepo.countByTopicName(normalize(topicName));
    }



    // --------------------- Filter by Company ---------------------

    /**
     * Get problems by company name (for all users).
     */
    public PaginatedResponse<ProblemResponseDTO> getProblemsCompanyWise(String companyName, int pageNo) {
        List<Problem> problemList = problemRepo.findByCompanyName(normalize(companyName));
        List<ProblemResponseDTO> responseList = ProblemConverter.problem_responseDTOConverter(problemList);
        return toPaginatedResponse(responseList, pageNo);
    }


    /**
     * Get problems by company for a specific user.
     */
    public PaginatedResponse<ProblemResponseDTO> getProblemsCompanyWise(String companyName, UUID userId, int pageNo) {
        validateUser(userId);
        List<Problem> problemList = problemRepo.findByCompanyName(normalize(companyName));
        List<ProblemResponseDTO> responseList = convertWithUserStatus(problemList, userId);
        return toPaginatedResponse(responseList, pageNo);
    }

    /**
     * Get count of problems associated with a company.
     */
    public long getProblemsCountCompanyWise(String companyName) {
        return problemRepo.countByCompanyName(normalize(companyName));
    }

    // --------------------- Filter by Difficulty ---------------------

    /**
     * Get problems by difficulty level (public view).
     */
    public PaginatedResponse<ProblemResponseDTO> getProblemByDifficulty(Difficulty difficulty, int pageNo) {
        List<Problem> problemList = problemRepo.findProblemByDifficulty(difficulty.toString());
        List<ProblemResponseDTO> responseList = ProblemConverter.problem_responseDTOConverter(problemList);
        return toPaginatedResponse(responseList, pageNo);
    }

    /**
     * Get problems by difficulty for a specific user.
     */
    public PaginatedResponse<ProblemResponseDTO> getProblemByDifficulty(Difficulty difficulty, UUID userId, int pageNo) {
        validateUser(userId);
        List<Problem> problemList = problemRepo.findProblemByDifficulty(difficulty.toString());
        List<ProblemResponseDTO> responseList = convertWithUserStatus(problemList, userId);
        return toPaginatedResponse(responseList, pageNo);
    }


    // --------------------- Filter by User Status ---------------------

    /**
     * Get problems filtered by user problem status (anonymous view).
     */
    public PaginatedResponse<ProblemResponseDTO> getProblemsByStatus(int pageNo, UserProblemStatus status) {
        List<Problem> problemList = problemRepo.getAllVisibleProblems();
        List<ProblemResponseDTO> filtered = problemList.stream()
                .map(ProblemConverter::problem_responseDTOConverter)
                .filter(dto -> status == UserProblemStatus.UNATTEMPTED)
                .collect(Collectors.toList());

        return toPaginatedResponse(filtered, pageNo);
    }

    /**
     * Get problems filtered by user problem status for specific user.
     */
    public PaginatedResponse<ProblemResponseDTO> getProblemsByStatus(int pageNo, UserProblemStatus status, UUID userId) {
        validateUser(userId);
        List<Problem> problemList = problemRepo.getAllVisibleProblems();
        List<ProblemResponseDTO> filtered = problemList.stream()
                .map(p -> toResponseDTO(p, userId))
                .filter(dto -> dto.getUserProblemStatus() == status)
                .collect(Collectors.toList());

        return toPaginatedResponse(filtered, pageNo);
    }

    // --------------------- Search ---------------------

    /**
     * Search visible problems by keyword.
     */
    public PaginatedResponse<ProblemResponseDTO> searchVisibleProblems(String keyword, int pageNo) {
        String normalizedKeyword = normalize(keyword).toLowerCase();

        if (pageNo == 0) {
            List<Problem> problemList = problemRepo.searchProblemsWithoutPagination(normalizedKeyword);
            List<ProblemResponseDTO> responseList = ProblemConverter.problem_responseDTOConverter(problemList);
            return new PaginatedResponse<>(
                    responseList,
                    1,
                    1,
                    responseList.size()
            );
        }

        Page<Problem> problemPage = problemRepo.searchProblemsWithPagination(normalizedKeyword, createPageRequest(pageNo));
        List<ProblemResponseDTO> content = ProblemConverter.problem_responseDTOConverter(problemPage.getContent());

        return new PaginatedResponse<>(
                content,
                pageNo,
                problemPage.getTotalPages(),
                problemPage.getTotalElements()
        );
    }

    public PaginatedResponse<ProblemResponseDTO> searchVisibleProblems(String keyword, int pageNo, UUID userId) {
        validateUser(userId);
        String normalizedKeyword = normalize(keyword).toLowerCase();

        if (pageNo == 0) {
            List<Problem> problemList = problemRepo.searchProblemsWithoutPagination(normalizedKeyword);
            List<ProblemResponseDTO> responseList = convertWithUserStatus(problemList, userId);
            return new PaginatedResponse<>(
                    responseList,
                    1,
                    1,
                    responseList.size()
            );
        }

        Page<Problem> problemPage = problemRepo.searchProblemsWithPagination(normalizedKeyword, createPageRequest(pageNo));
        List<ProblemResponseDTO> content = convertWithUserStatus(problemPage.getContent(), userId);

        return new PaginatedResponse<>(
                content,
                pageNo,
                problemPage.getTotalPages(),
                problemPage.getTotalElements()
        );
    }

    // --------------------- Add Test Cases ---------------------

    /**
     * Add test cases to a given problem.
     */
    public void addTestCases(List<TestCaseRequestDTO> testCaseRequestDTOList, UUID problemId) {
        Problem problem = this.getProblem(problemId);
        if (problem == null) throw new NotFoundException("Problem with id-" + problemId + " not found!!!");

        List<TestCase> testCases = TestCaseConverter.testCaseConverter(testCaseRequestDTOList, problem);
        problem.getTestCasesList().addAll(testCases);
        problemRepo.save(problem);
    }

    // --------------------- Helper Methods ---------------------

    private PageRequest createPageRequest(int pageNo) {
        return PageRequest.of(pageNo - 1, PAGE_SIZE, SORT_BY_PROBLEM_NO);
    }

    private void validateUser(UUID userId) {
        if (userService.getUserById(userId) == null) {
            throw new NotFoundException("User with id-" + userId + " not found!!!");
        }
    }

    private List<ProblemResponseDTO> convertWithUserStatus(List<Problem> problems, UUID userId) {
        return problems.stream().map(p -> toResponseDTO(p, userId)).collect(Collectors.toList());
    }

    private ProblemResponseDTO toResponseDTO(Problem problem, UUID userId) {
        ProblemResponseDTO dto = ProblemConverter.problem_responseDTOConverter(problem);
        dto.setUserProblemStatus(determineUserProblemStatus(userId, problem.getProblemId()));
        return dto;
    }

    private UserProblemStatus determineUserProblemStatus(UUID userId, UUID problemId) {
        List<Submission> submissions = submissionRepo.findAllByUserAndProblem(userId, problemId);
        if (submissions.isEmpty()) return UserProblemStatus.UNATTEMPTED;
        if (submissions.stream().anyMatch(s -> s.getSubmissionStatus() == SubmissionStatus.ACCEPTED)) {
            return UserProblemStatus.SOLVED;
        }
        return UserProblemStatus.ATTEMPTED;
    }

    private String normalize(String s) {
        return s.toUpperCase().trim().replaceAll("%20", " ");
    }

    private <T> List<T> paginateList(List<T> fullList, int pageNo) {
        int fromIndex = (pageNo - 1) * PAGE_SIZE;
        int toIndex = Math.min(fromIndex + PAGE_SIZE, fullList.size());
        if (fromIndex >= fullList.size()) return new ArrayList<>();
        return fullList.subList(fromIndex, toIndex);
    }

    private <T> PaginatedResponse<T> toPaginatedResponse(List<T> fullList, int pageNo) {
        if (pageNo == 0) {
            return new PaginatedResponse<>(
                    fullList,
                    1,
                    1,
                    fullList.size()
            );
        }

        int totalElements = fullList.size();
        int totalPages = (int) Math.ceil((double) totalElements / PAGE_SIZE);
        int fromIndex = (pageNo - 1) * PAGE_SIZE;
        int toIndex = Math.min(fromIndex + PAGE_SIZE, totalElements);

        List<T> pageContent = (fromIndex >= totalElements) ? new ArrayList<>() : fullList.subList(fromIndex, toIndex);

        return new PaginatedResponse<>(
                pageContent,
                pageNo,
                totalPages,
                totalElements
        );
    }
}
