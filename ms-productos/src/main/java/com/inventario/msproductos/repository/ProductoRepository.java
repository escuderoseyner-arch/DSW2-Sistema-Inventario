package com.inventario.msproductos.repository;

import com.inventario.msproductos.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByStockActualLessThanEqual(int stockMinimo);
}
