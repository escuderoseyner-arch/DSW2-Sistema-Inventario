package com.inventario.gestion.repository;

import com.inventario.gestion.model.Categoria;
import com.inventario.gestion.model.Producto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pruebas de la capa de acceso a datos de Producto:
 * insertar, actualizar, eliminar, listar y listar por categoria. Con H2 en memoria.
 */
@DataJpaTest
class ProductoRepositoryTest {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    private Categoria bebidas;

    @BeforeEach
    void prepararCategoria() {
        bebidas = categoriaRepository.save(Categoria.builder().nombre("Bebidas").build());
    }

    private Producto nuevo(String nombre, Categoria categoria) {
        return Producto.builder()
                .nombre(nombre)
                .descripcion("Producto de prueba")
                .precio(new BigDecimal("3.50"))
                .stock(10)
                .stockMinimo(2)
                .categoria(categoria)
                .build();
    }

    @Test
    @DisplayName("Insertar un producto")
    void insertar() {
        Producto guardado = productoRepository.save(nuevo("Inca Kola 500ml", bebidas));

        assertThat(guardado.getId()).isNotNull();
        assertThat(guardado.getCategoria().getNombre()).isEqualTo("Bebidas");
    }

    @Test
    @DisplayName("Listar productos")
    void listar() {
        productoRepository.save(nuevo("Inca Kola 500ml", bebidas));
        productoRepository.save(nuevo("Coca Cola 500ml", bebidas));

        assertThat(productoRepository.findAll()).hasSize(2);
    }

    @Test
    @DisplayName("Actualizar un producto")
    void actualizar() {
        Producto guardado = productoRepository.save(nuevo("Inca Kola 500ml", bebidas));

        guardado.setPrecio(new BigDecimal("4.00"));
        productoRepository.save(guardado);

        Producto actualizado = productoRepository.findById(guardado.getId()).orElseThrow();
        assertThat(actualizado.getPrecio()).isEqualByComparingTo("4.00");
    }

    @Test
    @DisplayName("Eliminar un producto")
    void eliminar() {
        Producto guardado = productoRepository.save(nuevo("Inca Kola 500ml", bebidas));

        productoRepository.deleteById(guardado.getId());

        assertThat(productoRepository.findById(guardado.getId())).isEmpty();
    }

    @Test
    @DisplayName("Listar productos por categoria")
    void listarPorCategoria() {
        Categoria limpieza = categoriaRepository.save(Categoria.builder().nombre("Limpieza").build());
        productoRepository.save(nuevo("Inca Kola 500ml", bebidas));
        productoRepository.save(nuevo("Lejia 1L", limpieza));

        assertThat(productoRepository.findByCategoriaId(bebidas.getId()))
                .hasSize(1)
                .first()
                .extracting(Producto::getNombre)
                .isEqualTo("Inca Kola 500ml");
    }
}
