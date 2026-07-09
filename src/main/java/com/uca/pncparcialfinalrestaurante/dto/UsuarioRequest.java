package com.uca.pncparcialfinalrestaurante.dto;

import com.uca.pncparcialfinalrestaurante.entity.Rol;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioRequest {

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @NotBlank
    private String nombreCompleto;

    @NotNull
    private Rol rol;

    // obligatorio solo si el rol es ENCARGADO
    private Long sucursalId;
}
