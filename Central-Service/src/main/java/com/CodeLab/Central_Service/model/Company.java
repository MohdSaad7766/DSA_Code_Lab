package com.CodeLab.Central_Service.model;


import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString


public class Company {


    private UUID companyId;


    private String companyName;

    private int totalProblem;

}
