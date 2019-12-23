package com.jtmthf.realworld.article;

import com.jtmthf.realworld.repository.NaturalRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ArticleRepository extends NaturalRepository<Article, Long, String>, JooqArticleRepository {

}
