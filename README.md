# DataFact

REST API para orquestar pipelines Spark construido con Scala 2.12, Maven y Akka HTTP.

## Características

- API REST para gestionar y ejecutar pipelines de Spark
- Almacenamiento en memoria de configuraciones de pipelines
- Simulación de ejecución de pipelines con resultados detallados
- JSON serialization/deserialization usando Circe
- Arquitectura modular con traits para extensibilidad

## Requisitos

- Java 8 o superior
- Maven 3.6+

## Compilación

```bash
mvn clean compile
```

## Empaquetado

```bash
mvn clean package
```

Esto genera un JAR ejecutable con todas las dependencias en `target/datafact-api-1.0-SNAPSHOT.jar`.

## Ejecución

```bash
java -jar target/datafact-api-1.0-SNAPSHOT.jar
```

El servidor se inicia en `http://0.0.0.0:8080/`

## Endpoints de la API

### 1. POST /pipelines

Guarda una configuración de pipeline en memoria.

**Request:**
```bash
curl -X POST http://localhost:8080/pipelines \
  -H "Content-Type: application/json" \
  -d '{
    "id": "pipeline-001",
    "name": "Sales Data Pipeline",
    "source": {
      "sourceType": "file",
      "path": "/data/sales.csv",
      "format": "csv",
      "options": {
        "header": "true",
        "delimiter": ","
      }
    },
    "validations": [
      {
        "rules": ["not_null(id)", "positive(amount)"],
        "failOnError": true
      }
    ],
    "transformations": [
      {
        "transformationType": "filter",
        "columns": ["date", "amount", "customer_id"],
        "expression": "amount > 0",
        "parameters": {}
      }
    ],
    "output": {
      "outputType": "file",
      "path": "/output/sales_aggregated",
      "format": "parquet",
      "mode": "overwrite",
      "options": {
        "compression": "snappy"
      }
    }
  }'
```

**Response (201 Created):**
```json
{
  "id": "pipeline-001",
  "name": "Sales Data Pipeline",
  ...
}
```

### 2. POST /pipelines/{id}/run

Ejecuta un pipeline utilizando el trait PipelineRunner y devuelve un PipelineRunResult simulado.

**Request:**
```bash
curl -X POST http://localhost:8080/pipelines/pipeline-001/run
```

**Response (200 OK):**
```json
{
  "pipelineId": "pipeline-001",
  "status": "SUCCESS",
  "startTime": 1762992634433,
  "endTime": 1762992634534,
  "recordsProcessed": 7153,
  "errors": [],
  "message": "Pipeline 'Sales Data Pipeline' executed successfully"
}
```

**Response (404 Not Found):**
```json
{
  "error": "Pipeline with id 'non-existent' not found"
}
```

### 3. GET /pipelines/{id}/last-report

Obtiene el último reporte de ejecución de un pipeline.

**Request:**
```bash
curl -X GET http://localhost:8080/pipelines/pipeline-001/last-report
```

**Response (200 OK):**
```json
{
  "pipelineId": "pipeline-001",
  "status": "SUCCESS",
  "startTime": 1762992634433,
  "endTime": 1762992634534,
  "recordsProcessed": 7153,
  "errors": [],
  "message": "Pipeline 'Sales Data Pipeline' executed successfully"
}
```

**Response (404 Not Found):**
```json
{
  "error": "No execution report found for pipeline 'non-existent'"
}
```

## Modelos de Datos

### PipelineConfig
Configuración completa de un pipeline:
- `id`: Identificador único del pipeline
- `name`: Nombre descriptivo
- `source`: Configuración de la fuente de datos (SourceConfig)
- `validations`: Lista de validaciones (ValidationConfig)
- `transformations`: Lista de transformaciones (TransformConfig)
- `output`: Configuración de salida (OutputConfig)

### SourceConfig
Configuración de fuente de datos:
- `sourceType`: Tipo de fuente (file, database, stream, etc.)
- `path`: Ruta o URI de la fuente
- `format`: Formato de los datos (csv, json, parquet, jdbc, etc.)
- `options`: Opciones adicionales específicas del formato

### ValidationConfig
Reglas de validación:
- `rules`: Lista de reglas de validación a aplicar
- `failOnError`: Si debe fallar el pipeline en caso de error

### TransformConfig
Configuración de transformación:
- `transformationType`: Tipo de transformación (filter, aggregate, join, etc.)
- `columns`: Columnas afectadas por la transformación
- `expression`: Expresión opcional (para filtros, cálculos, etc.)
- `parameters`: Parámetros adicionales específicos de la transformación

### OutputConfig
Configuración de salida:
- `outputType`: Tipo de salida (file, database, storage, etc.)
- `path`: Ruta o URI de destino
- `format`: Formato de salida (parquet, json, csv, etc.)
- `mode`: Modo de escritura (append, overwrite, etc.)
- `options`: Opciones adicionales específicas del formato

### PipelineRunResult
Resultado de la ejecución de un pipeline:
- `pipelineId`: ID del pipeline ejecutado
- `status`: Estado de la ejecución (SUCCESS, FAILED, etc.)
- `startTime`: Timestamp de inicio
- `endTime`: Timestamp de finalización
- `recordsProcessed`: Número de registros procesados
- `errors`: Lista de errores (si existen)
- `message`: Mensaje descriptivo del resultado

## Arquitectura

### PipelineRunner Trait
Interface para implementar diferentes estrategias de ejecución de pipelines:

```scala
trait PipelineRunner {
  def runPipeline(config: PipelineConfig): Future[PipelineRunResult]
}
```

La implementación actual `SimulatedPipelineRunner` simula la ejecución de pipelines con un pequeño delay y devuelve resultados aleatorios. Para producción, se puede implementar una versión que ejecute pipelines reales de Spark.

### PipelineStorage
Almacenamiento thread-safe en memoria usando `TrieMap` para gestionar configuraciones de pipelines y sus resultados de ejecución.

## Dependencias Principales

- **Scala**: 2.12.18
- **Akka HTTP**: 10.2.10
- **Akka Actors**: 2.6.20
- **Circe**: 0.14.6 (JSON serialization)
- **SLF4J**: 1.7.36 (Logging)

## Estructura del Proyecto

```
src/main/scala/com/datafact/
├── api/
│   ├── ApiRoutes.scala      # Definición de rutas HTTP
│   ├── ApiServer.scala      # Punto de entrada principal
│   └── PipelineStorage.scala # Almacenamiento en memoria
├── models/
│   └── Models.scala         # Modelos de datos
└── pipeline/
    └── PipelineRunner.scala # Trait y implementación simulada
```