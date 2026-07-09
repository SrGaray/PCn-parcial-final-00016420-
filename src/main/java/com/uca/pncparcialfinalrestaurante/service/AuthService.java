package com.uca.pncparcialfinalrestaurante.service;

import com.uca.pncparcialfinalrestaurante.dto.LoginRequest;
import com.uca.pncparcialfinalrestaurante.dto.LoginResponse;
import com.uca.pncparcialfinalrestaurante.entity.RefreshToken;
import com.uca.pncparcialfinalrestaurante.entity.Usuario;
import com.uca.pncparcialfinalrestaurante.repository.RefreshTokenRepository;
import com.uca.pncparcialfinalrestaurante.repository.UsuarioRepository;
import com.uca.pncparcialfinalrestaurante.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public LoginResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario o contrasena invalidos");
        }

        Usuario usuario = usuarioRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario o contrasena invalidos"));

        return generarTokens(usuario);
    }

    public LoginResponse refresh(String refreshTokenStr) {
        RefreshToken guardado = refreshTokenRepository.findByToken(refreshTokenStr)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token invalido"));

        if (guardado.isRevocado() || guardado.getFechaExpiracion().isBefore(Instant.now())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token expirado o revocado");
        }

        try {
            if (jwtService.isTokenExpirado(refreshTokenStr)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token expirado");
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token invalido");
        }

        // rotacion: el refresh token usado queda invalido y se emite uno nuevo
        guardado.setRevocado(true);
        refreshTokenRepository.save(guardado);

        return generarTokens(guardado.getUsuario());
    }

    private LoginResponse generarTokens(Usuario usuario) {
        Long sucursalId = usuario.getSucursal() != null ? usuario.getSucursal().getId() : null;

        String accessToken = jwtService.generarAccessToken(usuario.getUsername(), usuario.getRol().name(), sucursalId);
        String refreshToken = jwtService.generarRefreshToken(usuario.getUsername());

        RefreshToken entidad = RefreshToken.builder()
                .token(refreshToken)
                .usuario(usuario)
                .fechaExpiracion(Instant.now().plusMillis(jwtService.getRefreshExpirationMs()))
                .revocado(false)
                .build();
        refreshTokenRepository.save(entidad);

        return new LoginResponse(accessToken, refreshToken, "Bearer");
    }
}
