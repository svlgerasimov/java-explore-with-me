package ru.practicum.ewm.main.event.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.ewm.main.category.model.CategoryEntity;
import ru.practicum.ewm.main.event.dto.EventState;
import ru.practicum.ewm.main.user.model.UserEntity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "events")
@Getter
@Setter
@ToString
public class EventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "annotation")
    private String annotation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @ToString.Exclude
    private CategoryEntity category;

    @Column(name = "description")
    private String description;

    @Column(name = "event_date")
    private LocalDateTime eventDate;

    @AttributeOverrides({
            @AttributeOverride(name = "latitude", column = @Column(name = "location_latitude")),
            @AttributeOverride(name = "longitude", column = @Column(name = "location_longitude"))
    })
    private LocationModel location;

    @Column(name = "paid")
    private Boolean paid;

    @Column(name = "participant_limit")
    private Integer participantLimit;

    @Column(name = "request_moderation")
    private Boolean requestModeration;

    @Column(name = "title")
    private String title;

    @Column(name = "created_on")
    private LocalDateTime createdOn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator_id")
    @ToString.Exclude
    private UserEntity initiator;

    @Column(name = "published_on")
    private LocalDateTime publishedOn;

    @Column(name = "state")
    @Enumerated(value = EnumType.STRING)
    private EventState state;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventEntity eventEntity = (EventEntity) o;
        return Objects.nonNull(id) && id.equals(eventEntity.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
