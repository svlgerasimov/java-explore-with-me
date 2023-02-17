package ru.practicum.ewm.main.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import ru.practicum.ewm.main.exception.NotFoundException;
import ru.practicum.ewm.main.user.dto.UserDtoIn;
import ru.practicum.ewm.main.user.dto.UserDtoOut;
import ru.practicum.ewm.main.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.practicum.ewm.main.testutil.TestUtils.checkBadRequest;
import static ru.practicum.ewm.main.testutil.TestUtils.checkConflict;

@WebMvcTest(controllers = UserAdminController.class)
class UserControllerTest {

    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserService userService;
    @Autowired
    private MockMvc mvc;

    @Test
    void find_whenRequestWithoutNotRequiredParams_thenDefaultValues() throws Exception {
        mvc.perform(get("/admin/users"))
                .andExpect(status().isOk());

        verify(userService).find(
                eq(Collections.emptyList()),
                eq(0),
                eq(10)
        );
    }

    @Test
    void find_whenCorrectRequest_thenStatusOkAndReturnListOfDto() throws Exception {
        List<UserDtoOut> dtos = List.of(
                UserDtoOut.builder()
                        .id(1L)
                        .email("mail1@mail.com")
                        .name("User Name 1")
                        .build(),
                UserDtoOut.builder()
                        .id(2L)
                        .email("mail2@mail.com")
                        .name("User Name 2")
                        .build()
        );

        when(userService.find(
                eq(List.of(1L, 2L)),
                eq(10),
                eq(20)
        ))
                .thenReturn(dtos);

        mvc.perform(get("/admin/users")
                        .param("ids", "1", "2")
                        .param("from", "10")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dtos)));
    }



    @Test
    void find_whenNegativeFrom_ThenStatusBadRequest() throws Exception {
        MockHttpServletRequestBuilder requestBuilder =
                get("/admin/users")
                        .param("from", "-1");
        checkBadRequest(mvc, requestBuilder);
    }

    @Test
    void find_whenNegativeSize_ThenStatusBadRequest() throws Exception {
        MockHttpServletRequestBuilder requestBuilder =
                get("/admin/users")
                        .param("size", "-1");
        checkBadRequest(mvc, requestBuilder);
    }

    @Test
    void find_whenZeroSize_ThenStatusBadRequest() throws Exception {
        MockHttpServletRequestBuilder requestBuilder =
                get("/admin/users")
                        .param("size", "0");
        checkBadRequest(mvc, requestBuilder);
    }

    @Test
    void post_whenValidDto_ThenStatusCreatedAndReturnDto() throws Exception {
        UserDtoIn userDtoIn = UserDtoIn.builder()
                .email("mail@mail.com")
                .name("User Name")
                .build();
        UserDtoOut userDtoOut = UserDtoOut.builder()
                .id(1L)
                .email("mail@mail.com")
                .name("User Name")
                .build();

        when(userService.add(eq(userDtoIn)))
                .thenReturn(userDtoOut);

        mvc.perform(post("/admin/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDtoIn)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(userDtoOut)));
    }

    private void checkBadPostRequest(UserDtoIn userDtoIn) throws Exception {
        MockHttpServletRequestBuilder requestBuilder =
                post("/admin/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDtoIn));
        checkBadRequest(mvc, requestBuilder);
    }

    @Test
    void post_whenDtoWithBlankName_ThenStatusBadRequest() throws Exception {
        UserDtoIn userDtoIn = UserDtoIn.builder()
                .email("mail@mail.com")
                .name("   ")
                .build();
        checkBadPostRequest(userDtoIn);
    }

    @Test
    void post_whenDtoWithBlankEmail_ThenStatusBadRequest() throws Exception {
        UserDtoIn userDtoIn = UserDtoIn.builder()
                .email("")
                .name("User Name")
                .build();
        checkBadPostRequest(userDtoIn);
    }

    @Test
    void post_whenDtoWithInvalidEmail_ThenStatusBadRequest() throws Exception {
        UserDtoIn userDtoIn = UserDtoIn.builder()
                .email("mail.mail.com")
                .name("User Name")
                .build();
        checkBadPostRequest(userDtoIn);
    }

    @Test
    void post_whenRequestWithoutBody_ThenStatusBadRequest() throws Exception {
        MockHttpServletRequestBuilder requestBuilder =
                post("/admin/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON);
        checkBadRequest(mvc, requestBuilder);
    }

    @Test
    void post_whenServiceThrowsDataIntegrityViolation_ThenStatusConflict() throws Exception {
        UserDtoIn userDtoIn = UserDtoIn.builder()
                .email("mail@mail.com")
                .name("User Name")
                .build();

        when(userService.add(any()))
                .thenThrow(new DataIntegrityViolationException("message"));

        checkConflict(mvc,
                post("/admin/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDtoIn)));
    }

    @Test
    void delete_whenSuccessful_ThenStatusNoContent() throws Exception {
        mvc.perform(delete("/admin/users/1"))
                .andExpect(status().is(204));
    }

    @Test
    void delete_whenServiceThrowNotFound_ThenStatusNotFound() throws Exception {
        doThrow(new NotFoundException("some message"))
                .when(userService).delete(anyLong());

        mvc.perform(delete("/admin/users/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.reason").isNotEmpty())
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }
}