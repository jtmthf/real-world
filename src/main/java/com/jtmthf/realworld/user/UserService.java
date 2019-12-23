package com.jtmthf.realworld.user;

import com.jtmthf.realworld.security.jwt.TokenProvider;
import com.jtmthf.realworld.user.dto.CreateUserDto;
import com.jtmthf.realworld.user.dto.ProfileDto;
import com.jtmthf.realworld.user.dto.UpdateUserDto;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.Collections;
import java.util.Optional;

@Service
@Validated
public class UserService {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final ApplicationEventPublisher eventPublisher;

    public UserService(
      AuthenticationManagerBuilder authenticationManagerBuilder,
      UserRepository userRepository,
      PasswordEncoder passwordEncoder,
      TokenProvider tokenProvider,
      ApplicationEventPublisher eventPublisher
    ) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.eventPublisher = eventPublisher;
    }

    public Authentication authenticate(String email, String password) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
          email,
          password
        );

        Authentication authentication = authenticationManagerBuilder.getObject()
          .authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return authentication;
    }

    public String getToken() {
        return tokenProvider.createToken(SecurityContextHolder.getContext().getAuthentication());
    }

    public String getToken(User user) {
        return tokenProvider.createToken(
          new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList())
        );
    }

    public Optional<User> getCurrentUser() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
          .map(Authentication::getName)
          .flatMap(userRepository::findByUsername);
    }

    public User createUser(@Valid CreateUserDto createUserDto) {
        User user = new User(
          createUserDto.getEmail(),
          createUserDto.getUsername(),
          passwordEncoder.encode(createUserDto.getPassword())
        );

        user = userRepository.save(user);
        eventPublisher.publishEvent(new UserEmailSet(user));

        return user;
    }

    @Transactional
    public User updateUser(@Valid UpdateUserDto updateUserDto) {
        User user = getCurrentUser().orElseThrow(IllegalStateException::new);

        if (updateUserDto.getEmail() != null) {
            user.setEmail(updateUserDto.getEmail());
            eventPublisher.publishEvent(new UserEmailSet(user));
        }
        if (updateUserDto.getUsername() != null) {
            user.setUsername(updateUserDto.getUsername());
        }
        if (updateUserDto.getBio() != null) {
            user.setBio(updateUserDto.getBio());
        }
        if (updateUserDto.getImage() != null) {
            user.setImage(updateUserDto.getImage());
        }
        if (updateUserDto.getPassword() != null) {
            user.setPasswordHash(passwordEncoder.encode(updateUserDto.getPassword()));
        }

        return user;
    }

    public Optional<ProfileDto> getProfileByUsername(String username) {
        return userRepository.findProfileByUsername(username);
    }

    public User getUserReferenceByUsername(String username) {
        return userRepository.getOneByNaturalId(username);
    }

    @Transactional
    public Optional<User> followUser(String username) {
        return Optional.ofNullable(userRepository.getOneByNaturalId(username))
          .map(user -> {
              User self = getCurrentUser().orElseThrow(IllegalStateException::new);
              self.follow(user);

              return user;
          });
    }

    @Transactional
    public Optional<User> unfollowUser(String username) {
        return Optional.ofNullable(userRepository.getOneByNaturalId(username))
          .map(user -> {
              User self = getCurrentUser().orElseThrow(IllegalStateException::new);
              self.unfollow(user);

              return user;
          });
    }

    @EventListener(UserEmailSet.class)
    @TransactionalEventListener
    public void onUserEmailSet(UserEmailSet userEmailSet) {
        SecurityContextHolder.getContext()
          .setAuthentication(
            new UsernamePasswordAuthenticationToken(
              userEmailSet.getUser(),
              null,
              Collections.emptyList()
            )
          );
    }

    private static class UserEmailSet {
        private final User user;

        UserEmailSet(User user) {
            this.user = user;
        }

        public User getUser() {
            return user;
        }
    }
}
