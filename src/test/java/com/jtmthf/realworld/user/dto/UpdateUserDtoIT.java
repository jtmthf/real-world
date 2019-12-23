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
public class UpdateUserDtoIT {

    @Autowired
    JacksonTester<UpdateUserDto> json;

    @Test
    public void testSerialize() throws Exception {
        UpdateUserDto updateUserDto = new UpdateUserDto(
          "jake@jake.jake",
          null,
          null,
          "I like to skateboard",
          "https://i.stack.imgur.com/xHWG8.jpg"
        );
        assertThat(json.write(updateUserDto)).isEqualToJson("update-user.expected.json");
    }
}
