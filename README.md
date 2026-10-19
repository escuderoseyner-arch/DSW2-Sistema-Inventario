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


## Ejecución local con Docker

### Requisitos

- Docker Desktop instalado y en ejecución.
- Docker Compose disponible mediante `docker compose`.
- Git para clonar el repositorio.

### Configuración de variables de entorno

En la raíz del proyecto, crea un archivo `.env` a partir de la plantilla `.env.example`.

En Windows PowerShell, ejecuta:

```powershell
Copy-Item .env.example .env
```

Edita el archivo `.env` y reemplaza los valores de ejemplo por credenciales propias:

```dotenv
DB_USER=postgres
DB_PASSWORD='CAMBIAR_POR_UNA_CONTRASENA_SEGURA'
JWT_SECRET='CAMBIAR_POR_UN_SECRETO_LARGO_Y_ALEATORIO'
ADMIN_PASSWORD='CAMBIAR_POR_UNA_CONTRASENA_DE_ADMINISTRADOR'
```

**Importante:**

- Utiliza contraseñas seguras y un secreto JWT largo y aleatorio.
- Las comillas simples permiten que Docker Compose interprete literalmente los valores que contienen caracteres como `$`.
- El archivo `.env` contiene credenciales reales y no debe subirse a GitHub.
- El archivo `.env.example` es una plantilla y no contiene secretos reales.

### Construcción y ejecución

Desde la carpeta raíz del proyecto, ejecuta:

```powershell
docker compose config --quiet
docker compose up --build -d
```

El primer comando valida la configuración de Docker Compose. El segundo construye la imagen de la API e inicia los contenedores de la aplicación y PostgreSQL.

Para comprobar el estado de los contenedores:

```powershell
docker compose ps
```

Para consultar los registros de la API:

```powershell
docker compose logs -f api
```

La API estará disponible localmente en:

`http://localhost:8080`

PostgreSQL utiliza un volumen persistente para conservar los datos cuando se detienen o recrean los contenedores.

### Detener los servicios

Para detener los contenedores sin eliminarlos:

```powershell
docker compose stop
```

Para detener los servicios y eliminar los contenedores y la red del proyecto, conservando el volumen de PostgreSQL:

```powershell
docker compose down
```

**Advertencia:** el comando `docker compose down -v` también elimina el volumen de PostgreSQL y los datos almacenados en él. No lo utilices si necesitas conservar la base de datos.

### Pruebas de la API

Utiliza Postman para probar el inicio de sesión y los endpoints de categorías, productos, inventario y movimientos.

1. Ejecuta `POST /api/auth/login` para obtener el token JWT.
2. Envía el token en las peticiones protegidas mediante `Authorization: Bearer <token>`.
3. Crea una categoría y un producto para realizar las pruebas de inventario.
4. Comprueba las entradas, salidas y validaciones de stock mediante los endpoints documentados anteriormente en este README.

Para ejecutar las pruebas automatizadas del proyecto:

```powershell
mvn test
```
