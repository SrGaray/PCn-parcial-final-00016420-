package com.uca.pncparcialfinalrestaurante.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SucursalResponse {
    private Long id;
    private String nombre;
    private String direccion;
}
