package com.jtmthf.realworld.user;

import com.jtmthf.realworld.user.dto.CreateUserDto;
import com.jtmthf.realworld.user.dto.LoginDto;
import com.jtmthf.realworld.user.dto.UpdateUserDto;
import com.jtmthf.realworld.user.dto.UserDto;
import com.jtmthf.realworld.user.mapper.UserMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(
      UserService userService,
      UserMapper userMapper
    ) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @PostMapping("/users/login")
    public UserDto login(@RequestBody LoginDto loginDto) {
        userService.authenticate(
          loginDto.getEmail(),
          loginDto.getPassword()
        );
        User user = userService.getCurrentUser().orElseThrow(IllegalStateException::new);

        return userMapper.createUserDto(user, userService.getToken());
    }

    @PostMapping("/users")
    public UserDto register(@RequestBody CreateUserDto createUserDto) {
        User user = userService.createUser(createUserDto);
        String token = userService.getToken();

        return userMapper.createUserDto(user, token);
    }

    @GetMapping("/user")
    public HttpEntity<?> getCurrentUser() {
        return userService.getCurrentUser()
          .map(user -> userMapper.createUserDto(user, userService.getToken()))
          .map(ResponseEntity::ok)
          .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/user")
    public UserDto updateUser(@RequestBody UpdateUserDto updateUserDto) {
        User user = userService.updateUser(updateUserDto);

        return userMapper.createUserDto(user, userService.getToken());
    }

    @GetMapping("/profiles/{username}")
    public HttpEntity<?> getProfile(@PathVariable String username) {
        return userService.getProfileByUsername(username)
          .map(ResponseEntity::ok)
          .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/profiles/{username}/follow")
    public HttpEntity<?> followUser(@PathVariable String username) {
        return userService.followUser(username)
          .map(user -> userMapper.createProfileDto(user, true))
          .map(ResponseEntity::ok)
          .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/profiles/{username}/unfollow")
    public HttpEntity<?> unfollowUser(@PathVariable String username) {
        return userService.unfollowUser(username)
          .map(user -> userMapper.createProfileDto(user, false))
          .map(ResponseEntity::ok)
          .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
