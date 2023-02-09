package ru.practicum.ewm.main.categories.dto;

import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CategoryDtoInPatchTest {

    @Autowired
    private JacksonTester<CategoryDtoInPatch> jacksonTester;
    private final CategoryDtoInPatch categoryDtoIn = new CategoryDtoInPatch("Category Name");
    private final String json = "{\"name\" : \"Category Name\"}";

    @Test
    void categoryDtoInPatchSerializationTest() throws IOException {
        assertThat(jacksonTester.write(categoryDtoIn))
                .isEqualToJson(json, JSONCompareMode.STRICT);
    }

    @Test
    void categoryDtoInPatchDeserializationTest() throws IOException {
        assertThat(jacksonTester.parse(json))
                .usingDefaultComparator()
                .isEqualTo(categoryDtoIn);
    }
}