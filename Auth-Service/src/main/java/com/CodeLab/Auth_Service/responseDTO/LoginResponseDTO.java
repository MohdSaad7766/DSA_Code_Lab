package com.CodeLab.Auth_Service.responseDTO;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LoginResponseDTO {
    private String token;
    private boolean isValid;

}
