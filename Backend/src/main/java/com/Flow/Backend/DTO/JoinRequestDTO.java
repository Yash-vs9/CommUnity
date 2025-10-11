package com.Flow.Backend.DTO;

public class JoinRequestDTO {
    private String username;
    private String profilePic; // optional, if you want to show user profile picture

    public JoinRequestDTO() {}

    public JoinRequestDTO(String username, String profilePic) {
        this.username = username;
        this.profilePic = profilePic;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }
}
