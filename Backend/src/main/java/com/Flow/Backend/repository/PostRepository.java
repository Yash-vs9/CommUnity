package com.Flow.Backend.repository;

import com.Flow.Backend.model.PostModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<PostModel,Long> {
    List<PostModel> findByCreatedByUser(String createdByUser);
    // Fetch the latest 10 posts based on creation date
    List<PostModel> findTop10ByOrderByCreatedAtDesc();
}
