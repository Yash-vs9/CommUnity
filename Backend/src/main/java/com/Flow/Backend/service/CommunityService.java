package com.Flow.Backend.service;

import com.Flow.Backend.DTO.*;
import com.Flow.Backend.exceptions.AccessDeniedException;
import com.Flow.Backend.exceptions.CommunityMemberException;
import com.Flow.Backend.exceptions.CommunityNotFoundException;
import com.Flow.Backend.exceptions.CreatorNotEditException;
import com.Flow.Backend.model.CommunityModel;
import com.Flow.Backend.model.PostModel;
import com.Flow.Backend.model.UserModel;
import com.Flow.Backend.repository.CommunityRepository;
import com.Flow.Backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommunityService {
    @Autowired
    private CommunityRepository communityRepository;
    @Autowired
    private UserRepository userRepository;
    @Transactional
    public String  createCommunity(CreateCommunity createCommunity){
        CommunityModel community=new CommunityModel();
        String username= SecurityContextHolder.getContext().getAuthentication().getName();
        community.setName(createCommunity.getName());
        community.setDescription(createCommunity.getDescription());
        community.setLogoUrl(createCommunity.getLogoUrl());
        community.setCreatedByUser(username);
        community.getAdmin().add(username);
        community.getMembers().add(username);
        communityRepository.save(community);
        return "Community "+createCommunity.getName()+" created successfully";
    }
    @Transactional
    public void deleteCommunity(Long communityId) {
        String currentusername=SecurityContextHolder.getContext().getAuthentication().getName();
        CommunityModel community = communityRepository.findById(communityId)
                .orElseThrow(() -> new CommunityNotFoundException("Community not found with id: " + communityId));
        if(!community.getAdmin().contains(currentusername)){
            throw new AccessDeniedException("Only an admin or the creator can delete the community details");
        }
        communityRepository.delete(community);
    }
    @Transactional
    public String editDescription(EditCommunityDTO editCommunityDTO){
        String currentusername=SecurityContextHolder.getContext().getAuthentication().getName();
        CommunityModel community=communityRepository.findById(editCommunityDTO.getId())
                .orElseThrow(()->new CommunityNotFoundException("Community not found with id: "+editCommunityDTO.getId()));
        if (!community.getAdmin().contains(currentusername)){
            throw new AccessDeniedException("Only an admin or the creator can edit community details");
        }
        community.setDescription(editCommunityDTO.getDescription());
        communityRepository.save(community);
        return "Description Updated Successfully";
    }
    @Transactional
    public String requestToJoin(Long communityId) {
        // Get username of logged-in user
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // Fetch the community
        CommunityModel community = communityRepository.findById(communityId)
                .orElseThrow(() -> new CommunityNotFoundException("Community not found with id: " + communityId));

        // Check if user is already a member
        if (community.getMembers().contains(username)) {
            return "You are already a member of this community";
        }
        if(community.getJoinRequests().contains(username)){
            return "You have already sent a join request";
        }

        // Add user to members list
        community.getJoinRequests().add(username);
        communityRepository.save(community);

        return "Join request sent to community admins";
    }
    @Transactional
    public String acceptJoinRequest(Long communityId, String requesterUsername) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        CommunityModel community = communityRepository.findById(communityId)
                .orElseThrow(() -> new CommunityNotFoundException("Community not found with id: " + communityId));

        // Only admin/creator can accept
        if (!community.getAdmin().contains(currentUsername)) {
            throw new AccessDeniedException("Only admins can accept join requests");
        }

        // Check if the user has requested
        if (!community.getJoinRequests().contains(requesterUsername)) {
            return requesterUsername + " has not requested to join";
        }

        // Add user to members and remove from requests
        community.getMembers().add(requesterUsername);
        community.getJoinRequests().remove(requesterUsername);
        communityRepository.save(community);

        return requesterUsername + " has been added to the community";
    }
    @Transactional(readOnly = true)
    public List<CommunityProfileDTO> getUserCommunitiesDTO(String username) {
        List<CommunityModel> communities = communityRepository.findByMembersContainsOrAdminContains(username, username);

        return communities.stream()
                .map(c -> {
                    String role = c.getAdmin().contains(username) ? "ADMIN" : "MEMBER";
                    CommunityProfileDTO dto = new CommunityProfileDTO();
                    dto.setId(c.getId());
                    dto.setName(c.getName());
                    dto.setLogoUrl(c.getLogoUrl());
                    dto.setRole(role);
                    return dto;
                })
                .toList();
    }
    @Transactional
    public String makeAdmin(Long communityId, String memberUsername) {
        // Get currently logged-in user
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        // Fetch community
        CommunityModel community = communityRepository.findById(communityId)
                .orElseThrow(() -> new CommunityNotFoundException("Community not found with id: " + communityId));

        // Check if current user is an admin
        if (!community.getAdmin().contains(currentUsername)) {
            throw new AccessDeniedException("Only an admin can promote members");
        }

        // Check if the member is part of this community
        if (!community.getMembers().contains(memberUsername)) {
            throw new CommunityMemberException(memberUsername + " is not a member of this community");
        }

        // Check if the member is already an admin
        if (community.getAdmin().contains(memberUsername)) {
            return memberUsername + " is already an admin";
        }

        // Promote member to admin
        community.getAdmin().add(memberUsername);
        communityRepository.save(community);

        return memberUsername + " has been promoted to admin in " + community.getName();
    }
    @Transactional
    public String demoteAdmin(Long communityId, String adminUsername) {
        // Get the currently logged-in user
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        // Fetch community
        CommunityModel community = communityRepository.findById(communityId)
                .orElseThrow(() -> new CommunityNotFoundException("Community not found with id: " + communityId));

        // Check if current user is an admin
        if (!community.getAdmin().contains(currentUsername)) {
            throw new AccessDeniedException("Only an admin can demote another admin");
        }

        // The creator (first admin in list) cannot be demoted
        String creatorUsername = community.getCreatedByUser(); // assuming first admin is creator
        if (adminUsername.equals(creatorUsername)) {
            throw new CreatorNotEditException("The creator of the community cannot be demoted");
        }

        // Check if the user is actually an admin
        if (!community.getAdmin().contains(adminUsername)) {
            return adminUsername + " is not an admin";
        }

        // Demote admin (remove from admin list)
        community.getAdmin().remove(adminUsername);
        communityRepository.save(community);

        return adminUsername + " has been demoted to member in " + community.getName()+" by "+ currentUsername;
    }
    @Transactional(readOnly = true)
    public List<JoinRequestDTO> getPendingJoinRequests(Long communityId) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        CommunityModel community = communityRepository.findById(communityId)
                .orElseThrow(() -> new CommunityNotFoundException("Community not found with id: " + communityId));

        // Map each username in joinRequests to DTO
        return community.getJoinRequests().stream()
                .map(username -> {
                    UserModel user = userRepository.findByUsername(username)
                            .orElseThrow(() -> new RuntimeException("User not found: " + username));
                    return new JoinRequestDTO(user.getUsername(), user.getProfilePic());
                })
                .toList();
    }
    @Transactional
    public String rejectJoinRequest(Long communityId, String requesterUsername) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        CommunityModel community = communityRepository.findById(communityId)
                .orElseThrow(() -> new CommunityNotFoundException("Community not found with id: " + communityId));

        // Only admin/creator can reject
        if (!community.getAdmin().contains(currentUsername)) {
            throw new AccessDeniedException("Only admins can reject join requests");
        }

        // Check if the user has actually requested
        if (!community.getJoinRequests().contains(requesterUsername)) {
            return requesterUsername + " has not requested to join";
        }

        // Remove from join requests
        community.getJoinRequests().remove(requesterUsername);
        communityRepository.save(community);

        return "Join request from " + requesterUsername + " has been rejected from "+currentUsername;
    }

    @Transactional
    public  List<AllCommunityDTO> getAllCreatedCommunities(){
        List<CommunityModel> communities = communityRepository.findAll();
        return communities.stream()
                .map(c->{
                    AllCommunityDTO dto = new AllCommunityDTO();
                    dto.setId(c.getId());
                    dto.setDescription(c.getDescription());
                    dto.setName(c.getName());
                    dto.setLogoUrl(c.getLogoUrl());
                    return dto;
                })
                .toList();
    }
    @Transactional(readOnly = true)
    public List<PostWithCommentsDTO> getCommunityPosts(Long communityId) {
        // Fetch community
        CommunityModel community = communityRepository.findById(communityId)
                .orElseThrow(() -> new RuntimeException("Community not found with id: " + communityId));

        // Fetch posts of that community
        List<PostModel> posts = community.getPosts();

        // Convert to DTOs
        return posts.stream().map(post -> {
            PostWithCommentsDTO dto = new PostWithCommentsDTO();
            dto.setId(post.getId());
            dto.setTitle(post.getTitle());
            dto.setDescription(post.getDescription());
            dto.setImageUrl(post.getImageUrl());
            dto.setCreatedAt(post.getCreatedAt());
            dto.setCreatedByUser(post.getCreatedByUser());
            dto.setLikes(post.getLikes());

            // Convert comments
            List<CommentResponseDTO> commentDTOs = post.getComments().stream().map(comment -> {
                CommentResponseDTO cDto = new CommentResponseDTO();
                cDto.setId(comment.getId());
                cDto.setUsername(comment.getUsername());
                cDto.setReply(comment.getReply());
                cDto.setCreatedAt(comment.getCreatedAt());
                return cDto;
            }).toList();

            dto.setComments(commentDTOs);
            return dto;
        }).toList();
    }
    @Transactional(readOnly = true)
    public CommunityDetailsDTO getCommunityDetails(Long communityId) {
        CommunityModel community = communityRepository.findById(communityId)
                .orElseThrow(() -> new RuntimeException("Community not found with id: " + communityId));

        CommunityDetailsDTO dto = new CommunityDetailsDTO();
        dto.setId(community.getId());
        dto.setName(community.getName());
        dto.setLogoUrl(community.getLogoUrl());
        dto.setDescription(community.getDescription());
        dto.setCreatedByUser(community.getCreatedByUser());

        List<CommunityMemberDTO> memberDTOs = new java.util.ArrayList<>();

        // Add creator (always ADMIN)
        CommunityMemberDTO creatorDTO = new CommunityMemberDTO();
        creatorDTO.setUsername(community.getCreatedByUser());
        creatorDTO.setRole("CREATOR");
        memberDTOs.add(creatorDTO);

        // Add admins
        if (community.getAdmin() != null) {
            for (String admin : community.getAdmin()) {
                if (!admin.equals(community.getCreatedByUser())) { // skip creator
                    CommunityMemberDTO adminDTO = new CommunityMemberDTO();
                    adminDTO.setUsername(admin);
                    adminDTO.setRole("ADMIN");
                    memberDTOs.add(adminDTO);
                }
            }
        }

        // Add members
        if (community.getMembers() != null) {
            for (String member : community.getMembers()) {
                // skip if already admin or creator
                if (!member.equals(community.getCreatedByUser()) &&
                        (community.getAdmin() == null || !community.getAdmin().contains(member))) {
                    CommunityMemberDTO memberDTO = new CommunityMemberDTO();
                    memberDTO.setUsername(member);
                    memberDTO.setRole("MEMBER");
                    memberDTOs.add(memberDTO);
                }
            }
        }

        dto.setMembers(memberDTOs);
        return dto;
    }
    @Transactional
    public String join(Long communityId) {
        // Get username of logged-in user
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // Fetch the community
        CommunityModel community = communityRepository.findById(communityId)
                .orElseThrow(() -> new CommunityNotFoundException("Community not found with id: " + communityId));

        // Check if user is already a member
        if (community.getMembers().contains(username)) {
            return "You are already a member of this community";
        }


        // Add user to members list
        community.getMembers().add(username);
        communityRepository.save(community);

        return "You has join the community "+community.getName()+" successfully";
    }
}
