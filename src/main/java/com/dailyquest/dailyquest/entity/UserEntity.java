package com.dailyquest.dailyquest.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users",indexes = {
        @Index(name = "idx_id",columnList = "id"),
        @Index(name = "idx_email",columnList = "email")
})
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long uid;

    private  String username;

    private  String id;

    private  String password;

    private String email;

    private  String role;


}
