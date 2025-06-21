package com.CodeLab.Central_Service.responseDTO;

import com.CodeLab.Central_Service.model.User;
import lombok.*;

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
