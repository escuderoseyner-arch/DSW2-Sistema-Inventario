package com.inventario.gestion.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    /** Nombre de usuario para iniciar sesion */
    @Column(nullable = false, unique = true, length = 50)
    private String usuario;

    /** Se guarda cifrada con BCrypt, nunca en texto plano */
    @Column(nullable = false, length = 100)
    private String password;

    @ManyToOne(optional = false)
    @JoinColumn(name = "rol_id")
    private Rol rol;
}
