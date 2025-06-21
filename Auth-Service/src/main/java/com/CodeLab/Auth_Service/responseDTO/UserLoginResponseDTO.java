package com.CodeLab.Auth_Service.responseDTO;

import com.CodeLab.Auth_Service.model.User;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserLoginResponseDTO {
    private String token;
    private boolean isValid;
    private User profile;
}
