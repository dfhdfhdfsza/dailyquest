package com.dailyquest.dailyquest.repository;

import com.dailyquest.dailyquest.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity,Long> {

    Boolean existsById(String id);

    Optional<UserEntity> findByloginId(String loginId);

    Optional<UserEntity> findByUsername(String username);
}
