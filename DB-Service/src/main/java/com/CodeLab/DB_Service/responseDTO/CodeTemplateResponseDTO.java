package com.CodeLab.DB_Service.responseDTO;

import com.CodeLab.DB_Service.enums.Language;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString

public class CodeTemplateResponseDTO {

    private String visibleTemplateCode;

    private String invisibleTemplateCode;

    private Language language;
}
