package com.uca.pncparcialfinalrestaurante.dto;

import com.uca.pncparcialfinalrestaurante.entity.EstadoMesa;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MesaRequest {

    @NotNull
    private Integer numero;

    @NotNull
    private Integer capacidad;

    private EstadoMesa estado;

    @NotNull
    private Long sucursalId;
}
