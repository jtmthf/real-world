package com.jtmthf.realworld.user.dto;

import com.jtmthf.realworld.annotation.WrapWith;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@WrapWith("user")
public class UpdateUserDto {
    private final @Email String email;
    private final @NotBlank @Size(max = 16) String username;
    private final @Size(min = 8) String password;
    private final String bio;
    private final @URL String image;

    public UpdateUserDto(String email, String username, String password, String bio, String image) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.bio = bio;
        this.image = image;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getBio() {
        return bio;
    }

    public String getImage() {
        return image;
    }
}
