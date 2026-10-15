package com.inventario.gestion.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "categoria")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String nombre;

    @Column(length = 200)
    private String descripcion;

    /**
     * Productos de esta categoria.
     * Sin cascade: borrar una categoria NUNCA borra sus productos
     * (si tiene productos, la BD rechaza el borrado y la API responde 409).
     * @JsonIgnore: no se muestra en el JSON para evitar el bucle infinito.
     */
    @OneToMany(mappedBy = "categoria")
    @JsonIgnore
    private List<Producto> productos;
}
