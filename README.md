# Facturación — Arquitectura Limpia

Implementación académica de referencia basada exclusivamente en el enunciado de Arquitectura Limpia. La regla principal es que los adaptadores externos dependen del caso de uso; las reglas de negocio no dependen de HTTP, AMQP ni de una base de datos. REST y RabbitMQ invocan el mismo `CreateInvoiceUseCase`; la persistencia se sustituye detrás del puerto `InvoiceRepository`.

## Requisitos
- Java 17 o superior
- Maven 3.9 o superior
- RabbitMQ solo si se habilita el consumidor
- PostgreSQL u Oracle solo si se elige persistencia JDBC

## Ejecutar
Desde la carpeta del proyecto: `mvn clean compile exec:java`. Por defecto inicia REST en `http://localhost:8080/invoices` y usa almacenamiento en memoria. La memoria se pierde al reiniciar.

Variables opcionales: `REST_PORT` (8080), `DB_MODE` (`memory`, `postgres` u `oracle`), `DB_URL`, `DB_USER`, `DB_PASSWORD`, `TAX_RATE` (0.15 de ejemplo), `REVIEW_THRESHOLD` (1000) y `AMQP_URI` más `AMQP_QUEUE` (`invoices.create`). Para base relacional ejecute previamente el script de `db/postgres/schema.sql` o `db/oracle/schema.sql`. Las conexiones son JDBC estándar y el esquema debe existir antes de arrancar.

## API REST
`POST /invoices`, Content-Type `application/json`:
```json
{"operationId":"op-1001","customerId":"CLI-001","lines":[{"description":"Servicio de ejemplo","quantity":2,"unitPrice":25.50}]}
```
Respuesta: factura con subtotal, impuesto, total, estado (`APPROVED` o `PENDING_REVIEW`) e identificador. `operationId` hace idempotente el procesamiento: un reenvío retorna la factura registrada. Se responde 422 ante reglas de negocio inválidas.

## AMQP
Cuando `AMQP_URI` está definida, consume mensajes JSON con el mismo esquema desde `invoices.create` (o `AMQP_QUEUE`). Confirma mensajes procesados; rechaza sin reencolar mensajes inválidos. En producción conviene configurar DLQ/reintentos, TLS y credenciales externas.

## Reglas demostrativas
Rechaza detalles inválidos y clientes cuyo identificador comienza por `RISK-`; calcula impuesto con tasa configurable y marca para revisión los subtotales superiores al umbral. La tasa y las reglas son parámetros didácticos de ejemplo, no constituyen asesoría ni determinación de normativa tributaria vigente.

## Estructura
- `domain`: entidades y excepciones de negocio.
- `application`: comando, puerto de persistencia y caso de uso.
- `adapters/in`: adaptadores REST y AMQP.
- `adapters/out`: adaptadores de persistencia en memoria y JDBC.
- `db`: esquemas iniciales PostgreSQL y Oracle.
- `docs/arquitectura.mmd`: diagrama de dependencias Mermaid.

## Subir a Git
Descomprima la carpeta, entre en `facturacion-arquitectura-limpia` y ejecute:
```bash
git init
git add .
git commit -m "Implementa facturación con arquitectura limpia"
```
Luego conecte el repositorio remoto y haga `git push -u origin main`.

## Verificación automatizada
Ejecute `mvn test`. Las pruebas cubren cálculo de totales, idempotencia, rechazo de cliente según regla antifraude y estado de revisión para montos sobre el umbral.
