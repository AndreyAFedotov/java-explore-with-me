package ru.practicum.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;


@Entity
@Table(name = "hits")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Hit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "app_id")
    @NotNull
    private App app;

    @Column(name = "uri", length = 4000, nullable = false)
    private String uri;

    @Column(name = "ip", length = 32, nullable = false)
    private String ip;

    @Column(name = "timestamp", nullable = false)
    @PastOrPresent
    private LocalDateTime timestamp;
}
