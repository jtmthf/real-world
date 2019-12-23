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
public class UserDtoIT {
  @Autowired
  JacksonTester<UserDto> json;

  @Test
  public void testSerialize() throws Exception {
    UserDto userDto = new UserDto(
      "jake@jake.jake",
      "jwt.token.here",
      "jake",
      "I work at statefarm",
      null
    );
    assertThat(json.write(userDto)).isEqualToJson("user.expected.json");
  }
}
