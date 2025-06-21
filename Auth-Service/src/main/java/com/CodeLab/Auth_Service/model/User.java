package com.CodeLab.Auth_Service.model;

import com.CodeLab.Auth_Service.enums.Gender;
import com.CodeLab.Auth_Service.enums.Language;
import com.CodeLab.Auth_Service.enums.UserCategory;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString


public class User {


    private UUID userId;


    private String name;


    private String email;

    private String password;


    private LocalDate createdAt;


    private LocalDate updatedAt;


    private Gender gender;


    private Location location;



    private UserCategory userCategory;


    private Language preferredLanguage;



}
