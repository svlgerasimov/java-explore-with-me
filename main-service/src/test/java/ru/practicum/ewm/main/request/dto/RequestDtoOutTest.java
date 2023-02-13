package ru.practicum.ewm.main.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class RequestDtoOutTest {

    @Autowired
    private JacksonTester<RequestDtoOut> jacksonTester;

    private final RequestDtoOut requestDtoOut = RequestDtoOut.builder()
            .created(LocalDateTime.of(2022, 9, 6,
                    21, 10, 5, 432000000))
            .event(1L)
            .id(3L)
            .requester(2L)
            .status(RequestState.PENDING)
            .build();

    @Test
    void requestDtoOutSerializationTest() throws IOException {
        assertThat(jacksonTester.write(requestDtoOut))
                .isStrictlyEqualToJson("RequestDtoOut.json");
    }

    @Test
    void requestDtoOutDeserializationTest() throws IOException {
        assertThat(jacksonTester.read("RequestDtoOut.json"))
                .usingRecursiveComparison()
                .isEqualTo(requestDtoOut);
    }

}