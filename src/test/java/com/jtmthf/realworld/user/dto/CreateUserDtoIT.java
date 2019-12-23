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
public class CreateUserDtoIT {
    @Autowired
    JacksonTester<CreateUserDto> json;

    @Test
    public void testSerialize() throws Exception {
        CreateUserDto createUserDto = new CreateUserDto(
          "Jacob",
          "jake@jake.jake",
          "jakejake"
        );
        assertThat(json.write(createUserDto)).isEqualToJson("create-user.expected.json");
    }
}
