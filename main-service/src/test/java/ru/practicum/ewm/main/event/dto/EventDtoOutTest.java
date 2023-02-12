package ru.practicum.ewm.main.event.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.ewm.main.category.dto.CategoryDtoOut;
import ru.practicum.ewm.main.user.dto.UserDtoOutShort;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class EventDtoOutTest {

    @Autowired
    private JacksonTester<EventDtoOut> jacksonTester;

    private final EventDtoOut eventDtoOut = EventDtoOut.builder()
            .annotation("Эксклюзивность нашего шоу гарантирует привлечение максимальной зрительской аудитории")
            .category(
                    CategoryDtoOut.builder()
                            .id(1L)
                            .name("Концерты")
                            .build()
            )
            .confirmedRequests(5)
            .createdOn(LocalDateTime.of(2022, 9, 6, 11, 0, 23))
            .description("Что получится, если соединить кукурузу и полёт? " +
                    "Создатели \"Шоу летающей кукурузы\" испытали эту идею на практике " +
                    "и воплотили в жизнь инновационный проект, предлагающий свежий взгляд на развлечения...")
            .eventDate(LocalDateTime.of(2024, 12, 31, 15, 10, 5))
            .id(1L)
            .initiator(
                    UserDtoOutShort.builder()
                            .id(3L)
                            .name("Фёдоров Матвей")
                            .build()
            )
            .location(
                    new LocationDto(55.754167, 37.62)
            )
            .paid(true)
            .participantLimit(10)
            .publishedOn(LocalDateTime.of(2022, 9, 6, 15, 10, 5))
            .requestModeration(true)
            .state(EventState.PUBLISHED)
            .title("Знаменитое шоу 'Летающая кукуруза'")
            .views(999L)
            .build();

    @Test
    void eventDtoOutSerializationTest() throws IOException {
        assertThat(jacksonTester.write(eventDtoOut))
                .isStrictlyEqualToJson("EventDtoOut.json");
    }

    @Test
    void eventDtoOutDeserializationTest() throws IOException {
        assertThat(jacksonTester.read("EventDtoOut.json"))
                .usingRecursiveComparison()
                .isEqualTo(eventDtoOut);
    }
}