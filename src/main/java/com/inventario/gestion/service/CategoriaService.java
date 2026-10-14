package com.inventario.gestion.service;

import com.inventario.gestion.dto.CategoriaRequest;
import com.inventario.gestion.exception.RecursoNoEncontradoException;
import com.inventario.gestion.exception.ReglaNegocioException;
import com.inventario.gestion.model.Categoria;
import com.inventario.gestion.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    @Transactional(readOnly = true)
    public List<Categoria> listar() {
        return categoriaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Categoria buscarPorId(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe la categoria con id " + id));
    }

    @Transactional
    public Categoria registrar(CategoriaRequest request) {
        String nombre = request.nombre().trim();
        if (categoriaRepository.existsByNombreIgnoreCase(nombre)) {
            throw new ReglaNegocioException("Ya existe una categoria con el nombre '" + nombre + "'");
        }
        Categoria categoria = Categoria.builder()
                .nombre(nombre)
                .descripcion(request.descripcion())
                .build();
        return categoriaRepository.save(categoria);
    }

    @Transactional
    public Categoria actualizar(Long id, CategoriaRequest request) {
        Categoria categoria = buscarPorId(id);
        String nombre = request.nombre().trim();
        if (categoriaRepository.existsByNombreIgnoreCaseAndIdNot(nombre, id)) {
            throw new ReglaNegocioException("Ya existe otra categoria con el nombre '" + nombre + "'");
        }
        categoria.setNombre(nombre);
        categoria.setDescripcion(request.descripcion());
        return categoriaRepository.save(categoria);
    }

    /**
     * Regla del plan: no se elimina una categoria que tenga productos asociados.
     * Si tiene productos, la BD rechaza el borrado (llave foranea) y devolvemos un 409.
     */
    public void eliminar(Long id) {
        Categoria categoria = buscarPorId(id);
        try {
            categoriaRepository.delete(categoria);
        } catch (DataIntegrityViolationException e) {
            throw new ReglaNegocioException(
                    "No se puede eliminar la categoria porque tiene productos asociados");
        }
    }
}
