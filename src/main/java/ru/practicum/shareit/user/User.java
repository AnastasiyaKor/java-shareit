package ru.practicum.shareit.user;

import lombok.*;

import javax.persistence.*;

@Setter
@Getter
@ToString(onlyExplicitlyIncluded = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ToString.Include
    private Long id;
    @Column(length = 255, nullable = false)
    @ToString.Include
    private String name;
    @Column(length = 513, nullable = false)
    @ToString.Include
    private String email;
}
