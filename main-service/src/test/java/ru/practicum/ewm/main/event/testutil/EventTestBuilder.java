package ru.practicum.ewm.main.event.testutil;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import ru.practicum.ewm.main.category.testutil.CategoryTestBuilder;
import ru.practicum.ewm.main.event.dto.*;
import ru.practicum.ewm.main.user.testutil.UserTestBuilder;

import java.time.LocalDateTime;
import java.time.temporal.ChronoField;

@NoArgsConstructor(staticName = "defaultBuilder")
@Getter
@Setter
@Accessors(chain = true, fluent = true)
public class EventTestBuilder {

    private final static LocalDateTime now = LocalDateTime.now().with(ChronoField.MILLI_OF_SECOND, 0);

    private String annotation = "Annotation 1";
    private CategoryTestBuilder categoryTestBuilder = CategoryTestBuilder.defaultBuilder();
    private Integer confirmedRequests = 10;
    private LocalDateTime createdOn = LocalDateTime.parse("2022-09-06T11:00:23");
    private String description = "Description 1";
    private LocalDateTime eventDate = now.plusYears(2);
    private Long id = 1L;
    private UserTestBuilder initiatorBuilder = UserTestBuilder.defaultBuilder();
    private LocationDto location = new LocationDto(51.51, 31.31);
    private Boolean paid = true;
    private Integer participantLimit = 11;
    private LocalDateTime publishedOn = LocalDateTime.parse("2022-09-07T15:10:05");
    private Boolean requestModeration = true;
    private EventState state = EventState.PUBLISHED;
    private EventStateAction stateAction = EventStateAction.CANCEL_REVIEW;
    private String title = "Title 1";
    private Long views = 1001L;

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
                .location(location)
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
                .location(location)
                .paid(paid)
                .participantLimit(participantLimit)
                .requestModeration(requestModeration)
                .title(title)
                .build();
    }

    public EventDtoInPatch buildEventDtoInPatch() {
        return EventDtoInPatch.builder()
                .annotation(annotation)
                .category(
                        categoryTestBuilder == null ? null : categoryTestBuilder.id()
                )
                .description(description)
                .eventDate(eventDate)
                .location(location)
                .paid(paid)
                .participantLimit(participantLimit)
                .requestModeration(requestModeration)
                .stateAction(stateAction)
                .title(title)
                .build();
    }
}
