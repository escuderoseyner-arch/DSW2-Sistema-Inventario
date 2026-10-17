package com.inventario.gestion.dto;

import com.inventario.gestion.model.Movimiento;
import com.inventario.gestion.model.TipoMovimiento;

import java.time.LocalDateTime;

public record MovimientoResponse(
        Long id,
        Long idProducto,
        String producto,
        TipoMovimiento tipo,
        Integer cantidad,
        LocalDateTime fecha,
        String usuario
) {
    public static MovimientoResponse desde(Movimiento m) {
        return new MovimientoResponse(
                m.getId(),
                m.getProducto().getId(),
                m.getProducto().getNombre(),
                m.getTipo(),
                m.getCantidad(),
                m.getFecha(),
                m.getUsuario().getUsuario());
    }
}
