package ru.practicum.ewm.main.category.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "categories")
@Getter
@Setter
@ToString
public class CategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CategoryEntity categoryEntity = (CategoryEntity) o;
        return Objects.nonNull(id) && id.equals(categoryEntity.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
