package ru.practicum.ewm.stats.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "apps")
@Getter
@Setter
@ToString
public class AppEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AppEntity appEntity = (AppEntity) o;
        return Objects.nonNull(id) && id.equals(appEntity.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
