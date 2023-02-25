package ru.practicum.ewm.main.event.model;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.ewm.main.event.dto.EventStateAdminAction;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "reviews")
@Getter
@Setter
public class ReviewEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private EventEntity event;

    @Column(name = "action")
    @Enumerated(value = EnumType.STRING)
    private EventStateAdminAction action;

    @Column(name = "comment")
    private String comment;

    @Column(name = "created_on")
    private LocalDateTime createdOn;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReviewEntity reviewEntity = (ReviewEntity) o;
        return Objects.nonNull(id) && id.equals(reviewEntity.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "ReviewEntity{" +
                "id=" + id +
                ", eventId=" + event.getId() +
                ", action=" + action +
                ", comment='" + comment + '\'' +
                ", createdOn=" + createdOn +
                '}';
    }
}
