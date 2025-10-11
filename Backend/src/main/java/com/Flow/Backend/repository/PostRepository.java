package com.Flow.Backend.repository;

import com.Flow.Backend.model.PostModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<PostModel,Long> {

}
