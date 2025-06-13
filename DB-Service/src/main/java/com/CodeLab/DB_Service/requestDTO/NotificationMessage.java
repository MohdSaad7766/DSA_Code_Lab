package com.CodeLab.DB_Service.requestDTO;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class NotificationMessage {
    String messageType; // Create-user-notification
    Object payload; // AppUser Object
}