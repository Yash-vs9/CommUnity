package com.Flow.Backend.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "events")
public class EventModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToMany(mappedBy = "likedEvents")
    private Set<UserModel> likedByUsers = new HashSet<>();

    @Column(nullable = false)
    private String createdByUser;

    private int likes;

    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "community_id")
    private CommunityModel community;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserModel user;

    public EventModel() {
    }

    public EventModel(String title, String description,
                      CommunityModel community, UserModel user) {
        this.title = title;
        this.description = description;
        this.community = community;
        this.user = user;
    }

    // Getters and setters for all fields...

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<UserModel> getLikedByUsers() {
        return likedByUsers;
    }

    public void setLikedByUsers(Set<UserModel> likedByUsers) {
        this.likedByUsers = likedByUsers;
    }

    public String getCreatedByUser() {
        return createdByUser;
    }

    public void setCreatedByUser(String createdByUser) {
        this.createdByUser = createdByUser;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public CommunityModel getCommunity() {
        return community;
    }

    public void setCommunity(CommunityModel community) {
        this.community = community;
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }
}
