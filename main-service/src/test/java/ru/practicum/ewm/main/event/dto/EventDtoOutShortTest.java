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
class EventDtoOutShortTest {

    @Autowired
    private JacksonTester<EventDtoOutShort> jacksonTester;

    private final EventDtoOutShort eventDtoOutShort = EventDtoOutShort.builder()
            .annotation("Эксклюзивность нашего шоу гарантирует привлечение максимальной зрительской аудитории")
            .category(
                    CategoryDtoOut.builder()
                            .id(1L)
                            .name("Концерты")
                            .build()
            )
            .confirmedRequests(5)
            .eventDate(LocalDateTime.of(2024, 3, 10, 14, 30, 0))
            .id(1L)
            .initiator(
                    UserDtoOutShort.builder()
                            .id(3L)
                            .name("Фёдоров Матвей")
                            .build()
            )
            .paid(true)
            .title("Знаменитое шоу 'Летающая кукуруза'")
            .views(999L)
            .build();

    @Test
    void eventDtoOutShortSerializationTest() throws IOException {
        assertThat(jacksonTester.write(eventDtoOutShort))
                .isStrictlyEqualToJson("EventDtoOutShort.json");
    }

    @Test
    void eventDtoOutDeserializationTest() throws IOException {
        assertThat(jacksonTester.read("EventDtoOutShort.json"))
                .usingRecursiveComparison()
                .isEqualTo(eventDtoOutShort);
    }

}