package com.dailyquest.dailyquest.repository;

import com.dailyquest.dailyquest.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity,Integer> {


}
