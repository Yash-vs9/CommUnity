package com.Flow.Backend.service;

import com.Flow.Backend.DTO.CreateCommunity;
import com.Flow.Backend.model.CommunityModel;
import com.Flow.Backend.repository.CommunityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public class CommunityService {
    @Autowired
    private CommunityRepository communityRepository;


    @Transactional
    public String  createCommunity(CreateCommunity createCommunity){
        CommunityModel community=new CommunityModel();
        community.setName(createCommunity.getName());
        community.setDescription(createCommunity.getDescription());
        community.setLogoUrl(createCommunity.getLogoUrl());
        community.getAdmin().add(createCommunity.getUsername());
        community.getMembers().add(createCommunity.getUsername());
        communityRepository.save(community);
        return "Community"+createCommunity.getName()+"created successfully";
    }
    @Transactional
    public void deleteCommunity(Long communityId) {
        CommunityModel community = communityRepository.findById(communityId)
                .orElseThrow(() -> new RuntimeException("Community not found with id: " + communityId));
        communityRepository.delete(community);
    }
}
