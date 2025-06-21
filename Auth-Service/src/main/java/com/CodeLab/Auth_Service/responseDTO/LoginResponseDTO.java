package com.CodeLab.Auth_Service.responseDTO;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LoginResponseDTO {
    private String token;
    private boolean isValid;
    private Object object;
    private UUID userId;
}
