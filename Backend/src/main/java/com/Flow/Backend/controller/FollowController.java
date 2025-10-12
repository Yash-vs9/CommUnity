package com.Flow.Backend.controller;

import com.Flow.Backend.DTO.FollowNameDTO;
import com.Flow.Backend.DTO.FollowerDTO;
import com.Flow.Backend.DTO.SendFollowDTO;
import com.Flow.Backend.service.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/follow")
public class FollowController {
    @Autowired
    private FollowService followService;

    @PostMapping("/send")
    public ResponseEntity<String> sendFollow(@RequestBody FollowNameDTO followNameDTO) {
        return ResponseEntity.ok(followService.sendFollowRequest(followNameDTO.getUsername()));
    }

    @PostMapping("/accept")
    public ResponseEntity<String> acceptFollow(@RequestBody  FollowNameDTO followNameDTO) {
        return ResponseEntity.ok(followService.acceptFollowRequest(followNameDTO.getUsername()));
    }

    @PostMapping("/followBack")
    public ResponseEntity<String> followBack(@RequestBody FollowNameDTO followNameDTO) {
        return ResponseEntity.ok(followService.followBack(followNameDTO.getUsername()));
    }

    @PostMapping("/unfollow")
    public ResponseEntity<String> unfollow(@RequestBody FollowNameDTO followNameDTO) {
        return ResponseEntity.ok(followService.unfollow(followNameDTO.getUsername()));
    }

    @GetMapping("/{userId}/getFollowers")
    public List<FollowerDTO> getFollowersByUserId(@PathVariable Long userId){
        return followService.getFollowerOfUser(userId);
    }

    @GetMapping("/{userId}/getFollowing")
    public List<FollowerDTO> getFollowingByUserId(@PathVariable Long userId){
        return followService.getFollowingOfUser(userId);
    }

    @GetMapping("/{userId}/sentRequest")
    public List<FollowerDTO> getSendRequests(@PathVariable Long userId){
        return followService.getSendRequests(userId);
    }
    @GetMapping("/{userId}/receiverRequest")
    public  List<FollowerDTO> getReceivedRequests(@PathVariable Long userId){
        return followService.getReceivedRequests(userId);
    }
}
