# DSW2 - Sistema de Gestión de Inventario

Sistema de gestión de inventario basado en arquitectura de microservicios REST con Spring Boot.

## Arquitectura

El sistema está compuesto por los siguientes microservicios:

| Microservicio | Puerto | Descripción |
|---|---|---|
| **api-gateway** | 8080 | Puerta de entrada — enruta peticiones a los microservicios |
| **ms-usuarios** | 8081 | Autenticación, gestión de usuarios y roles |
| **ms-productos** | 8082 | CRUD de productos y categorías, control de stock |
| **ms-movimientos** | 8083 | Registro de entradas, salidas y ajustes de inventario |
| **ms-alertas** | 9090 (TCP) | Servidor de sockets TCP para alertas de stock bajo |

## Tecnologías

- Java 17
- Spring Boot 3.3.5
- Spring Cloud Gateway
- Spring Data JPA
- MySQL
- Sockets TCP (Java SE)

## Requisitos

- JDK 17 o superior
- MySQL 8.x
- IntelliJ IDEA
- Postman (para pruebas)

## Cómo ejecutar

1. Crear las bases de datos en MySQL (se crean automáticamente al iniciar):
   - `db_usuarios`
   - `db_productos`
   - `db_movimientos`

2. Configurar usuario y contraseña de MySQL en cada `application.properties`

3. Ejecutar cada microservicio en este orden:
   1. `ms-usuarios`
   2. `ms-productos`
   3. `ms-movimientos`
   4. `ms-alertas`
   5. `api-gateway`

4. Acceder vía API Gateway en `http://localhost:8080`

## Endpoints principales

### Productos
- `GET /api/productos` — Listar todos
- `GET /api/productos/{id}` — Buscar por ID
- `POST /api/productos` — Crear producto
- `PUT /api/productos/{id}` — Actualizar producto
- `DELETE /api/productos/{id}` — Eliminar producto

### Categorías
- `GET /api/categorias` — Listar todas
- `POST /api/categorias` — Crear categoría

### Movimientos
- `GET /api/movimientos` — Listar todos
- `POST /api/movimientos` — Registrar movimiento (ENTRADA/SALIDA/AJUSTE)

### Usuarios
- `GET /api/usuarios` — Listar todos
- `POST /api/usuarios` — Crear usuario

## Integrantes

- Escudero Campos, Seyner Joaquin — I202502247
- [Brian]
- [Kevin]
- [Luuis]
- [Yan Strike]

## Curso

Desarrollo de Servicios Web II (5401) — CIBERTEC — Ciclo VI — 2026
