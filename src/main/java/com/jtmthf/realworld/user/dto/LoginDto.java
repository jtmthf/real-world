package com.jtmthf.realworld.user.dto;

import com.jtmthf.realworld.annotation.WrapWith;

@WrapWith("user")
public class LoginDto {
    private final String email;
    private final String password;

    public LoginDto(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
