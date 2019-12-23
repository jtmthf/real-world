package com.jtmthf.realworld.user;

import com.jtmthf.realworld.article.Article;
import com.jtmthf.realworld.comment.Comment;
import org.hibernate.annotations.NaturalId;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;

@Entity
@Table(name = "usr")
public class User implements UserDetails {

  @Id
  @GeneratedValue
  private Long id;

  @Email
  @Column(nullable = false, unique = true)
  private String email;

  @Size(min = 1, max = 16)
  @Column(nullable = false, unique = true, length = 16)
  @NaturalId(mutable = true)
  private String username;

  @Column(nullable = false)
  private String passwordHash;

  @Size(max = 512)
  @Column(length = 512)
  private String bio;

  @Size(max = 2048)
  @Column(length = 2048)
  private String image;

  @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
  @JoinTable(
    name = "usr_follow",
    joinColumns = @JoinColumn(name = "follower_id", nullable = false),
    inverseJoinColumns = @JoinColumn(name = "followed_id", nullable = false),
    uniqueConstraints = @UniqueConstraint(columnNames = { "follower_id", "followed_id" })
  )
  private Set<User> following = new HashSet<>();

  @ManyToMany(mappedBy = "following")
  private Set<User> followers = new HashSet<>();

  @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
  @JoinTable(
    name = "usr_favorite",
    joinColumns = @JoinColumn(name = "usr_id"),
    inverseJoinColumns = @JoinColumn(name = "article_id"),
    uniqueConstraints = @UniqueConstraint(columnNames = { "usr_id", "article_id" })
  )
  private Set<Article> favorites = new HashSet<>();

  @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<Article> articles = new HashSet<>();

  @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<Comment> comments = new HashSet<>();

  protected User() {}

  public User(String email, String username, String passwordHash) {
    this.email = email;
    this.username = username;
    this.passwordHash = passwordHash;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Collections.emptyList();
  }

  @Override
  public String getPassword() {
    return getPasswordHash();
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  public void follow(User user) {
    following.add(user);
    user.followers.add(this);
  }

  public void unfollow(User user) {
    following.remove(user);
    user.followers.remove(this);
  }

  public void addArticle(Article article) {
    this.articles.add(article);
    article.setAuthor(this);
  }

  public void removeArticle(Article article) {
    this.articles.remove(article);
    article.setAuthor(null);
  }

  public void favorite(Article article) {
    this.favorites.add(article);
    article.getFavoritedBy().add(this);
  }

  public void unfavorite(Article article) {
    this.favorites.remove(article);
    article.getFavoritedBy().remove(this);
  }

  public Long getId() {
    return id;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  @Override
  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPasswordHash() {
    return passwordHash;
  }

  public void setPasswordHash(String passwordHash) {
    this.passwordHash = passwordHash;
  }

  public String getBio() {
    return bio;
  }

  public void setBio(String bio) {
    this.bio = bio;
  }

  public String getImage() {
    return image;
  }

  public void setImage(String image) {
    this.image = image;
  }

  public Set<User> getFollowing() {
    return following;
  }

  public Set<User> getFollowers() {
    return followers;
  }

  public Set<Article> getArticles() {
    return articles;
  }

  public Set<Article> getFavorites() {
    return favorites;
  }

  public Set<Comment> getComments() {
    return comments;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof User)) return false;
    User user = (User) o;
    return getUsername().equals(user.getUsername());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getUsername());
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", User.class.getSimpleName() + "[", "]")
      .add("id=" + id)
      .add("email='" + email + "'")
      .add("username='" + username + "'")
      .add("passwordHash='" + passwordHash + "'")
      .add("bio='" + bio + "'")
      .add("image='" + image + "'")
      .toString();
  }

  public static User fromClaims(String email, Long id) {
    User user = new User();
    user.id = id;
    user.email = email;

    return user;
  }
}
