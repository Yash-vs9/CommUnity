package com.Flow.Backend.controller;

import com.Flow.Backend.DTO.AllCommunityDTO;
import com.Flow.Backend.DTO.CommunityProfileDTO;
import com.Flow.Backend.DTO.CreateCommunity;
import com.Flow.Backend.DTO.EditCommunityDTO;
import com.Flow.Backend.service.CommunityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/communities")
public class CommunityController {
    @Autowired
    private CommunityService communityService;

    @PostMapping("/create")
    public String createCommunity(@RequestBody CreateCommunity createCommunity){
        return communityService.createCommunity(createCommunity);
    }
    @DeleteMapping("/{id}/delete")
    public String deletecommunity(@PathVariable Long id){
        communityService.deleteCommunity(id);
        return "Community deleted successfully";
    }
    @PostMapping("/update")
    public String updateDescription(@RequestBody EditCommunityDTO editCommunityDTO){
        return communityService.editDescription(editCommunityDTO);
    }
    @GetMapping("/getAllByUser")
    public List<CommunityProfileDTO> getAllCommunitiesByUser(){
        return communityService.getUserCommunitiesDTO(SecurityContextHolder.getContext().getAuthentication().getName());
    }
    @PostMapping("/request-join/{communityId}")
    public String requestToJoin(@PathVariable Long communityId){
        return communityService.requestToJoin(communityId);
    }
    @PostMapping("/accept-join/{communityId}/{username}")
    public String acceptJoinRequest(@PathVariable Long communityId,@PathVariable String username){
        return communityService.acceptJoinRequest(communityId,username);
    }
    @PostMapping("/make-admin/{communityId}/{memberUsername}")
    public String makeAdmin(@PathVariable Long communityId,@PathVariable String memberUsername){
        return communityService.makeAdmin(communityId,memberUsername);
    }
    @PostMapping("/demote-admin/{communityId}/{adminUsername}")
    public String demoteAdmin(@PathVariable Long communityId,@PathVariable String adminUsername){
        return communityService.demoteAdmin(communityId,adminUsername);
    }
    @PostMapping("/reject-join/{communityId}/{username}")
    public String rejectjoinrequest(@PathVariable Long communityId,@PathVariable String username){
        return communityService.rejectJoinRequest(communityId,username);
    }
    @GetMapping("/getAll")
    public List<AllCommunityDTO> getAllCommunities(){
        return communityService.getAllCreatedCommunities();
    }
}
