package com.CodeLab.DB_Service.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString

@Entity
@Table(name = "Companies")
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "company_id",updatable = false)
    private UUID companyId;

    @Column(name = "company_name",nullable = false)
    private String companyName;

    @Column(name = "total_problem")
    private int totalProblem;

}
