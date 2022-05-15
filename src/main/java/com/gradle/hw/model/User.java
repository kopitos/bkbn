package com.gradle.hw.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = true, length = 45)
    @NonNull
    private String email;

    @Column(nullable = false, length = 64)
    private String password;

    @Column(name = "first name", nullable = false, length = 20)
    @NonNull
    private String firstName;

    @Column(name = "last name", nullable = false, length = 20)
    @NonNull
    private String lastName;

}
