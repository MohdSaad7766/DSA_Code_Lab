package com.CodeLab.Central_Service.integration;

import com.CodeLab.Central_Service.enums.Difficulty;
import com.CodeLab.Central_Service.enums.UserProblemStatus;
import com.CodeLab.Central_Service.model.*;
import com.CodeLab.Central_Service.requestDTO.*;

import com.CodeLab.Central_Service.responseDTO.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Component;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Component
public class DBService extends RestAPI{

    @Value("${db.service.base}")
    String baseURL;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    private ObjectMapper objectMapper;

    //User Endpoint calls

    public IsEmailAlreadyPresentResponseDTO callIsUserEmailAlreadyExists(String email){
        String endpoint = "/user/is-user-already-present/"+email;

        Object response = this.makeGetCall(baseURL,endpoint,new HashMap<>());

        return modelMapper.map(response, IsEmailAlreadyPresentResponseDTO.class);
    }

    public UserRegisteredResponseDTO callRegisterUser(UserRequestDTO userRequestDTO){
        String endpoint = "/user/register";

        Object response = this.makePostCall(baseURL,endpoint,userRequestDTO,new HashMap<>());

        return modelMapper.map(response,UserRegisteredResponseDTO.class);
    }

    public void callGenerateOtp(OTPGenerateRequestDTO requestDTO){
        String endpoint = "/otp/generate";

        this.makePostCall(baseURL,endpoint,requestDTO,new HashMap<>());
    }

    public OTPVerificationResponseDTO callVerifyUserOtp(UserRequestDTO requestDTO, String otp){
        String endpoint = "/otp/verify-user";
        HashMap<String,String> map = new HashMap<>();
        map.put("otp",otp);

        Object response = this.makePutCall(baseURL,endpoint,requestDTO,map);

        return modelMapper.map(response, OTPVerificationResponseDTO.class);

    }

    public OTPVerificationResponseDTO callVerifyAdminOtp(AdminRequestDTO requestDTO, String otp){
        String endpoint = "/otp/verify-admin";
        HashMap<String,String> map = new HashMap<>();
        map.put("otp",otp);

        Object response = this.makePutCall(baseURL,endpoint,requestDTO,map);

        return modelMapper.map(response, OTPVerificationResponseDTO.class);

    }


    //Admin Endpoints

    public IsEmailAlreadyPresentResponseDTO callIsAdminEmailAlreadyExists(String email){
        String endpoint = "/admin/is-admin-already-present/"+email;

        Object response = this.makeGetCall(baseURL,endpoint,new HashMap<>());

        return modelMapper.map(response, IsEmailAlreadyPresentResponseDTO.class);
    }

    public AdminRegisteredResponseDTO callRegisterAdmin(AdminRequestDTO adminRequestDTO){
        String endpoint = "/admin/register";

        Object response = this.makePostCall(baseURL,endpoint,adminRequestDTO,new HashMap<>());

        return modelMapper.map(response,AdminRegisteredResponseDTO.class);
    }


    //Problem endpoint calls

    public ProblemAddedResponseDTO callAddProblem(ProblemRequestDTO requestDTO){
        String endpoint = "/problem/add";

        Object response = this.makePostCall(baseURL,endpoint,requestDTO,new HashMap<>());
        if(response == null){
            System.out.println("The response is null");
            return null;
        }

        return modelMapper.map(response,ProblemAddedResponseDTO.class);
    }

    public PaginatedResponse<?> callGetProblemsByPage(int pageNo){
        String endpoint = "/problem/get/"+pageNo;
        Object response = this.makeGetCall(baseURL,endpoint,new HashMap<>());
        return objectMapper.convertValue(response,PaginatedResponse.class);
    }


    public PaginatedResponse<?> callGetProblemsByPage(int pageNo,UUID userId){
        String endpoint = "/problem/get-with-status/"+pageNo;
        HashMap<String,String> map = new HashMap<>();
        map.put("userId",userId+"");
        Object response = this.makeGetCall(baseURL,endpoint,map);
        return objectMapper.convertValue(response,PaginatedResponse.class);
    }
    public ProblemResponseDTO callGetProblemForUser(UUID problemId){
        String endpoint = "/problem/get-for-user/"+problemId;

        Object response = this.makeGetCall(baseURL,endpoint,new HashMap<>());
        if (response == null){
            return null;
        }

        return modelMapper.map(response,ProblemResponseDTO.class);
    }


    public Problem callGetProblemForSystem(UUID problemId){
        String endpoint = "/problem/get-for-system/"+problemId;

        Object response = this.makeGetCall(baseURL,endpoint,new HashMap<>());
        if (response == null){
            return null;
        }

        return modelMapper.map(response,Problem.class);
    }


