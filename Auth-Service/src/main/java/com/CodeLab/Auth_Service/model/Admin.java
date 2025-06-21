package com.CodeLab.Auth_Service.model;


import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString


public class Admin {

    private UUID adminId;

    private String name;


    private String email;


    private String password;
}
