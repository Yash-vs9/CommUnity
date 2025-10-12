package com.Flow.Backend.repository;

import com.Flow.Backend.model.EventModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<EventModel,Long> {
}
