package com.Flow.Backend.service;

import com.Flow.Backend.DTO.*;
import com.Flow.Backend.exceptions.PasswordWrongException;
import com.Flow.Backend.exceptions.UserAlreadyExistException;

import com.Flow.Backend.exceptions.UserNotFoundException;
import com.Flow.Backend.model.CommunityModel;
import com.Flow.Backend.model.MyUserDetailService;
import com.Flow.Backend.model.UserModel;
import com.Flow.Backend.repository.CommunityRepository;
import com.Flow.Backend.repository.UserRepository;
import com.Flow.Backend.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private CommunityRepository communityRepository;
    @Transactional
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
    @Transactional
    public String login(LoginBody loginBody) {
        UserModel user = userRepository.findByEmail(loginBody.getEmail())
                .orElseThrow(() -> new UserNotFoundException("This email is not registered"));
        String username = user.getUsername();
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, loginBody.getPassword()));
        } catch (Exception e) {
            throw new PasswordWrongException("Password Incorrect");
        }
        UserDetails userDetails = myUserDetailService.loadUserByUsername(username);
        String jwt = jwtUtils.generateToken(userDetails.getUsername());
        return jwt;
    }

    @Transactional
    public List<CommunityProfileDTO> getUserCommunitiesById(Long userId) {
        UserModel user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        String username = user.getUsername(); // or email if you store emails

        // Fetch all communities where user is either a member or admin
        List<CommunityModel> memberCommunities = communityRepository.findAllByMemberUsername(username);
        List<CommunityModel> adminCommunities = communityRepository.findAllByAdminUsername(username);

        // Combine and remove duplicates
        List<CommunityModel> allCommunities = memberCommunities.stream()
                .peek(c -> { if (adminCommunities.contains(c)) adminCommunities.remove(c); })
                .collect(Collectors.toList());
        allCommunities.addAll(adminCommunities);

        // Map to DTO with role
        return allCommunities.stream().map(c -> {
            CommunityProfileDTO dto = new CommunityProfileDTO();
            dto.setId(c.getId());
            dto.setName(c.getName());
            dto.setLogoUrl(c.getLogoUrl());
            dto.setRole(c.getAdmin().contains(username) ? "admin" : "member");
            return dto;
        }).collect(Collectors.toList());
    }
    public List<UserModel> searchUsers(String query) {
        return userRepository.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(query, query);
    }


}
