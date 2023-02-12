package ru.practicum.ewm.main.user.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UserDtoOutShortTest {

    @Autowired
    private JacksonTester<UserDtoOutShort> jacksonTester;

    private final UserDtoOutShort userDtoOutShort = UserDtoOutShort.builder()
            .id(1L)
            .name("User Name")
            .build();

    @Test
    void userDtoOutShortSerializationTest() throws IOException {
        assertThat(jacksonTester.write(userDtoOutShort))
                .isStrictlyEqualToJson("UserDtoOutShort.json");
    }

    @Test
    void userDtoOutShortDeserializationTest() throws IOException {
        assertThat(jacksonTester.read("UserDtoOutShort.json"))
                .usingRecursiveComparison()
                .isEqualTo(userDtoOutShort);
    }
}