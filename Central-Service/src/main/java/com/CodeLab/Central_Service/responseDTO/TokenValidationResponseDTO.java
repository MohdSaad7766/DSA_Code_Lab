package com.CodeLab.Central_Service.responseDTO;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString

public class TokenValidationResponseDTO {
    UUID userId;
    Object object;
    boolean isValid;
    String message;
}
