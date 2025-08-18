package com.dailyquest.dailyquest.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users",indexes = {
        @Index(name = "idx_login_id",columnList = "loginId"),
        @Index(name = "idx_email",columnList = "email")
})
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "uid")
    private Long uid;

    @Column(nullable=false, length=20)
    private  String username;

    @Column(name="login_id", nullable=false, length=20) // 길이 명확
    private  String loginId;

    @Column(nullable=false, length=60)  // BCrypt 해시 길이에 맞춤
    private  String password;

    @Column(nullable=false, length=30) // 이메일 길이 제한
    private String email;

    @Column(nullable=false, length=20)
    private  String role;


}
