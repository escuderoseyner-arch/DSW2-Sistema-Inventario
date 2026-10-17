# DSW2 - Sistema de Gestión de Inventario

API REST para administrar productos, categorías, inventario y movimientos, con login seguro de usuarios.

## Tecnologías
- Java 17
- Spring Boot (Spring MVC, Spring Data JPA, Spring Security)
- Lombok
- PostgreSQL
- Maven + IntelliJ IDEA
- Postman y JUnit para pruebas

## Equipo
Proyecto del curso Desarrollo de Servicios Web II - CIBERTEC.

## Módulo Inventario y Movimientos

Permite consultar las existencias y registrar entradas y salidas de stock. El stock de un producto solo cambia mediante movimientos; el `PUT /api/productos/{id}` no lo modifica.

Todos los endpoints requieren el token JWT (`Authorization: Bearer <token>`) obtenido en `POST /api/auth/login`. Cualquier usuario autenticado (ADMIN o EMPLEADO) puede usarlos.

### Endpoints

| Método | Ruta | Función | Respuesta |
|---|---|---|---|
| GET | `/api/inventario` | Consultar las existencias de todos los productos | 200 |
| GET | `/api/inventario/producto/{id}` | Consultar el inventario de un producto | 200 / 404 |
| GET | `/api/inventario/stock-bajo` | Listar productos con `stock <= stockMinimo` | 200 |
| POST | `/api/movimientos/entrada` | Registrar una entrada e incrementar el stock | 201 / 400 / 404 |
| POST | `/api/movimientos/salida` | Registrar una salida y disminuir el stock | 201 / 400 / 404 / 409 |
| GET | `/api/movimientos` | Listar movimientos, del más reciente al más antiguo | 200 |
| GET | `/api/movimientos/{id}` | Consultar un movimiento por ID | 200 / 404 |
| GET | `/api/movimientos/producto/{id}` | Consultar los movimientos de un producto | 200 / 404 |

### JSON

Solicitud de entrada y salida:

```json
{ "idProducto": 1, "cantidad": 10 }
```

Respuesta de un movimiento:

```json
{
  "id": 1,
  "idProducto": 1,
  "producto": "Monitor LG 24",
  "tipo": "ENTRADA",
  "cantidad": 10,
  "fecha": "2026-10-17T10:30:00",
  "usuario": "admin"
}
```

Respuesta de inventario:

```json
{
  "idProducto": 1,
  "nombre": "Monitor LG 24",
  "categoria": "Electronica",
  "stock": 20,
  "stockMinimo": 2,
  "stockBajo": false
}
```

### Reglas de negocio

- La cantidad debe ser mayor que 0 (si no, 400).
- Una salida no puede ser mayor que el stock disponible (409 con el mensaje "Stock insuficiente...").
- El producto debe existir (si no, 404).
- Cada movimiento guarda producto, tipo (`ENTRADA` o `SALIDA`), cantidad, fecha y usuario responsable.
- El usuario responsable se toma del token, no del cuerpo de la petición.
- El historial no se puede modificar ni eliminar desde la API.
- Un producto con movimientos no se puede eliminar: la llave foránea lo impide y `ProductoService` responde 409.
- El stock y el movimiento se guardan en una misma transacción; si falla uno, se revierte el otro.

### Limitación conocida

No hay bloqueo de concurrencia sobre el stock. Si dos salidas del mismo producto llegan exactamente al mismo tiempo, ambas podrían validarse con el mismo stock y dejarlo en negativo. Para una versión futura se puede bloquear la fila del producto (`@Lock(PESSIMISTIC_WRITE)`) o agregar un campo de versión (`@Version`).

### Archivos agregados

| Capa | Archivo | Descripción |
|---|---|---|
| model | `TipoMovimiento` | Enum con `ENTRADA` y `SALIDA` |
| model | `Movimiento` | Entidad (tabla `movimiento`): `id`, `producto`, `usuario`, `tipo`, `cantidad`, `fecha` |
| repository | `MovimientoRepository` | `findAllByOrderByFechaDescIdDesc()` y `findByProductoIdOrderByFechaDescIdDesc(idProducto)` |
| dto | `MovimientoRequest` | Datos de entrada con validaciones (`idProducto` y `cantidad` obligatorios, `cantidad > 0`) |
| dto | `MovimientoResponse` | Salida de un movimiento; `desde(Movimiento)` convierte la entidad |
| dto | `InventarioResponse` | Salida de inventario; `desde(Producto)` convierte la entidad y calcula `stockBajo` |
| service | `MovimientoService` | `registrarEntrada`, `registrarSalida`, `listar`, `buscarPorId`, `listarPorProducto` |
| service | `InventarioService` | `listar`, `buscarPorProducto`, `listarStockBajo` |
| controller | `MovimientoController` | Rutas de `/api/movimientos` |
| controller | `InventarioController` | Rutas de `/api/inventario` |

Funciones principales:

- `MovimientoService.registrarEntrada(request)`: busca el producto, suma la cantidad al stock y guarda el movimiento `ENTRADA`.
- `MovimientoService.registrarSalida(request)`: busca el producto, valida que la cantidad no supere el stock, resta la cantidad y guarda el movimiento `SALIDA`.
- `MovimientoService.usuarioActual()`: obtiene el usuario autenticado del contexto de seguridad.
- `InventarioService.listarStockBajo()`: filtra los productos con `stock <= stockMinimo`.

No se modificaron archivos de otros módulos. Solo se agregaron las carpetas "Inventario" y "Movimientos" a `postman/DSW2-Sistema-Inventario.postman_collection.json`, junto con la variable `token`.

### Pruebas

- `MovimientoRepositoryTest` (JUnit + `@DataJpaTest` + H2): insertar, listar, actualizar, eliminar y listar por producto.
- `MovimientoControllerTest` (JUnit + MockMvc): entrada, salida, salida mayor al stock (409), cantidad inválida (400), producto inexistente (404), petición sin token (401), historial por producto, movimiento inexistente (404) e inventario con stock bajo.

Para ejecutarlas: `mvn test`. Resultado actual: 35 pruebas, 0 fallos (14 son del módulo de movimientos e inventario).

### Probar en Postman

1. Ejecutar `POST /api/auth/login` y copiar el valor de `token` en la variable `token` de la colección.
2. Crear una categoría y un producto (carpetas "Categoria" y "Productos").
3. Usar las carpetas "Movimientos" e "Inventario" cambiando el `idProducto` por el del producto creado.
