package ru.practicum.ewm.main.compilations.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.ewm.main.event.model.EventEntity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "compilations_events")
@Getter
@Setter
@ToString
public class CompilationEventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "comp_id")
    CompilationEntity compilation;

    @ManyToOne
    @JoinColumn(name = "event_id")
    EventEntity event;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompilationEventEntity compilationEventEntity = (CompilationEventEntity) o;
        return Objects.nonNull(id) && id.equals(compilationEventEntity.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
