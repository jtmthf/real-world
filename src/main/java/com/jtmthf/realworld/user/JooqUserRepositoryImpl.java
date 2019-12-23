package com.jtmthf.realworld.user;

import com.jtmthf.realworld.db.tables.Usr;
import com.jtmthf.realworld.security.SecurityUtils;
import com.jtmthf.realworld.user.dto.ProfileDto;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.jtmthf.realworld.db.Tables.USR;
import static com.jtmthf.realworld.db.Tables.USR_FOLLOW;
import static org.jooq.impl.DSL.field;

@Repository
@Transactional(readOnly = true)
class JooqUserRepositoryImpl implements JooqUserRepository {

    private final DSLContext dsl;

    public JooqUserRepositoryImpl(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Override
    public Optional<ProfileDto> findProfileByUsername(String username) {
        Usr profile = USR.as("profile");
        Usr self = USR.as("self");
        Field<String> usernameField = profile.USERNAME;
        Field<String> bio = profile.BIO;
        Field<String> image = profile.IMAGE;
        Condition usernameEq = profile.USERNAME.eq(username);

        return SecurityUtils.getCurrentUserLogin()
          .flatMap(login -> dsl.select(usernameField, bio, image, field(self.ID.isNotNull()))
            .from(profile
              .leftJoin(USR_FOLLOW).onKey(USR_FOLLOW.FOLLOWED_ID)
              .leftJoin(self).onKey(USR_FOLLOW.FOLLOWER_ID))
            .where(usernameEq)
            .and(self.ID.isNull().or(self.USERNAME.eq(login)))
            .fetchOptionalInto(ProfileDto.class))
          .or(() -> dsl.select(usernameField, bio, image)
            .from(profile)
            .where(usernameEq)
            .fetchOptionalInto(ProfileDto.class));
    }
}
