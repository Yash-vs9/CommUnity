package com.Flow.Backend.DTO;

import java.util.List;

public class CommunityDetailsDTO {
    private Long id;
    private String name;
    private String description;
    private String logoUrl;
    private String createdByUser;
    private List<CommunityMemberDTO> members;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getCreatedByUser() {
        return createdByUser;
    }

    public void setCreatedByUser(String createdByUser) {
        this.createdByUser = createdByUser;
    }

    public List<CommunityMemberDTO> getMembers() {
        return members;
    }

    public void setMembers(List<CommunityMemberDTO> members) {
        this.members = members;
    }
}
