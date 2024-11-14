package com.inventario.inventario_api.model;

import jakarta.persistence.*;

@Entity
@Table(name = "usuarios") //TODO al usar user da conflito con H2 y Postgres
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-generate the ID
    private Long id;

    private String name;
    private String email;
    private String password;

    // Default constructor for JPA
    public User() {}

    // Constructor for easy creation
    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

}
