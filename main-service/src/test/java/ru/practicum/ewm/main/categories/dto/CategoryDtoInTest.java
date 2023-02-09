package ru.practicum.ewm.main.categories.dto;

import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CategoryDtoInTest {

    @Autowired
    private JacksonTester<CategoryDtoIn> jacksonTester;
    private final CategoryDtoIn categoryDtoIn = new CategoryDtoIn("Category Name");
    private final String json = "{\"name\" : \"Category Name\"}";

    @Test
    void categoryDtoInSerializationTest() throws IOException {
        assertThat(jacksonTester.write(categoryDtoIn))
                .isEqualToJson(json, JSONCompareMode.STRICT);
    }

    @Test
    void categoryDtoInDeserializationTest() throws IOException {
        assertThat(jacksonTester.parse(json))
                .usingDefaultComparator()
                .isEqualTo(categoryDtoIn);
    }
}