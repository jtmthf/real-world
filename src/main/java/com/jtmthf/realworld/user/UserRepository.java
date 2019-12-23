package com.jtmthf.realworld.user;

import com.jtmthf.realworld.repository.NaturalRepository;

import java.util.Optional;

public interface UserRepository extends NaturalRepository<User, Long, String>, JooqUserRepository {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);
}
