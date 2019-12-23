package com.jtmthf.realworld.user;

import com.jtmthf.realworld.security.jwt.TokenProvider;
import com.jtmthf.realworld.user.dto.CreateUserDto;
import com.jtmthf.realworld.user.dto.UpdateUserDto;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
public class UserServiceTest {
  @Mock
  AuthenticationManagerBuilder authenticationManagerBuilder;

  @Mock
  UserRepository userRepository;

  @Mock
  PasswordEncoder passwordEncoder;

  @Mock
  TokenProvider tokenProvider;

  @Mock
  ApplicationEventPublisher eventPublisher;

  private UserService userService;

  @Before
  public void setUp() {
    userService =
      new UserService(
        authenticationManagerBuilder,
        userRepository,
        passwordEncoder,
        tokenProvider,
        eventPublisher
      );
  }

  @Test
  public void injectDependencies() {
    assertThat(userService).isNotNull();
  }

  @Test
  public void authenticate() {
    AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
    when(authenticationManagerBuilder.getObject()).thenReturn(authenticationManager);
    when(
      authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken("username@example.com", "password")
      )
    )
      .thenReturn(mock(Authentication.class));
    when(
      authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken("username@example.com", "incorrect")
      )
    )
      .thenThrow(BadCredentialsException.class);

    assertThat(userService.authenticate("username@example.com", "password")).isNotNull();
    assertThatThrownBy(() -> userService.authenticate("username@example.com", "incorrect"))
      .isInstanceOf(BadCredentialsException.class);
  }

  @Test
  public void getCurrentUserNone() {
    assertThat(userService.getCurrentUser()).isEmpty();
  }

  @Test
  @WithAnonymousUser
  public void getAnonymousUser() {
    when(userRepository.findByUsername("anonymous")).thenReturn(Optional.empty());

    assertThat(userService.getCurrentUser()).isEmpty();
  }

  @Test
  @WithMockUser
  public void getCurrentUser() {
    when(userRepository.findByUsername("user"))
      .thenReturn(Optional.of(new User("user@example.com", "user", "hash")));

    assertThat(userService.getCurrentUser()).get().extracting(User::getUsername).isEqualTo("user");
  }

  @Test
  public void createUser() {
    when(passwordEncoder.encode(anyString())).thenReturn("hash");
    when(userRepository.save(any()))
      .then(
        invocation -> {
          User userSpy = spy(invocation.<User>getArgument(0));
          when(userSpy.getId()).thenReturn(1L);

          return userSpy;
        }
      );

    User user = userService.createUser(new CreateUserDto("user", "user@example.com", "password"));

    assertThat(user.getId()).isEqualTo(1L);
    assertThat(user.getUsername()).isEqualTo("user");
    assertThat(user.getEmail()).isEqualTo("user@example.com");
    assertThat(user.getPasswordHash()).isEqualTo("hash");
    assertThat(user.getBio()).isNull();
    assertThat(user.getImage()).isNull();
  }

  @Test(expected = IllegalStateException.class)
  public void updateUserWhenNotAuthenticated() {
    userService.updateUser(
      new UpdateUserDto(
        "user@example.com",
        "user",
        "password",
        "a long bio...",
        "https://example.com/cat.jpg"
      )
    );
  }

  @Test(expected = IllegalStateException.class)
  @WithAnonymousUser
  public void updateUserWhenAnonymous() {
    userService.updateUser(
      new UpdateUserDto(
        "user@example.com",
        "user",
        "password",
        "a long bio...",
        "https://example.com/cat.jpg"
      )
    );
  }

  @Test
  @WithMockUser
  public void updateUser() {
    User user = spy(new User("user@example.com", "user", "hash"));
    when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
    when(passwordEncoder.encode(anyString())).thenReturn("hash");

    userService.updateUser(
      new UpdateUserDto(null, null, "new password", "a long bio...", "https://example.com/cat.jpg")
    );

    verify(user, times(0)).setEmail(anyString());
    verify(user, times(0)).setUsername(anyString());
    verify(user).setBio("a long bio...");
    verify(user).setImage("https://example.com/cat.jpg");
    verify(user).setPasswordHash("hash");
  }
}
