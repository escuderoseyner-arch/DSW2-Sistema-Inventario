package com.inventario.msproductos.controller;

import com.inventario.msproductos.model.Producto;
import com.inventario.msproductos.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @GetMapping
    public List<Producto> listar() {
        return productoService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> buscar(@PathVariable Long id) {
        return productoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Producto crear(@RequestBody Producto producto) {
        return productoService.guardar(producto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizar(@PathVariable Long id, @RequestBody Producto producto) {
        return productoService.buscarPorId(id)
                .map(p -> {
                    p.setCodigo(producto.getCodigo());
                    p.setNombre(producto.getNombre());
                    p.setPrecio(producto.getPrecio());
                    p.setStockActual(producto.getStockActual());
                    p.setStockMinimo(producto.getStockMinimo());
                    p.setCategoria(producto.getCategoria());
                    return ResponseEntity.ok(productoService.guardar(p));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/stock")
    public ResponseEntity<String> actualizarStock(@PathVariable Long id, @RequestParam int cantidad) {
        productoService.actualizarStock(id, cantidad);
        return ResponseEntity.ok("Stock actualizado");
    }
}
