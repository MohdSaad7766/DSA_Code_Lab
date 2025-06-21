package com.CodeLab.Auth_Service.model;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@ToString

public class Pair{
    String credentials;
    User user;
    Admin admin;
    UUID userId;

    public Pair(String c,User user,UUID userId){
        this.credentials = c;
        this.user = user;
        this.userId = userId;
        this.admin = null;
    }

    public Pair(String c,Admin admin,UUID userId){
        this.credentials = c;
        this.admin = admin;
        this.userId = userId;
        this.user = null;
    }

}
