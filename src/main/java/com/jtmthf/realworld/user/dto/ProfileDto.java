package com.jtmthf.realworld.user.dto;

import com.jtmthf.realworld.annotation.WrapWith;

@WrapWith("profile")
public class ProfileDto {
    private final String username;
    private final String bio;
    private final String image;
    private final boolean following;

    public ProfileDto(String username, String bio, String image) {
        this(username, bio, image, false);
    }

    public ProfileDto(String username, String bio, String image, boolean following) {
        this.username = username;
        this.bio = bio;
        this.image = image;
        this.following = following;
    }

    public String getUsername() {
        return username;
    }

    public String getBio() {
        return bio;
    }

    public String getImage() {
        return image;
    }

    public boolean isFollowing() {
        return following;
    }
}