    public PaginatedResponse<?> callGetProblemsTopicWise(int pageNo,String topicName){
        String endpoint = "/problem/get-by-topic/"+pageNo;
        HashMap<String,String> map = new HashMap<>();
        map.put("topicName",topicName);

        Object response = this.makeGetCall(baseURL,endpoint,map);
        return objectMapper.convertValue(response,PaginatedResponse.class);
    }

    public PaginatedResponse<?> callGetProblemsTopicWise(int pageNo,String topicName,UUID userId){
        String endpoint = "/problem/get-by-topic-with-status/"+pageNo;
        HashMap<String,String> map = new HashMap<>();
        map.put("topicName",topicName);
        map.put("userId",userId+"");

        Object response = this.makeGetCall(baseURL,endpoint,map);
        return objectMapper.convertValue(response,PaginatedResponse.class);
    }



    public PaginatedResponse<?> callGetProblemsCompanyWise(int pageNo,String companyName){
        String endpoint = "/problem/get-by-company/"+pageNo;
        HashMap<String,String> map = new HashMap<>();
        map.put("companyName",companyName);

        Object response = this.makeGetCall(baseURL,endpoint,map);
        return objectMapper.convertValue(response,PaginatedResponse.class);
    }

    public PaginatedResponse<?> callGetProblemsCompanyWise(int pageNo,String companyName,UUID userId){
        String endpoint = "/problem/get-by-company-with-status/"+pageNo;
        HashMap<String,String> map = new HashMap<>();
        map.put("companyName",companyName);
        map.put("userId",userId+"");


        Object response = this.makeGetCall(baseURL,endpoint,map);
        return objectMapper.convertValue(response,PaginatedResponse.class);
    }

    public PaginatedResponse<?> callGetProblemsStatusWise(int pageNo,UserProblemStatus status, UUID userId){
        String endpoint = "/problem/get-by-status-with-status/"+pageNo;
        HashMap<String,String> map = new HashMap<>();
        map.put("status",status+"");
        map.put("userId",userId+"");


        Object response = this.makeGetCall(baseURL,endpoint,map);
        return objectMapper.convertValue(response,PaginatedResponse.class);
    }

    public PaginatedResponse<?> callGetProblemsStatusWise(int pageNo,UserProblemStatus status){
        String endpoint = "/problem/get-by-status/"+pageNo;
        HashMap<String,String> map = new HashMap<>();
        map.put("status",status+"");

        Object response = this.makeGetCall(baseURL,endpoint,map);
        return objectMapper.convertValue(response,PaginatedResponse.class);
    }


    public long callGetProblemsCountTopicWise(String topicName){
        String endpoint = "/problem/get-count-by-topic";
        HashMap<String,String> map = new HashMap<>();
        map.put("topicName",topicName);

        Object response = this.makeGetCall(baseURL,endpoint,map);

        return modelMapper.map(response,Long.class);

    }


    public long callGetProblemsCountCompanyWise(String companyName){
        String endpoint = "/problem/get-count-by-company";
        HashMap<String,String> map = new HashMap<>();
        map.put("companyName",companyName);

        Object response = this.makeGetCall(baseURL,endpoint,map);

        return modelMapper.map(response,Long.class);
    }

    public PaginatedResponse<?> callSearchProblem(String keyword,int pageNo){
        String endpoint = "/problem/search/"+pageNo;
        HashMap<String,String> map = new HashMap<>();
        map.put("keyword",keyword);

        Object response = this.makeGetCall(baseURL,endpoint,map);
        return objectMapper.convertValue(response,PaginatedResponse.class);
    }

    public PaginatedResponse<?> callSearchProblem(String keyword,int pageNo,UUID userId){
        String endpoint = "/problem/search-with-status/"+pageNo;
        HashMap<String,String> map = new HashMap<>();
        map.put("keyword",keyword);
        map.put("userId",userId+"");

        Object response = this.makeGetCall(baseURL,endpoint,map);
        return objectMapper.convertValue(response,PaginatedResponse.class);
    }

    public PaginatedResponse<?> callGetProblemByDifficulty(int pageNo,Difficulty difficulty){
        String endpoint = "/problem/get-by-difficulty/"+pageNo;
        HashMap<String,String> map = new HashMap<>();
        map.put("difficulty",difficulty.toString());


        Object response = this.makeGetCall(baseURL,endpoint,map);
        return objectMapper.convertValue(response,PaginatedResponse.class);
    }

    public PaginatedResponse<?> callGetProblemByDifficulty(int pageNo,Difficulty difficulty,UUID userId){
        String endpoint = "/problem/get-by-difficulty-with-status/"+pageNo;
        HashMap<String,String> map = new HashMap<>();
        map.put("difficulty",difficulty.toString());
        map.put("userId",userId+"");

        Object response = this.makeGetCall(baseURL,endpoint,map);
        return objectMapper.convertValue(response,PaginatedResponse.class);
    }

