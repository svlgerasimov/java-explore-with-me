package ru.practicum.ewm.main.event.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import javax.persistence.EntityManager;

@SpringBootTest
//@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class EventServiceImplTest {

    @InjectMocks
    private final EventServiceImpl eventService;
//    @Mock
//    private final StatClient statClient;
    private EntityManager em;

    @Test
    @Sql(scripts = "/sql/clear-events.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Sql(scripts = "/sql/get-events-prepare.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void findByEventIdAndInitiatorIdTest() {
        eventService.findByEventIdAndInitiatorId(1L, 3L);
    }
}