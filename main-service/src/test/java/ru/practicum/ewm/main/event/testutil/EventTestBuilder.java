package ru.practicum.ewm.main.event.testutil;

import lombok.*;
import lombok.experimental.Accessors;
import ru.practicum.ewm.main.category.testutil.CategoryTestBuilder;
import ru.practicum.ewm.main.event.dto.*;
import ru.practicum.ewm.main.event.model.EventEntity;
import ru.practicum.ewm.main.event.model.LocationModel;
import ru.practicum.ewm.main.user.testutil.UserTestBuilder;

import java.time.LocalDateTime;

@NoArgsConstructor(staticName = "defaultBuilder")
@Getter
@Setter
@Accessors(chain = true, fluent = true)
public class EventTestBuilder {
    private String annotation = "Normal length annotation 1";
    private CategoryTestBuilder categoryTestBuilder = CategoryTestBuilder.defaultBuilder();
    private Integer confirmedRequests = 10;
    private LocalDateTime createdOn = LocalDateTime.parse("2022-09-06T11:00:23");
    private String description = "Normal length Description 1";
    private LocalDateTime eventDate = LocalDateTime.parse("2032-09-06T11:00:23");
    private Long id = 1L;
    private UserTestBuilder initiatorBuilder = UserTestBuilder.defaultBuilder();
    private Location location = new Location();
    private Boolean paid = true;
    private Integer participantLimit = 11;
    private LocalDateTime publishedOn = LocalDateTime.parse("2022-09-07T15:10:05");
    private Boolean requestModeration = true;
    private EventState state = EventState.PUBLISHED;
    private EventStateAction stateAction = EventStateAction.CANCEL_REVIEW;
    private String title = "Title 1";
    private Long views = 1001L;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Location {
        private Double lat = 51.51;
        private Double lon = 31.31;
    }

    public EventDtoOut buildEventDtoOut() {
        return EventDtoOut.builder()
                .annotation(annotation)
                .category(
                        categoryTestBuilder == null ? null : categoryTestBuilder.buildCategoryDtoOut()
                )
                .confirmedRequests(confirmedRequests)
                .createdOn(createdOn)
                .description(description)
                .eventDate(eventDate)
                .id(id)
                .initiator(
                        initiatorBuilder == null ? null : initiatorBuilder.buildDtoOutShort()
                )
                .location(
                        location == null ? null : new LocationDto(location.lat, location.lon)
                )
                .paid(paid)
                .participantLimit(participantLimit)
                .publishedOn(publishedOn)
                .requestModeration(requestModeration)
                .state(state)
                .title(title)
                .views(views)
                .build();
    }

    public EventDtoOutShort buildEventDtoOutShort() {
        return EventDtoOutShort.builder()
                .annotation(annotation)
                .category(
                        categoryTestBuilder == null ? null : categoryTestBuilder.buildCategoryDtoOut()
                )
                .confirmedRequests(confirmedRequests)
                .eventDate(eventDate)
                .id(id)
                .initiator(
                        initiatorBuilder == null ? null : initiatorBuilder.buildDtoOutShort()
                )
                .paid(paid)
                .title(title)
                .views(views)
                .build();
    }

    public EventDtoIn buildEventDtoIn() {
        return EventDtoIn.builder()
                .annotation(annotation)
                .category(
                        categoryTestBuilder == null ? null : categoryTestBuilder.id()
                )
                .description(description)
                .eventDate(eventDate)
                .location(
                        location == null ? null : new LocationDto(location.lat, location.lon)
                )
                .paid(paid)
                .participantLimit(participantLimit)
                .requestModeration(requestModeration)
                .title(title)
                .build();
    }

    public EventDtoInInitiatorPatch buildEventDtoInPatch() {
        return EventDtoInInitiatorPatch.builder()
                .annotation(annotation)
                .category(
                        categoryTestBuilder == null ? null : categoryTestBuilder.id()
                )
                .description(description)
                .eventDate(eventDate)
                .location(
                        location == null ? null : new LocationDto(location.lat, location.lon)
                )
                .paid(paid)
                .participantLimit(participantLimit)
                .requestModeration(requestModeration)
                .stateAction(stateAction)
                .title(title)
                .build();
    }

    public EventEntity buildEventEntity() {
        EventEntity eventEntity = new EventEntity();
        eventEntity.setId(id);
        eventEntity.setAnnotation(annotation);
        eventEntity.setCategory(
                categoryTestBuilder == null ? null : categoryTestBuilder.buildCategoryEntity()
        );
        eventEntity.setDescription(description);
        eventEntity.setEventDate(eventDate);
        if (location == null) {
            eventEntity.setLocation(null);
        } else {
            LocationModel locationModel = new LocationModel();
            locationModel.setLatitude(location.lat);
            locationModel.setLongitude(location.lon);
            eventEntity.setLocation(locationModel);
        }
        eventEntity.setPaid(paid);
        eventEntity.setParticipantLimit(participantLimit);
        eventEntity.setRequestModeration(requestModeration);
        eventEntity.setTitle(title);
        eventEntity.setCreatedOn(createdOn);
        eventEntity.setInitiator(
                initiatorBuilder == null ? null : initiatorBuilder.buildUserEntity())
        ;
        eventEntity.setPublishedOn(publishedOn);
        eventEntity.setState(state);
        return eventEntity;
    }
}
