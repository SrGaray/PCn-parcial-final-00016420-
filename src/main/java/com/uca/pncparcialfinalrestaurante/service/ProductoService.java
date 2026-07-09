package com.uca.pncparcialfinalrestaurante.service;

import com.uca.pncparcialfinalrestaurante.dto.ProductoRequest;
import com.uca.pncparcialfinalrestaurante.dto.ProductoResponse;
import com.uca.pncparcialfinalrestaurante.entity.Producto;
import com.uca.pncparcialfinalrestaurante.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;

    public List<ProductoResponse> listar() {
        return productoRepository.findAll().stream().map(this::toResponse).toList();
    }

    public ProductoResponse crear(ProductoRequest request) {
        Producto producto = Producto.builder()
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .precio(request.getPrecio())
                .disponible(request.getDisponible() != null ? request.getDisponible() : true)
                .build();
        return toResponse(productoRepository.save(producto));
    }

    public ProductoResponse actualizar(Long id, ProductoRequest request) {
        Producto producto = buscarProducto(id);
        producto.setNombre(request.getNombre());
        producto.setDescripcion(request.getDescripcion());
        producto.setPrecio(request.getPrecio());
        if (request.getDisponible() != null) {
            producto.setDisponible(request.getDisponible());
        }
        return toResponse(productoRepository.save(producto));
    }

    public void eliminar(Long id) {
        productoRepository.delete(buscarProducto(id));
    }

    Producto buscarProducto(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
    }

    private ProductoResponse toResponse(Producto producto) {
        return new ProductoResponse(producto.getId(), producto.getNombre(), producto.getDescripcion(),
                producto.getPrecio(), producto.getDisponible());
    }
}
