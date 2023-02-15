package ru.practicum.ewm.main.compilations.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "compilations")
@Getter
@Setter
@ToString
public class CompilationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pinned")
    Boolean pinned;

    @Column(name = "title")
    String title;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompilationEntity compilationEntity = (CompilationEntity) o;
        return Objects.nonNull(id) && id.equals(compilationEntity.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
