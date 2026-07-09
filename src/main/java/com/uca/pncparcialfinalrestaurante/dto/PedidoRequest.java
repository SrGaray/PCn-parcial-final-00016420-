package com.uca.pncparcialfinalrestaurante.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PedidoRequest {

    @NotNull
    private Long mesaId;

    @NotEmpty
    @Valid
    private List<ItemPedidoRequest> items;
}
