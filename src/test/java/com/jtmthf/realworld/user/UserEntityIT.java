package com.jtmthf.realworld.user;

import com.jtmthf.realworld.config.DatabaseConfiguration;
import org.hibernate.Session;
import org.hibernate.SimpleNaturalIdLoadAccess;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jooq.AutoConfigureJooq;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import javax.persistence.PersistenceException;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureJooq
@Import(DatabaseConfiguration.class)
public class UserEntityIT {
  private static final String EMAIL = "username@example.com";
  private static final String USERNAME = "username";
  private static final String PASSWORD_HASH = "hash";
  private static final String BIO = "a long bio...";
  private static final String IMAGE = "https://example.com/dog.jpg";

  @Autowired
  TestEntityManager entityManager;

  @Test(expected = PersistenceException.class)
  public void emptyFails() {
    User user = new User();

    entityManager.persistAndFlush(user);
  }

  @Test
  public void minimalUser() {
    User user = createUser();

    user = entityManager.persistFlushFind(user);

    assertUser(user);
  }

  @Test
  public void getByNaturalId() {
    User user = createUser();

    entityManager.persistAndFlush(user);
    entityManager.clear();
    user = getNaturalIdLoadAccess().load(USERNAME);

    assertUser(user);
  }

  @Test
  public void canSetBioAndImage() {
    User user = createUser();

    entityManager.persistAndFlush(user);

    user.setBio(BIO);
    user.setImage(IMAGE);
    entityManager.persistAndFlush(user);

    assertUser(user, true);
  }

  @Test
  public void userFollow() {
    User user1 = createUser();
    User user2 = new User("username2@example.com", "username2", "hash");

    user1.follow(user2);
    entityManager.persistAndFlush(user1);

    assertThat(user1.getFollowing()).contains(user2);
    assertThat(user2.getFollowers()).contains(user1);

    user2.follow(user1);
    entityManager.persistAndFlush(user2);

    assertThat(user2.getFollowing()).contains(user1);
    assertThat(user1.getFollowers()).contains(user2);

    user1.unfollow(user2);
    entityManager.persistAndFlush(user1);

    assertThat(user1.getFollowing()).isEmpty();
    assertThat(user2.getFollowers()).isEmpty();

    user2.unfollow(user1);
    entityManager.persistAndFlush(user2);

    assertThat(user2.getFollowing()).isEmpty();
    assertThat(user1.getFollowers()).isEmpty();
  }

  @Test
  public void userFollowReference() {
    User user1 = createUser();

    entityManager.persistAndFlush(user1);
    entityManager.clear();

    User user2 = new User("username2@example.com", "username2", "hash");
    user2.follow(getNaturalIdLoadAccess().getReference(USERNAME));
    entityManager.persistAndFlush(user2);

    assertThat(user2.getFollowing()).contains(user1);
  }

  private User createUser() {
    return new User(EMAIL, USERNAME, PASSWORD_HASH);
  }

  private SimpleNaturalIdLoadAccess<User> getNaturalIdLoadAccess() {
    return entityManager.getEntityManager().unwrap(Session.class).bySimpleNaturalId(User.class);
  }

  private void assertUser(User user) {
    assertUser(user, false);
  }

  private void assertUser(User user, boolean withOptionalFields) {
    assertThat(user.getId()).isNotNull();
    assertThat(user.getEmail()).isEqualTo(EMAIL);
    assertThat(user.getUsername()).isEqualTo(USERNAME);
    assertThat(user.getPasswordHash()).isEqualTo(PASSWORD_HASH);
    if (withOptionalFields) {
      assertThat(user.getBio()).isEqualTo(BIO);
      assertThat(user.getImage()).isEqualTo(IMAGE);
    } else {
      assertThat(user.getBio()).isNull();
      assertThat(user.getImage()).isNull();
    }
  }
}
