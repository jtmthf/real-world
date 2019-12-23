package com.jtmthf.realworld.user;

import com.jtmthf.realworld.config.DatabaseConfiguration;
import com.jtmthf.realworld.user.dto.ProfileDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jooq.AutoConfigureJooq;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.spel.spi.EvaluationContextExtension;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@RunWith(SpringRunner.class)
@DataJpaTest(properties = "logging.level.org.jooq.tools.LoggerListener: debug")
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureJooq
@Import(DatabaseConfiguration.class)
public class UserRepositoryIT {
    private static final String EMAIL = "username@example.com";
    private static final String USERNAME = "username";
    private static final String PASSWORD_HASH = "hash";
    private static final String BIO = "a long bio...";
    private static final String IMAGE = "https://example.com/dog.jpg";

    @Autowired
    UserRepository userRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Test(expected = DataIntegrityViolationException.class)
    public void emptyFails() {
        User user = new User();

        userRepository.saveAndFlush(user);
        userRepository.flush();
    }

    @Test
    public void minimalUser() {
        User user = createUser();

        userRepository.saveAndFlush(user);
        userRepository.flush();

        assertUser(user);
    }

    @Test
    public void getByNaturalId() {
        User user = createUser();

        userRepository.saveAndFlush(user);
        entityManager.detach(user);

        assertThat(userRepository.findByNaturalId(USERNAME)).get().satisfies(this::assertUser);
    }

    @Test
    public void canSetBioAndImage() {
        User user = createUser();

        userRepository.saveAndFlush(user);

        user.setBio(BIO);
        user.setImage(IMAGE);

        userRepository.saveAndFlush(user);

        assertUser(user, true);
    }

    @Test
    public void userFollow() {
        User user1 = createUser();
        User user2 = new User("username2@example.com", "username2", "hash");

        user1.follow(user2);
        userRepository.saveAndFlush(user1);

        assertThat(user1.getFollowing()).contains(user2);
        assertThat(user2.getFollowers()).contains(user1);

        user2.follow(user1);
        userRepository.saveAndFlush(user2);

        assertThat(user2.getFollowing()).contains(user1);
        assertThat(user1.getFollowers()).contains(user2);

        user1.unfollow(user2);
        userRepository.saveAndFlush(user1);

        assertThat(user1.getFollowing()).isEmpty();
        assertThat(user2.getFollowers()).isEmpty();

        user2.unfollow(user1);
        userRepository.saveAndFlush(user2);

        assertThat(user2.getFollowing()).isEmpty();
        assertThat(user1.getFollowers()).isEmpty();
    }

    @Test
    public void userFollowReference() {
        User user1 = createUser();

        userRepository.saveAndFlush(user1);
        entityManager.clear();

        User user2 = new User("username2@example.com", "username2", "hash");
        user2.follow(userRepository.getOneByNaturalId(USERNAME));
        userRepository.saveAndFlush(user2);

        assertThat(user2.getFollowing()).contains(user1);
    }

    @Test
    public void findProfileByUsernameAnonymous() {
        User user1 = createUser();
        User user2 = new User("username2@example.com", "username2", "hash");

        user1.follow(user2);
        userRepository.saveAndFlush(user1);

        Optional<ProfileDto> profileDtoOptional = userRepository.findProfileByUsername("USERNAME2");

        assertThat(profileDtoOptional)
          .hasValueSatisfying(profileDto -> {
              assertThat(profileDto.getUsername()).isEqualTo("username2");
              assertThat(profileDto.getBio()).isNull();
              assertThat(profileDto.getImage()).isNull();
              assertThat(profileDto.isFollowing()).isFalse();
          });
    }

    @Test
    @WithMockUser(username = USERNAME)
    public void findProfileByUsername() {
        User user1 = createUser();
        User user2 = new User("username2@example.com", "username2", "hash");

        user1.follow(user2);
        userRepository.saveAndFlush(user1);

        Optional<ProfileDto> profileDtoOptional = userRepository.findProfileByUsername("USERNAME2");

        assertThat(profileDtoOptional)
          .hasValueSatisfying(profileDto -> {
              assertThat(profileDto.getUsername()).isEqualTo("username2");
              assertThat(profileDto.getBio()).isNull();
              assertThat(profileDto.getImage()).isNull();
              assertThat(profileDto.isFollowing()).isTrue();
          });
    }

    private User createUser() {
        return new User(EMAIL, USERNAME, PASSWORD_HASH);
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

    @TestConfiguration
    static class UserRepositoryITConfiguration {

        @Bean
        EvaluationContextExtension securityExtension() {
            return new SecurityEvaluationContextExtension();
        }

        private static class SecurityEvaluationContextExtension implements EvaluationContextExtension {

            @Override
            public String getExtensionId() {
                return "security";
            }

            @Override
            public SecurityExpressionRoot getRootObject() {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                return new SecurityExpressionRoot(authentication) {
                };
            }
        }
    }
}
