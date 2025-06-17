package com.CodeLab.Central_Service.responseDTO;

import com.fasterxml.jackson.annotation.JsonGetter;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserRegistrationRequestResponseDTO {
    private String message;
    private boolean isValidEmail;
}
