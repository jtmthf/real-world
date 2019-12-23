package com.jtmthf.realworld.article;

import com.github.slugify.Slugify;
import com.jtmthf.realworld.article.dto.ArticleDto;
import com.jtmthf.realworld.article.dto.CreateArticleDto;
import com.jtmthf.realworld.article.dto.WrappedArticleDto;
import com.jtmthf.realworld.security.SecurityUtils;
import com.jtmthf.realworld.tag.Tag;
import com.jtmthf.realworld.tag.TagService;
import com.jtmthf.realworld.user.User;
import com.jtmthf.realworld.user.UserService;
import com.jtmthf.realworld.user.dto.ProfileDto;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final TagService tagService;
    private final UserService userService;
    private final Slugify slg = new Slugify();

    public ArticleService(ArticleRepository articleRepository, TagService tagService, UserService userService) {
        this.articleRepository = articleRepository;
        this.tagService = tagService;
        this.userService = userService;
    }

    public List<ArticleDto> listArticles(String tag, String author, String favorited, Pageable pageable) {
        Comparator
        return articleRepository.findBy(tag, author, favorited, pageable);
    }

    @Transactional
    public ArticleDto createArticle(CreateArticleDto createArticleDto) {
        ProfileDto author = SecurityUtils.getCurrentUserLogin()
          .flatMap(userService::getProfileByUsername)
          .orElseThrow(IllegalStateException::new);

        Article article = new Article(
          slg.slugify(createArticleDto.getTitle()),
          createArticleDto.getTitle(),
          createArticleDto.getDescription(),
          createArticleDto.getBody()
        );

        article.addTags(tagService.createTags(createArticleDto.getTagList()));
        article.setAuthor(userService.getUserReferenceByUsername(author.getUsername()));

        article = articleRepository.save(article);

        return WrappedArticleDto.builder()
          .slug(article.getSlug())
          .title(article.getTitle())
          .description(article.getDescription())
          .body(article.getBody())
          .tagList(article.getTags().stream().map(Tag::getName).collect(Collectors.toList()))
          .favorited(false)
          .favoritesCount(0)
          .author(author)
          .build();
    }
}
