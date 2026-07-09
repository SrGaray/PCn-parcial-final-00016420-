package com.uca.pncparcialfinalrestaurante.controller;

import com.uca.pncparcialfinalrestaurante.dto.SucursalRequest;
import com.uca.pncparcialfinalrestaurante.dto.SucursalResponse;
import com.uca.pncparcialfinalrestaurante.service.SucursalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sucursales")
@RequiredArgsConstructor
public class SucursalController {

    private final SucursalService sucursalService;

    @GetMapping
    public List<SucursalResponse> listar() {
        return sucursalService.listar();
    }

    @GetMapping("/{id}")
    public SucursalResponse obtener(@PathVariable Long id) {
        return sucursalService.obtener(id);
    }

    @PostMapping
    public SucursalResponse crear(@Valid @RequestBody SucursalRequest request) {
        return sucursalService.crear(request);
    }

    @PutMapping("/{id}")
    public SucursalResponse actualizar(@PathVariable Long id, @Valid @RequestBody SucursalRequest request) {
        return sucursalService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        sucursalService.eliminar(id);
    }
}
