package com.CodeLab.Central_Service.requestDTO;

import com.CodeLab.Central_Service.enums.Gender;
import com.CodeLab.Central_Service.enums.Language;
import com.CodeLab.Central_Service.enums.UserCategory;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString

public class UserRequestDTO {

    private String name;

    private String email;

    private String password;

    private Gender gender;

    private LocationRequestDTO locationRequestDTO;

    private UserCategory userCategory;

    private Language preferredLanguage;

}
