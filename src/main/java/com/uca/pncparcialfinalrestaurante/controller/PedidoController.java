package com.uca.pncparcialfinalrestaurante.controller;

import com.uca.pncparcialfinalrestaurante.dto.PedidoRequest;
import com.uca.pncparcialfinalrestaurante.dto.PedidoResponse;
import com.uca.pncparcialfinalrestaurante.security.UsuarioPrincipal;
import com.uca.pncparcialfinalrestaurante.service.PedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;

    @PreAuthorize("hasRole('CLIENTE')")
    @PostMapping
    public PedidoResponse crear(@Valid @RequestBody PedidoRequest request, @AuthenticationPrincipal UsuarioPrincipal actor) {
        return pedidoService.crear(request, actor);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public List<PedidoResponse> listar(@AuthenticationPrincipal UsuarioPrincipal actor) {
        return pedidoService.listar(actor);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public PedidoResponse obtener(@PathVariable Long id, @AuthenticationPrincipal UsuarioPrincipal actor) {
        return pedidoService.obtener(id, actor);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'ENCARGADO')")
    @PostMapping("/{id}/confirmar")
    public PedidoResponse confirmar(@PathVariable Long id, @AuthenticationPrincipal UsuarioPrincipal actor) {
        return pedidoService.confirmar(id, actor);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{id}/cancelar")
    public PedidoResponse cancelar(@PathVariable Long id, @AuthenticationPrincipal UsuarioPrincipal actor) {
        return pedidoService.cancelar(id, actor);
    }
}
