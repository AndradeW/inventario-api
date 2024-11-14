package com.inventario.inventario_api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "usuarios") //TODO al usar user da conflito con H2 y Postgres
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank()
    @Column(name = "nombre")
    @JsonProperty("nombre")
    private String name;

    @NotBlank(message = "El correo electronico es obligatorio")
    @Email
    @JsonProperty("correo_electronico")
    private String email;

    @NotBlank(message = "El password es obligatorio")
    private String password;

    private String direccion;

    private String telefono;
}
