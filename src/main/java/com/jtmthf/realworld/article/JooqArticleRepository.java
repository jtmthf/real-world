package com.jtmthf.realworld.article;

import com.jtmthf.realworld.article.dto.ArticleDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface JooqArticleRepository {
    List<ArticleDto> findBy(String tag, String author, String favorited, Pageable pageable);

    List<ArticleDto> findByFeed(Pageable pageable);

    Optional<ArticleDto> findBySlug(String slug);
}
