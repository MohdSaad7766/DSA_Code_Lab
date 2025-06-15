package com.CodeLab.DB_Service.model;

import com.CodeLab.DB_Service.enums.Gender;
import com.CodeLab.DB_Service.enums.Language;
import com.CodeLab.DB_Service.enums.UserCategory;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString

@Entity
@Table(name = "Users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id",updatable = false)
    private UUID userId;

    @Column(name = "user_name",nullable = false)
    private String name;

    @Column(name = "user_email",nullable = false,unique = true,updatable = false)
    private String email;

    @Column(name = "user_password",nullable = false)
    private String password;

    @Column(name = "created_at",nullable = false,updatable = false)
    @CreationTimestamp
    private LocalDate createdAt;

    @Column(name = "updated_at",nullable = false)
    @UpdateTimestamp
    private LocalDate updatedAt;

    @Column(name = "gender",nullable = false)
    @Enumerated(value = EnumType.STRING)
    private Gender gender;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "location_id",nullable = false)
    private Location location;


    @Column(name = "user_category",nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserCategory userCategory;

    @Column(name = "preferred_language",nullable = false)
    @Enumerated(value = EnumType.STRING)
    private Language preferredLanguage;

}
