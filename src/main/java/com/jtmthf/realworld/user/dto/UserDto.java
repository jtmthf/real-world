package com.jtmthf.realworld.user.dto;

import com.jtmthf.realworld.annotation.WrapWith;

@WrapWith("user")
public class UserDto {
    private final String email;
    private final String token;
    private final String username;
    private final String bio;
    private final String image;

    public UserDto(String email, String token, String username, String bio, String image) {
        this.email = email;
        this.token = token;
        this.username = username;
        this.bio = bio;
        this.image = image;
    }

    public String getEmail() {
        return email;
    }

    public String getToken() {
        return token;
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
}
