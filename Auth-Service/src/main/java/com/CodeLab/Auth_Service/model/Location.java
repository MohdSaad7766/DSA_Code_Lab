package com.CodeLab.Auth_Service.model;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString

public class Location {


    private UUID locationId;


    private String city;


    private String state;


    private String country;
}
