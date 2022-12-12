package ru.practicum.shareit.item;

import lombok.*;
import ru.practicum.shareit.user.User;

import javax.persistence.*;

@Setter
@Getter

@Builder
@Entity(name = "items")
@Table(name = "items", schema = "public")
@RequiredArgsConstructor
@AllArgsConstructor
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name", length = 255)
    private String name;
    @Column(name = "description", length = 1024)
    private String description;
    @Column(name = "available")
    private Boolean available;
    @ManyToOne
    @JoinColumn(name = "owner", nullable = false)
    private User owner;
    @Column(name = "request")
    private Long request;
}
