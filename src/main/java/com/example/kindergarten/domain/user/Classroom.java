package com.example.kindergarten.domain.user;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "classroom")
public class Classroom {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    protected Classroom() {}

    public Classroom(String name) {
        this.name = name;
    }
}

