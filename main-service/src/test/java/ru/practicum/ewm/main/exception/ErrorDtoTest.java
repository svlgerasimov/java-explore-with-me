package ru.practicum.ewm.main.exception;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@Slf4j
class ErrorDtoTest {

    @Autowired
    private JacksonTester<ErrorDto> jacksonTester;

    private final ErrorDto errorDto = ErrorDto.builder()
            .status("BAD_REQUEST")
            .reason("Incorrectly made request.")
            .message("Failed to convert value of ")
            .timestamp(LocalDateTime.of(2022, 9, 7, 9, 10, 50))
            .build();

    @Test
    void errorDtoSerializationTest() throws IOException {
        assertThat(jacksonTester.write(errorDto))
                .isStrictlyEqualToJson("ErrorDto.json");
    }

    @Test
    void errorDtoDeserializationTest() throws IOException {
        assertThat(jacksonTester.read("ErrorDto.json"))
                .usingRecursiveComparison()
                .isEqualTo(errorDto);
    }
}