package com.Flow.Backend.repository;

import com.Flow.Backend.model.PostModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<PostModel,Long> {
    List<PostModel> findByCreatedByUser(String createdByUser);

}
