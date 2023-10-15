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

    @Column(name = "uri")
    @NotBlank
    private String uri;

    @Column(name = "ip")
    @NotBlank
    private String ip;

    @Column(name = "timestamp")
    @NotNull
    @PastOrPresent
    private LocalDateTime timestamp;
}
