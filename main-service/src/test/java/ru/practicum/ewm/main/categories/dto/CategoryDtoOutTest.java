package ru.practicum.ewm.main.categories.dto;

import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CategoryDtoOutTest {

    @Autowired
    private JacksonTester<CategoryDtoOut> jacksonTester;
    private final CategoryDtoOut categoryDtoOut = new CategoryDtoOut(3L, "Category Name");
    private final String json =
            "{\"id\" : 3," +
            "\"name\" : \"Category Name\"}";

    @Test
    void categoryDtoOutSerializationTest() throws IOException {
        assertThat(jacksonTester.write(categoryDtoOut))
                .isEqualToJson(json, JSONCompareMode.STRICT);
    }

    @Test
    void categoryDtoOutDeserializationTest() throws IOException {
        assertThat(jacksonTester.parse(json))
                .usingDefaultComparator()
                .isEqualTo(categoryDtoOut);
    }
}