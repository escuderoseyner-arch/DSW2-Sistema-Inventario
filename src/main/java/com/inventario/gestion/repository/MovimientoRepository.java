package com.inventario.gestion.repository;

import com.inventario.gestion.model.Movimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {

    List<Movimiento> findAllByOrderByFechaDescIdDesc();

    List<Movimiento> findByProductoIdOrderByFechaDescIdDesc(Long idProducto);
}
