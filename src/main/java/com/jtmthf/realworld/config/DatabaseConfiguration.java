package com.jtmthf.realworld.config;

import com.jtmthf.realworld.repository.NaturalRepositoryImpl;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(
  basePackages = "com.jtmthf.realworld",
  repositoryBaseClass = NaturalRepositoryImpl.class
)
@EnableJpaAuditing
public class DatabaseConfiguration {}
