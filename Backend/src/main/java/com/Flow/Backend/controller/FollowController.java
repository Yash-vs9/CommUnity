package com.Flow.Backend.controller;

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
    public ResponseEntity<String> sendFollow(@RequestBody SendFollowDTO dto) {
        return ResponseEntity.ok(followService.sendFollowRequest(dto));
    }

    @PostMapping("/accept")
    public ResponseEntity<String> acceptFollow(@RequestBody SendFollowDTO dto) {
        return ResponseEntity.ok(followService.acceptFollowRequest(dto));
    }

    @PostMapping("/followBack")
    public ResponseEntity<String> followBack(@RequestBody SendFollowDTO dto) {
        return ResponseEntity.ok(followService.followBack(dto));
    }

    @PostMapping("/unfollow")
    public ResponseEntity<String> unfollow(@RequestBody SendFollowDTO dto) {
        return ResponseEntity.ok(followService.unfollow(dto));
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
