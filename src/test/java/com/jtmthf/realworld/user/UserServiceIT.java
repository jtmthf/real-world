package com.jtmthf.realworld.user;

import com.jtmthf.realworld.user.dto.CreateUserDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.validation.ConstraintViolationException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class UserServiceIT {
  @Autowired
  UserService userService;

  @Autowired
  EntityManager entityManager;

  @Autowired
  UserRepository userRepository;

  @AfterTransaction
  public void clearUsers() {
    userRepository.deleteAll();
  }

  @Test
  public void createUserValidation() {
    assertThatThrownBy(() -> userService.createUser(new CreateUserDto(null, null, null)))
      .isInstanceOf(ConstraintViolationException.class)
      .hasMessageContaining("username: must not be blank")
      .hasMessageContaining("email: must not be null")
      .hasMessageContaining("password: must not be null");

    assertThatThrownBy(() -> userService.createUser(new CreateUserDto("", null, null)))
      .isInstanceOf(ConstraintViolationException.class)
      .hasMessageContaining("username: must not be blank");

    assertThatThrownBy(() -> userService.createUser(new CreateUserDto(" ", null, null)))
      .isInstanceOf(ConstraintViolationException.class)
      .hasMessageContaining("username: must not be blank");

    assertThatThrownBy(
      () -> userService.createUser(new CreateUserDto("extremely_long_username", null, null))
    )
      .isInstanceOf(ConstraintViolationException.class)
      .hasMessageContaining("username: size must be between 1 and 16");

    assertThatThrownBy(() -> userService.createUser(new CreateUserDto(null, "user", null)))
      .isInstanceOf(ConstraintViolationException.class)
      .hasMessageContaining("email: must be a well-formed email address");

    assertThatThrownBy(() -> userService.createUser(new CreateUserDto(null, null, "1234")))
      .isInstanceOf(ConstraintViolationException.class)
      .hasMessageContaining("password: size must be between 8 and 72");

    User user = userService.createUser(new CreateUserDto("user", "user@example.com", "password"));
    assertThat(user.getId()).isNotNull();
    assertThat(user.getUsername()).isEqualTo("user");
    assertThat(user.getEmail()).isEqualTo("user@example.com");
    assertThat(user.getPasswordHash()).isNotBlank();
    assertThat(user.getBio()).isNull();
    assertThat(user.getImage()).isNull();
  }

  @Test
  public void duplicateUsername() {
    userService.createUser(new CreateUserDto("user", "user1@example.com", "password"));

    assertThatThrownBy(
      () -> {
        userService.createUser(new CreateUserDto("user", "user2@example.com", "password"));
        entityManager.flush();
      }
    )
      .isInstanceOf(PersistenceException.class)
      .hasStackTraceContaining("duplicate key value violates unique constraint");
  }

  @Test
  public void duplicateUsernameCaseInsensitive() {
    userService.createUser(new CreateUserDto("user", "user1@example.com", "password"));

    assertThatThrownBy(
      () -> {
        userService.createUser(new CreateUserDto("USER", "user2@example.com", "password"));
        entityManager.flush();
      }
    )
      .isInstanceOf(PersistenceException.class)
      .hasStackTraceContaining("duplicate key value violates unique constraint");
  }

  @Test
  public void duplicateEmail() {
    userService.createUser(new CreateUserDto("user1", "user@example.com", "password"));

    assertThatThrownBy(
      () -> {
        userService.createUser(new CreateUserDto("user2", "user@example.com", "password"));
        entityManager.flush();
      }
    )
      .isInstanceOf(PersistenceException.class)
      .hasStackTraceContaining("duplicate key value violates unique constraint");
  }

  @Test
  public void duplicateEmailCaseInsensitive() {
    userService.createUser(new CreateUserDto("user1", "user@example.com", "password"));

    assertThatThrownBy(
      () -> {
        userService.createUser(new CreateUserDto("user2", "USER@EXAMPLE.COM", "password"));
        entityManager.flush();
      }
    )
      .isInstanceOf(PersistenceException.class)
      .hasStackTraceContaining("duplicate key value violates unique constraint");
  }

  @Test
  public void createUserThenGetToken() {
    User user = userService.createUser(new CreateUserDto("user", "user@example.com", "password"));
    String token = userService.getToken(user);

    TestTransaction.flagForCommit();
    TestTransaction.end();

    assertThat(token).isNotEmpty();
    assertThat(SecurityContextHolder.getContext().getAuthentication())
      .isInstanceOf(UsernamePasswordAuthenticationToken.class)
      .extracting(Authentication::getName)
      .isEqualTo("user");
  }

  @Test
  public void loginUserThenGetToken() {
    userService.createUser(new CreateUserDto("user", "user@example.com", "password"));

    TestTransaction.flagForCommit();
    TestTransaction.end();
    SecurityContextHolder.getContext().setAuthentication(null);

    userService.authenticate("user@example.com", "password");
    String token = userService.getToken();

    assertThat(token).isNotEmpty();
    assertThat(SecurityContextHolder.getContext().getAuthentication())
      .isInstanceOf(UsernamePasswordAuthenticationToken.class)
      .extracting(Authentication::getName)
      .isEqualTo("user");
  }

  @Test
  public void getCurrentUser() {
    userService.createUser(new CreateUserDto("user", "user@example.com", "password"));

    TestTransaction.flagForCommit();
    TestTransaction.end();
    SecurityContextHolder.getContext().setAuthentication(null);

    userService.authenticate("user@example.com", "password");

    assertThat(userService.getCurrentUser()).get().extracting(User::getUsername).isEqualTo("user");
  }
}
