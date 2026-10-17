package com.inventario.gestion.controller;

import com.inventario.gestion.dto.MovimientoRequest;
import com.inventario.gestion.dto.MovimientoResponse;
import com.inventario.gestion.service.MovimientoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movimientos")
@RequiredArgsConstructor
public class MovimientoController {

    private final MovimientoService movimientoService;

    @PostMapping("/entrada")
    public ResponseEntity<MovimientoResponse> registrarEntrada(@Valid @RequestBody MovimientoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(movimientoService.registrarEntrada(request));
    }

    @PostMapping("/salida")
    public ResponseEntity<MovimientoResponse> registrarSalida(@Valid @RequestBody MovimientoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(movimientoService.registrarSalida(request));
    }

    @GetMapping
    public ResponseEntity<List<MovimientoResponse>> listar() {
        return ResponseEntity.ok(movimientoService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovimientoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(movimientoService.buscarPorId(id));
    }

    @GetMapping("/producto/{id}")
    public ResponseEntity<List<MovimientoResponse>> listarPorProducto(@PathVariable Long id) {
        return ResponseEntity.ok(movimientoService.listarPorProducto(id));
    }
}
