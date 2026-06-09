# EX1_S3_Guias_Transporte

Microservicio Spring Boot para gestionar pedidos y generar guías de despacho, adaptado a la pauta de Semana 3.

## Funciones implementadas

- Guardado temporal de guías en EFS usando `efs.path=/app/efs`.
- Subida automática a AWS S3 con estructura `fecha/transportista/guia.pdf`.
- Creación, subida, descarga con validación simple de permisos, actualización, eliminación y consulta de historial.
- Dockerfile actualizado.
- Pipeline GitHub Actions para construir, publicar imagen Docker Hub y desplegar en EC2.

## Endpoints principales

- `POST /api/guias`: crea guía, la guarda en EFS y la sube a S3.
- `POST /api/guias/upload`: sube una guía existente a S3.
- `GET /api/guias/{fecha}/{transportista}/{idGuia}?usuarioSolicitante=transportista`: descarga guía con validación.
- `PUT /api/guias/{fecha}/{transportista}/{idGuia}`: actualiza una guía.
- `DELETE /api/guias/{fecha}/{transportista}/{idGuia}`: elimina una guía.
- `GET /api/guias?fecha=2026-06-08&transportista=transportistaX`: consulta historial por transportista y fecha.

## Endpoints mínimos para pauta 2-5

Crear/subir guía generada:
```bash
curl -X POST http://localhost:8080/api/guias/demo?transportista=transportistaX
```

Consultar historial por fecha y transportista:
```bash
curl "http://localhost:8080/api/guias?fecha=2026-06-09&transportista=transportistaX"
```

Modificar/actualizar una guía generada:
```bash
curl -X PUT "http://localhost:8080/api/guias/2026-06-09/transportistaX/guia-demo/generar" \
  -H "Content-Type: application/json" \
  -d '{"numeroPedido":"PED-2002","transportista":"transportistaX","destinatario":"Cliente Actualizado","direccionDestino":"Calle Nueva 456","descripcionCarga":"Carga actualizada"}'
```

Descargar guía:
```bash
curl -o guia.pdf "http://localhost:8080/api/guias/2026-06-09/transportistaX/guia-demo?usuarioSolicitante=transportistaX"
```
