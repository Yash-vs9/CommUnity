package com.Flow.Backend.DTO;

import java.util.List;

public class ProfileDTO {
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String bio;
    private List<FollowerDTO> follower;
    private List<FollowerDTO> following;
    private String profilePic;
    private List<String> follow_req;
    private List<PostsDetails> postsDetails;
    private List<CommunityProfileDTO> communities;
    private List<EventDetailsDTO> events;
    public List<String> getFollow_req() {
        return follow_req;
    }

    public void setFollow_req(List<String> follow_req) {
        this.follow_req = follow_req;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<FollowerDTO> getFollower() {
        return follower;
    }

    public void setFollower(List<FollowerDTO> follower) {
        this.follower = follower;
    }

    public List<FollowerDTO> getFollowing() {
        return following;
    }

    public void setFollowing(List<FollowerDTO> following) {
        this.following = following;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public List<PostsDetails> getPostsDetails() {
        return postsDetails;
    }

    public void setPostsDetails(List<PostsDetails> postsDetails) {
        this.postsDetails = postsDetails;
    }

    public List<CommunityProfileDTO> getCommunities() {
        return communities;
    }

    public void setCommunities(List<CommunityProfileDTO> communities) {
        this.communities = communities;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public List<EventDetailsDTO> getEvents() {
        return events;
    }

    public void setEvents(List<EventDetailsDTO> events) {
        this.events = events;
    }
}
