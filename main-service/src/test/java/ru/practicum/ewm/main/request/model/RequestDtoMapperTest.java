package ru.practicum.ewm.main.request.model;


import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;
import ru.practicum.ewm.main.event.testutil.EventTestBuilder;
import ru.practicum.ewm.main.request.dto.RequestDtoOut;
import ru.practicum.ewm.main.request.dto.RequestStatus;
import ru.practicum.ewm.main.user.testutil.UserTestBuilder;

import java.time.LocalDateTime;
import java.util.List;

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
        requestEntity.setStatus(RequestStatus.PENDING);

        RequestDtoOut requestDtoOut = mapper.toDto(requestEntity);

        assertThat(requestDtoOut)
                .extracting("id", "requester", "event", "created", "status")
                .containsExactly(1L, 2L, 3L, created, RequestStatus.PENDING);
    }

    @Test
    void toDtoListTest() {
        List<RequestEntity> requestEntities = List.of(new RequestEntity(), new RequestEntity());

        RequestEntity requestEntity = requestEntities.get(0);
        requestEntity.setId(1L);
        requestEntity.setRequester(UserTestBuilder.defaultBuilder().id(2L).buildUserEntity());
        requestEntity.setEvent(EventTestBuilder.defaultBuilder().id(3L).buildEventEntity());
        LocalDateTime created1 = LocalDateTime.of(2020, 1, 1, 0, 0, 0);
        requestEntity.setCreated(created1);
        requestEntity.setStatus(RequestStatus.PENDING);

        requestEntity = requestEntities.get(1);
        requestEntity.setId(11L);
        requestEntity.setRequester(UserTestBuilder.defaultBuilder().id(12L).buildUserEntity());
        requestEntity.setEvent(EventTestBuilder.defaultBuilder().id(13L).buildEventEntity());
        LocalDateTime created2 = LocalDateTime.of(2021, 1, 1, 0, 0, 0);
        requestEntity.setCreated(created2);
        requestEntity.setStatus(RequestStatus.CONFIRMED);

        assertThat(mapper.toDto(requestEntities))
                .extracting("id", "requester", "event", "created", "status")
                .containsExactly(
                        Tuple.tuple(1L, 2L, 3L, created1, RequestStatus.PENDING),
                        Tuple.tuple(11L, 12L, 13L, created2, RequestStatus.CONFIRMED)
                );
    }
}