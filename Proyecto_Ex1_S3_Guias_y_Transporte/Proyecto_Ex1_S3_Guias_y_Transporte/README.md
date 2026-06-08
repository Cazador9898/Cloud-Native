# Proyecto_Ex1_S3_Guias_y_Transporte

Proyecto único en Spring Boot para gestión de guías de despacho y transportistas.
Funciona en el puerto 8080.

## Ejecutar local

```bash
mvn spring-boot:run
```

URL base:

```text
http://localhost:8080
```

Consola H2:

```text
http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:guiasdb
Usuario: sa
Password: vacío
```

## Endpoints principales

### Crear transportista

```http
POST /api/transportistas
```

Body:

```json
{
  "nombre": "Transportes Rápidos",
  "rut": "11111111-1",
  "email": "contacto@transportes.cl"
}
```

### Listar transportistas

```http
GET /api/transportistas
```

### Crear guía de despacho

```http
POST /api/guias
```

Body:

```json
{
  "transportista": "Transportes Rápidos",
  "destinatario": "Cliente Demo",
  "direccionDestino": "Av. Siempre Viva 123",
  "detallePedido": "Pedido de prueba"
}
```

La guía se guarda temporalmente en la ruta configurada como EFS:

```properties
app.efs.path=/tmp/efs/guias
```

### Subir guía a S3

```http
POST /api/guias/{id}/subir-s3
```

Por defecto está en modo simulación local:

```properties
app.s3.enabled=false
```

Para AWS real, cambiar a:

```properties
app.s3.enabled=true
app.s3.bucket=nombre-del-bucket
app.s3.region=us-east-1
```

La estructura generada es:

```text
fecha/transportista/guia.txt
```

Ejemplo:

```text
2026-06-08/Transportes_Rapidos/GUIA-12345678.txt
```

### Descargar guía con validación de permisos

```http
GET /api/guias/{id}/descargar
Header: X-API-KEY = duoc123
```

### Actualizar guía

```http
PUT /api/guias/{id}
```

### Eliminar guía

```http
DELETE /api/guias/{id}
```

### Consultar guías por transportista y fecha

```http
GET /api/guias/consultar?transportista=Transportes Rápidos&fecha=2026-06-08
```

## Puerto

Todo el proyecto usa:

```properties
server.port=8080
```
