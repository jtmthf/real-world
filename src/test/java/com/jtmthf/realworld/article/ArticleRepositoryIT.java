package com.jtmthf.realworld.article;

import com.jtmthf.realworld.article.dto.ArticleDto;
import com.jtmthf.realworld.config.DatabaseConfiguration;
import com.jtmthf.realworld.tag.Tag;
import com.jtmthf.realworld.user.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jooq.AutoConfigureJooq;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest(properties = "logging.level.org.jooq.tools.LoggerListener: debug")
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureJooq
@Import(DatabaseConfiguration.class)
public class ArticleRepositoryIT {

    @Autowired
    ArticleRepository articleRepository;

    @Test
    public void basic() {
        articleRepository.findBy(null, null, null, PageRequest.of(0, 20));
    }

    @Test
    public void findByAll() {
        Article article = new Article(
          "how-to-train-your-dragon",
          "How to train your dragon",
          "Ever wonder how?",
          "It takes a Jacobian"
        );
        article.addTags(List.of(new Tag("dragons"), new Tag("training")));
        User author = new User("user@example.com", "user", "hash");
        author.addArticle(article);

        Article persistedArticle = articleRepository.saveAndFlush(article);
        List<ArticleDto> articleDtos = articleRepository.findBy(null, null, null, PageRequest.of(0, 20));

        assertThat(articleDtos).hasOnlyOneElementSatisfying(articleDto -> {
            assertThat(articleDto.getSlug()).isEqualTo("how-to-train-your-dragon");
            assertThat(articleDto.getTitle()).isEqualTo("How to train your dragon");
            assertThat(articleDto.getDescription()).isEqualTo("Ever wonder how?");
            assertThat(articleDto.getBody()).isEqualTo("It takes a Jacobian");
            assertThat(articleDto.getCreatedAt()).isEqualTo(persistedArticle.getCreatedAt());
            assertThat(articleDto.getUpdatedAt()).isEqualTo(persistedArticle.getUpdatedAt());
            assertThat(articleDto.getTagList()).containsExactlyInAnyOrder("dragons", "training");
            assertThat(articleDto.isFavorited()).isFalse();
            assertThat(articleDto.getFavoritesCount()).isZero();
            assertThat(articleDto.getAuthor().getUsername()).isEqualTo("user");
            assertThat(articleDto.getAuthor().getBio()).isNull();
            assertThat(articleDto.getAuthor().getImage()).isNull();
            assertThat(articleDto.getAuthor().isFollowing()).isFalse();
        });
    }

    @Test
    public void findByTag() {
        Article article1 = new Article(
          "how-to-train-your-dragon",
          "How to train your dragon",
          "Ever wonder how?",
          "It takes a Jacobian"
        );
        Article article2 = new Article(
          "how-to-train-your-dragon-2",
          "How to train your dragon 2",
          "So toothless",
          "It a dragon"
        );
        Tag dragons = new Tag("dragons");
        Tag training = new Tag("training");
        Tag sequel = new Tag("sequel");
        article1.addTags(List.of(dragons, training));
        article2.addTags(List.of(dragons, training, sequel));
        User author = new User("user@example.com", "user", "hash");
        author.addArticle(article1);
        author.addArticle(article2);

        articleRepository.saveAll(List.of(article1, article2));
        articleRepository.flush();
        List<ArticleDto> articleDtos = articleRepository.findBy("sequel", null, null, PageRequest.of(0, 20));

        assertThat(articleDtos).hasOnlyOneElementSatisfying(articleDto -> {
            assertThat(articleDto.getSlug()).isEqualTo("how-to-train-your-dragon-2");
            assertThat(articleDto.getTitle()).isEqualTo("How to train your dragon 2");
            assertThat(articleDto.getDescription()).isEqualTo("So toothless");
            assertThat(articleDto.getBody()).isEqualTo("It a dragon");
            assertThat(articleDto.getTagList()).containsExactlyInAnyOrder("dragons", "training", "sequel");
            assertThat(articleDto.isFavorited()).isFalse();
            assertThat(articleDto.getFavoritesCount()).isZero();
            assertThat(articleDto.getAuthor().getUsername()).isEqualTo("user");
            assertThat(articleDto.getAuthor().getBio()).isNull();
            assertThat(articleDto.getAuthor().getImage()).isNull();
            assertThat(articleDto.getAuthor().isFollowing()).isFalse();
        });
    }
}