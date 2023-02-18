package ru.practicum.ewm.main.user.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UserDtoOutTest {

    @Autowired
    private JacksonTester<UserDtoOut> jacksonTester;

    private final UserDtoOut userDtoOut = UserDtoOut.builder()
            .id(1L)
            .email("mail@mail.com")
            .name("User Name")
            .build();

    @Test
    void userDtoOutSerializationTest() throws IOException {
        assertThat(jacksonTester.write(userDtoOut))
                .isStrictlyEqualToJson("UserDtoOut.json");
    }

    @Test
    void userDtoOutDeserializationTest() throws IOException {
        assertThat(jacksonTester.read("UserDtoOut.json"))
                .usingRecursiveComparison()
                .isEqualTo(userDtoOut);
    }
}