# Peticiones para Postman

Todas las peticiones van **directo al puerto de cada servicio**.
Header en POST/PUT: `Content-Type: application/json`. Ejecútalas en este orden.

## 1. Crear categoría
`POST http://localhost:8082/api/categorias`
```json
{
  "nombre": "Computo",
  "estado": "ACTIVO"
}
```
Respuesta `201 Created`:
```json
{"id":1,"nombre":"Computo","estado":"ACTIVO"}
```

## 2. Crear producto (usa la categoría 1)
`POST http://localhost:8083/api/productos`
```json
{
  "nombre": "Laptop",
  "precio": 2500,
  "stock": 10,
  "categoriaId": 1
}
```
Respuesta `201 Created` (la categoría la trae Feign desde categoria-service):
```json
{"id":1,"nombre":"Laptop","precio":2500.0,"stock":10,"categoriaId":1,
 "categoria":{"id":1,"nombre":"Computo","estado":"ACTIVO"}}
```

## 3. Registrar cliente
`POST http://localhost:8084/api/clientes`
```json
{
  "nombre": "Carlos",
  "apellido": "Ramirez",
  "direccion": "Av. Los Olivos 123, Lima",
  "estado": "ACTIVO",
  "nroDni": "45678912"
}
```
Respuesta `201 Created`.

## 4. Registrar pedido
`POST http://localhost:8085/api/pedidos`
```json
{
  "clienteId": 1,
  "productoId": 1,
  "cantidad": 2
}
```
Respuesta `201 Created`. El estado inicial siempre es `PENDIENTE`.

## 5. Consultar pedido
`GET http://localhost:8085/api/pedidos/1`

Respuesta `200 OK`:
```json
{
  "id": 1,
  "cantidad": 2,
  "cliente": { "id": 1, "nombre": "Carlos", "apellido": "Ramirez" },
  "producto": { "id": 1, "nombre": "Laptop", "precio": 2500 },
  "estado": "PENDIENTE"
}
```

## Cambios de estado del pedido (sin body)
- `PUT http://localhost:8085/api/pedidos/1/atender`
- `PUT http://localhost:8085/api/pedidos/1/anular`
- `PUT http://localhost:8085/api/pedidos/1/pendiente`

## Pruebas de error
| Petición | Resultado esperado |
|---|---|
| `POST :8083/api/productos` con `"precio": 0` | 400, `"precio": "El precio debe ser mayor a cero"` |
| `POST :8083/api/productos` con `"categoriaId": 99` | 404, la categoría no existe en categoria-service |
| `POST :8084/api/clientes` con `"nroDni": "12ab"` | 400, el nro_dni debe tener 8 dígitos |
| `POST :8084/api/clientes` repitiendo un DNI | 409, valor duplicado |
| `POST :8085/api/pedidos` con `{"cantidad": 0}` | 400 con errores en clienteId, productoId y cantidad |
| `POST :8085/api/pedidos` con `"clienteId": 99` | 404, el cliente no existe en cliente-service |
| `GET :8085/api/pedidos/99` | 404, pedido no encontrado |
| `DELETE :8085/api/pedidos/1` | 405, un pedido no se elimina |
| Apagar cliente-service y `POST :8085/api/pedidos` | 503, servicio dependiente no disponible |
