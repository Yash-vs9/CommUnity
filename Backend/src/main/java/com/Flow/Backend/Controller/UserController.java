package com.Flow.Backend.controller;

import com.Flow.Backend.DTO.CommunityProfileDTO;
import com.Flow.Backend.DTO.FollowerDTO;
import com.Flow.Backend.DTO.LoginBody;
import com.Flow.Backend.DTO.RegisterBody;
import com.Flow.Backend.model.MyUserDetailService;
import com.Flow.Backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private MyUserDetailService myUserDetailService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Map<String,String>> register(@RequestBody RegisterBody registerBody){
        String jwt=userService.register(registerBody);
        return ResponseEntity.ok(Map.of("token",jwt));
    }
    @PostMapping("/login")
    public ResponseEntity<Map<String,String>> login(@RequestBody LoginBody loginBody){
        String jwt= userService.login(loginBody);
        return  ResponseEntity.ok(Map.of("token",jwt));
   }
   @GetMapping("/{userId}/communities")
    public List<CommunityProfileDTO> getUserCommunities(@PathVariable Long userId){
        return userService.getUserCommunitiesById(userId);
   }
   @GetMapping("/{userId}/getfollowers")
    public List<FollowerDTO> getFollowersByUserId(@PathVariable Long userId){
        return userService.getFollowerOfUser(userId);
   }

    @GetMapping("/{userId}/getfollowing")
    public List<FollowerDTO> getFollowingByUserId(@PathVariable Long userId){
        return userService.getFollowingOfUser(userId);
    }
}