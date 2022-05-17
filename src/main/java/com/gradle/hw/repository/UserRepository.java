package com.gradle.hw.repository;

import com.gradle.hw.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;


public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(@Param("email") String email);
}
