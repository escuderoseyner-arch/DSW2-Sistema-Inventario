package com.inventario.gestion.repository;

import com.inventario.gestion.model.Categoria;
import com.inventario.gestion.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByCategoriaId(Long idCategoria);
}
