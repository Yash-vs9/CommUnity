package com.Flow.Backend.service;

import com.Flow.Backend.DTO.RegisterBody;
import com.Flow.Backend.model.UserModel;
import com.Flow.Backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public String register(RegisterBody registerBody){
        if (userRepository.findByEmail(registerBody.getEmail()).isPresent()) {
            throw new UserAlreadyExistException("Email already exists");
        }

        if (userRepository.findByUsername(registerBody.getUsername()).isPresent()) {
            throw new UserAlreadyExistException("Username already exists");
        }
        UserModel user=new UserModel();
        user.setEmail(registerBody.getEmail());
        user.setFirstName(registerBody.getFirstName());
        user.setLastName(registerBody.getLastName());
        user.setUsername(registerBody.getUsername());
        user.setPassword(passwordEncoder.encode(registerBody.getPassword()));

        UserModel savedUser = userRepository.save(user);
        UserDetails userDetails = userDetailService.loadUserByUsername(savedUser.getUsername());
        String jwt = jwtUtil.generateToken(savedUser.getUsername());

        return jwt;
    }
}
