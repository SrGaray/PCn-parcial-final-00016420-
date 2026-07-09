package com.uca.pncparcialfinalrestaurante.dto;

import com.uca.pncparcialfinalrestaurante.entity.EstadoMesa;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MesaResponse {
    private Long id;
    private Integer numero;
    private Integer capacidad;
    private EstadoMesa estado;
    private Long sucursalId;
}
