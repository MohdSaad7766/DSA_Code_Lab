package com.CodeLab.Central_Service.responseDTO;

import com.CodeLab.Central_Service.model.Admin;
import lombok.*;

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
