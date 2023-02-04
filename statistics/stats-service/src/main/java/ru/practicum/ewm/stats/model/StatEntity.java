package ru.practicum.ewm.stats.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "stats")
@Getter
@Setter
@ToString
public class StatEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "app")
    private String app;

    @Column(name = "url")
    private String url;

    @Column(name = "ip")
    private String ip;

    @Column(name = "hit_timestamp")
    private LocalDateTime timestamp;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StatEntity statEntity = (StatEntity) o;
        return Objects.nonNull(id) && id.equals(statEntity.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
