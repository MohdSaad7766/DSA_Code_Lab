package com.CodeLab.DB_Service.model;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString

@Entity
@Table(name = "Contests")
public class Contest {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "contest_id",updatable = false)
    private UUID contestId;

    @Column(name = "name")
    private String contestName;

    @Column(name = "description",columnDefinition = "TEXT")
    private String contestDescription;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "duration")
    private long duration; //In Seconds

    @Column(name = "start_reminder_sent")
    private boolean startReminderSent = false;

    @Column(name = "leaderboard_generated")
    private boolean leaderboardGenerated = false;
}
