# Microservicios: Comunicación entre Servicios (Eureka + OpenFeign)

Sistema de pedidos con cinco proyectos Spring Boot. Cada servicio tiene su propia base de
datos MySQL, se registra en **Eureka** y se comunica con los demás **solo por REST mediante
OpenFeign**, usando el nombre lógico con el que está registrado. No hay relaciones JPA entre
servicios ni consultas a bases de datos ajenas.

```
                    ┌──────────────────────┐
                    │  eureka-server :8761 │  ← registro y descubrimiento
                    └──────────▲───────────┘
        ┌───────────────┬──────┴────────┬────────────────┐
categoria-service   producto-service   cliente-service   pedido-service
     :8082      ◄─Feign─   :8083            :8084     ─Feign─► cliente / producto
 bd_categoria_dev    bd_producto_dev   bd_cliente_dev     bd_pedido_dev  :8085
```

## Tecnologías
- Java 17, Spring Boot 3.5.14, Spring Cloud 2025.0.0
- Spring Web, Spring Data JPA, Bean Validation, Lombok
- Netflix Eureka Server / Client, Spring Cloud OpenFeign (con LoadBalancer)
- MySQL 8 (también probado con MariaDB 10.4 de XAMPP)
- Maven (se incluye `mvnw` en cada proyecto)

## Requisitos
- JDK 17 o superior (probado con JDK 21)
- MySQL en `localhost:3306`, usuario `root` y contraseña vacía
- Postman (o curl) para las pruebas

## Servicios y puertos
| Servicio | Puerto | Base de datos | Consume a |
|---|---|---|---|
| eureka-server | 8761 | — | — |
| categoria-service | 8082 | bd_categoria_dev | — |
| producto-service | 8083 | bd_producto_dev | categoria-service |
| cliente-service | 8084 | bd_cliente_dev | — |
| pedido-service | 8085 | bd_pedido_dev | cliente-service, producto-service |

## Configuración
- `application.yaml` activa el perfil `dev` (`spring.profiles.active: dev`).
- `application-dev.yml` define el puerto, el nombre, el datasource, `ddl-auto: update`,
  `show-sql: true`, `empresa.ambiente: desarrollo` y el registro en Eureka
  (`http://localhost:8761/eureka`).
- Las tablas las crea Hibernate al arrancar.

## Puesta en marcha
1. Crear las bases de datos:
   ```bash
   mysql -u root < database/crear_bases.sql
   ```
   (La URL JDBC también incluye `createDatabaseIfNotExist=true`.)
2. Levantar los servicios **en este orden**, cada uno en su propia terminal (o con Run en el IDE):
   1. `eureka-server`
   2. `categoria-service`
   3. `producto-service`
   4. `cliente-service`
   5. `pedido-service`
   ```bash
   cd eureka-server && ./mvnw spring-boot:run
   ```
3. Abrir http://localhost:8761 y comprobar que aparecen las 4 instancias registradas.
   Los servicios tardan unos 30 segundos en ser visibles para Feign tras registrarse.

## Endpoints

### categoria-service (8082)
| Método | Ruta | Descripción |
|---|---|---|
| POST | /api/categorias | Crear (nombre obligatorio, estado ACTIVO/INACTIVO, por defecto ACTIVO) |
| GET | /api/categorias | Listar |
| GET | /api/categorias/{id} | Obtener |
| PUT | /api/categorias/{id} | Actualizar |
| DELETE | /api/categorias/{id} | Eliminar |

### producto-service (8083)
| Método | Ruta | Descripción |
|---|---|---|
| POST | /api/productos | Crear (valida la categoría vía Feign; nombre obligatorio, precio > 0) |
| GET | /api/productos | Listar con su categoría |
| GET | /api/productos/{id} | Obtener con su categoría |
| PUT | /api/productos/{id} | Actualizar |
| DELETE | /api/productos/{id} | Eliminar |

### cliente-service (8084)
| Método | Ruta | Descripción |
|---|---|---|
| POST | /api/clientes | Registrar (todos los campos obligatorios, `nroDni` de 8 dígitos y único) |
| GET | /api/clientes | Listar |
| GET | /api/clientes/{id} | Consultar por código |
| PUT | /api/clientes/{id} | Actualizar |
| DELETE | /api/clientes/{id} | Eliminar |

### pedido-service (8085)
| Método | Ruta | Descripción |
|---|---|---|
| POST | /api/pedidos | Registrar (valida cliente y producto vía Feign; estado inicial PENDIENTE) |
| GET | /api/pedidos | Listar con cliente y producto |
| GET | /api/pedidos/{id} | Obtener pedido completo |
| PUT | /api/pedidos/{id}/anular | Cambiar a ANULADO |
| PUT | /api/pedidos/{id}/pendiente | Cambiar a PENDIENTE |
| PUT | /api/pedidos/{id}/atender | Cambiar a ATENDIDO |

Los pedidos no se eliminan: no existe DELETE y la ruta responde 405.

Ejemplo de `GET /api/pedidos/1`:
```json
{"id":1,"cantidad":2,"cliente":{"id":1,"nombre":"Carlos","apellido":"Ramirez"},"producto":{"id":1,"nombre":"Laptop","precio":2500},"estado":"PENDIENTE"}
```

Los bodies de Postman están en [postman/peticiones.md](postman/peticiones.md).

## Manejo de errores
Cada servicio tiene un `@RestControllerAdvice` global que responde en JSON:
```json
{"timestamp":"...","status":400,"error":"Bad Request","mensaje":"Error de validación en los datos enviados",
 "ruta":"/api/pedidos","errores":{"cantidad":"La cantidad debe ser mayor a cero"}}
```
| Código | Caso |
|---|---|
| 400 | Validación (`@Valid`), JSON mal formado o parámetro inválido |
| 404 | Recurso inexistente, local o en el servicio remoto |
| 405 | Método no permitido (ej. DELETE de pedidos) |
| 409 | DNI de cliente duplicado |
| 503 | Servicio dependiente caído o no registrado en Eureka (Feign) |

## Estructura de paquetes (por servicio)
```
com.tecsup.<servicio>
├── model        entidades JPA
├── dto          DTOs (datos remotos y respuesta de error)
├── repository   interfaces JpaRepository
├── services     lógica de negocio
├── controller   controladores REST
├── client       interfaces @FeignClient (solo producto y pedido)
└── exception    excepción 404 y @RestControllerAdvice
```

## Notas
- Con MariaDB (XAMPP), Hibernate muestra el aviso `HHH000511` sobre la versión 5.5.5. No afecta
  al funcionamiento del laboratorio.
- Las pruebas `*ApplicationTests` de Initializr levantan el contexto completo y necesitan MySQL
  encendido. Para compilar sin ejecutarlas: `./mvnw package -DskipTests`.
