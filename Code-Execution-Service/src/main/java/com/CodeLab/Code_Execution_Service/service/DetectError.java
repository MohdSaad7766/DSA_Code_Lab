package com.CodeLab.Code_Execution_Service.service;

import com.CodeLab.Code_Execution_Service.requestDTO.RunCodeRequestDTO;
import com.CodeLab.Code_Execution_Service.responseDTO.RunCodeResponseDTO;
import org.springframework.stereotype.Service;

@Service
public class DetectError {
    public boolean isOutputMatched(RunCodeResponseDTO responseDTO, RunCodeRequestDTO requestDTO){
//        String expected = requestDTO.getExpectedOutput().replaceAll(",","").replace("[","").replace("]","");

        return responseDTO.getOutput().equals(requestDTO.getExpectedOutput());
    }
}
