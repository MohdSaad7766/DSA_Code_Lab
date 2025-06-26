package com.CodeLab.Notification_Service.controller;

import com.CodeLab.Notification_Service.requestDTO.ContestStartRequestDTO;
import com.CodeLab.Notification_Service.requestDTO.NotificationMessage;
import com.CodeLab.Notification_Service.requestDTO.OTPGenerateRequestDTO;
import com.CodeLab.Notification_Service.responseDTO.GeneralResponseDTO;
import com.CodeLab.Notification_Service.service.SendMailService;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RabbitMQController {

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    SendMailService sendMailService;

    @RabbitListener(queues = "codelab-notification-queue")
    public void consumeMessage(@Payload NotificationMessage notificationMessage) throws Exception{
        String message = notificationMessage.getMessageType();
        Object payload = notificationMessage.getPayload();

        GeneralResponseDTO responseDTO = new GeneralResponseDTO();
        if(message.equals("OTP")){

            OTPGenerateRequestDTO requestDTO = modelMapper.map(payload,OTPGenerateRequestDTO.class);
            try {
                sendMailService.sendOtpMail(requestDTO);

            } catch (Exception e) {
                responseDTO.setMessage(e.getMessage());
                System.out.println(requestDTO);
            }
        }
        else if(notificationMessage.getMessageType().equals("Contest-Reminder")){
            ContestStartRequestDTO requestDTO = modelMapper.map(payload,ContestStartRequestDTO.class);
            try {
                sendMailService.contestReminderMail(requestDTO);

            } catch (Exception e) {
                responseDTO.setMessage(e.getMessage());
                System.out.println(requestDTO);
            }
        }
    }
}
