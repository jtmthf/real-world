package com.jtmthf.realworld.article.dto;

import com.jtmthf.realworld.annotation.WrapWith;
import com.jtmthf.realworld.user.dto.ProfileDto;

import java.time.Instant;
import java.util.List;

/**
 * Needed until https://github.com/FasterXML/jackson-annotations/issues/152
 */
@WrapWith("article")
public class WrappedArticleDto extends ArticleDto {
    private WrappedArticleDto(String slug, String title, String description, String body, List<String> tagList, Instant createdAt, Instant updatedAt, boolean favorited, int favoritesCount, ProfileDto author) {
        super(slug, title, description, body, tagList, createdAt, updatedAt, favorited, favoritesCount, author);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends ArticleDto.Builder<Builder> {
        @Override
        public WrappedArticleDto build() {
            return new WrappedArticleDto(
              slug,
              title,
              description,
              body,
              tagList,
              createdAt,
              updatedAt,
              favorited,
              favoritesCount,
              author
            );
        }
    }
}
