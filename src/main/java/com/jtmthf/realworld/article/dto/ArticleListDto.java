package com.jtmthf.realworld.article.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;
import java.util.List;

public class ArticleListDto {
    private final List<ArticleDto> articles;

    public ArticleListDto(ArticleDto... articles) {
        this(Arrays.asList(articles));
    }

    public ArticleListDto(List<ArticleDto> articles) {
        this.articles = articles;
    }

    public List<ArticleDto> getArticles() {
        return articles;
    }

    @JsonProperty
    private int articlesCount() {
        return articles.size();
    }
}
