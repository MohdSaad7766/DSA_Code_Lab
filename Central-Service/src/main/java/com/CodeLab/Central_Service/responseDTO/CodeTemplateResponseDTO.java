package com.CodeLab.Central_Service.responseDTO;

import com.CodeLab.Central_Service.enums.Language;
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
