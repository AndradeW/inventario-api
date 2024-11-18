package com.inventario.inventario_api.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "usuarios") //TODO al usar user da conflito con H2 y Postgres
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String username;
    private String email;
    private String password;
    private String role;

    private String address;
    private String phone;

    private boolean locked;
    private boolean disabled;

}
