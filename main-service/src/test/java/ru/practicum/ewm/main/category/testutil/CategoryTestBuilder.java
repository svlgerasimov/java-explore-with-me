package ru.practicum.ewm.main.category.testutil;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import ru.practicum.ewm.main.category.dto.CategoryDtoOut;
import ru.practicum.ewm.main.category.model.CategoryEntity;

@NoArgsConstructor(staticName = "defaultBuilder")
@Getter
@Setter
@Accessors(chain = true, fluent = true)
public class CategoryTestBuilder {
    private Long id = 1L;
    private String name = "Cat Name 1";

    public CategoryDtoOut buildCategoryDtoOut() {
        return CategoryDtoOut.builder()
                .id(id)
                .name(name)
                .build();
    }

    public CategoryEntity buildCategoryEntity() {
        CategoryEntity entity = new CategoryEntity();
        entity.setId(id);
        entity.setName(name);
        return entity;
    }
}
