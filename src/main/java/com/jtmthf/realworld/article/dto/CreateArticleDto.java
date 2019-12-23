package com.jtmthf.realworld.article.dto;

import com.jtmthf.realworld.annotation.WrapWith;

import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.StringJoiner;

@WrapWith("article")
public class CreateArticleDto {
    @NotEmpty
    private final String title;

    @NotEmpty
    private final String description;

    @NotEmpty
    private final String body;

    private final List<String> tagList;

    public CreateArticleDto(String title, String description, String body, List<String> tagList) {
        this.title = title;
        this.description = description;
        this.body = body;
        this.tagList = tagList;
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

    @Override
    public String toString() {
        return new StringJoiner(", ", CreateArticleDto.class.getSimpleName() + "[", "]")
          .add("title='" + title + "'")
          .add("description='" + description + "'")
          .add("body='" + body + "'")
          .add("tagList=" + tagList)
          .toString();
    }
}
