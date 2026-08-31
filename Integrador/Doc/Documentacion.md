# Documentación del Sistema de Gestión Veterinaria "Huellas & Salud"

## 1. Introducción
El presente documento detalla las decisiones arquitectónicas y de modelado tomadas para el desarrollo del sistema de gestión Veterinaria. El diseño se centró en construir un **modelo de dominio rico**, asegurando que la lógica de negocio resida en las entidades correspondientes y facilitando la persistencia mediante **JPA**.

## 2. Decisiones de Diseño y Modelado
* **Polimorfismo en Servicios:** Se optó por utilizar herencia, creando una clase abstracta `Servicio` de la cual extienden subclases específicas como `Consulta`, `Peluqueria`, `Guarderia` y `Vacunacion`. Cada servicio tiene sus atributos y comportamientos radicalmente distintos. Evitamos la estructura `enum` para no forzar columnas nulas en la base de datos, garantizando la cohesión.
* **Inmutabilidad de Precios Históricos:** Se incluyó el atributo `precioHistorico` directamente en la clase `Servicio`. Al instanciar un servicio y agregarlo a un `Turno`, se copia el `precioBase` actual desde `TipoServicio`. Si el catálogo de precios muta en el futuro, los reportes históricos leerán el valor inmutable del servicio ya brindado, protegiendo la integridad contable.
* **Turno como Raíz del Agregado:** El `Turno` actúa como la entidad central. Mantiene relaciones *Many-To-One* con `Mascota` y `Veterinario` (previniendo solapamientos lógicos), y contiene una colección `@OneToMany` de `Servicios` con `CascadeType.ALL`, permitiendo agrupar múltiples prácticas (ej. Consulta + Vacunación) en una misma cita.
* **Separación de Vacunas y Vacunación:** La clase `Vacuna` funciona como un catálogo inmutable para la validación y el cálculo de vencimientos (enfermedad que previene y periodicidad en meses). La clase `Vacunacion` hereda de `Servicio` y representa el acto físico de aplicar la dosis dentro de un turno, evitando la duplicación de datos de referencia.
* **Borrado Lógico:** Para la cancelación de turnos, se optó por un cambio de estado en lugar de un borrado físico. El atributo `estado` transiciona a `CANCELADO`, lo cual es vital para mantener estadísticas de ausentismo y auditorías. El historial médico se genera filtrando a nivel de base de datos para omitir estas cancelaciones.

## 3. Ubicación de las Reglas de Negocio
Las reglas de negocio fueron extraídas de los servicios (evitando el anti-patrón de Modelo Anémico) y encapsuladas directamente en el Dominio:
* **Dueño y Ficha Obligatorios:** En el constructor de `Mascota`, se exige un objeto `Cliente` para su instanciación. La autogeneración del número de ficha se controla lógicamente garantizando correlatividad.
* **Límites de Cancelación:** El método `cancelar(ahora: LocalDateTime)` de la clase `Turno` compara la hora actual con la hora programada. Lanza una excepción propia (`CancelacionFueradeTermino`) si no se cumple el plazo de 24 horas de aviso previo.
* **Disponibilidad y Solapamiento:** La clase `Veterinario` es la encargada de revisar su propia disponibilidad mediante el método `validarDisponibilidad(fecha, hora, duracion)`. Analiza su lista de horarios y cruza los días (manejando correctamente formatos con y sin tildes lógicos) para prevenir colisiones de agenda.
* **Validación Clínica de Inmunidad:** La clase `Mascota` utiliza el método `tieneVacunaVigente` para escanear su propio historial de turnos atendidos, evaluar la periodicidad de la vacuna solicitada y bloquear aplicaciones prematuras lanzando `VacunaVigenteException`.

## 4. Decisiones de Infraestructura y Persistencia
* **PostgreSQL en la Nube (Neon):** Para la persistencia de datos mediante Hibernate, se utilizó PostgreSQL remoto en lugar de H2 local. Esto facilitó el desarrollo colaborativo del equipo (evitando desincronización de bases de datos locales) y proporcionó un entorno de pruebas robusto y tolerante a fallos.
* **Generación de DDL:** Se utilizó la propiedad `hibernate.hbm2ddl.auto` seteada en `update` para mapear de forma transparente las entidades a tablas, y se recurrió a scripts de DDL puros únicamente para el reseteo limpio del esquema durante el ciclo de pruebas.

## 5. Dificultades Encontradas y su Resolución
1. **Desincronización del Esquema Relacional:** Durante las iteraciones del modelo (cambio de nombres de entidades al plural), se generaron "tablas fantasma" en PostgreSQL que rompieron las claves foráneas (ej. `turno` vs `turnos`). Se resolvió ejecutando un `DROP SCHEMA public CASCADE` directamente en el motor y permitiendo a Hibernate reconstruir la topología limpia.
2. **Conflictos Transaccionales (Foreign Key Violations):** Al editar un turno y reemplazar su lista de servicios, Hibernate lanzaba errores de violación de integridad al intentar borrar registros atados prematuramente. Se solucionó eliminando instrucciones `flush()` manuales de los controladores/servicios, delegando la resolución del estado transaccional íntegramente al `commit()` de JPA.
3. **Bloqueos de Interfaz por Validaciones de Dominio:** Al blindar los modelos para que no acepten datos nulos o vacíos, la interfaz bloqueaba la autogeneración de campos (como el número de ficha). La solución fue desacoplar el `disableProperty` en JavaFX y manejar el valor híbrido (`null` o escrito) mediante lógica autoincremental en el bloque `try-catch` del Servicio.

## 6. Funcionalidades
**Implementadas:**
* ABM completo de Clientes, Mascotas, Veterinarios y Especialidades.
* Configuración de Tipos de Servicio (prácticas, precios, duración y cupos) y Catálogo de Vacunas.
* Motor de Agendamiento de Turnos con máquina de estados restrictiva (Pendiente -> Confirmado -> Atendido -> Cancelado).
* Dashboard Operativo de Guardería (control de jaulas) y Peluquería (turnos diarios).
* Dashboard de Vacunaciones con detección algorítmica de próximos vencimientos (30 días) y dosis atrasadas.
* Historial Médico interactivo con generación y exportación automática a formato PDF (librería iText).
* Arquitectura Defensiva por Capas: Validaciones estrictas en el dominio interceptadas y presentadas amigablemente en la UI.

