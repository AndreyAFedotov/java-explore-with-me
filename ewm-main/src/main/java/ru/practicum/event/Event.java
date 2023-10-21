package ru.practicum.event;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import ru.practicum.category.Category;
import ru.practicum.enums.EventState;
import ru.practicum.location.Location;
import ru.practicum.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "annotation", length = 2000)
    private String annotation;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "paid")
    private Boolean paid;

    @Column(name = "event_date")
    private LocalDateTime eventDate;

    @ManyToOne
    @JoinColumn(name = "initiator_id")
    private User initiator;

    @Column(name = "description", length = 5000)
    private String description;

    @Column(name = "participant_limit")
    private Integer participantLimit;

    @Column(name = "state", length = 100)
    @Enumerated(EnumType.STRING)
    private EventState state;

    @CreationTimestamp
    @Column(name = "created_on")
    private LocalDateTime createdOn;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;

    @Column(name = "request_moderation")
    private Boolean requestModeration;

    @Column(name = "published_on")
    private LocalDateTime publishedOn;

    @Column(name = "title", length = 255)
    private String title;

}
