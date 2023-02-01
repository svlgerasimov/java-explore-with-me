package ru.practicum.ewm.stats.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.ewm.stats.dto.StatDtoIn;
import ru.practicum.ewm.stats.dto.StatDtoOut;
import ru.practicum.ewm.stats.service.StatService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = StatController.class)
class StatControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StatService statService;

    @Autowired
    private MockMvc mvc;

    private StatDtoIn.StatDtoInBuilder statDtoInBuilder;
    private StatDtoOut.StatDtoOutBuilder statDtoOutBuilder;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final String statsStart = "2020-05-05 00:00:00";
    private final String statsEnd = "2035-05-05 00:00:00";

    @BeforeEach
    void setUp() {
        statDtoInBuilder = StatDtoIn.builder()
                .app("ewm-main-service")
                .ip("192.163.0.1")
                .uri("/events/1")
                .timestamp(LocalDateTime.parse("2022-09-06 11:00:23", dateTimeFormatter));

        statDtoOutBuilder = StatDtoOut.builder()
                .app("ewm-main-service")
                .uri("/events/1")
                .hits(6L);
    }

    private void testSaveHitWithValidDto(StatDtoIn statDtoIn) throws Exception {
        doNothing().when(statService).save(statDtoIn);

        mvc.perform(post("/hit")
                        .content(objectMapper.writeValueAsString(statDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        verify(statService).save(statDtoIn);
    }

    private void testSaveHitWithInvalidDto(StatDtoIn statDtoIn) throws Exception {
        mvc.perform(post("/hit")
                        .content(objectMapper.writeValueAsString(statDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(statService, never()).save(any());
    }

    @Test
    void saveHit_givenCorrectDto_andThenReturnStatusCreated() throws Exception {
        StatDtoIn statDtoIn = statDtoInBuilder.build();

        testSaveHitWithValidDto(statDtoIn);
    }

    @Test
    void saveHit_givenDtoWithBlankApp_andThenStatusBadRequest() throws Exception {
        StatDtoIn statDtoIn = statDtoInBuilder.app("  ").build();

        testSaveHitWithInvalidDto(statDtoIn);
    }

    @Test
    void saveHit_givenDtoWithNullApp_andThenStatusBadRequest() throws Exception {
        StatDtoIn statDtoIn = statDtoInBuilder.app(null).build();

        testSaveHitWithInvalidDto(statDtoIn);
    }

    @Test
    void saveHit_givenDtoWithBlankUri_andThenStatusBadRequest() throws Exception {
        StatDtoIn statDtoIn = statDtoInBuilder.uri("  ").build();

        testSaveHitWithInvalidDto(statDtoIn);
    }

    @Test
    void saveHit_givenDtoWithNullUri_andThenStatusBadRequest() throws Exception {
        StatDtoIn statDtoIn = statDtoInBuilder.uri("  ").build();

        testSaveHitWithInvalidDto(statDtoIn);
    }

    @Test
    void saveHit_givenDtoWithInvalidIp_andThenStatusBadRequest() throws Exception {
        StatDtoIn statDtoIn = statDtoInBuilder.ip("192.163.0.256").build();

        testSaveHitWithInvalidDto(statDtoIn);
    }

    @Test
    void saveHit_givenDtoWithNullIp_andThenStatusBadRequest() throws Exception {
        StatDtoIn statDtoIn = statDtoInBuilder.ip(null).build();

        testSaveHitWithInvalidDto(statDtoIn);
    }

    @Test
    void saveHit_givenDtoWithInvalidTimestampFormat_andThenStatusBadRequest() throws  Exception {
        String json = "{\n" +
                "  \"app\": \"ewm-main-service\",\n" +
                "  \"uri\": \"/events/1\",\n" +
                "  \"ip\": \"192.163.0.1\",\n" +
                "  \"timestamp\": \"2022-09-06 11:00\"\n" +
                "}";

        mvc.perform(post("/hit")
                        .content(json)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(statService, never()).save(any());
    }

    @Test
    void saveHit_givenDtoWithNullTimestamp_andThenStatusBadRequest() throws Exception {
        StatDtoIn statDtoIn = statDtoInBuilder.timestamp(null).build();

        testSaveHitWithInvalidDto(statDtoIn);
    }

    private void testGetStatsWithValidParameters(List<StatDtoOut> dtos, String[] uris,
                                                 Boolean unique) throws Exception {
        when(statService.get(
                eq(LocalDateTime.parse(statsStart, dateTimeFormatter)),
                eq(LocalDateTime.parse(statsEnd, dateTimeFormatter)),
                eq(Arrays.asList(uris)),
                eq(unique)))
                .thenReturn(dtos);

        mvc.perform(get("/stats")
                        .param("start", statsStart)
                        .param("end", statsEnd)
                        .param("uris", uris)
                        .param("unique", String.valueOf(unique)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dtos)));
    }

    private void testGetStatsWithInvalidParameters(String start, String end, String[] uris,
                                                   Boolean unique) throws Exception {
        mvc.perform(get("/stats")
                        .param("start", start)
                        .param("end", end)
                        .param("uris", uris)
                        .param("unique", String.valueOf(unique)))
                .andExpect(status().isBadRequest());

        verify(statService, never())
                .get(any(), any(), anyList(), anyBoolean());
    }

    @Test
    void getStats_givenCorrectRequest_andThenStatusOkAndDtoInResponseBody() throws Exception {
        StatDtoOut statDtoOut = statDtoOutBuilder.build();
        List<StatDtoOut> dtos = List.of(statDtoOut);
        String[] uris = new String[] {statDtoOut.getUri()};
        boolean unique = true;

        testGetStatsWithValidParameters(dtos, uris, unique);
    }

    @Test
    void getStats_givenInvalidStartFormat_andThenStatusBadRequest() throws Exception {
        StatDtoOut statDtoOut = statDtoOutBuilder.build();
        String[] uris = new String[] {statDtoOut.getUri()};
        boolean unique = true;

        testGetStatsWithInvalidParameters("2020-05-05 00:00", statsEnd, uris, unique);
    }

    @Test
    void getStats_givenNullStart_andThenStatusBadRequest() throws Exception {
        StatDtoOut statDtoOut = statDtoOutBuilder.build();
        String[] uris = new String[] {statDtoOut.getUri()};
        boolean unique = true;

        testGetStatsWithInvalidParameters(null, statsEnd, uris, unique);
    }

    @Test
    void getStats_givenInvalidEndFormat_andThenStatusBadRequest() throws Exception {
        StatDtoOut statDtoOut = statDtoOutBuilder.build();
        String[] uris = new String[] {statDtoOut.getUri()};
        boolean unique = true;

        testGetStatsWithInvalidParameters(statsStart, "2035-05-05 00:00", uris, unique);
    }

    @Test
    void getStats_givenNullEnd_andThenStatusBadRequest() throws Exception {
        StatDtoOut statDtoOut = statDtoOutBuilder.build();
        String[] uris = new String[] {statDtoOut.getUri()};
        boolean unique = true;

        testGetStatsWithInvalidParameters(statsStart, null, uris, unique);
    }

    @Test
    void getStats_givenAbsentUrls_andThenDefaultValueEmptyListAndStatusOkAndDtoInResponseBody()
            throws Exception {
        List<StatDtoOut> dtos = List.of();
        boolean unique = true;

        when(statService.get(
                eq(LocalDateTime.parse(statsStart, dateTimeFormatter)),
                eq(LocalDateTime.parse(statsEnd, dateTimeFormatter)),
                eq(Collections.emptyList()),
                eq(unique)))
                .thenReturn(dtos);

        mvc.perform(get("/stats")
                        .param("start", statsStart)
                        .param("end", statsEnd)
                        .param("unique", String.valueOf(unique)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dtos)));
    }

    @Test
    void getStats_givenAbsentUnique_andThenDefaultFalseAndStatusOkAndDtoInResponseBody()
            throws Exception {
        StatDtoOut statDtoOut = statDtoOutBuilder.build();
        List<StatDtoOut> dtos = List.of(statDtoOut);
        String[] uris = new String[] {statDtoOut.getUri()};

        when(statService.get(
                eq(LocalDateTime.parse(statsStart, dateTimeFormatter)),
                eq(LocalDateTime.parse(statsEnd, dateTimeFormatter)),
                eq(Arrays.asList(uris)),
                eq(false)))
                .thenReturn(dtos);

        mvc.perform(get("/stats")
                        .param("start", statsStart)
                        .param("end", statsEnd)
                        .param("uris", uris))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dtos)));
    }
}