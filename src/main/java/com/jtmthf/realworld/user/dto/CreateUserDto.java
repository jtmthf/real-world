package com.jtmthf.realworld.user.dto;

import com.jtmthf.realworld.annotation.WrapWith;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.StringJoiner;

@WrapWith("user")
public class CreateUserDto {
    @NotBlank
    @Size(min = 1, max = 16)
    private final String username;

    @NotNull
    @Email
    private final String email;

    @NotNull
    @Size(min = 8, max = 72)
    private final String password;

    public CreateUserDto(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", CreateUserDto.class.getSimpleName() + "[", "]")
          .add("username='" + username + "'")
          .add("email='" + email + "'")
          .add("password='" + password + "'")
          .toString();
    }
}
