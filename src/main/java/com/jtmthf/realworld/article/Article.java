package com.jtmthf.realworld.article;

import com.jtmthf.realworld.comment.Comment;
import com.jtmthf.realworld.tag.Tag;
import com.jtmthf.realworld.user.User;
import org.hibernate.annotations.NaturalId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.UniqueConstraint;
import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class Article {
    @Id
    @GeneratedValue
    private Long id;

    @NaturalId(mutable = true)
    @Column(nullable = false, unique = true)
    private String slug;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false, length = Integer.MAX_VALUE)
    private String body;

    @Column(nullable = false, updatable = false)
    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @ManyToMany(mappedBy = "favorites", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<User> favoritedBy = new HashSet<>();

    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Comment> comments = new HashSet<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
      name = "article_tag",
      joinColumns = @JoinColumn(name = "article_id"),
      inverseJoinColumns = @JoinColumn(name = "tag_id"),
      uniqueConstraints = @UniqueConstraint(columnNames = {"article_id", "tag_id"})
    )
    private Set<Tag> tags = new HashSet<>();

    protected Article() {
    }

    public Article(String slug, String title, String description, String body) {
        this.slug = slug;
        this.title = title;
        this.description = description;
        this.body = body;
    }

    public void addComment(Comment comment, User author) {
        this.comments.add(comment);
        author.getComments().add(comment);
        comment.setArticle(this);
        comment.setAuthor(author);
    }

    public void removeComment(Comment comment, User author) {
        this.comments.remove(comment);
        author.getComments().remove(comment);
        comment.setArticle(null);
        comment.setAuthor(null);
    }

    public void addTags(Collection<? extends Tag> tags) {
        this.tags.addAll(tags);
        tags.forEach(tag -> tag.getArticles().add(this));
    }

    public Long getId() {
        return id;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Set<User> getFavoritedBy() {
        return favoritedBy;
    }

    public Set<Comment> getComments() {
        return comments;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Article article = (Article) o;
        return slug.equals(article.slug);
    }

    @Override
    public int hashCode() {
        return Objects.hash(slug);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Article.class.getSimpleName() + "[", "]")
          .add("id=" + id)
          .add("slug='" + slug + "'")
          .add("title='" + title + "'")
          .add("description='" + description + "'")
          .add("body='" + body + "'")
          .add("createdAt=" + createdAt)
          .add("updatedAt=" + updatedAt)
          .add("author=" + author)
          .add("favoritedBy=" + favoritedBy)
          .add("comments=" + comments)
          .add("tags=" + tags)
          .toString();
    }
}
