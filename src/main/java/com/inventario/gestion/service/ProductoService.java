package com.inventario.gestion.service;

import com.inventario.gestion.dto.ProductoRequest;
import com.inventario.gestion.exception.RecursoNoEncontradoException;
import com.inventario.gestion.exception.ReglaNegocioException;
import com.inventario.gestion.model.Categoria;
import com.inventario.gestion.model.Producto;
import com.inventario.gestion.repository.CategoriaRepository;
import com.inventario.gestion.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository pRepository;
    private final CategoriaRepository categoriaRepository;

    //guardar
    @Transactional
    public Producto guardarProducto(ProductoRequest request){
        Categoria categoria = buscarCategoria(request.idCategoria());
        return pRepository.save(Producto.builder()
                .nombre(request.nombre().trim())
                .descripcion(request.descripcion())
                .precio(request.precio())
                .stock(request.stock() != null ? request.stock() : 0)   // si no mandan stock, empieza en 0
                .stockMinimo(request.stockMinimo())
                .categoria(categoria)
                .build());
    }

    //listar
    @Transactional(readOnly = true)
    public List<Producto> listarProductos(){
        return pRepository.findAll();
    }

    //Listar segun categoria
    @Transactional(readOnly = true)
    public List<Producto> listarProductoSegunCategoria(Long idCategoria){
        buscarCategoria(idCategoria);   // 404 si la categoria no existe
        return pRepository.findByCategoriaId(idCategoria);
    }

    // buscar segun id
    @Transactional(readOnly = true)
    public Producto buscarProducto(Long id){
        return pRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el producto con el id: " + id));
    }

    //actualizar (el stock NO se toca: lo cambian los movimientos)
    @Transactional
    public Producto actualizarProducto(Long id, ProductoRequest request){
        Producto p = buscarProducto(id);
        p.setNombre(request.nombre().trim());
        p.setDescripcion(request.descripcion());
        p.setPrecio(request.precio());
        p.setStockMinimo(request.stockMinimo());
        p.setCategoria(buscarCategoria(request.idCategoria()));
        return pRepository.save(p);
    }

    // buscar categoria por id (lanza 404 si no existe)
    @Transactional(readOnly = true)
    public Categoria buscarCategoria(Long idCategoria){
        return categoriaRepository.findById(idCategoria)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe la categoria con id " + idCategoria));
    }

    //borrar (no se permite si el producto ya tiene movimientos)
    public void eliminarProducto(Long id){
        Producto p = buscarProducto(id);
        try {
            pRepository.delete(p);
        } catch (DataIntegrityViolationException e) {
            throw new ReglaNegocioException(
                    "No se puede eliminar el producto porque tiene movimientos registrados");
        }
    }
}
