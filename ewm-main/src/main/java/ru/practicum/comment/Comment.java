package ru.practicum.comment;

import lombok.*;
import ru.practicum.enums.MessageUpdateInitiator;
import ru.practicum.event.Event;
import ru.practicum.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reply_to_id")
    private Comment replyToId;

    @Column(name = "answered")
    private Boolean answered = false;

    @Column(name = "text", length = 7000, nullable = false)
    private String text;

    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn;

    @Column(name = "last_updated_on", nullable = false)
    private LocalDateTime lastUpdatedOn;

    @Column(name = "update_initiator")
    private MessageUpdateInitiator updateInitiator;

}
