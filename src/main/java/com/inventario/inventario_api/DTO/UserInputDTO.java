package com.inventario.inventario_api.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserInputDTO {

    @NotBlank()
    private String name;
    @NotBlank()
    private String username;
    @NotBlank()
    @Email
    private String email;
    @NotBlank()
    private String role;
    @NotBlank()
    private String password;

    private String address;
    private String phone;

}