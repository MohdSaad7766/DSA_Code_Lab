package com.CodeLab.Auth_Service.responseDTO;

import com.CodeLab.Auth_Service.model.Admin;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AdminLoginResponseDTO {
    private String token;
    private boolean isValid;
    private Admin profile;
}
