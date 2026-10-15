package com.inventario.gestion.config;

import com.inventario.gestion.model.Rol;
import com.inventario.gestion.model.Usuario;
import com.inventario.gestion.repository.RolRepository;
import com.inventario.gestion.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Al iniciar la app crea los roles ADMIN y EMPLEADO y un usuario "admin"
 * si todavia no existen. Asi siempre hay alguien para hacer el primer login.
 */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin-password}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        Rol admin = crearRolSiNoExiste("ADMIN");
        crearRolSiNoExiste("EMPLEADO");

        if (!usuarioRepository.existsByUsuario("admin")) {
            usuarioRepository.save(Usuario.builder()
                    .nombre("Administrador")
                    .usuario("admin")
                    .password(passwordEncoder.encode(adminPassword))
                    .rol(admin)
                    .build());
        }
    }

    private Rol crearRolSiNoExiste(String nombre) {
        return rolRepository.findByNombre(nombre)
                .orElseGet(() -> rolRepository.save(Rol.builder().nombre(nombre).build()));
    }
}
