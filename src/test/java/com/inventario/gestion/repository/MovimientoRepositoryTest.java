package com.inventario.gestion.repository;

import com.inventario.gestion.model.Categoria;
import com.inventario.gestion.model.Movimiento;
import com.inventario.gestion.model.Producto;
import com.inventario.gestion.model.Rol;
import com.inventario.gestion.model.TipoMovimiento;
import com.inventario.gestion.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class MovimientoRepositoryTest {

    @Autowired
    private MovimientoRepository movimientoRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    private Producto inca;
    private Producto lejia;
    private Usuario responsable;

    @BeforeEach
    void prepararDatos() {
        Categoria categoria = categoriaRepository.save(Categoria.builder().nombre("General").build());
        inca = productoRepository.save(nuevoProducto("Inca Kola 500ml", categoria));
        lejia = productoRepository.save(nuevoProducto("Lejia 1L", categoria));
        Rol rol = rolRepository.save(Rol.builder().nombre("EMPLEADO").build());
        responsable = usuarioRepository.save(Usuario.builder()
                .nombre("Empleado Prueba")
                .usuario("empleado1")
                .password("clave")
                .rol(rol)
                .build());
    }

    private Producto nuevoProducto(String nombre, Categoria categoria) {
        return Producto.builder()
                .nombre(nombre)
                .precio(new BigDecimal("3.50"))
                .stock(10)
                .stockMinimo(2)
                .categoria(categoria)
                .build();
    }

    private Movimiento nuevo(Producto producto, TipoMovimiento tipo, int cantidad) {
        return Movimiento.builder()
                .producto(producto)
                .usuario(responsable)
                .tipo(tipo)
                .cantidad(cantidad)
                .fecha(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Insertar un movimiento")
    void insertar() {
        Movimiento guardado = movimientoRepository.save(nuevo(inca, TipoMovimiento.ENTRADA, 5));

        assertThat(guardado.getId()).isNotNull();
        assertThat(guardado.getTipo()).isEqualTo(TipoMovimiento.ENTRADA);
        assertThat(guardado.getUsuario().getUsuario()).isEqualTo("empleado1");
    }

    @Test
    @DisplayName("Listar movimientos")
    void listar() {
        movimientoRepository.save(nuevo(inca, TipoMovimiento.ENTRADA, 5));
        movimientoRepository.save(nuevo(lejia, TipoMovimiento.SALIDA, 3));

        assertThat(movimientoRepository.findAllByOrderByFechaDescIdDesc()).hasSize(2);
    }

    @Test
    @DisplayName("Actualizar un movimiento")
    void actualizar() {
        Movimiento guardado = movimientoRepository.save(nuevo(inca, TipoMovimiento.ENTRADA, 5));

        guardado.setCantidad(8);
        movimientoRepository.save(guardado);

        Movimiento actualizado = movimientoRepository.findById(guardado.getId()).orElseThrow();
        assertThat(actualizado.getCantidad()).isEqualTo(8);
    }

    @Test
    @DisplayName("Eliminar un movimiento")
    void eliminar() {
        Movimiento guardado = movimientoRepository.save(nuevo(inca, TipoMovimiento.ENTRADA, 5));

        movimientoRepository.deleteById(guardado.getId());

        assertThat(movimientoRepository.findById(guardado.getId())).isEmpty();
    }

    @Test
    @DisplayName("Listar movimientos de un producto")
    void listarPorProducto() {
        movimientoRepository.save(nuevo(inca, TipoMovimiento.ENTRADA, 5));
        movimientoRepository.save(nuevo(inca, TipoMovimiento.SALIDA, 2));
        movimientoRepository.save(nuevo(lejia, TipoMovimiento.ENTRADA, 1));

        assertThat(movimientoRepository.findByProductoIdOrderByFechaDescIdDesc(inca.getId()))
                .hasSize(2)
                .allMatch(m -> m.getProducto().getId().equals(inca.getId()));
    }
}
