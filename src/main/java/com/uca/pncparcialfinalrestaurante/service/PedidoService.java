package com.uca.pncparcialfinalrestaurante.service;

import com.uca.pncparcialfinalrestaurante.dto.ItemPedidoRequest;
import com.uca.pncparcialfinalrestaurante.dto.ItemPedidoResponse;
import com.uca.pncparcialfinalrestaurante.dto.PedidoRequest;
import com.uca.pncparcialfinalrestaurante.dto.PedidoResponse;
import com.uca.pncparcialfinalrestaurante.entity.*;
import com.uca.pncparcialfinalrestaurante.repository.PedidoRepository;
import com.uca.pncparcialfinalrestaurante.repository.ProductoRepository;
import com.uca.pncparcialfinalrestaurante.repository.UsuarioRepository;
import com.uca.pncparcialfinalrestaurante.security.UsuarioPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;
    private final MesaService mesaService;

    public PedidoResponse crear(PedidoRequest request, UsuarioPrincipal actor) {
        Mesa mesa = mesaService.buscarMesa(request.getMesaId());
        Usuario cliente = usuarioRepository.findById(actor.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario invalido"));

        Pedido pedido = Pedido.builder()
                .cliente(cliente)
                .mesa(mesa)
                .estado(EstadoPedido.PENDIENTE)
                .fechaCreacion(LocalDateTime.now())
                .build();

        for (ItemPedidoRequest itemReq : request.getItems()) {
            Producto producto = productoRepository.findById(itemReq.getProductoId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));

            if (!Boolean.TRUE.equals(producto.getDisponible())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El producto " + producto.getNombre() + " no esta disponible");
            }

            pedido.getItems().add(ItemPedido.builder()
                    .pedido(pedido)
                    .producto(producto)
                    .cantidad(itemReq.getCantidad())
                    .precioUnitario(producto.getPrecio())
                    .build());
        }

        return toResponse(pedidoRepository.save(pedido));
    }

    public List<PedidoResponse> listar(UsuarioPrincipal actor) {
        List<Pedido> pedidos = switch (actor.getUsuario().getRol()) {
            case ADMIN -> pedidoRepository.findAll();
            case ENCARGADO -> pedidoRepository.findByMesaSucursalId(actor.getSucursalId());
            case CLIENTE -> pedidoRepository.findByClienteId(actor.getId());
        };
        return pedidos.stream().map(this::toResponse).toList();
    }

    public PedidoResponse obtener(Long id, UsuarioPrincipal actor) {
        Pedido pedido = buscarPedido(id);
        validarAcceso(pedido, actor);
        return toResponse(pedido);
    }

    public PedidoResponse confirmar(Long id, UsuarioPrincipal actor) {
        Pedido pedido = buscarPedido(id);
        validarAcceso(pedido, actor);

        if (actor.getUsuario().getRol() == Rol.CLIENTE) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Un cliente no puede confirmar pedidos");
        }
        if (pedido.getEstado() != EstadoPedido.PENDIENTE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Solo se pueden confirmar pedidos pendientes");
        }

        pedido.setEstado(EstadoPedido.CONFIRMADO);
        return toResponse(pedidoRepository.save(pedido));
    }

    public PedidoResponse cancelar(Long id, UsuarioPrincipal actor) {
        Pedido pedido = buscarPedido(id);
        validarAcceso(pedido, actor);

        if (pedido.getEstado() == EstadoPedido.ENTREGADO || pedido.getEstado() == EstadoPedido.CANCELADO) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El pedido ya no se puede cancelar");
        }

        pedido.setEstado(EstadoPedido.CANCELADO);
        return toResponse(pedidoRepository.save(pedido));
    }

    private Pedido buscarPedido(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido no encontrado"));
    }

    // Regla de negocio no trivial: autorizacion por atributo (sucursal), no solo por rol.
    // Un ENCARGADO solo puede operar pedidos de mesas de SU sucursal; se compara
    // la sucursal del usuario autenticado contra la sucursal de la mesa del pedido.
    private void validarAcceso(Pedido pedido, UsuarioPrincipal actor) {
        Rol rol = actor.getUsuario().getRol();

        if (rol == Rol.ADMIN) {
            return;
        }

        if (rol == Rol.ENCARGADO) {
            Long sucursalPedido = pedido.getMesa().getSucursal().getId();
            if (!sucursalPedido.equals(actor.getSucursalId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tenes acceso a pedidos de otra sucursal");
            }
            return;
        }

        // CLIENTE: solo sus propios pedidos
        if (!pedido.getCliente().getId().equals(actor.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tenes acceso a pedidos de otro cliente");
        }
    }

    private PedidoResponse toResponse(Pedido pedido) {
        List<ItemPedidoResponse> items = pedido.getItems().stream()
                .map(i -> new ItemPedidoResponse(i.getProducto().getId(), i.getProducto().getNombre(),
                        i.getCantidad(), i.getPrecioUnitario()))
                .toList();

        BigDecimal total = pedido.getItems().stream()
                .map(i -> i.getPrecioUnitario().multiply(BigDecimal.valueOf(i.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new PedidoResponse(pedido.getId(), pedido.getCliente().getUsername(), pedido.getMesa().getId(),
                pedido.getMesa().getSucursal().getId(), pedido.getEstado(), pedido.getFechaCreacion(), items, total);
    }
}
