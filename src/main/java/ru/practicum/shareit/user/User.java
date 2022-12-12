package ru.practicum.shareit.user;

import lombok.*;

import javax.persistence.*;

@Setter
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 255, nullable = false)
    private String name;
    @Column(length = 513, nullable = false)
    private String email;
}
