package ru.practicum.ewm.main.categories.model;

import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;
import ru.practicum.ewm.main.categories.dto.CategoryDtoIn;
import ru.practicum.ewm.main.categories.dto.CategoryDtoInPatch;
import ru.practicum.ewm.main.categories.dto.CategoryDtoOut;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CategoryDtoMapperTest {

    private final CategoryDtoMapper mapper = new CategoryDtoMapperImpl();

    @Test
    void fromDtoTest() {
        CategoryDtoIn categoryDtoIn = CategoryDtoIn.builder().name("Cat").build();

        CategoryEntity categoryEntity = mapper.fromDto(categoryDtoIn);

        assertThat(categoryEntity)
                .extracting("id", "name")
                .containsExactly(null, "Cat");
    }

    @Test
    void updateByDto_whenNonNullFieldsIdDto_thenUpdateFieldsInEntity() {
        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setId(1L);
        categoryEntity.setName("Cat 1");

        CategoryDtoInPatch categoryDtoInPatch = CategoryDtoInPatch.builder().name("Cat 2").build();

        mapper.updateByDto(categoryEntity, categoryDtoInPatch);

        assertThat(categoryEntity)
                .extracting("id", "name")
                .containsExactly(1L, "Cat 2");
    }

    @Test
    void updateByDto_whenNullFieldsIdDto_thenOriginalFieldsInEntity() {
        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setId(1L);
        categoryEntity.setName("Cat 1");

        CategoryDtoInPatch categoryDtoInPatch = CategoryDtoInPatch.builder().name(null).build();

        mapper.updateByDto(categoryEntity, categoryDtoInPatch);

        assertThat(categoryEntity)
                .extracting("id", "name")
                .containsExactly(1L, "Cat 1");
    }

    @Test
    void toDtoSingleTest() {
        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setId(1L);
        categoryEntity.setName("Cat 1");

        CategoryDtoOut categoryDtoOut = mapper.toDto(categoryEntity);

        assertThat(categoryDtoOut).extracting("id", "name")
                .containsExactly(1L, "Cat 1");
    }

    @Test
    void toDtoListTest() {
        CategoryEntity categoryEntity1 = new CategoryEntity();
        categoryEntity1.setId(1L);
        categoryEntity1.setName("Cat 1");

        CategoryEntity categoryEntity2 = new CategoryEntity();
        categoryEntity2.setId(2L);
        categoryEntity2.setName("Cat 2");

        List<CategoryDtoOut> categoryDtos = mapper.toDto(List.of(categoryEntity1, categoryEntity2));

        assertThat(categoryDtos).extracting("id", "name")
                .containsExactly(
                        Tuple.tuple(1L, "Cat 1"),
                        Tuple.tuple(2L, "Cat 2")
                );
    }
}