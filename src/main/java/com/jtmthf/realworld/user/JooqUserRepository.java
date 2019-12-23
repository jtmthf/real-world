package com.jtmthf.realworld.user;

import com.jtmthf.realworld.user.dto.ProfileDto;

import java.util.Optional;

interface JooqUserRepository {
    Optional<ProfileDto> findProfileByUsername(String username);
}
