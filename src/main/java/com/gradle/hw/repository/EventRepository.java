package com.gradle.hw.repository;

import com.gradle.hw.model.Event;
import com.gradle.hw.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByUser(User user);
}
