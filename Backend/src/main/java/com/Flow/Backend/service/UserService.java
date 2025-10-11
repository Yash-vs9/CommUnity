package com.Flow.Backend.service;

import com.Flow.Backend.DTO.RegisterBody;
import com.Flow.Backend.exceptions.UserAlreadyExistException;
import com.Flow.Backend.filter.JwtFilter;
import com.Flow.Backend.model.MyUserDetailService;
import com.Flow.Backend.model.UserModel;
import com.Flow.Backend.repository.UserRepository;
import com.Flow.Backend.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private MyUserDetailService myUserDetailService;
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
        UserDetails userDetails = myUserDetailService.loadUserByUsername(savedUser.getUsername());
        String jwt = jwtUtils.generateToken(savedUser.getUsername());

        return jwt;
    }

}
