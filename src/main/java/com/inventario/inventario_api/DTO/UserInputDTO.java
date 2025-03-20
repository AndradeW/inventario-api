package com.inventario.inventario_api.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@Builder
public class UserInputDTO {

    @NotBlank()
    private String username;
    @NotBlank()
    @Email
    private String email;
    @NotBlank()
    private String password;

    private String[] role;

    private String name;
    private String address;
    private String phone;
}
