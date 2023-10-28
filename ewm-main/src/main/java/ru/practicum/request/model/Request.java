package ru.practicum.request.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import ru.practicum.enums.RequestStatus;
import ru.practicum.event.Event;
import ru.practicum.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "requests")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "requester_id", nullable = false)
    private User requester;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50, nullable = false)
    private RequestStatus status;

    @CreationTimestamp
    @Column(name = "created_on")
    private LocalDateTime created;
}
