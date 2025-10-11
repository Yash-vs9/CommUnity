package com.Flow.Backend.service;

import com.Flow.Backend.DTO.CommunityProfileDTO;
import com.Flow.Backend.DTO.CreateCommunity;
import com.Flow.Backend.DTO.EditCommunityDTO;
import com.Flow.Backend.model.CommunityModel;
import com.Flow.Backend.repository.CommunityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommunityService {
    @Autowired
    private CommunityRepository communityRepository;

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
        CommunityModel community = communityRepository.findById(communityId)
                .orElseThrow(() -> new RuntimeException("Community not found with id: " + communityId));
        communityRepository.delete(community);
    }
    @Transactional
    public String editDescription(EditCommunityDTO editCommunityDTO){
        CommunityModel community=communityRepository.findById(editCommunityDTO.getId())
                .orElseThrow(()->new RuntimeException("Community not found with id: "+editCommunityDTO.getId()));
        community.setDescription(editCommunityDTO.getDescription());
        communityRepository.save(community);
        return "Description Updated Successfully";
    }
    @Transactional
    public String joinCommunity(Long communityId) {
        // Get username of logged-in user
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // Fetch the community
        CommunityModel community = communityRepository.findById(communityId)
                .orElseThrow(() -> new RuntimeException("Community not found with id: " + communityId));

        // Check if user is already a member
        if (community.getMembers().contains(username)) {
            return "You are already a member of this community";
        }

        // Add user to members list
        community.getMembers().add(username);
        communityRepository.save(community);

        return "Joined community: " + community.getName();
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
                .orElseThrow(() -> new RuntimeException("Community not found with id: " + communityId));

        // Check if current user is an admin
        if (!community.getAdmin().contains(currentUsername)) {
            throw new RuntimeException("Only an admin can promote members");
        }

        // Check if the member is part of this community
        if (!community.getMembers().contains(memberUsername)) {
            throw new RuntimeException(memberUsername + " is not a member of this community");
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
                .orElseThrow(() -> new RuntimeException("Community not found with id: " + communityId));

        // Check if current user is an admin
        if (!community.getAdmin().contains(currentUsername)) {
            throw new RuntimeException("Only an admin can demote another admin");
        }

        // The creator (first admin in list) cannot be demoted
        String creatorUsername = community.getCreatedByUser(); // assuming first admin is creator
        if (adminUsername.equals(creatorUsername)) {
            throw new RuntimeException("The creator of the community cannot be demoted");
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

}
