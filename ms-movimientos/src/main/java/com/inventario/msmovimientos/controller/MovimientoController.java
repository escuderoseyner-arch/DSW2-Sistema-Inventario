package com.inventario.msmovimientos.controller;

import com.inventario.msmovimientos.model.Movimiento;
import com.inventario.msmovimientos.service.MovimientoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movimientos")
public class MovimientoController {

    @Autowired
    private MovimientoService movimientoService;

    @GetMapping
    public List<Movimiento> listar() {
        return movimientoService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Movimiento> buscar(@PathVariable Long id) {
        return movimientoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/producto/{productoId}")
    public List<Movimiento> buscarPorProducto(@PathVariable Long productoId) {
        return movimientoService.buscarPorProducto(productoId);
    }

    @PostMapping
    public Movimiento registrar(@RequestBody Movimiento movimiento) {
        return movimientoService.registrar(movimiento);
    }
}
