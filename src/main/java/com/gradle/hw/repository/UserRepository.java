package com.gradle.hw.repository;

import com.gradle.hw.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u from User u WHERE u.email = ?1")
    public User findByEmail(String email);
}
