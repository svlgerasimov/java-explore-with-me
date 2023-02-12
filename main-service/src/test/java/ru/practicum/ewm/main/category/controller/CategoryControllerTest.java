package ru.practicum.ewm.main.category.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import ru.practicum.ewm.main.category.dto.CategoryDtoIn;
import ru.practicum.ewm.main.category.dto.CategoryDtoInPatch;
import ru.practicum.ewm.main.category.dto.CategoryDtoOut;
import ru.practicum.ewm.main.category.service.CategoryService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.practicum.ewm.main.testutil.TestUtils.checkBadRequest;

@WebMvcTest(controllers = CategoryController.class)
class CategoryControllerTest {

    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private CategoryService categoryService;
    @Autowired
    private MockMvc mvc;

    @Test
    void post_whenValidDto_thenStatusCreatedAndReturnDto() throws Exception {
        CategoryDtoIn categoryDtoIn = CategoryDtoIn.builder().name("Category").build();
        CategoryDtoOut categoryDtoOut = CategoryDtoOut.builder()
                .id(2L).name("Category").build();

        when(categoryService.add(eq(categoryDtoIn)))
                .thenReturn(categoryDtoOut);

        mvc.perform(post("/admin/categories")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryDtoIn)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(categoryDtoOut)));
    }

    private void checkBadPostRequest(CategoryDtoIn categoryDtoIn) throws Exception {
        MockHttpServletRequestBuilder requestBuilder =
                post("/admin/categories")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryDtoIn));

        checkBadRequest(mvc, requestBuilder);
    }

    @Test
    void post_whenDtoWithAbsentName_thenStatusBadRequest() throws Exception {
        CategoryDtoIn categoryDtoIn = CategoryDtoIn.builder().name(null).build();

        checkBadPostRequest(categoryDtoIn);
    }

    @Test
    void post_whenDtoWithBlankName_thenStatusBadRequest() throws Exception {
        CategoryDtoIn categoryDtoIn = CategoryDtoIn.builder().name("  ").build();

        checkBadPostRequest(categoryDtoIn);
    }

    @Test
    void delete_whenSuccessful_thenStatusNoContent() throws Exception {
        mvc.perform(delete("/admin/categories/1"))
                .andExpect(status().is(204));
    }

    @Test
    void patch_whenValidDto_thenStatusOkAndReturnDto() throws Exception {
        CategoryDtoInPatch categoryDtoInPatch = CategoryDtoInPatch.builder().name("Category").build();
        CategoryDtoOut categoryDtoOut = CategoryDtoOut.builder()
                .id(2L).name("Category").build();
        when(categoryService.patch(eq(2L), eq(categoryDtoInPatch)))
                .thenReturn(categoryDtoOut);

        mvc.perform(patch("/admin/categories/2")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryDtoInPatch)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(categoryDtoOut)));
    }

    @Test
    void patch_whenDtoWithAbsentName_thenStatusOk() throws Exception {
        CategoryDtoInPatch categoryDtoInPatch = CategoryDtoInPatch.builder().name(null).build();

        mvc.perform(patch("/admin/categories/2")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryDtoInPatch)))
                .andExpect(status().isOk());
    }

    @Test
    void patch_whenDtoWithBlankName_thenStatusBadRequest() throws Exception {
        CategoryDtoInPatch categoryDtoInPatch = CategoryDtoInPatch.builder().name("  ").build();

        checkBadRequest(
                mvc,
                patch("/admin/categories/2")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryDtoInPatch))
        );
    }

    @Test
    void findAll_whenRequestWithoutNotRequiredParams_thenDefaultValues() throws Exception {
        mvc.perform(get("/categories"))
                .andExpect(status().isOk());

        verify(categoryService).findAll(0, 10);
    }

    @Test
    void findAll_whenCorrectRequest_thenStatusOkAndReturnListOfDto() throws Exception {
        List<CategoryDtoOut> dtos = List.of(
                CategoryDtoOut.builder().id(1L).name("Cat1").build(),
                CategoryDtoOut.builder().id(2L).name("Cat2").build()
        );

        when(categoryService.findAll(10, 20))
                .thenReturn(dtos);

        mvc.perform(get("/categories")
                        .param("from", "10")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dtos)));
    }

    @Test
    void findAll_whenNegativeFrom_ThenStatusBadRequest() throws Exception {
        checkBadRequest(
                mvc,
                get("/categories")
                        .param("from", "-1")
        );
    }

    @Test
    void findAll_whenNegativeSize_ThenStatusBadRequest() throws Exception {
        checkBadRequest(
                mvc,
                get("/categories")
                        .param("size", "-1")
        );
    }

    @Test
    void findAll_whenZeroFrom_ThenStatusOk() throws Exception {
        mvc.perform(get("/categories")
                        .param("from", "0"))
                .andExpect(status().isOk());
    }

    @Test
    void findAll_whenZeroSize_ThenStatusBadRequest() throws Exception {
        checkBadRequest(
                mvc,
                get("/categories")
                        .param("size", "0")
        );
    }

    @Test
    void findById_statusOkAndReturnDto() throws Exception {
        CategoryDtoOut categoryDtoOut = CategoryDtoOut.builder().id(1L).name("Cat1").build();

        when(categoryService.findById(1L))
                .thenReturn(categoryDtoOut);

        mvc.perform(get("/categories/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(categoryDtoOut)));
    }
}