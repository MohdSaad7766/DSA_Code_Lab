package com.CodeLab.Central_Service.model;


import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString


public class Topic {

    private UUID topicId;


    private String topicName;


    private int totalProblem;
}
