package com.Flow.Backend.repository;

import com.Flow.Backend.model.EventModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface EventRepository extends JpaRepository<EventModel,Long> {
    @Query(value = "SELECT * FROM events ORDER BY created_at DESC LIMIT 5", nativeQuery = true)
    List<EventModel> findLatestFiveEvents();

    List<EventModel> findAllByOrderByCreatedAtDesc();
}

