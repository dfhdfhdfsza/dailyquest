package com.dailyquest.dailyquest.entity;

import jakarta.persistence.*;
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

    private  String loginId;

    private  String password;

    private String email;

    private  String role;


}
