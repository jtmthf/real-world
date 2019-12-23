package com.jtmthf.realworld.repository;

import org.hibernate.NaturalIdLoadAccess;
import org.hibernate.Session;
import org.hibernate.SimpleNaturalIdLoadAccess;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.Map;
import java.util.Optional;

@Transactional(readOnly = true)
public class NaturalRepositoryImpl<T, ID extends Serializable, NID extends Serializable>
  extends SimpleJpaRepository<T, ID>
  implements NaturalRepository<T, ID, NID> {
  private final EntityManager entityManager;

  public NaturalRepositoryImpl(
    JpaEntityInformation<T, ?> entityInformation,
    EntityManager entityManager
  ) {
    super(entityInformation, entityManager);
    this.entityManager = entityManager;
  }

  @Override
  public Optional<T> findByNaturalId(NID naturalId) {
    return getSimpleNaturalIdLoadAccess().loadOptional(naturalId);
  }

  @Override
  public Optional<T> findByNaturalId(Map<String, Object> naturalIds) {
    return getNaturalIdLoadAccess(naturalIds).loadOptional();
  }

  @Override
  public T getOneByNaturalId(NID naturalId) {
    return getSimpleNaturalIdLoadAccess().getReference(naturalId);
  }

  @Override
  public T getOneByNaturalId(Map<String, Object> naturalIds) {
    return getNaturalIdLoadAccess(naturalIds).getReference();
  }

  private SimpleNaturalIdLoadAccess<T> getSimpleNaturalIdLoadAccess() {
    return entityManager.unwrap(Session.class).bySimpleNaturalId(this.getDomainClass());
  }

  private NaturalIdLoadAccess<T> getNaturalIdLoadAccess(Map<String, Object> naturalIds) {
    NaturalIdLoadAccess<T> loadAccess = entityManager.unwrap(Session.class)
      .byNaturalId(this.getDomainClass());
    naturalIds.forEach(loadAccess::using);

    return loadAccess;
  }
}
