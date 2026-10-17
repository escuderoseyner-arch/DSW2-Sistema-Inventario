package com.inventario.gestion.service;

import com.inventario.gestion.dto.MovimientoRequest;
import com.inventario.gestion.dto.MovimientoResponse;
import com.inventario.gestion.exception.RecursoNoEncontradoException;
import com.inventario.gestion.exception.ReglaNegocioException;
import com.inventario.gestion.model.Movimiento;
import com.inventario.gestion.model.Producto;
import com.inventario.gestion.model.TipoMovimiento;
import com.inventario.gestion.model.Usuario;
import com.inventario.gestion.repository.MovimientoRepository;
import com.inventario.gestion.repository.ProductoRepository;
import com.inventario.gestion.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MovimientoService {

    private final MovimientoRepository movimientoRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public MovimientoResponse registrarEntrada(MovimientoRequest request) {
        Producto producto = buscarProducto(request.idProducto());
        producto.setStock(producto.getStock() + request.cantidad());
        return guardar(producto, TipoMovimiento.ENTRADA, request.cantidad());
    }

    @Transactional
    public MovimientoResponse registrarSalida(MovimientoRequest request) {
        Producto producto = buscarProducto(request.idProducto());
        if (request.cantidad() > producto.getStock()) {
            throw new ReglaNegocioException("Stock insuficiente: el producto '" + producto.getNombre()
                    + "' tiene " + producto.getStock() + " unidades y se pidieron " + request.cantidad());
        }
        producto.setStock(producto.getStock() - request.cantidad());
        return guardar(producto, TipoMovimiento.SALIDA, request.cantidad());
    }

    @Transactional(readOnly = true)
    public List<MovimientoResponse> listar() {
        return movimientoRepository.findAllByOrderByFechaDescIdDesc().stream()
                .map(MovimientoResponse::desde)
                .toList();
    }

    @Transactional(readOnly = true)
    public MovimientoResponse buscarPorId(Long id) {
        return movimientoRepository.findById(id)
                .map(MovimientoResponse::desde)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el movimiento con id " + id));
    }

    @Transactional(readOnly = true)
    public List<MovimientoResponse> listarPorProducto(Long idProducto) {
        buscarProducto(idProducto);
        return movimientoRepository.findByProductoIdOrderByFechaDescIdDesc(idProducto).stream()
                .map(MovimientoResponse::desde)
                .toList();
    }

    private MovimientoResponse guardar(Producto producto, TipoMovimiento tipo, Integer cantidad) {
        productoRepository.save(producto);
        Movimiento movimiento = Movimiento.builder()
                .producto(producto)
                .usuario(usuarioActual())
                .tipo(tipo)
                .cantidad(cantidad)
                .fecha(LocalDateTime.now())
                .build();
        return MovimientoResponse.desde(movimientoRepository.save(movimiento));
    }

    private Producto buscarProducto(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el producto con el id: " + id));
    }

    private Usuario usuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            throw new RecursoNoEncontradoException("No hay un usuario autenticado para registrar el movimiento");
        }
        return usuarioRepository.findByUsuario(auth.getName())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe el usuario responsable: " + auth.getName()));
    }
}
