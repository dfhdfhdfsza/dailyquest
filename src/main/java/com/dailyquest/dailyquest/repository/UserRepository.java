package com.dailyquest.dailyquest.repository;

import com.dailyquest.dailyquest.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity,Long> {

    boolean existsByLoginId(String id);

    Optional<UserEntity> findByLoginId(String uid);

    Optional<UserEntity> findByUid(long uid);

    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByLoginIdAndEmail(String loginId, String email);

    boolean existsByEmail(String email);
}
