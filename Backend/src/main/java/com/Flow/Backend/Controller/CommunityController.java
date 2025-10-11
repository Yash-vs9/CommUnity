package com.Flow.Backend.Controller;

import com.Flow.Backend.DTO.CreateCommunity;
import com.Flow.Backend.service.CommunityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/communities")
public class CommunityController {
    @Autowired
    private CommunityService communityService;

    @PostMapping("/getcommunities")
    public String createcommunity(@RequestBody CreateCommunity createCommunity){
        return communityService.createCommunity(createCommunity);
    }
    @DeleteMapping("/{id}/deletecommunity")
    public String deletecommunity(@PathVariable Long id){
        communityService.deleteCommunity(id);
        return "Community deleted successfully";
    }
}
