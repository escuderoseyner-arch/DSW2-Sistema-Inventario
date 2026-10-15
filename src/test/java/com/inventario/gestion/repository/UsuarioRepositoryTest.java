package com.inventario.gestion.repository;

import com.inventario.gestion.model.Rol;
import com.inventario.gestion.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pruebas de la capa de acceso a datos de Usuario:
 * insertar, actualizar, eliminar y listar. Con H2 en memoria.
 */
@DataJpaTest
class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private Rol rolEmpleado;

    @BeforeEach
    void prepararRol() {
        rolEmpleado = rolRepository.save(Rol.builder().nombre("EMPLEADO").build());
    }

    private Usuario nuevo(String usuario) {
        return Usuario.builder()
                .nombre("Usuario de prueba")
                .usuario(usuario)
                .password(encoder.encode("clave123"))
                .rol(rolEmpleado)
                .build();
    }

    @Test
    @DisplayName("Insertar un usuario con contrasena cifrada")
    void insertar() {
        Usuario guardado = usuarioRepository.save(nuevo("jperez"));

        assertThat(guardado.getId()).isNotNull();
        assertThat(guardado.getPassword()).isNotEqualTo("clave123");           // no esta en texto plano
        assertThat(encoder.matches("clave123", guardado.getPassword())).isTrue(); // es BCrypt valido
    }

    @Test
    @DisplayName("Listar usuarios")
    void listar() {
        usuarioRepository.save(nuevo("jperez"));
        usuarioRepository.save(nuevo("mlopez"));

        assertThat(usuarioRepository.findAll()).hasSize(2);
    }

    @Test
    @DisplayName("Actualizar un usuario")
    void actualizar() {
        Usuario guardado = usuarioRepository.save(nuevo("jperez"));

        guardado.setNombre("Juan Perez");
        usuarioRepository.save(guardado);

        Usuario actualizado = usuarioRepository.findById(guardado.getId()).orElseThrow();
        assertThat(actualizado.getNombre()).isEqualTo("Juan Perez");
    }

    @Test
    @DisplayName("Eliminar un usuario")
    void eliminar() {
        Usuario guardado = usuarioRepository.save(nuevo("jperez"));

        usuarioRepository.deleteById(guardado.getId());

        assertThat(usuarioRepository.findById(guardado.getId())).isEmpty();
    }

    @Test
    @DisplayName("Buscar usuario por nombre de usuario")
    void buscarPorUsuario() {
        usuarioRepository.save(nuevo("jperez"));

        assertThat(usuarioRepository.findByUsuario("jperez")).isPresent();
        assertThat(usuarioRepository.findByUsuario("noexiste")).isEmpty();
    }
}
