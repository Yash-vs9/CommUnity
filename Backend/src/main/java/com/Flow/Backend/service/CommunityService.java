package com.Flow.Backend.service;

import com.Flow.Backend.DTO.CreateCommunity;
import com.Flow.Backend.DTO.EditCommunityDTO;
import com.Flow.Backend.model.CommunityModel;
import com.Flow.Backend.repository.CommunityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
