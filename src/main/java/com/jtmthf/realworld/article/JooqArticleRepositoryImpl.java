package com.jtmthf.realworld.article;

import com.jtmthf.realworld.article.dto.ArticleDto;
import com.jtmthf.realworld.db.tables.Usr;
import com.jtmthf.realworld.security.SecurityUtils;
import com.jtmthf.realworld.user.dto.ProfileDto;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record13;
import org.jooq.TableOnConditionStep;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.jtmthf.realworld.db.Tables.ARTICLE;
import static com.jtmthf.realworld.db.Tables.ARTICLE_TAG;
import static com.jtmthf.realworld.db.Tables.TAG;
import static com.jtmthf.realworld.db.Tables.USR;
import static com.jtmthf.realworld.db.Tables.USR_FAVORITE;
import static com.jtmthf.realworld.db.Tables.USR_FOLLOW;
import static org.jooq.impl.DSL.arrayAgg;
import static org.jooq.impl.DSL.boolOr;
import static org.jooq.impl.DSL.count;
import static org.jooq.impl.DSL.inline;
import static org.jooq.impl.DSL.select;
import static org.jooq.impl.DSL.trueCondition;
import static org.jooq.util.postgres.PostgresDSL.array;

@Repository
class JooqArticleRepositoryImpl implements JooqArticleRepository {
    private static final Usr AUTHOR = USR.as("author");
    private static final Usr USR_FAVORITED = USR.as("usr_favorited");

    private final DSLContext dsl;

