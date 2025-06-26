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
@Table(name = "Topics")
public class Topic {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "topic_id",updatable = false)
    private UUID topicId;

    @Column(name = "topic_name",unique = true)
    private String topicName;

    @Column(name = "total_problem")
    private int totalProblem;

}
