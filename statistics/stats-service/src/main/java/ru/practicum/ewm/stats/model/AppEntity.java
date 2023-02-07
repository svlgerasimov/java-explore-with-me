package ru.practicum.ewm.stats.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "apps")
@Getter
@Setter
@ToString
public class AppEntity {

    @Id
    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AppEntity appEntity = (AppEntity) o;
        return Objects.nonNull(name) && name.equals(appEntity.name);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
