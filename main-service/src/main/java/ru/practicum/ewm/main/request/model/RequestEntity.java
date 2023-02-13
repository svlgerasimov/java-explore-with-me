package ru.practicum.ewm.main.request.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.ewm.main.event.model.EventEntity;
import ru.practicum.ewm.main.request.dto.RequestState;
import ru.practicum.ewm.main.user.model.UserEntity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "requests")
@Getter
@Setter
@ToString
public class RequestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created")
    private LocalDateTime created;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    @ToString.Exclude
    private EventEntity event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id")
    @ToString.Exclude
    private UserEntity requester;

    @Column(name = "status")
    private RequestState status;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RequestEntity requestEntity = (RequestEntity) o;
        return Objects.nonNull(id) && id.equals(requestEntity.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
