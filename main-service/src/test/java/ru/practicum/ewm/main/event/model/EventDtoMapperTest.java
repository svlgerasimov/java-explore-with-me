package ru.practicum.ewm.main.event.model;

import org.junit.jupiter.api.Test;
import ru.practicum.ewm.main.category.model.CategoryDtoMapperImpl;
import ru.practicum.ewm.main.category.model.CategoryEntity;
import ru.practicum.ewm.main.category.testutil.CategoryTestBuilder;
import ru.practicum.ewm.main.event.dto.*;
import ru.practicum.ewm.main.event.testutil.EventTestBuilder;
import ru.practicum.ewm.main.user.model.UserDtoMapperImpl;
import ru.practicum.ewm.main.user.model.UserEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class EventDtoMapperTest {
    
    private static final EventDtoMapper mapper = new EventDtoMapperImpl(
            new UserDtoMapperImpl(),
            new CategoryDtoMapperImpl(),
            new LocationMapperImpl());

    @Test
    void createFromDtoTest() {
        EventTestBuilder eventTestBuilder = EventTestBuilder.defaultBuilder();

        EventDtoIn eventDtoIn = eventTestBuilder.buildEventDtoIn();
        UserEntity initiatorEntity = eventTestBuilder.initiatorBuilder().buildUserEntity();
        CategoryEntity categoryEntity = eventTestBuilder.categoryTestBuilder().buildCategoryEntity();
        LocalDateTime createdOn = eventTestBuilder.createdOn();

        eventTestBuilder
                .id(null)
                .state(EventState.PENDING)
                .publishedOn(null);

        assertThat(mapper.createFromDto(eventDtoIn, initiatorEntity, categoryEntity, createdOn))
                .usingRecursiveComparison()
                .isEqualTo(eventTestBuilder.buildEventEntity());
    }

    @Test
    void updateByDtoTest() {
        EventTestBuilder eventTestBuilder = EventTestBuilder.defaultBuilder();

        EventEntity eventEntity = eventTestBuilder.buildEventEntity();

        Double locationLat = eventTestBuilder.location().lat() + 10;
        Double locationLon = eventTestBuilder.location().lon() + 20;

        eventTestBuilder
                .annotation("new annotation")
                .categoryTestBuilder(CategoryTestBuilder.defaultBuilder()
                        .id(100L).name("new cat name"))
                .description("new description")
                .eventDate(eventTestBuilder.eventDate().plusYears(1))
                .location(new EventTestBuilder.Location(locationLat, locationLon))
                .paid(!eventTestBuilder.paid())
                .participantLimit(eventTestBuilder.participantLimit() + 2)
                .title("new title");

        mapper.updateByDto(eventEntity, eventTestBuilder.buildEventDtoInPatch(),
                eventTestBuilder.categoryTestBuilder().buildCategoryEntity());

        EventEntity expected = eventTestBuilder.buildEventEntity();
        assertThat(eventEntity)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    void updateByDto_whenNullFieldsInDto_thenOriginalValuesInEntity() {
        EventTestBuilder eventTestBuilder = EventTestBuilder.defaultBuilder();

        EventEntity eventEntity = eventTestBuilder.buildEventEntity();

        EventDtoInPatch eventDtoInPatch = EventDtoInPatch.builder().build();

        mapper.updateByDto(eventEntity, eventDtoInPatch, null);

        assertThat(eventEntity)
                .usingRecursiveComparison()
                .isEqualTo(eventTestBuilder.buildEventEntity());
    }

    @Test
    void toDtoFullTest() {
        EventTestBuilder eventTestBuilder = EventTestBuilder.defaultBuilder();

        EventEntity eventEntity = eventTestBuilder.buildEventEntity();

        eventTestBuilder.confirmedRequests(23).views(999L);
        EventDtoOut eventDtoOut = eventTestBuilder.buildEventDtoOut();

        assertThat(mapper.toDtoFull(eventEntity, 23, 999L))
                .isEqualTo(eventDtoOut);
    }

    @Test
    void toDtoShortTest() {
        EventTestBuilder eventTestBuilder = EventTestBuilder.defaultBuilder();

        EventEntity eventEntity = eventTestBuilder.buildEventEntity();

        eventTestBuilder.confirmedRequests(23).views(999L);
        EventDtoOutShort eventDtoOutShort = eventTestBuilder.buildEventDtoOutShort();

        assertThat(mapper.toDtoShort(eventEntity, 23, 999L))
                .isEqualTo(eventDtoOutShort);
    }

    @Test
    void toDtoShortListTest() {
        EventTestBuilder eventTestBuilder = EventTestBuilder.defaultBuilder()
                .id(1L).confirmedRequests(21).views(1000L);
        EventEntity eventEntity1 = eventTestBuilder.buildEventEntity();
        EventDtoOutShort eventDtoOutShort1 = eventTestBuilder.buildEventDtoOutShort();

        eventTestBuilder.id(2L).confirmedRequests(22).views(2000L);
        EventEntity eventEntity2 = eventTestBuilder.buildEventEntity();
        EventDtoOutShort eventDtoOutShort2 = eventTestBuilder.buildEventDtoOutShort();

        List<EventEntity> entities = List.of(eventEntity1, eventEntity2);
        List<EventDtoOutShort> dtos = List.of(eventDtoOutShort1, eventDtoOutShort2);
        Map<Long, Integer> confirmedRequests = Map.of(1L, 21, 2L, 22);
        Map<Long, Long> views = Map.of(1L, 1000L, 2L, 2000L);

        assertThat(mapper.toDtoShort(entities, confirmedRequests, views))
                .containsExactlyElementsOf(dtos);
    }
}