    public JooqArticleRepositoryImpl(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Override
    public List<ArticleDto> findBy(
      String tag,
      String authorName,
      String favorited,
      Pageable pageable
    ) {
        Optional<Long> userId = SecurityUtils.getCurrentUserId();

        Condition condition = trueCondition();
        if (tag != null) {
            condition = condition.and(TAG.NAME.eq(tag));
        }
        if (authorName != null) {
            condition = condition.and(AUTHOR.USERNAME.eq(authorName));
        }
        if (favorited != null) {
            condition = condition.and(USR_FAVORITED.USERNAME.eq(favorited));
        }

        return dsl.select(
          ARTICLE.SLUG,
          ARTICLE.TITLE,
          ARTICLE.DESCRIPTION,
          ARTICLE.BODY,
          ARTICLE.CREATED_AT,
          ARTICLE.UPDATED_AT,
          array(select(TAG.NAME)
            .from(ARTICLE_TAG.join(TAG).onKey())
            .where(ARTICLE_TAG.ARTICLE_ID.eq(ARTICLE.ID))
          ).as("tag_list"),
          userId.map(id -> boolOr(USR_FAVORITE.USR_ID.eq(id)).as("favorited"))
            .orElse(inline(false).as("favorited")),
          count(USR_FAVORITE).as("favorites_count"),
          AUTHOR.USERNAME,
          AUTHOR.BIO,
          AUTHOR.IMAGE,
          userId.map(id -> boolOr(USR_FOLLOW.FOLLOWER_ID.eq(id)).as("author_following"))
            .orElse(inline(false).as("author_following"))
        )
          .from(applyJoins())
          .where(condition)
          .groupBy(ARTICLE.ID, AUTHOR.ID)
          .orderBy(ARTICLE.CREATED_AT.desc())
          .limit(pageable.getPageSize())
          .offset((int) pageable.getOffset())
          .fetchStream()
          .map(this::buildFromRecord)
          .collect(Collectors.toList());
    }

    @Override
    public List<ArticleDto> findByFeed(Pageable pageable) {
        Long userId = SecurityUtils.getCurrentUserId().orElseThrow(IllegalStateException::new);

        return dsl.select(
          ARTICLE.SLUG,
          ARTICLE.TITLE,
          ARTICLE.DESCRIPTION,
          ARTICLE.BODY,
          ARTICLE.CREATED_AT,
          ARTICLE.UPDATED_AT,
          arrayAgg(TAG.NAME).as("tag_list"),
          boolOr(USR_FAVORITE.USR_ID.eq(userId)).as("favorited"),
          count(USR_FAVORITE).as("favorites_count"),
          AUTHOR.USERNAME,
          AUTHOR.BIO,
          AUTHOR.IMAGE,
          boolOr(USR_FOLLOW.FOLLOWER_ID.eq(userId)).as("author_following")
        ).from(
          ARTICLE.join(AUTHOR).onKey(ARTICLE.AUTHOR_ID)
            .join(USR_FOLLOW).on(ARTICLE.AUTHOR_ID.eq(USR_FOLLOW.FOLLOWED_ID))
            .leftJoin(ARTICLE_TAG).onKey(ARTICLE_TAG.ARTICLE_ID)
            .leftJoin(TAG).onKey()
        ).where(USR_FOLLOW.FOLLOWER_ID.eq(userId))
          .groupBy(ARTICLE.ID, AUTHOR.ID)
          .orderBy(ARTICLE.CREATED_AT.desc())
          .limit(pageable.getPageSize())
          .offset((int) pageable.getOffset())
          .fetchStream()
          .map(this::buildFromRecord)
          .collect(Collectors.toList());

    }

    @Override
    public Optional<ArticleDto> findBySlug(String slug) {
        Optional<Long> userId = SecurityUtils.getCurrentUserId();

        return dsl.select(
          ARTICLE.SLUG,
          ARTICLE.TITLE,
          ARTICLE.DESCRIPTION,
          ARTICLE.BODY,
          ARTICLE.CREATED_AT,
          ARTICLE.UPDATED_AT,
          arrayAgg(TAG.NAME).as("tag_list"),
          userId.map(id -> boolOr(USR_FAVORITE.USR_ID.eq(id)).as("favorited"))
            .orElse(inline(false).as("favorited")),
          count(USR_FAVORITE).as("favorites_count"),
          AUTHOR.USERNAME,
          AUTHOR.BIO,
          AUTHOR.IMAGE,
          userId.map(id -> boolOr(USR_FOLLOW.FOLLOWER_ID.eq(id)).as("author_following"))
            .orElse(inline(false).as("author_following"))
        )
          .from(applyJoins())
          .where(ARTICLE.SLUG.eq(slug))
          .groupBy(ARTICLE.ID, AUTHOR.ID)
          .fetchOptional()
          .map(this::buildFromRecord);
    }

    private TableOnConditionStep<Record> applyJoins() {
        return ARTICLE.join(AUTHOR).onKey(ARTICLE.AUTHOR_ID)
          .leftJoin(ARTICLE_TAG).onKey(ARTICLE_TAG.ARTICLE_ID)
          .leftJoin(TAG).onKey()
          .leftJoin(USR_FAVORITE).onKey(USR_FAVORITE.ARTICLE_ID)
          .leftJoin(USR_FAVORITED).on(USR_FAVORITE.USR_ID.eq(USR_FAVORITED.ID));
    }

    private ArticleDto buildFromRecord(Record13<String, String, String, String, Timestamp, Timestamp, String[], Boolean, Integer, String, String, String, Boolean> record) {
        return ArticleDto.builder()
          .slug(record.get(ARTICLE.SLUG))
          .title(record.get(ARTICLE.TITLE))
          .description(record.get(ARTICLE.DESCRIPTION))
          .body(record.get(ARTICLE.BODY))
          .tagList(record.get("tag_list", String[].class))
          .createdAt(record.get(ARTICLE.CREATED_AT).toInstant())
          .updatedAt(record.get(ARTICLE.UPDATED_AT).toInstant())
          .favorited(record.get("favorited", Boolean.class))
          .favoritesCount(record.get("favorites_count", Integer.class))
          .author(
            new ProfileDto(
              record.get(AUTHOR.USERNAME),
              record.get(AUTHOR.BIO),
              record.get(AUTHOR.IMAGE),
              record.get("author_following", Boolean.class)
            )
          )
          .build();
    }
}
