package com.jtmthf.realworld.tag;

import com.jtmthf.realworld.article.Article;
import org.hibernate.annotations.NaturalId;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;

@Entity
public class Tag {
  @Id
  @GeneratedValue
  private Long id;

  @Column(nullable = false, updatable = false, unique = true)
  @NaturalId
  private String name;

  @ManyToMany(mappedBy = "tags")
  private Set<Article> articles = new HashSet<>();

  protected Tag() {}

  public Tag(String name) {
    this.name = name;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public Set<Article> getArticles() {
    return articles;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Tag tag = (Tag) o;
    return name.equals(tag.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", Tag.class.getSimpleName() + "[", "]")
      .add("id=" + id)
      .add("name='" + name + "'")
      .add("articles=" + articles)
      .toString();
  }
}
