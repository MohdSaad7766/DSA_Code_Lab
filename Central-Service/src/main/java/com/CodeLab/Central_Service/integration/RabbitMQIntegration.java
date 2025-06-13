package com.CodeLab.Central_Service.integration;


import com.CodeLab.Central_Service.requestDTO.NotificationMessage;
import com.CodeLab.Central_Service.requestDTO.OTPGenerateRequestDTO;
import com.CodeLab.Central_Service.requestDTO.RunCodeRequestDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RabbitMQIntegration {
    @Autowired
    RabbitTemplate rabbitTemplate;

    // Notification
    String notificationExchangeName = "codelab-notification-exchange";
    String notificationRoutingKey = "codelab-notification-route-123";

    public void insertMessageToQueue(NotificationMessage requestDTO){
        rabbitTemplate.convertAndSend(notificationExchangeName, notificationRoutingKey, requestDTO);
    }

    // Shared Exchange
    String executionExchangeName = "codelab-code-execution-exchange";

    // Normal Submission
    String normalRoutingKey = "normal-code-execution-route";

    public void sendNormalCodeExecutionRequest(List<RunCodeRequestDTO> requestDTOS) {
        rabbitTemplate.convertAndSend(executionExchangeName, normalRoutingKey, requestDTOS);
    }

    // Contest Submission
    String contestRoutingKey = "contest-code-execution-route";

    public void sendContestCodeExecutionRequest(List<RunCodeRequestDTO> requestDTOS) {
        rabbitTemplate.convertAndSend(executionExchangeName, contestRoutingKey, requestDTOS);
    }
}