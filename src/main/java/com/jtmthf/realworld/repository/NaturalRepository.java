package com.jtmthf.realworld.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import java.io.Serializable;
import java.util.Map;
import java.util.Optional;

@NoRepositoryBean
public interface NaturalRepository<T, ID extends Serializable, NID extends Serializable>
  extends JpaRepository<T, ID> {
  Optional<T> findByNaturalId(NID naturalId);

  Optional<T> findByNaturalId(Map<String, Object> naturalIds);

  T getOneByNaturalId(NID naturalId);

  T getOneByNaturalId(Map<String, Object> naturalIds);
}
