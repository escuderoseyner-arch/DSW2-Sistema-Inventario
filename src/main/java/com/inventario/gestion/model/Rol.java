package com.inventario.gestion.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "rol")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** ADMIN o EMPLEADO */
    @Column(nullable = false, unique = true, length = 30)
    private String nombre;
}
