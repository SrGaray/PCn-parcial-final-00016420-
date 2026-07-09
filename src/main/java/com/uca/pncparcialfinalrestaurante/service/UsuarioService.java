package com.uca.pncparcialfinalrestaurante.service;

import com.uca.pncparcialfinalrestaurante.dto.UsuarioRequest;
import com.uca.pncparcialfinalrestaurante.dto.UsuarioResponse;
import com.uca.pncparcialfinalrestaurante.entity.Rol;
import com.uca.pncparcialfinalrestaurante.entity.Sucursal;
import com.uca.pncparcialfinalrestaurante.entity.Usuario;
import com.uca.pncparcialfinalrestaurante.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final SucursalService sucursalService;
    private final PasswordEncoder passwordEncoder;

    public List<UsuarioResponse> listar() {
        return usuarioRepository.findAll().stream().map(this::toResponse).toList();
    }

    public UsuarioResponse crear(UsuarioRequest request) {
        if (usuarioRepository.existsByUsername(request.getUsername())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El username ya esta en uso");
        }

        if (request.getRol() == Rol.ENCARGADO && request.getSucursalId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El encargado debe pertenecer a una sucursal");
        }

        Sucursal sucursal = request.getSucursalId() != null
                ? sucursalService.buscarSucursal(request.getSucursalId())
                : null;

        Usuario usuario = Usuario.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .nombreCompleto(request.getNombreCompleto())
                .rol(request.getRol())
                .sucursal(sucursal)
                .build();

        return toResponse(usuarioRepository.save(usuario));
    }

    public void eliminar(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        usuarioRepository.delete(usuario);
    }

    private UsuarioResponse toResponse(Usuario usuario) {
        Long sucursalId = usuario.getSucursal() != null ? usuario.getSucursal().getId() : null;
        return new UsuarioResponse(usuario.getId(), usuario.getUsername(), usuario.getNombreCompleto(),
                usuario.getRol(), sucursalId);
    }
}
