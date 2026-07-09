package com.uca.pncparcialfinalrestaurante.config;

import com.uca.pncparcialfinalrestaurante.entity.*;
import com.uca.pncparcialfinalrestaurante.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final SucursalRepository sucursalRepository;
    private final MesaRepository mesaRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (sucursalRepository.count() > 0) {
            return;
        }

        Sucursal centro = sucursalRepository.save(Sucursal.builder()
                .nombre("Sucursal Centro")
                .direccion("Av. Principal 123")
                .build());

        Sucursal norte = sucursalRepository.save(Sucursal.builder()
                .nombre("Sucursal Norte")
                .direccion("Calle Los Pinos 456")
                .build());

        mesaRepository.save(Mesa.builder().numero(1).capacidad(4).estado(EstadoMesa.LIBRE).sucursal(centro).build());
        mesaRepository.save(Mesa.builder().numero(2).capacidad(2).estado(EstadoMesa.LIBRE).sucursal(centro).build());
        mesaRepository.save(Mesa.builder().numero(1).capacidad(6).estado(EstadoMesa.LIBRE).sucursal(norte).build());

        productoRepository.save(Producto.builder().nombre("Pupusa revuelta").descripcion("Queso y chicharron")
                .precio(new BigDecimal("1.25")).disponible(true).build());
        productoRepository.save(Producto.builder().nombre("Gaseosa 500ml").descripcion(null)
                .precio(new BigDecimal("1.50")).disponible(true).build());
        productoRepository.save(Producto.builder().nombre("Yuca frita").descripcion("Porcion con curtido")
                .precio(new BigDecimal("3.00")).disponible(true).build());

        usuarioRepository.save(Usuario.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin123"))
                .nombreCompleto("Administrador General")
                .rol(Rol.ADMIN)
                .build());

        usuarioRepository.save(Usuario.builder()
                .username("encargado.centro")
                .password(passwordEncoder.encode("encargado123"))
                .nombreCompleto("Encargado Sucursal Centro")
                .rol(Rol.ENCARGADO)
                .sucursal(centro)
                .build());

        usuarioRepository.save(Usuario.builder()
                .username("encargado.norte")
                .password(passwordEncoder.encode("encargado123"))
                .nombreCompleto("Encargado Sucursal Norte")
                .rol(Rol.ENCARGADO)
                .sucursal(norte)
                .build());

        usuarioRepository.save(Usuario.builder()
                .username("cliente1")
                .password(passwordEncoder.encode("cliente123"))
                .nombreCompleto("Cliente de Prueba")
                .rol(Rol.CLIENTE)
                .build());
    }
}
