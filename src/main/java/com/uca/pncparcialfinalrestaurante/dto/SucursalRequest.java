package com.uca.pncparcialfinalrestaurante.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SucursalRequest {

    @NotBlank
    private String nombre;

    @NotBlank
    private String direccion;
}
