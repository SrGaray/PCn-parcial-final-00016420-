package com.uca.pncparcialfinalrestaurante.service;

import com.uca.pncparcialfinalrestaurante.dto.MesaRequest;
import com.uca.pncparcialfinalrestaurante.dto.MesaResponse;
import com.uca.pncparcialfinalrestaurante.entity.EstadoMesa;
import com.uca.pncparcialfinalrestaurante.entity.Mesa;
import com.uca.pncparcialfinalrestaurante.entity.Rol;
import com.uca.pncparcialfinalrestaurante.entity.Sucursal;
import com.uca.pncparcialfinalrestaurante.repository.MesaRepository;
import com.uca.pncparcialfinalrestaurante.security.UsuarioPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MesaService {

    private final MesaRepository mesaRepository;
    private final SucursalService sucursalService;

    public List<MesaResponse> listar(UsuarioPrincipal actor) {
        List<Mesa> mesas = actor.getUsuario().getRol() == Rol.ENCARGADO
                ? mesaRepository.findBySucursalId(actor.getSucursalId())
                : mesaRepository.findAll();
        return mesas.stream().map(this::toResponse).toList();
    }

    public MesaResponse crear(MesaRequest request) {
        Sucursal sucursal = sucursalService.buscarSucursal(request.getSucursalId());
        Mesa mesa = Mesa.builder()
                .numero(request.getNumero())
                .capacidad(request.getCapacidad())
                .estado(request.getEstado() != null ? request.getEstado() : EstadoMesa.LIBRE)
                .sucursal(sucursal)
                .build();
        return toResponse(mesaRepository.save(mesa));
    }

    public MesaResponse actualizar(Long id, MesaRequest request, UsuarioPrincipal actor) {
        Mesa mesa = buscarMesa(id);
        validarAccesoSucursal(mesa, actor);

        mesa.setNumero(request.getNumero());
        mesa.setCapacidad(request.getCapacidad());
        if (request.getEstado() != null) {
            mesa.setEstado(request.getEstado());
        }
        return toResponse(mesaRepository.save(mesa));
    }

    Mesa buscarMesa(Long id) {
        return mesaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mesa no encontrada"));
    }

    // Regla de negocio: un Encargado solo puede operar mesas de su propia sucursal.
    // No alcanza con validar el rol, hay que comparar la sucursal del usuario contra la de la mesa.
    void validarAccesoSucursal(Mesa mesa, UsuarioPrincipal actor) {
        boolean esEncargado = actor.getUsuario().getRol() == Rol.ENCARGADO;
        if (esEncargado && !mesa.getSucursal().getId().equals(actor.getSucursalId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tenes acceso a mesas de otra sucursal");
        }
    }

    private MesaResponse toResponse(Mesa mesa) {
        return new MesaResponse(mesa.getId(), mesa.getNumero(), mesa.getCapacidad(),
                mesa.getEstado(), mesa.getSucursal().getId());
    }
}
