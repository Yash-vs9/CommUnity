package com.Flow.Backend.repository;

import com.Flow.Backend.model.PostModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface PostRepository extends JpaRepository<PostModel,Long> {
    List<PostModel> findByCreatedByUser(String createdByUser);
    // Fetch the latest 10 posts based on creation date
    List<PostModel> findTop10ByOrderByCreatedAtDesc();
}
