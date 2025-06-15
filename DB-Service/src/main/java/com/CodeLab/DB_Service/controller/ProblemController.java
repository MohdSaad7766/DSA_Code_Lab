package com.CodeLab.DB_Service.controller;

import com.CodeLab.DB_Service.enums.Difficulty;
import com.CodeLab.DB_Service.enums.UserProblemStatus;
import com.CodeLab.DB_Service.exceptions.NotFoundException;
import com.CodeLab.DB_Service.model.Problem;
import com.CodeLab.DB_Service.requestDTO.ProblemRequestDTO;
import com.CodeLab.DB_Service.requestDTO.TestCaseRequestDTO;
import com.CodeLab.DB_Service.responseDTO.GeneralResponseDTO;
import com.CodeLab.DB_Service.responseDTO.ProblemAddedResponseDTO;
import com.CodeLab.DB_Service.responseDTO.ProblemResponseDTO;
import com.CodeLab.DB_Service.service.ProblemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/db/problem")
public class ProblemController {

    @Autowired
    ProblemService problemService;

    @PostMapping("/add")
    public ProblemAddedResponseDTO addProblem(@RequestBody ProblemRequestDTO problemRequestDTO){
        Problem problem  = problemService.addProblem(problemRequestDTO,true);
        ProblemAddedResponseDTO responseDTO = new ProblemAddedResponseDTO();
        responseDTO.setProblem(problem);
        if(problem != null){
            responseDTO.setMessage("Problem Added Successfully:)");
        }
        else{
            responseDTO.setMessage("Problem Not Added!!!");
        }
        return responseDTO;
    }


    @GetMapping("/get/{pageNo}")
    public List<ProblemResponseDTO> getProblemsByPage(@PathVariable int pageNo){
        return problemService.getProblems(pageNo);
    }

    @GetMapping("/get-with-status/{pageNo}")
    public List<ProblemResponseDTO> getProblemsByPage(@PathVariable int pageNo,@RequestParam UUID userId){
        return problemService.getProblems(pageNo,userId);
    }

    @GetMapping("/get-for-user/{problemId}")
    public ProblemResponseDTO getProblemByIdForUser(@PathVariable UUID problemId){
        return problemService.getProblemForUser(problemId);
    }

    @GetMapping("/get-for-system/{problemId}")
    public Problem getProblemByIdForAdmin(@PathVariable UUID problemId){
        return problemService.getProblem(problemId);
    }

    @GetMapping("/get-by-topic/{pageNo}")
    public List<ProblemResponseDTO> getProblemsTopicWise(@PathVariable int pageNo,@RequestParam String topicName){
        return problemService.getProblemsTopicWise(topicName,pageNo);
    }

    @GetMapping("/get-by-topic-with-status/{pageNo}")
    public List<ProblemResponseDTO> getProblemsTopicWise(@PathVariable int pageNo,@RequestParam String topicName,@RequestParam UUID userId){
        return problemService.getProblemsTopicWise(topicName,userId,pageNo);
    }

    @GetMapping("/get-by-company/{pageNo}")
    public List<ProblemResponseDTO> getProblemsCompanyWise(@PathVariable int pageNo,@RequestParam String companyName){
        return problemService.getProblemsCompanyWise(companyName,pageNo);
    }

    @GetMapping("/get-by-company-with-status/{pageNo}")
    public List<ProblemResponseDTO> getProblemsCompanyWise(@PathVariable int pageNo,@RequestParam String companyName,@RequestParam UUID userId){
        return problemService.getProblemsCompanyWise(companyName,userId,pageNo);
    }

    @GetMapping("/get-count-by-topic")
    public long getProblemsCountTopicWise(@RequestParam String topicName){
        return problemService.getProblemsCountTopicWise(topicName);
    }

    @GetMapping("/get-count-by-company")
    public long getProblemsCountCompanyWise(@RequestParam String companyName){
        return problemService.getProblemsCountCompanyWise(companyName);
    }



    @GetMapping("/search/{pageNo}")
    public List<ProblemResponseDTO> searchProblem(@PathVariable int pageNo,@RequestParam String keyword){
        System.out.println("search by page");
        return problemService.searchVisibleProblems(keyword,pageNo);
    }

    @GetMapping("/search-with-status/{pageNo}")
    public List<ProblemResponseDTO> searchProblem(@PathVariable int pageNo,@RequestParam String keyword,@RequestParam UUID userId){
        System.out.println("search by page");
        return problemService.searchVisibleProblems(keyword,pageNo,userId);
    }

    @DeleteMapping("/delete/{problemId}")
    public GeneralResponseDTO deleteById(@PathVariable UUID problemId){
        GeneralResponseDTO responseDTO = new GeneralResponseDTO();
        try {
            problemService.deleteProblem(problemId);
            responseDTO.setMessage("Problem with id-"+problemId+" has been deleted succussfully:)");
            return responseDTO;
        }
        catch (NotFoundException e){
            responseDTO.setMessage(e.getMessage());
            return responseDTO;
        }
    }

    @DeleteMapping("/delete")
    public GeneralResponseDTO deleteAll(){
        GeneralResponseDTO responseDTO = new GeneralResponseDTO();
        try {
            problemService.deleteAllProblems();

            responseDTO.setMessage("All problems have been deleted successfully:)");
            return responseDTO;
        }
        catch (NotFoundException e){
            responseDTO.setMessage(e.getMessage());
            return responseDTO;
        }
    }

    @PostMapping("/all-testcases/{problemId}")
    public GeneralResponseDTO addTestCaseList(@RequestBody List<TestCaseRequestDTO> testCaseRequestDTOList,@PathVariable UUID problemId){
        GeneralResponseDTO generalResponseDTO = new GeneralResponseDTO();

        try {
            problemService.addTestCases(testCaseRequestDTOList,problemId);
            generalResponseDTO.setMessage("All TestCases are Added Successfully");

        } catch (RuntimeException e) {
            generalResponseDTO.setMessage(e.getMessage());
        }
        return generalResponseDTO;

    }

    @GetMapping("/get-by-difficulty/{pageNo}")
    public List<ProblemResponseDTO> getProblemByDifficulty(@PathVariable int pageNo,@RequestParam Difficulty difficulty){
        return problemService.getProblemByDifficulty(difficulty,pageNo);
    }

    @GetMapping("/get-by-difficulty-with-status/{pageNo}")
    public List<ProblemResponseDTO> getProblemByDifficulty(@PathVariable int pageNo,@RequestParam Difficulty difficulty,@RequestParam UUID userId){
        return problemService.getProblemByDifficulty(difficulty,userId,pageNo);
    }

    @GetMapping("/get-by-status/{pageNo}")
    public List<ProblemResponseDTO> getProblemByStatus(@PathVariable int pageNo,@RequestParam UserProblemStatus status){
        return problemService.getProblemsByStatus(pageNo,status);
    }

    @GetMapping("/get-by-status-with-status/{pageNo}")
    public List<ProblemResponseDTO> getProblemByStatus(@PathVariable int pageNo,@RequestParam UserProblemStatus status, @RequestParam UUID userId){
        return problemService.getProblemsByStatus(pageNo,status,userId);
    }
}
