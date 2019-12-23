package com.jtmthf.realworld.article.dto;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.jtmthf.realworld.user.dto.ProfileDto;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

public class ArticleDto {
    private final String slug;
    private final String title;
    private final String description;
    private final String body;
    private final List<String> tagList;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final boolean favorited;
    private final int favoritesCount;

    @JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
    private final ProfileDto author;

    public ArticleDto(
      String slug,
      String title,
      String description,
      String body,
      List<String> tagList,
      Instant createdAt,
      Instant updatedAt,
      boolean favorited,
      int favoritesCount,
      ProfileDto author
    ) {
        this.slug = slug;
        this.title = title;
        this.description = description;
        this.body = body;
        this.tagList = tagList;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.favorited = favorited;
        this.favoritesCount = favoritesCount;
        this.author = author;
    }

    public String getSlug() {
        return slug;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getBody() {
        return body;
    }

    public List<String> getTagList() {
        return tagList;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public boolean isFavorited() {
        return favorited;
    }

    public int getFavoritesCount() {
        return favoritesCount;
    }

    public ProfileDto getAuthor() {
        return author;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder<B extends Builder<B>> {
        protected String slug;
        protected String title;
        protected String description;
        protected String body;
        protected List<String> tagList;
        protected Instant createdAt;
        protected Instant updatedAt;
        protected boolean favorited;
        protected int favoritesCount;
        protected ProfileDto author;

        protected Builder() {
        }

        public B slug(String slug) {
            this.slug = slug;
            return self();
        }

        public B title(String title) {
            this.title = title;
            return self();
        }

        public B description(String description) {
            this.description = description;
            return self();
        }

        public B body(String body) {
            this.body = body;
            return self();
        }

        public B tagList(String... tagList) {
            return tagList(Arrays.asList(tagList));
        }

        public B tagList(List<String> tagList) {
            this.tagList = tagList;
            return self();
        }

        public B createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return self();
        }

        public B updatedAt(Instant updatedAt) {
            this.updatedAt = updatedAt;
            return self();
        }

        public B favorited(boolean favorited) {
            this.favorited = favorited;
            return self();
        }

        public B favoritesCount(int favoritesCount) {
            this.favoritesCount = favoritesCount;
            return self();
        }

        public B author(ProfileDto author) {
            this.author = author;
            return self();
        }

        public ArticleDto build() {
            return new ArticleDto(
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

        @SuppressWarnings("unchecked")
        final B self() {
            return (B) this;
        }
    }
}
