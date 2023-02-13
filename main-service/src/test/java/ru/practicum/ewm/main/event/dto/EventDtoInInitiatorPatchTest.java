package ru.practicum.ewm.main.event.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class EventDtoInInitiatorPatchTest {

    @Autowired
    private JacksonTester<EventDtoInInitiatorPatch> jacksonTester;

    private final EventDtoInInitiatorPatch eventDtoInInitiatorPatch = EventDtoInInitiatorPatch.builder()
            .annotation("Сап прогулки по рекам и каналам – это возможность увидеть Практикбург с другого ракурса")
            .category(3L)
            .description("От английского SUP - Stand Up Paddle — \"стоя на доске с веслом\", " +
                    "гавайская разновидность сёрфинга, в котором серфер, стоя на доске, " +
                    "катается на волнах и при этом гребет веслом, а не руками, как в классическом серфинге.")
            .eventDate(LocalDateTime.of(2023, 10, 11, 23, 10, 5))
            .location(
                    new LocationDto(55.754167, 37.62)
            )
            .paid(true)
            .participantLimit(7)
            .requestModeration(false)
            .stateAction(EventStateAction.CANCEL_REVIEW)
            .title("Сап прогулки по рекам и каналам")
            .build();

    @Test
    void eventDtoInPatchSerializationTest() throws IOException {
        assertThat(jacksonTester.write(eventDtoInInitiatorPatch))
                .isStrictlyEqualToJson("EventDtoInPatch.json");
    }

    @Test
    void eventDtoInPatchDeserializationTest() throws IOException {
        assertThat(jacksonTester.read("EventDtoInPatch.json"))
                .usingRecursiveComparison()
                .isEqualTo(eventDtoInInitiatorPatch);
    }
}