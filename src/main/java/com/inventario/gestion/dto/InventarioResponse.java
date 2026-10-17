package com.inventario.gestion.dto;

import com.inventario.gestion.model.Producto;

public record InventarioResponse(
        Long idProducto,
        String nombre,
        String categoria,
        Integer stock,
        Integer stockMinimo,
        boolean stockBajo
) {
    public static InventarioResponse desde(Producto p) {
        return new InventarioResponse(
                p.getId(),
                p.getNombre(),
                p.getCategoria().getNombre(),
                p.getStock(),
                p.getStockMinimo(),
                p.getStock() <= p.getStockMinimo());
    }
}
