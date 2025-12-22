package com.example.kindergarten.domain.user;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@Table(name = "app_user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private UserRole role;
}
