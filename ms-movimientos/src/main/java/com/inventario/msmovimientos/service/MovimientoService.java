package com.inventario.msmovimientos.service;

import com.inventario.msmovimientos.model.Movimiento;
import com.inventario.msmovimientos.repository.MovimientoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
public class MovimientoService {

    @Autowired
    private MovimientoRepository movimientoRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${ms-productos.url}")
    private String msProductosUrl;

    public List<Movimiento> listarTodos() {
        return movimientoRepository.findAll();
    }

    public Optional<Movimiento> buscarPorId(Long id) {
        return movimientoRepository.findById(id);
    }

    public List<Movimiento> buscarPorProducto(Long productoId) {
        return movimientoRepository.findByProductoId(productoId);
    }

    public Movimiento registrar(Movimiento movimiento) {
        Movimiento saved = movimientoRepository.save(movimiento);

        // Comunicar con ms-productos para actualizar stock
        int cantidad = movimiento.getCantidad();
        if ("SALIDA".equalsIgnoreCase(movimiento.getTipo())) {
            cantidad = -cantidad;
        }

        restTemplate.put(
            msProductosUrl + "/api/productos/" + movimiento.getProductoId() + "/stock?cantidad=" + cantidad,
            null
        );

        return saved;
    }
}
