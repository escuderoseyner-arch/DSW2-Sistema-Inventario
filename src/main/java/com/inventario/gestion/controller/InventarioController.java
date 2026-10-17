package com.inventario.gestion.controller;

import com.inventario.gestion.dto.InventarioResponse;
import com.inventario.gestion.service.InventarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/inventario")
@RequiredArgsConstructor
public class InventarioController {

    private final InventarioService inventarioService;

    @GetMapping
    public ResponseEntity<List<InventarioResponse>> listar() {
        return ResponseEntity.ok(inventarioService.listar());
    }

    @GetMapping("/producto/{id}")
    public ResponseEntity<InventarioResponse> buscarPorProducto(@PathVariable Long id) {
        return ResponseEntity.ok(inventarioService.buscarPorProducto(id));
    }

    @GetMapping("/stock-bajo")
    public ResponseEntity<List<InventarioResponse>> listarStockBajo() {
        return ResponseEntity.ok(inventarioService.listarStockBajo());
    }
}
