package ru.practicum.ewm.main.event.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.ewm.main.event.dto.EventDtoOut;
import ru.practicum.ewm.main.event.dto.EventDtoOutShort;
import ru.practicum.ewm.stats.client.StatClient;

import javax.persistence.EntityManager;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class EventServiceImplTest {

    private final EventServiceImpl eventService;
    @MockBean
    private final StatClient statClient;
    private EntityManager em;

    @Test
    @Sql(scripts = "/sql/clear-events.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Sql(scripts = "/sql/get-events-prepare.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void findByEventIdAndInitiatorIdTest() {
        EventDtoOut eventDtoOut = eventService.findByEventIdAndInitiatorId(1L, 3L);
        assertThat(eventDtoOut).extracting(
                EventDtoOut::getId,
                dto -> dto.getInitiator().getId()
        )
                .containsExactly(1L, 3L);
    }

    @Test
    @Sql(scripts = "/sql/clear-events.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Sql(scripts = "/sql/get-events-prepare.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void findAllByInitiatorIdTest() {
        List<EventDtoOutShort> eventDtoOutShorts = eventService.findAllByInitiatorId(4L, 1, 1);
        assertThat(eventDtoOutShorts).hasSize(1);
    }
}