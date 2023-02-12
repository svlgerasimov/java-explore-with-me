package ru.practicum.ewm.main.category.service;

import lombok.RequiredArgsConstructor;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.ewm.main.category.dto.CategoryDtoIn;
import ru.practicum.ewm.main.category.dto.CategoryDtoInPatch;
import ru.practicum.ewm.main.category.dto.CategoryDtoOut;
import ru.practicum.ewm.main.category.model.CategoryEntity;
import ru.practicum.ewm.main.exception.NotFoundException;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class CategoryServiceImplIntegrationTest {

    private final CategoryServiceImpl categoryService;
    private final EntityManager em;

    @Test
    @Sql(scripts = "/sql/clear-categories.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void addTest() {
        CategoryDtoIn categoryDtoIn = CategoryDtoIn.builder()
                .name("Cat name")
                .build();

        CategoryDtoOut categoryDtoOut = categoryService.add(categoryDtoIn);
        assertThat(categoryDtoOut).extracting("id").isNotNull();
        assertThat(categoryDtoOut).hasFieldOrPropertyWithValue("name", "Cat name");

        List<CategoryEntity> categoryEntities =
                em.createQuery("select c from CategoryEntity c", CategoryEntity.class)
                        .getResultList();
        assertThat(categoryEntities)
                .extracting("id")
                .doesNotContainNull();
        assertThat(categoryEntities)
                .extracting("name")
                .containsExactly("Cat name");
    }

    @Test
    @Sql(scripts = "/sql/clear-categories.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void add_whenSaveCategoryWithExistingName_thenThrowDataIntegrityViolationException() {
        CategoryDtoIn.CategoryDtoInBuilder categoryDtoInBuilder =
                CategoryDtoIn.builder().name("Cat name");

        categoryService.add(categoryDtoInBuilder.build());

        assertThatThrownBy(() -> categoryService.add(categoryDtoInBuilder.build()))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @Sql(scripts = "/sql/clear-categories.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Sql(scripts = "/sql/get-categories-prepare.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void deleteTest() {
        categoryService.delete(3L);

        List<CategoryEntity> categoryEntities =
                em.createQuery("select c from CategoryEntity c", CategoryEntity.class)
                        .getResultList();

        assertThat(categoryEntities)
                .hasSize(9)
                .extracting("id")
                .doesNotContain(3L);
    }

    @Test
    @Sql(scripts = "/sql/clear-categories.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Sql(scripts = "/sql/get-categories-prepare.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void delete_whenAbsentId_thenThrowNotFoundException() {
        assertThatThrownBy(() -> categoryService.delete(10000L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @Sql(scripts = "/sql/clear-categories.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Sql(scripts = "/sql/get-categories-prepare.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void patch_whenNonNullFieldsInDto_thenUpdateFieldsInEntity() {
        CategoryDtoInPatch categoryDtoInPatch = CategoryDtoInPatch.builder()
                .name("patched name")
                .build();

        CategoryDtoOut categoryDtoOut = categoryService.patch(3L, categoryDtoInPatch);
        assertThat(categoryDtoOut)
                .extracting("id", "name")
                .containsExactly(3L, "patched name");

        assertThat(
                em.createQuery("select c from CategoryEntity c", CategoryEntity.class)
                .getResultList())
                .hasSize(10)
                .extracting("name")
                .doesNotContain("name3");

        assertThat(
                em.createQuery("select c from CategoryEntity c where c.id=3L", CategoryEntity.class)
                        .getSingleResult()
        )
                .hasFieldOrPropertyWithValue("name", "patched name");
    }

    @Test
    @Sql(scripts = "/sql/clear-categories.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Sql(scripts = "/sql/get-categories-prepare.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void patch_whenNullFieldsInDto_thenUpdateFieldsInEntity() {
        CategoryDtoInPatch categoryDtoInPatch = CategoryDtoInPatch.builder()
                .name(null)
                .build();

        CategoryDtoOut categoryDtoOut = categoryService.patch(3L, categoryDtoInPatch);
        assertThat(categoryDtoOut)
                .extracting("id", "name")
                .containsExactly(3L, "name3");

        assertThat(
                em.createQuery("select c from CategoryEntity c", CategoryEntity.class)
                        .getResultList())
                .hasSize(10);

        assertThat(
                em.createQuery("select c from CategoryEntity c where c.id=3L", CategoryEntity.class)
                        .getSingleResult()
        )
                .hasFieldOrPropertyWithValue("name", "name3");
    }

    @Test
    @Sql(scripts = "/sql/clear-categories.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Sql(scripts = "/sql/get-categories-prepare.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void patch_whenPatchCategoryWithExistingName_thenThrowDataIntegrityViolationException() {
        CategoryDtoIn categoryDtoIn = CategoryDtoIn.builder().name("name3").build();

        assertThatThrownBy(() -> categoryService.add(categoryDtoIn))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @Sql(scripts = "/sql/clear-categories.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Sql(scripts = "/sql/get-categories-prepare.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void findAll_allCategoriesInPage() {
        assertThat(categoryService.findAll(0, 100))
                .extracting("id", "name")
                .containsExactlyInAnyOrder(
                        Tuple.tuple(1L, "name1"),
                        Tuple.tuple(2L, "name2"),
                        Tuple.tuple(3L, "name3"),
                        Tuple.tuple(4L, "name4"),
                        Tuple.tuple(5L, "name5"),
                        Tuple.tuple(6L, "name6"),
                        Tuple.tuple(7L, "name7"),
                        Tuple.tuple(8L, "name8"),
                        Tuple.tuple(9L, "name9"),
                        Tuple.tuple(10L, "name10")
                );
    }

    @Test
    void findAll_whenEmptyTable_thenReturnEmptyList() {
        assertThat(categoryService.findAll(0, 100))
                .isEmpty();
    }

    @Test
    @Sql(scripts = "/sql/clear-categories.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Sql(scripts = "/sql/get-categories-prepare.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void findAll_partOfCategoriesInPage() {
        assertThat(categoryService.findAll(4, 2))
                .hasSize(2);
    }

    @Test
    @Sql(scripts = "/sql/clear-categories.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Sql(scripts = "/sql/get-categories-prepare.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void findByIdTest() {
        assertThat(categoryService.findById(3L))
                .extracting("id", "name")
                .containsExactly(3L, "name3");
    }

    @Test
    @Sql(scripts = "/sql/clear-categories.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Sql(scripts = "/sql/get-categories-prepare.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void findById_whenAbsentId_thenThrowNotFoundException() {
        assertThatThrownBy(() -> categoryService.findById(10000L))
                .isInstanceOf(NotFoundException.class);
    }
}