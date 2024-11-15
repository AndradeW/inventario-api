package com.inventario.inventario_api.DTO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserDTO {

    private Long id;

    private String name;
    private String username;
    private String email;
    private String role;

    private String address;
    private String phone;

}


