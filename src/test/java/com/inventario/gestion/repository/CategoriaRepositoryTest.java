package com.inventario.gestion.repository;

import com.inventario.gestion.model.Categoria;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pruebas de la capa de acceso a datos de Categoria:
 * insertar, actualizar, eliminar y listar (lo que pide la rubrica).
 * Se ejecutan con H2 en memoria, no necesitan PostgreSQL.
 */
@DataJpaTest
class CategoriaRepositoryTest {

    @Autowired
    private CategoriaRepository categoriaRepository;

    private Categoria nueva(String nombre, String descripcion) {
        return Categoria.builder().nombre(nombre).descripcion(descripcion).build();
    }

    @Test
    @DisplayName("Insertar una categoria")
    void insertar() {
        Categoria guardada = categoriaRepository.save(nueva("Bebidas", "Gaseosas y jugos"));

        assertThat(guardada.getId()).isNotNull();
        assertThat(guardada.getNombre()).isEqualTo("Bebidas");
    }

    @Test
    @DisplayName("Listar categorias")
    void listar() {
        categoriaRepository.save(nueva("Bebidas", "Gaseosas y jugos"));
        categoriaRepository.save(nueva("Limpieza", "Productos de limpieza"));

        List<Categoria> lista = categoriaRepository.findAll();

        assertThat(lista).hasSize(2);
    }

    @Test
    @DisplayName("Actualizar una categoria")
    void actualizar() {
        Categoria guardada = categoriaRepository.save(nueva("Bebidas", "Gaseosas"));

        guardada.setDescripcion("Gaseosas, jugos y agua");
        categoriaRepository.save(guardada);

        Categoria actualizada = categoriaRepository.findById(guardada.getId()).orElseThrow();
        assertThat(actualizada.getDescripcion()).isEqualTo("Gaseosas, jugos y agua");
    }

    @Test
    @DisplayName("Eliminar una categoria")
    void eliminar() {
        Categoria guardada = categoriaRepository.save(nueva("Bebidas", "Gaseosas"));

        categoriaRepository.deleteById(guardada.getId());

        Optional<Categoria> buscada = categoriaRepository.findById(guardada.getId());
        assertThat(buscada).isEmpty();
    }

    @Test
    @DisplayName("Detectar nombres duplicados sin importar mayusculas")
    void existePorNombre() {
        categoriaRepository.save(nueva("Bebidas", "Gaseosas"));

        assertThat(categoriaRepository.existsByNombreIgnoreCase("bebidas")).isTrue();
        assertThat(categoriaRepository.existsByNombreIgnoreCase("Snacks")).isFalse();
    }
}
