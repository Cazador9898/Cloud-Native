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
