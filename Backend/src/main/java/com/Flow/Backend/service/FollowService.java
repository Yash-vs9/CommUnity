package com.Flow.Backend.service;

import com.Flow.Backend.DTO.FollowerDTO;
import com.Flow.Backend.DTO.SendFollowDTO;
import com.Flow.Backend.model.UserModel;
import com.Flow.Backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FollowService {
    @Autowired
    private UserRepository userRepository;
    @Transactional
    public List<FollowerDTO> getFollowerOfUser(Long userId){
        UserModel user=userRepository.findById(userId)
                .orElseThrow(()-> new RuntimeException("User not found with id: "+userId));
        List<String> followerUsernames=user.getFollowers();
        return followerUsernames.stream()
                .map(username -> userRepository.findByUsername(username)
                        .map(followerUser -> {
                            FollowerDTO dto = new FollowerDTO();
                            dto.setId(followerUser.getId());
                            dto.setUsername(followerUser.getUsername());
                            // Assuming you have a profilePic field (if not, keep null)
                            dto.setProfilePic(null);
                            return dto;
                        })
                        .orElse(null))
                .filter(follower -> follower != null)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<FollowerDTO> getFollowingOfUser(Long userId){
        UserModel user=userRepository.findById(userId)
                .orElseThrow(()-> new RuntimeException("User not found with id: "+userId));
        List<String> followerUsernames=user.getFollowing();
        return followerUsernames.stream()
                .map(username -> userRepository.findByUsername(username)
                        .map(followerUser -> {
                            FollowerDTO dto = new FollowerDTO();
                            dto.setId(followerUser.getId());
                            dto.setUsername(followerUser.getUsername());
                            // Assuming you have a profilePic field (if not, keep null)
                            dto.setProfilePic(null);
                            return dto;
                        })
                        .orElse(null))
                .filter(follower -> follower != null)
                .collect(Collectors.toList());
    }
    @Transactional
    public String sendFollowRequest(SendFollowDTO dto) {
        String senderUsername = dto.getSenderUsername();
        String receiverUsername = dto.getReceiverUsername();

        if (senderUsername.equals(receiverUsername)) {
            return "You cannot follow yourself.";
        }

        UserModel sender = userRepository.findByUsername(senderUsername)
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        UserModel receiver = userRepository.findByUsername(receiverUsername)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        // Already following
        if (sender.getFollowing().contains(receiverUsername)) {
            return "You are already following " + receiverUsername;
        }

        // Already sent request
        if (sender.getSentRequest().contains(receiverUsername)) {
            return "Follow request already sent to " + receiverUsername;
        }

        // Add pending request
        sender.getSentRequest().add(receiverUsername);
        receiver.getRecievedRequest().add(senderUsername);

        userRepository.save(sender);
        userRepository.save(receiver);

        return "Follow request sent to " + receiverUsername;
    }

    // Accept a follow request
    @Transactional
    public String acceptFollowRequest(SendFollowDTO dto) {
        String receiverUsername = dto.getReceiverUsername(); // the one accepting
        String senderUsername = dto.getSenderUsername(); // the one who sent

        UserModel receiver = userRepository.findByUsername(receiverUsername)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));
        UserModel sender = userRepository.findByUsername(senderUsername)
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        if (!receiver.getRecievedRequest().contains(senderUsername)) {
            return "No follow request from " + senderUsername;
        }

        // Remove pending requests
        receiver.getRecievedRequest().remove(senderUsername);
        sender.getSentRequest().remove(receiverUsername);

        // Update following/follower lists
        sender.getFollowing().add(receiverUsername);
        receiver.getFollowers().add(senderUsername);

        userRepository.save(receiver);
        userRepository.save(sender);

        return senderUsername + " is now following " + receiverUsername;
    }

    // 🟠 Follow back (no request flow, direct follow)
    @Transactional
    public String followBack(SendFollowDTO dto) {
        String followerUsername = dto.getSenderUsername(); // one who clicks follow back
        String targetUsername = dto.getReceiverUsername(); // one who already follows

        UserModel follower = userRepository.findByUsername(followerUsername)
                .orElseThrow(() -> new RuntimeException("Follower not found"));
        UserModel target = userRepository.findByUsername(targetUsername)
                .orElseThrow(() -> new RuntimeException("Target not found"));

        // If already following back
        if (follower.getFollowing().contains(targetUsername)) {
            return "You already follow " + targetUsername;
        }

        // Follow back instantly
        follower.getFollowing().add(targetUsername);
        target.getFollowers().add(followerUsername);

        userRepository.save(follower);
        userRepository.save(target);

        return followerUsername + " followed back " + targetUsername;
    }

    // 🔴 Unfollow
    @Transactional
    public String unfollow(SendFollowDTO dto) {
        String followerUsername = dto.getSenderUsername();
        String targetUsername = dto.getReceiverUsername();

        UserModel follower = userRepository.findByUsername(followerUsername)
                .orElseThrow(() -> new RuntimeException("Follower not found"));
        UserModel target = userRepository.findByUsername(targetUsername)
                .orElseThrow(() -> new RuntimeException("Target not found"));

        if (!follower.getFollowing().contains(targetUsername)) {
            return "You are not following " + targetUsername;
        }

        follower.getFollowing().remove(targetUsername);
        target.getFollowers().remove(followerUsername);

        userRepository.save(follower);
        userRepository.save(target);

        return "You unfollowed " + targetUsername;
    }

}
