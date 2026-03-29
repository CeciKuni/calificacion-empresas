# Proyecto de Testing - Calificación de Empresas

Proyecto de automatización de pruebas para validar el motor de Calificación de Empresas y Enriquecimientos. Implementa pruebas con API mocking utilizando WireMock para simular diferentes escenarios: aprobación, rechazo y errores del servidor.

## Reporte de Ejecución (GitHub Pages)

- Reporte publicado: [https://cecikuni.github.io/calificacion-empresas/](https://cecikuni.github.io/calificacion-empresas/)
- Si el último deploy terminó recién, puede demorar 1-2 minutos en actualizarse.

## Descripción

Este proyecto automatiza la validación del flujo completo de calificación crediticia:

1. Consulta al servicio de **Calificación** para obtener una política ganadora y detalle de oferta
2. Consulta al servicio de **Enriquecimientos** para obtener controles adicionales
3. Comparación de controles entre ambos servicios
4. Validación del estado de la oferta (aprobada/rechazada) según los controles
5. Validación de los montos calculados por línea de crédito con ponderadores

El proyecto incluye **WireMock** para simular las APIs y poder probar tres escenarios sin necesidad de servicios reales:

- **Caso Aprobado**: Todos los controles pasan (resultado = 1)
- **Caso Rechazado**: Al menos un control falla (resultado = 2)
- **Caso Error 500**: Simula error interno del servidor

## Tecnologías Utilizadas

- **Java 17** - Lenguaje de programación
- **Maven 3.9.4** - Gestión de dependencias y build
- **TestNG** - Framework de testing
- **Rest Assured** - Cliente HTTP para pruebas de APIs REST
- **WireMock 3.3.1** - Mock server para simular APIs
- **Jackson** - Procesamiento de JSON
- **Logback** - Sistema de logging
- **AssertJ** - Aserciones fluidas
- **Json Schema Validator** - Validación de esquemas JSON

## Estructura del Proyecto

```
calificacion-empresas/
├── src/test/
│   ├── java/com/ckuniyoshi/
│   │   ├── constants/          # Constantes (URLs, líneas, políticas, etc.)
│   │   ├── dto/                # DTOs para requests
│   │   ├── tests/              # Clase principal de tests
│   │   │   └── CalificacionEmpresas.java
│   │   └── utils/              # Utilidades y helpers
│   │       ├── CalificacionMockServer.java  # Servidor WireMock
│   │       ├── ResponseCalificacion.java
│   │       ├── ResponseEnriquecimientos.java
│   │       ├── EstadosOferta.java
│   │       ├── CalcularOferta/
│   │       └── DefinirPoliticas/
│   └── resources/
│       ├── config.properties   # Configuración de URLs
│       ├── logback.xml         # Configuración de logs
│       ├── testng.xml          # Suite de TestNG
│       ├── data/
│       │   └── pruebas.csv     # Datos de prueba (CUITs y facturación)
│       └── schemas/            # Esquemas JSON para validación
│           ├── ResponseCalificacion200.json
│           └── ResponseCalificacion500.json
├── logs/                       # Archivos de log y CSV de resultados
├── pom.xml
└── README.md
```

## Requisitos Previos

- **Java JDK 17** o superior
- **Maven 3.9.4** o superior
- Variables de entorno configuradas:
  - `JAVA_HOME` apuntando al JDK
  - `PATH` incluyendo Maven

### Verificar Versión de Java

Para verificar tu versión de Java instalada:

```bash
java -version
```

**Salida esperada (ejemplo):**

```
java version "17.0.8" 2023-07-18 LTS
Java(TM) SE Runtime Environment (build 17.0.8+9-LTS-211)
```

### ⚠️ Compatibilidad de Versiones

| Versión JDK | Estado       | Notas                                                        |
| ----------- | ------------ | ------------------------------------------------------------ |
| JDK < 17    | No soportado | El proyecto usa características de Java 17 (text blocks)     |
| JDK 17      | Recomendado  | Versión target del proyecto                                  |
| JDK 18+     | Compatible   | Java mantiene retrocompatibilidad hacia versiones anteriores |

**Si tienes JDK < 17:**

- Descarga JDK 17 desde [Oracle](https://www.oracle.com/java/technologies/downloads/#java17) o [OpenJDK](https://adoptium.net/)
- Actualiza la variable `JAVA_HOME` para apuntar a la nueva instalación

## Instalación

1. Clonar el repositorio:

```bash
git clone <url-del-repositorio>
cd calificacion-empresas
```

2. Instalar dependencias:

```bash
mvn clean install
```

## Ejecución de Tests

### Ejecutar todos los tests

```bash
mvn clean test
```

### Ver resultados

- **Consola**: Logs detallados de cada paso de las pruebas
- **CSV**: Archivo generado en `logs/pruebas.csv` con resumen de resultados

## Escenarios de Prueba

El archivo `data/inputs.csv` contiene los casos de prueba:

| CUIT        | Facturación Anual | Escenario                            |
| ----------- | ----------------- | ------------------------------------ |
| 27325760457 | $600.000.000      | Aprobado - Todos los controles pasan |
| 27258680524 | $500.000.000      | Rechazado - Control1 con resultado 2 |
| 20111222333 | $400.000.000      | Error 500 - Error del servidor       |

### Flujo de Validación

1. **Lectura de datos**: Se leen los datos del CSV (CUIT y facturación anual)
2. **Llamado a Calificación**: POST al endpoint `/calificacion`
   - Status 200: Valida esquema JSON, obtiene política, estado, oferta y controles
   - Status 500: Valida esquema de error y termina el test
3. **Llamado a Enriquecimientos**: POST al endpoint `/enriquecimientos`
   - Obtiene controles adicionales
4. **Comparación de Controles**: Verifica si algún control tiene resultado = 2 (rechazo)
   - Si hay rechazo: Se omiten las validaciones de estado y montos
   - Si no hay rechazo: Continúa con validaciones
5. **Validación de Estado**: Compara el estado calculado vs el recibido
6. **Validación de Montos**: Calcula montos esperados con ponderadores y compara con los recibidos

### Cálculo de Montos

```
Facturación Mensual = Facturación Anual / 12

Monto Línea 1 = Facturación Mensual × 0.5
Monto Línea 2 = Facturación Mensual × 0.8
Monto Línea 3 = Facturación Mensual × 1.2
```

## WireMock - Mocks Implementados

El servidor WireMock se ejecuta en `http://localhost:8089` y simula ambas APIs:

### Endpoint: `/calificacion`

- **CUIT 27325760457**: Respuesta aprobada con 3 líneas de crédito
- **CUIT 27258680524**: Respuesta con oferta vacía (caso rechazado)
- **CUIT 20111222333**: Error 500

### Endpoint: `/enriquecimientos`

- **CUIT 27325760457**: control1="1", control2="1" (aprobado)
- **CUIT 27258680524**: control1="2", control2="1" (rechazado)
- **CUIT 20111222333**: Error 500

## Validaciones Implementadas

- Validación de esquemas JSON con Json Schema Validator
- Validación de códigos de estado HTTP (200, 500)
- Comparación de controles entre servicios
- Cálculo y validación de montos con ponderadores
- Validación de estado de oferta según lógica de negocio
- Generación de reporte CSV con resultados

## Logs

Los logs se generan en dos lugares:

1. **Consola**: Nivel INFO con detalles de cada paso
2. **Archivo**: `logs/logback.log` con histórico completo

Formato de logs:

```
[timestamp] [nivel] [clase] - mensaje
```

## Características Destacadas

- **Mocking de APIs**: No requiere servicios reales para ejecutar pruebas
- **Data-Driven Testing**: Lee casos de prueba desde CSV
- **Validación de Esquemas**: Garantiza estructura correcta de respuestas
- **Reporting**: Genera CSV con resultados ejecutados
- **Arquitectura limpia**: Separación clara entre DTOs, utils, constants y tests
- **Reutilización**: Clases de utilidad para cálculos y validaciones
- **Manejo de errores**: Validación de casos de error (500)

## Autor

Cecilia Kuniyoshi

## Licencia

Este proyecto es de uso educativo y de portfolio.
