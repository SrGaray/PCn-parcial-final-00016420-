package com.uca.pncparcialfinalrestaurante.dto;

import com.uca.pncparcialfinalrestaurante.entity.EstadoPedido;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class PedidoResponse {
    private Long id;
    private String clienteUsername;
    private Long mesaId;
    private Long sucursalId;
    private EstadoPedido estado;
    private LocalDateTime fechaCreacion;
    private List<ItemPedidoResponse> items;
    private BigDecimal total;
}
