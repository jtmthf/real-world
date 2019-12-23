package com.jtmthf.realworld.article.mapper;

import com.jtmthf.realworld.article.Article;
import com.jtmthf.realworld.article.dto.ArticleDto;
import com.jtmthf.realworld.article.dto.WrappedArticleDto;
import com.jtmthf.realworld.security.SecurityUtils;
import com.jtmthf.realworld.tag.Tag;
import com.jtmthf.realworld.user.mapper.UserMapper;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class ArticleMapper {

    private final UserMapper userMapper;

    public ArticleMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public ArticleDto createArticleDto(Article article) {
        return createArticleDto(article, false);
    }

    public ArticleDto createArticleDto(Article article, boolean wrapped) {
        ArticleDto.Builder builder = wrapped ? WrappedArticleDto.builder() : ArticleDto.builder();

        return builder
          .slug(article.getSlug())
          .title(article.getTitle())
          .description(article.getDescription())
          .body(article.getBody())
          .tagList(article.getTags().stream().map(Tag::getName).collect(Collectors.toList()))
          .createdAt(article.getCreatedAt())
          .updatedAt(article.getUpdatedAt())
          .favorited(article.getFavoritedBy().stream().anyMatch(user ->
            SecurityUtils.getCurrentUserLogin().filter(login -> login.equals(user.getEmail())).isPresent()
          ))
          .favoritesCount(article.getFavoritedBy().size())
          .author(userMapper.createProfileDto(article.getAuthor(), true))
          .build();
    }
}
