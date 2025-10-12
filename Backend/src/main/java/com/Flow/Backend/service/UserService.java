package com.Flow.Backend.service;

import com.Flow.Backend.DTO.*;
import com.Flow.Backend.exceptions.PasswordWrongException;
import com.Flow.Backend.exceptions.UserAlreadyExistException;

import com.Flow.Backend.exceptions.UserNotFoundException;
import com.Flow.Backend.model.CommunityModel;
import com.Flow.Backend.model.EventModel;
import com.Flow.Backend.model.MyUserDetailService;
import com.Flow.Backend.model.UserModel;
import com.Flow.Backend.repository.CommunityRepository;
import com.Flow.Backend.repository.EventRepository;
import com.Flow.Backend.repository.PostRepository;
import com.Flow.Backend.repository.UserRepository;
import com.Flow.Backend.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
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
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private EventRepository eventRepository;
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
    @Transactional
    public List<UserModel> searchUsers(String username) {
        return userRepository.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(username, username);
    }
    @Transactional
    public ProfileDTO getProfile(){
        String username=SecurityContextHolder.getContext().getAuthentication().getName();
        UserModel user = userRepository.findByUsername(username)
                .orElseThrow(()->new UserNotFoundException("User not found"));
        ProfileDTO dto=new ProfileDTO();
        dto.setUsername(username);
        dto.setFollow_req(user.getRecievedRequest());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setProfilePic(user.getProfilePic());
        dto.setEmail(user.getEmail());
        dto.setId(user.getId());
        dto.setBio(user.getBio());
        List<FollowerDTO> followers = user.getFollowers().stream()
                .map(followerUsername -> userRepository.findByUsername(followerUsername)
                        .map(followerUser -> {
                            FollowerDTO followDTO = new FollowerDTO();
                            followDTO.setId(followerUser.getId());
                            followDTO.setUsername(followerUser.getUsername());
                            followDTO.setProfilePic(followerUser.getProfilePic());
                            return followDTO;
                        })
                        .orElse(null))
                .filter(Objects::nonNull)
                .toList();

        List<FollowerDTO> following = user.getFollowing().stream()
                .map(followingUsername -> userRepository.findByUsername(followingUsername)
                        .map(followerUser -> {
                            FollowerDTO followDTO = new FollowerDTO();
                            followDTO.setId(followerUser.getId());
                            followDTO.setUsername(followerUser.getUsername());
                            followDTO.setProfilePic(followerUser.getProfilePic());
                            return followDTO;
                        })
                        .orElse(null))
                .filter(Objects::nonNull)
                .toList();
        List<CommunityProfileDTO> communities = communityRepository
                .findByMembersContainsOrAdminContains(username, username)
                .stream()
                .map(c -> {
                    String role = c.getAdmin().contains(username) ? "ADMIN" : "MEMBER";
                    CommunityProfileDTO communityDTO = new CommunityProfileDTO();
                    communityDTO.setId(c.getId());
                    communityDTO.setName(c.getName());
                    communityDTO.setLogoUrl(c.getLogoUrl());
                    communityDTO.setRole(role);
                    return communityDTO;
                })
                .toList();
        List<PostsDetails> posts = postRepository.findByCreatedByUser(username)
                .stream()
                .map(p -> {
                    PostsDetails postDTO = new PostsDetails();
                    postDTO.setId(p.getId());
                    postDTO.setTitle(p.getTitle());
                    postDTO.setDescription(p.getDescription());
                    postDTO.setImageUrl(p.getImageUrl());
                    postDTO.setCreatedAt(p.getCreatedAt());
                    postDTO.setCommunityId(p.getCommunity() != null ? p.getCommunity().getId() : null);
                    return postDTO;
                })
                .toList();
        List<EventModel> userEvents = eventRepository.findAll().stream()
                .filter(event ->
                        event.getUser().getUsername().equals(username)
                                || event.getJoinedUsers().contains(username))
                .toList();

        List<EventDetailsDTO> eventDTOs = userEvents.stream().map(event -> {
            EventDetailsDTO e = new EventDetailsDTO();
            e.setId(event.getId());
            e.setTitle(event.getTitle());
            e.setDescription(event.getDescription());
            e.setLocation(event.getLocation());
            e.setHostedBy(event.getHostedBy());
            e.setCreatedAt(event.getCreatedAt());
            e.setCommunityId(event.getCommunity() != null ? event.getCommunity().getId() : null);
            e.setCommunityName(event.getCommunity() != null ? event.getCommunity().getName() : null);
            return e;
        }).toList();
        dto.setFollower(followers);
        dto.setFollowing(following);
        dto.setCommunities(communities);
        dto.setPostsDetails(posts);
        dto.setEvents(eventDTOs);
        return dto;
    }

    @Transactional
    public String editProfilePic(String url){
        UserModel user=userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(()-> new UserNotFoundException("User not found"));
        user.setProfilePic(url);
        userRepository.save(user);
        return "Profile Pic Changed Successfully";
    }
    @Transactional
    public String editName(EditNameDTO editNameDTO){
        UserModel user=userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(()-> new UserNotFoundException("User not found"));
        if (!editNameDTO.getFirstName().isEmpty()&& !editNameDTO.getFirstName().equals(" ")){
            user.setFirstName(editNameDTO.getFirstName());
        }
        user.setLastName(editNameDTO.getLastName());
        userRepository.save(user);
        return "Full Name Changed Successfully";
    }
    @Transactional
    public String editBio(String bio){
        UserModel user=userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(()->new UserNotFoundException("User not Found"));
        user.setBio(bio);
        userRepository.save(user);
        return "Bio Changed Successfully";
    }
    @Transactional
    public ProfileDTO getOthersProfile(Long id){
        UserModel user = userRepository.findById(id)
                .orElseThrow(()->new UserNotFoundException("User not found"));
        String username= user.getUsername();;
        ProfileDTO dto=new ProfileDTO();
        dto.setUsername(user.getUsername());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setProfilePic(user.getProfilePic());
        dto.setEmail(user.getEmail());
        dto.setId(user.getId());
        dto.setBio(user.getBio());
        List<FollowerDTO> followers = user.getFollowers().stream()
                .map(followerUsername -> userRepository.findByUsername(followerUsername)
                        .map(followerUser -> {
                            FollowerDTO followDTO = new FollowerDTO();
                            followDTO.setId(followerUser.getId());
                            followDTO.setUsername(followerUser.getUsername());
                            followDTO.setProfilePic(followerUser.getProfilePic());
                            return followDTO;
                        })
                        .orElse(null))
                .filter(Objects::nonNull)
                .toList();

        List<FollowerDTO> following = user.getFollowing().stream()
                .map(followingUsername -> userRepository.findByUsername(followingUsername)
                        .map(followerUser -> {
                            FollowerDTO followDTO = new FollowerDTO();
                            followDTO.setId(followerUser.getId());
                            followDTO.setUsername(followerUser.getUsername());
                            followDTO.setProfilePic(followerUser.getProfilePic());
                            return followDTO;
                        })
                        .orElse(null))
                .filter(Objects::nonNull)
                .toList();
        List<CommunityProfileDTO> communities = communityRepository
                .findByMembersContainsOrAdminContains(username, username)
                .stream()
                .map(c -> {
                    String role = c.getAdmin().contains(username) ? "ADMIN" : "MEMBER";
                    CommunityProfileDTO communityDTO = new CommunityProfileDTO();
                    communityDTO.setId(c.getId());
                    communityDTO.setName(c.getName());
                    communityDTO.setLogoUrl(c.getLogoUrl());
                    communityDTO.setRole(role);
                    return communityDTO;
                })
                .toList();
        List<PostsDetails> posts = postRepository.findByCreatedByUser(username)
                .stream()
                .map(p -> {
                    PostsDetails postDTO = new PostsDetails();
                    postDTO.setId(p.getId());
                    postDTO.setTitle(p.getTitle());
                    postDTO.setDescription(p.getDescription());
                    postDTO.setImageUrl(p.getImageUrl());
                    postDTO.setCreatedAt(p.getCreatedAt());
                    postDTO.setCommunityId(p.getCommunity() != null ? p.getCommunity().getId() : null);
                    return postDTO;
                })
                .toList();
        List<EventModel> userEvents = eventRepository.findAll().stream()
                .filter(event ->
                        event.getUser().getUsername().equals(username)
                                || event.getJoinedUsers().contains(username))
                .toList();

        List<EventDetailsDTO> eventDTOs = userEvents.stream().map(event -> {
            EventDetailsDTO e = new EventDetailsDTO();
            e.setId(event.getId());
            e.setTitle(event.getTitle());
            e.setDescription(event.getDescription());
            e.setLocation(event.getLocation());
            e.setHostedBy(event.getHostedBy());
            e.setCreatedAt(event.getCreatedAt());
            e.setCommunityId(event.getCommunity() != null ? event.getCommunity().getId() : null);
            e.setCommunityName(event.getCommunity() != null ? event.getCommunity().getName() : null);
            return e;
        }).toList();
        dto.setFollower(followers);
        dto.setFollowing(following);
        dto.setCommunities(communities);
        dto.setPostsDetails(posts);
        dto.setEvents(eventDTOs);
        return dto;
    }
}
