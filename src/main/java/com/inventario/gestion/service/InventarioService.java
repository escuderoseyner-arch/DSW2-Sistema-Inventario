package com.inventario.gestion.service;

import com.inventario.gestion.dto.InventarioResponse;
import com.inventario.gestion.exception.RecursoNoEncontradoException;
import com.inventario.gestion.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventarioService {

    private final ProductoRepository productoRepository;

    @Transactional(readOnly = true)
    public List<InventarioResponse> listar() {
        return productoRepository.findAll().stream()
                .map(InventarioResponse::desde)
                .toList();
    }

    @Transactional(readOnly = true)
    public InventarioResponse buscarPorProducto(Long idProducto) {
        return productoRepository.findById(idProducto)
                .map(InventarioResponse::desde)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe el producto con el id: " + idProducto));
    }

    @Transactional(readOnly = true)
    public List<InventarioResponse> listarStockBajo() {
        return productoRepository.findAll().stream()
                .filter(p -> p.getStock() <= p.getStockMinimo())
                .map(InventarioResponse::desde)
                .toList();
    }
}
