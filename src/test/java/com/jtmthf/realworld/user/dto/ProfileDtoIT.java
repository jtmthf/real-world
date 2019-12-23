package com.jtmthf.realworld.user.dto;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@JsonTest(properties = "embedded.postgresql.enabled=false")
public class ProfileDtoIT {

    @Autowired
    JacksonTester<ProfileDto> json;

    @Test
    public void testSerialize() throws Exception {
        ProfileDto profileDto = new ProfileDto(
          "jake",
          "I work at statefarm",
          "https://static.productionready.io/images/smiley-cyrus.jpg",
          false
        );
        assertThat(json.write(profileDto)).isEqualToJson("profile.expected.json");
    }
}
