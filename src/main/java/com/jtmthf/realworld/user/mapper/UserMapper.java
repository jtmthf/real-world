package com.jtmthf.realworld.user.mapper;

import com.jtmthf.realworld.user.User;
import com.jtmthf.realworld.user.dto.ProfileDto;
import com.jtmthf.realworld.user.dto.UserDto;
import org.springframework.stereotype.Service;

@Service
public class UserMapper {

  public UserDto createUserDto(User user, String token) {
    return new UserDto(user.getEmail(), token, user.getUsername(), user.getBio(), user.getImage());
  }

  public ProfileDto createProfileDto(User user, boolean following) {
    return new ProfileDto(user.getUsername(), user.getBio(), user.getImage(), following);
  }
}