    public GeneralResponseDTO callDeleteById(UUID problemId){
        String endpoint = "/problem/delete/"+problemId;

        Object response = this.makeDeleteCall(baseURL,endpoint,new HashMap<>());

        return modelMapper.map(response,GeneralResponseDTO.class);

    }


    public GeneralResponseDTO callDeleteAll(){
        String endpoint = "/problem/delete";

        Object response = this.makeDeleteCall(baseURL,endpoint,new HashMap<>());

        return modelMapper.map(response,GeneralResponseDTO.class);
    }

    //submission

    public Submission callAddSubmission(SubmissionRequestDTO requestDTO){
        String endpoint = "/submission/add";

        Object response = this.makePostCall(baseURL,endpoint,requestDTO,new HashMap<>());

       return  objectMapper.convertValue(response, Submission.class);

    }

    public Submission callGetSubmissionById(UUID submissionId){
        String endpoint = "/submission/get-by-id/"+submissionId;

        Object object = this.makeGetCall(baseURL,endpoint,new HashMap<>());
        if (object == null){
            return null;
        }
        return objectMapper.convertValue(object, Submission.class);
    }

    public List<Submission> callGetSubmissionByUserId(UUID userId){
        String endpoint = "/submission/get-by-user-id/"+userId;


        List<Object> response = this.makeGetCallAsList(baseURL,endpoint,new HashMap<>());

        List<Submission> list = new ArrayList<>();

        for(Object object : response){
            list.add(objectMapper.convertValue(object, Submission.class));
        }
        return list;
    }

    public List<Submission> callGetSubmissionByUserIdAndProblemId(UUID userId,UUID problemId){
        String endpoint = "/submission/get-by-user-id-and-problem-id/"+userId;
        HashMap<String,String> map = new HashMap<>();
        map.put("problemId",problemId+"");

        List<Object> response = this.makeGetCallAsList(baseURL,endpoint,map);

        List<Submission> list = new ArrayList<>();

        for(Object object : response){
            list.add(objectMapper.convertValue(object, Submission.class));
        }
        return list;
    }

    public Submission callGetLatestSubmissionOfUser(UUID userId){
        String endpoint = "/submission/get-latest-of-user/"+userId;

        Object object = this.makeGetCall(baseURL,endpoint,new HashMap<>());
        if (object == null){
            return null;
        }

        return objectMapper.convertValue(object, Submission.class);
    }

    public List<Submission> callGetSubmissionsByProblemId(UUID problemId){
        String endpoint = "/submission/get-by-problem-id/"+problemId;
        List<Object> response = this.makeGetCallAsList(baseURL,endpoint,new HashMap<>());

        List<Submission> list = new ArrayList<>();

        for(Object object : response){
            list.add(objectMapper.convertValue(object, Submission.class));
        }
        return list;
    }

    public List<Submission> callGetAllSubmissions(){
        String endpoint = "/submission/get-all";
        List<Object> response = this.makeGetCallAsList(baseURL,endpoint,new HashMap<>());

        List<Submission> list = new ArrayList<>();

        for(Object object : response){
            list.add(objectMapper.convertValue(object, Submission.class));
        }
        return list;
    }


    //contest
    public ContestAddedResponseDTO callAddContest(ContestRequestDTO requestDTO) {
        String endpoint = "/contest/add";
        Object object = this.makePostCall(baseURL, endpoint, requestDTO, new HashMap<>());
        return objectMapper.convertValue(object, ContestAddedResponseDTO.class);
    }

    public Contest callGetContestById(UUID contestId){
        String endpoint = "/contest/get/" + contestId;
        Object response = this.makeGetCall(baseURL, endpoint, new HashMap<>());
        return objectMapper.convertValue(response, Contest.class);
    }

    public List<UpcomingContestResponseDTO> callGetUpcomingContests(UUID userId) {
        String endpoint = "/contest/get-upcoming-contests";

        HashMap<String, String> map = new HashMap<>();
        map.put("userId", userId.toString());

        List<Object> response = this.makeGetCallAsList(baseURL, endpoint, map);
        List<UpcomingContestResponseDTO> list = new ArrayList<>();
        for (Object obj : response) {
            list.add(objectMapper.convertValue(obj, UpcomingContestResponseDTO.class));
        }
        return list;
    }

