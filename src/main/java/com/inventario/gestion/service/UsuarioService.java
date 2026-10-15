package com.inventario.gestion.service;

import com.inventario.gestion.dto.UsuarioRequest;
import com.inventario.gestion.dto.UsuarioResponse;
import com.inventario.gestion.exception.RecursoNoEncontradoException;
import com.inventario.gestion.exception.ReglaNegocioException;
import com.inventario.gestion.model.Rol;
import com.inventario.gestion.model.Usuario;
import com.inventario.gestion.repository.RolRepository;
import com.inventario.gestion.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listar() {
        return usuarioRepository.findAll().stream().map(UsuarioResponse::desde).toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponse buscarPorId(Long id) {
        return UsuarioResponse.desde(obtener(id));
    }

    @Transactional
    public UsuarioResponse registrar(UsuarioRequest request) {
        String usuario = request.usuario().trim();
        if (usuarioRepository.existsByUsuario(usuario)) {
            throw new ReglaNegocioException("El usuario '" + usuario + "' ya existe");
        }
        Usuario nuevo = Usuario.builder()
                .nombre(request.nombre().trim())
                .usuario(usuario)
                .password(passwordEncoder.encode(request.password()))   // BCrypt
                .rol(obtenerRol(request.rol()))
                .build();
        return UsuarioResponse.desde(usuarioRepository.save(nuevo));
    }

    @Transactional
    public UsuarioResponse actualizar(Long id, UsuarioRequest request) {
        Usuario existente = obtener(id);
        String usuario = request.usuario().trim();
        if (usuarioRepository.existsByUsuarioAndIdNot(usuario, id)) {
            throw new ReglaNegocioException("El usuario '" + usuario + "' ya existe");
        }
        existente.setNombre(request.nombre().trim());
        existente.setUsuario(usuario);
        existente.setPassword(passwordEncoder.encode(request.password()));   // BCrypt
        existente.setRol(obtenerRol(request.rol()));
        return UsuarioResponse.desde(usuarioRepository.save(existente));
    }

    /** No se elimina un usuario que ya registro movimientos (la BD lo impide). */
    public void eliminar(Long id) {
        Usuario usuario = obtener(id);
        try {
            usuarioRepository.delete(usuario);
        } catch (DataIntegrityViolationException e) {
            throw new ReglaNegocioException(
                    "No se puede eliminar el usuario porque tiene movimientos registrados");
        }
    }

    private Usuario obtener(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el usuario con id " + id));
    }

    private Rol obtenerRol(String nombre) {
        return rolRepository.findByNombre(nombre.trim().toUpperCase())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe el rol '" + nombre + "'. Use ADMIN o EMPLEADO"));
    }
}
