package ru.practicum.ewm.main.event.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.ewm.main.event.dto.*;
import ru.practicum.ewm.main.event.service.EventService;
import ru.practicum.ewm.main.event.testutil.EventTestBuilder;
import ru.practicum.ewm.main.exception.ConditionsNotMetException;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static ru.practicum.ewm.main.testutil.TestUtils.checkBadRequest;

@WebMvcTest(controllers = EventController.class)
@Slf4j
class EventControllerTest {

    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private EventService eventService;
    @Autowired
    private MockMvc mvc;

    @Test
    void post_whenValidDto_thenStatusCreatedAndReturnDto() throws Exception {
        EventTestBuilder eventTestBuilder = EventTestBuilder.defaultBuilder();
        EventDtoIn eventDtoIn = eventTestBuilder.buildEventDtoIn();
        EventDtoOut eventDtoOut = eventTestBuilder.buildEventDtoOut();
        Long userId = 11L;

        when(eventService.add(eq(userId), eq(eventDtoIn)))
                .thenReturn(eventDtoOut);

        assertThat(objectMapper.readValue(objectMapper.writeValueAsString(eventDtoIn), EventDtoIn.class))
                .isEqualTo(eventDtoIn);

        mvc.perform(post("/users/11/events")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventDtoIn)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(eventDtoOut)));
    }

    private void checkBadPostRequest(EventDtoIn eventDtoIn) throws Exception {
        checkBadRequest(
                mvc,
                post("/users/11/events")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventDtoIn))
        );
    }

    @Test
    void post_whenDtoWithBlankAnnotation_thenStatusBadRequest() throws Exception {
        checkBadPostRequest(EventTestBuilder.defaultBuilder()
                .annotation("  ")
                .buildEventDtoIn());
    }

    @Test
    void post_whenDtoWithNullCategory_thenStatusBadRequest() throws Exception {
        EventTestBuilder eventTestBuilder = EventTestBuilder.defaultBuilder();
        eventTestBuilder.categoryTestBuilder().id(null);
        checkBadPostRequest(eventTestBuilder.buildEventDtoIn());
    }

    @Test
    void post_whenDtoWithBlankDescription_thenStatusBadRequest() throws Exception {
        checkBadPostRequest(EventTestBuilder.defaultBuilder()
                .description("  ")
                .buildEventDtoIn());
    }

    @Test
    void post_whenDtoWithNullEventDate_thenStatusBadRequest() throws Exception {
        checkBadPostRequest(EventTestBuilder.defaultBuilder()
                .eventDate(null)
                .buildEventDtoIn());
    }

    @Test
    void post_whenDtoWithNullLocation_thenStatusBadRequest() throws Exception {
        checkBadPostRequest(EventTestBuilder.defaultBuilder()
                .location(null)
                .buildEventDtoIn());
    }

    @Test
    void post_whenDtoWithNullLocationLat_thenStatusBadRequest() throws Exception {
        EventTestBuilder eventTestBuilder = EventTestBuilder.defaultBuilder();
        LocationDto locationDto = eventTestBuilder.location();
        locationDto = new LocationDto(null, locationDto.getLon());
        checkBadPostRequest(eventTestBuilder.location(locationDto).buildEventDtoIn());
    }

    @Test
    void post_whenDtoWithNullLocationLon_thenStatusBadRequest() throws Exception {
        EventTestBuilder eventTestBuilder = EventTestBuilder.defaultBuilder();
        LocationDto locationDto = eventTestBuilder.location();
        locationDto = new LocationDto(locationDto.getLat(), null);
        checkBadPostRequest(eventTestBuilder.location(locationDto).buildEventDtoIn());
    }

    @Test
    void post_whenServiceThrowsConditionsNotMetException_thenStatusConflict() throws Exception {
        doThrow(new ConditionsNotMetException("some message"))
                .when(eventService).add(any(), any());

        mvc.perform(post("/users/11/events")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(EventTestBuilder.defaultBuilder().buildEventDtoIn())))
                .andExpect(status().is(409))
                .andExpect(jsonPath("$.status").value("FORBIDDEN"))
                .andExpect(jsonPath("$.reason").isNotEmpty())
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    void findAllByInitiatorId_whenRequestWithoutNotRequiredParams_thenDefaultValues() throws Exception {
        mvc.perform(get("/users/11/events"))
                .andExpect(status().isOk());

        verify(eventService).findAllByInitiatorId(11L, 0, 10);
    }

    @Test
    void findAllByInitiatorId_whenCorrectRequest_thenStatusOkAndReturnListOfDto() throws Exception {
        EventTestBuilder eventTestBuilder = EventTestBuilder.defaultBuilder();
        List<EventDtoOutShort> dtos = List.of(
                eventTestBuilder.id(1L).buildEventDtoOutShort(),
                eventTestBuilder.id(2L).buildEventDtoOutShort()
        );

        when(eventService.findAllByInitiatorId(11L, 10, 20))
                .thenReturn(dtos);

        mvc.perform(get("/users/11/events")
                        .param("from", "10")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dtos)));
    }

    @Test
    void findAllByInitiatorId_whenNegativeFrom_ThenStatusBadRequest() throws Exception {
        checkBadRequest(
                mvc,
                get("/users/11/events")
                        .param("from", "-1")
        );
    }

    @Test
    void findAllByInitiatorId_whenNegativeSize_ThenStatusBadRequest() throws Exception {
        checkBadRequest(
                mvc,
                get("/users/11/events")
                        .param("size", "-1")
        );
    }

    @Test
    void findAllByInitiatorId_whenZeroSize_ThenStatusBadRequest() throws Exception {
        checkBadRequest(
                mvc,
                get("/users/11/events")
                        .param("size", "0")
        );
    }

    @Test
    void findByEventIdAndInitiatorId_statusOkAndReturnDto() throws Exception {
        EventDtoOut eventDtoOut = EventTestBuilder.defaultBuilder().buildEventDtoOut();

        when(eventService.findByEventIdAndInitiatorId(22L, 11L))
                .thenReturn(eventDtoOut);

        mvc.perform(get("/users/11/events/22"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(eventDtoOut)));
    }

    @Test
    void patch_whenValidDto_thenStatusOkAndReturnDto() throws Exception {
        EventTestBuilder eventTestBuilder = EventTestBuilder.defaultBuilder();
        EventDtoInPatch eventDtoInPatch = eventTestBuilder.buildEventDtoInPatch();
        EventDtoOut eventDtoOut = eventTestBuilder.buildEventDtoOut();

        when(eventService.patch(22L, 11L, eventDtoInPatch))
                .thenReturn(eventDtoOut);

        mvc.perform(patch("/users/11/events/22")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventDtoInPatch)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(eventDtoOut)));
    }

    @Test
    void patch_whenDtoWithNullFields_thenStatusOk() throws Exception {
        mvc.perform(patch("/users/11/events/22")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                EventDtoInPatch.builder()
                                        .annotation(null)
                                        .category(null)
                                        .description(null)
                                        .eventDate(null)
                                        .location(null)
                                        .paid(null)
                                        .participantLimit(null)
                                        .requestModeration(null)
                                        .stateAction(null)
                                        .title(null)
                                        .build()
                        )))
                .andExpect(status().isOk());
    }

    private void checkBadPatchRequest(EventDtoInPatch eventDtoInPatch) throws Exception {
        checkBadRequest(
                mvc,
                patch("/users/11/events/22")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventDtoInPatch))
        );
    }

    @Test
    void patch_whenDtoWithBlankAnnotation_thenStatusBadRequest() throws Exception {
        EventDtoInPatch eventDtoInPatch = EventTestBuilder.defaultBuilder()
                .annotation("").buildEventDtoInPatch();
        checkBadPatchRequest(eventDtoInPatch);
    }

    @Test
    void patch_whenDtoWithBlankDescription_thenStatusBadRequest() throws Exception {
        EventDtoInPatch eventDtoInPatch = EventTestBuilder.defaultBuilder()
                .description("").buildEventDtoInPatch();
        checkBadPatchRequest(eventDtoInPatch);
    }

    @Test
    void patch_whenDtoWithNullLocationLat_thenStatusBadRequest() throws Exception {
        EventDtoInPatch eventDtoInPatch = EventTestBuilder.defaultBuilder()
                .location(new LocationDto(null, 31.31))
                .buildEventDtoInPatch();
        checkBadPatchRequest(eventDtoInPatch);
    }

    @Test
    void patch_whenDtoWithNullLocationLon_thenStatusBadRequest() throws Exception {
        EventDtoInPatch eventDtoInPatch = EventTestBuilder.defaultBuilder()
                .location(new LocationDto(51.51, null))
                .buildEventDtoInPatch();
        checkBadPatchRequest(eventDtoInPatch);
    }

    @Test
    void patch_whenDtoWithBlankTitle_thenStatusBadRequest() throws Exception {
        EventDtoInPatch eventDtoInPatch = EventTestBuilder.defaultBuilder()
                .title("").buildEventDtoInPatch();
        checkBadPatchRequest(eventDtoInPatch);
    }

}