package com.uca.pncparcialfinalrestaurante.service;

import com.uca.pncparcialfinalrestaurante.dto.SucursalRequest;
import com.uca.pncparcialfinalrestaurante.dto.SucursalResponse;
import com.uca.pncparcialfinalrestaurante.entity.Sucursal;
import com.uca.pncparcialfinalrestaurante.repository.SucursalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SucursalService {

    private final SucursalRepository sucursalRepository;

    public List<SucursalResponse> listar() {
        return sucursalRepository.findAll().stream().map(this::toResponse).toList();
    }

    public SucursalResponse obtener(Long id) {
        return toResponse(buscarSucursal(id));
    }

    public SucursalResponse crear(SucursalRequest request) {
        Sucursal sucursal = Sucursal.builder()
                .nombre(request.getNombre())
                .direccion(request.getDireccion())
                .build();
        return toResponse(sucursalRepository.save(sucursal));
    }

    public SucursalResponse actualizar(Long id, SucursalRequest request) {
        Sucursal sucursal = buscarSucursal(id);
        sucursal.setNombre(request.getNombre());
        sucursal.setDireccion(request.getDireccion());
        return toResponse(sucursalRepository.save(sucursal));
    }

    public void eliminar(Long id) {
        sucursalRepository.delete(buscarSucursal(id));
    }

    Sucursal buscarSucursal(Long id) {
        return sucursalRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sucursal no encontrada"));
    }

    private SucursalResponse toResponse(Sucursal sucursal) {
        return new SucursalResponse(sucursal.getId(), sucursal.getNombre(), sucursal.getDireccion());
    }
}
