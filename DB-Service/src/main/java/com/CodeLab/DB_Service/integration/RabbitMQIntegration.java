package com.CodeLab.DB_Service.integration;

import com.CodeLab.DB_Service.requestDTO.ContestStartRequestDTO;
import com.CodeLab.DB_Service.requestDTO.NotificationMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQIntegration {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    private final String notificationExchangeName = "codelab-notification-exchange";
    private final String notificationRoutingKey = "codelab-notification-route-123";

    public void sendContestStartMail(ContestStartRequestDTO dto) {
        System.out.println("🐰 Publishing to RabbitMQ: " + dto);
        NotificationMessage message = new NotificationMessage("Contest-Reminder", dto);
        rabbitTemplate.convertAndSend(notificationExchangeName, notificationRoutingKey, message);
    }
}
