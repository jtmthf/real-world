package com.jtmthf.realworld.article.dto;

import com.jtmthf.realworld.user.dto.ProfileDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@JsonTest(properties = "embedded.postgresql.enabled=false")
public class ArticleListDtoIT {

    @Autowired
    JacksonTester<ArticleListDto> json;

    @Test
    public void testSerialize() throws Exception {
        ArticleDto.Builder builder = ArticleDto.builder()
          .tagList("dragons", "training")
          .createdAt(Instant.parse("2016-02-18T03:22:56.637Z"))
          .updatedAt(Instant.parse("2016-02-18T03:48:35.824Z"))
          .favorited(false)
          .favoritesCount(0)
          .author(new ProfileDto(
            "jake",
            "I work at statefarm",
            "https://i.stack.imgur.com/xHWG8.jpg",
            false
          ));
        ArticleListDto articleListDto = new ArticleListDto(
          builder
            .slug("how-to-train-your-dragon")
            .title("How to train your dragon")
            .description("Ever wonder how?")
            .body("It takes a Jacobian")
            .build(),
          builder
            .slug("how-to-train-your-dragon-2")
            .title("How to train your dragon 2")
            .description("So toothless")
            .body("It a dragon")
            .build()
        );

        assertThat(json.write(articleListDto)).isEqualToJson("article-list.expected.json");
    }
}
