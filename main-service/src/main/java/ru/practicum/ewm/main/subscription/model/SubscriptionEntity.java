package ru.practicum.ewm.main.subscription.model;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.ewm.main.user.model.UserEntity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "subscriptions")
@Getter
@Setter
public class SubscriptionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "subscriber_id")
    private UserEntity subscriber;

    @ManyToOne
    @JoinColumn(name = "publisher_id")
    private UserEntity publisher;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubscriptionEntity subscriptionEntity = (SubscriptionEntity) o;
        return Objects.nonNull(id) && id.equals(subscriptionEntity.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "SubscriptionEntity{" +
                "id=" + id +
                ", subscriber=" + subscriber.getId() +
                ", publisher=" + publisher.getId() +
                '}';
    }
}
