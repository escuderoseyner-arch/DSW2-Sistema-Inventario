package com.inventario.gestion.controller;

import com.inventario.gestion.dto.ProductoRequest;
import com.inventario.gestion.model.Producto;
import com.inventario.gestion.service.ProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    //GUARDAR
    @PostMapping
    public ResponseEntity<Producto> guardar(@Valid @RequestBody ProductoRequest pRequest){
        return  ResponseEntity.status(HttpStatus.CREATED).body(productoService.guardarProducto(pRequest));
    }

    //LISTAR
    @GetMapping
    public ResponseEntity<List<Producto>> listar(){
        return ResponseEntity.ok(productoService.listarProductos());
    }

    //LISTAR SEGUN CATEGORIA
    @GetMapping("/categoria/{idCategoria}")
    public ResponseEntity<List<Producto>>listarSegunCategoria(@PathVariable Long idCategoria){
        return  ResponseEntity.ok(productoService.listarProductoSegunCategoria(idCategoria));
    }

    //BUSCAR SEGUN ID
    @GetMapping("/{id}")
    public ResponseEntity<Producto> buscarId(@PathVariable Long id){
        return ResponseEntity.ok(productoService.buscarProducto(id));
    }

    //ACTUALIZAR
    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizar(@PathVariable Long id, @Valid @RequestBody ProductoRequest pRequest){
        return ResponseEntity.ok(productoService.actualizarProducto(id, pRequest));
    }

    //ELIMINAR
    @DeleteMapping("/{id}")
    public ResponseEntity<Void>eliminar(@PathVariable Long id){
        productoService.eliminarProducto(id);
        return ResponseEntity.noContent().build();
    }
}
