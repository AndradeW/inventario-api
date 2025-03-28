package com.inventario.inventario_api.DTO;

import lombok.Builder;

@Builder
public record UserDTO(Long id,
                      String name,
                      String username,
                      String email,
                      String[] role,

                      String address,
                      String phone) {
}