    public List<UpcomingContestResponseDTO> callGetUpcomingContestsByPage(int pageNo, UUID userId) {
        String endpoint = "/contest/get-upcoming-contests/" + pageNo;

        HashMap<String, String> map = new HashMap<>();
        map.put("userId", userId.toString());

        List<Object> response = this.makeGetCallAsList(baseURL, endpoint, map);

        List<UpcomingContestResponseDTO> list = new ArrayList<>();
        for (Object obj : response) {
            list.add(objectMapper.convertValue(obj, UpcomingContestResponseDTO.class));
        }
        return list;
    }

    public ContestRegistrationResponseDTO callRegisterForContest(UUID userId, UUID contestId) {
        String endpoint = "/contest/register";
        HashMap<String, String> map = new HashMap<>();
        map.put("userId", userId.toString());
        map.put("contestId", contestId.toString());

        Object response = this.makePostCall(baseURL, endpoint,null, map);
        return objectMapper.convertValue(response, ContestRegistrationResponseDTO.class);
    }


    public List<LiveContestResponseDTO> callGetLiveContests(UUID userId) {
        String endpoint = "/contest/get-live-contests";

        HashMap<String, String> map = new HashMap<>();
        map.put("userId", userId.toString());
        List<Object> response = this.makeGetCallAsList(baseURL, endpoint, map);

        List<LiveContestResponseDTO> list = new ArrayList<>();
        for (Object obj : response) {
            list.add(objectMapper.convertValue(obj, LiveContestResponseDTO.class));
        }
        return list;
    }

    public List<LiveContestResponseDTO> callGetLiveContestsByPage(int pageNo, UUID userId) {
        String endpoint = "/contest/get-live-contests/" + pageNo;

        HashMap<String, String> map = new HashMap<>();
        map.put("userId", userId.toString());
        List<Object> response = this.makeGetCallAsList(baseURL, endpoint, map);

        List<LiveContestResponseDTO> list = new ArrayList<>();
        for (Object obj : response) {
            list.add(objectMapper.convertValue(obj, LiveContestResponseDTO.class));
        }
        return list;
    }


    public ContestStartResponseDTO callUserStartsContest(UUID userId, UUID contestId) {
        String endpoint = "/contest/user-start-contest";
        HashMap<String, String> map = new HashMap<>();
        map.put("userId", userId.toString());
        map.put("contestId", contestId.toString());

        Object object = this.makePostCall(baseURL, endpoint, null, map);
        return objectMapper.convertValue(object, ContestStartResponseDTO.class);
    }


    public Problem callGetContestProblem(UUID problemId) {
        String endpoint = "/contest/problem/" + problemId;
        Object response = this.makeGetCall(baseURL, endpoint, new HashMap<>());
        return objectMapper.convertValue(response, Problem.class);
    }

    public PartialContestSubmission callSubmitPartialContest(PartialContestSubmissionRequestDTO requestDTO){
        String endpoint = "/contest/partial-submit";
        Object response = this.makePostCall(baseURL,endpoint,requestDTO,new HashMap<>());
        return objectMapper.convertValue(response,PartialContestSubmission.class);
    }

    public FullContestSubmission callSubmitContest(UUID userId,UUID contestId){
        String endpoint = "/contest/submit";
        HashMap<String,String> map = new HashMap<>();
        map.put("userId",userId+"");
        map.put("contestId",contestId+"");

        Object response = this.makePostCall(baseURL,endpoint,null,map);

        return objectMapper.convertValue(response,FullContestSubmission.class);

    }

    public List<PastContestResponseListDTO> callGetPastContests(UUID userId) {
        String endpoint = "/contest/get-past-contests";

        HashMap<String, String> map = new HashMap<>();
        map.put("userId", userId.toString());
        List<Object> response = this.makeGetCallAsList(baseURL, endpoint, map);

        List<PastContestResponseListDTO> list = new ArrayList<>();
        for (Object obj : response) {
            list.add(objectMapper.convertValue(obj, PastContestResponseListDTO.class));
        }
        return list;
    }

    public List<PastContestResponseListDTO> callGetPastContestsByPage(int pageNo, UUID userId) {
        String endpoint = "/contest/get-past-contests/"+pageNo;

        HashMap<String, String> map = new HashMap<>();
        map.put("userId", userId.toString());
        List<Object> response = this.makeGetCallAsList(baseURL, endpoint, map);

        List<PastContestResponseListDTO> list = new ArrayList<>();
        for (Object obj : response) {
            list.add(objectMapper.convertValue(obj, PastContestResponseListDTO.class));
        }
        return list;
    }

    public PastContestResponseDTO callGetPastContestDetails(UUID userId,UUID contestId){
        String endpoint = "/contest/get-past-contest-detail/"+contestId;
        HashMap<String, String> map = new HashMap<>();
        map.put("userId", userId.toString());

        Object response = this.makeGetCall(baseURL,endpoint,map);

        return objectMapper.convertValue(response,PastContestResponseDTO.class);
    }


}
