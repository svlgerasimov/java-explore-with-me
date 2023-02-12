package ru.practicum.ewm.main.event.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class EventDtoInTest {

    @Autowired
    private JacksonTester<EventDtoIn> jacksonTester;

    private EventDtoIn.EventDtoInBuilder eventDtoInBuilder;

    @BeforeEach
    void setUp() {
        eventDtoInBuilder = EventDtoIn.builder()
                .annotation("Сплав на байдарках похож на полет.")
                .category(2L)
                .description("Сплав на байдарках похож на полет. На спокойной воде — это парение. " +
                        "На бурной, порожистой — выполнение фигур высшего пилотажа. " +
                        "И то, и другое дарят чувство обновления, феерические эмоции, яркие впечатления.")
                .eventDate(LocalDateTime.of(2024, 12, 31, 15, 10, 5))
                .location(
                        new LocationDto(55.754167, 37.62)
                )
                .paid(true)
                .participantLimit(10)
                .requestModeration(true)
                .title("Сплав на байдарках");
    }

    @Test
    void eventDtoInSerializationTest() throws IOException {
        assertThat(jacksonTester.write(eventDtoInBuilder.build()))
                .isStrictlyEqualToJson("EventDtoIn.json");
    }

    @Test
    void eventDtoInDeserializationTest() throws IOException {
        assertThat(jacksonTester.read("EventDtoIn.json"))
                .usingRecursiveComparison()
                .isEqualTo(eventDtoInBuilder.build());
    }

    @Test
    void eventDtoInDeserialization_whenOnlyRequiredFieldsInJson_thenDefaultValuesInDto() throws IOException {
        EventDtoIn eventDtoIn = eventDtoInBuilder
                .paid(false)
                .participantLimit(0)
                .requestModeration(true)
                .build();

        assertThat(jacksonTester.read("EventDtoIn_OnlyRequired.json"))
                .usingRecursiveComparison()
                .isEqualTo(eventDtoIn);
    }
}