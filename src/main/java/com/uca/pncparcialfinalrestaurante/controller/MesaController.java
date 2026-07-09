package com.uca.pncparcialfinalrestaurante.controller;

import com.uca.pncparcialfinalrestaurante.dto.MesaRequest;
import com.uca.pncparcialfinalrestaurante.dto.MesaResponse;
import com.uca.pncparcialfinalrestaurante.security.UsuarioPrincipal;
import com.uca.pncparcialfinalrestaurante.service.MesaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mesas")
@RequiredArgsConstructor
public class MesaController {

    private final MesaService mesaService;

    @PreAuthorize("hasAnyRole('ADMIN', 'ENCARGADO')")
    @GetMapping
    public List<MesaResponse> listar(@AuthenticationPrincipal UsuarioPrincipal actor) {
        return mesaService.listar(actor);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public MesaResponse crear(@Valid @RequestBody MesaRequest request) {
        return mesaService.crear(request);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'ENCARGADO')")
    @PutMapping("/{id}")
    public MesaResponse actualizar(@PathVariable Long id, @Valid @RequestBody MesaRequest request,
                                    @AuthenticationPrincipal UsuarioPrincipal actor) {
        return mesaService.actualizar(id, request, actor);
    }
}
