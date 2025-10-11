package com.Flow.Backend.repository;

import com.Flow.Backend.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserModel,Long> {
    Optional<UserModel> findByEmail(String email);

    Optional<UserModel> findByUsername(String username);
    Optional<UserModel> findById(Long id);
}
