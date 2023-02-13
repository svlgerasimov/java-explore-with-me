package ru.practicum.ewm.main.request.model;


import org.junit.jupiter.api.Test;
import ru.practicum.ewm.main.event.testutil.EventTestBuilder;
import ru.practicum.ewm.main.request.dto.RequestDtoOut;
import ru.practicum.ewm.main.request.dto.RequestState;
import ru.practicum.ewm.main.user.testutil.UserTestBuilder;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class RequestDtoMapperTest {

    private final RequestDtoMapper mapper = new RequestDtoMapperImpl();

    @Test
    void toDtoTest() {
        RequestEntity requestEntity = new RequestEntity();
        requestEntity.setId(1L);
        requestEntity.setRequester(UserTestBuilder.defaultBuilder().id(2L).buildUserEntity());
        requestEntity.setEvent(EventTestBuilder.defaultBuilder().id(3L).buildEventEntity());
        LocalDateTime created = LocalDateTime.of(2020, 1, 1, 0, 0, 0);
        requestEntity.setCreated(created);
        requestEntity.setStatus(RequestState.PENDING);

        RequestDtoOut requestDtoOut = mapper.toDto(requestEntity);

        assertThat(requestDtoOut)
                .extracting("id", "requester", "event", "created", "status")
                .containsExactly(1L, 2L, 3L, created, RequestState.PENDING);
    }

}