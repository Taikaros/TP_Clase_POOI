# Introducción
El Presente Documento detalla las decisiones arquitectonicas y de modelado tomadas para el desarrollo del sistema de gestion Veterinaria. El diseño se centro en construir un **modelo de dominio rico**, asegurando que la logica de negocio resida en las entidades correspondientes y facilitando la persistencia mediante **JPA**.

## Justificación de Decisiones de Diseño Específicas
*  Se optó por utilizar herencia, creando una clase abstracta `Servicio` de la cual extienden subclases específicas como `Consulta`, `Peluqueria`, `Guarderia` y `Vacunacion`, porque cada servicio tiene sus `atributos` y `comportamientos` radicalmente distintos. Por ello no se utilizó la estructura `enum` dado que forzaría a tener atributos nulos en la base de datos dependiendo del tipo de servicio, rompiendo la cohesión.
*  Para el precio histórico se resolvió incluyendo `precioHistorico` directamente en la clase abstracta `Servicio`. Al momento de instanciar un servicio y agregarlo a un `Turno`, se copia el `precioBase` actual desde `TipoServicio` hacia `precioHistorico`. De esta manera, si el catálogo de precios cambia en el futuro, los reportes históricos leerán el valor inmutable guardado en la instancia del servicio ya brindado
*  Asociaciones Complejas: El `Turno`actúa como la entidad central(raiz del agregado). Tiene una relacion *Many-To-One* con `Mascota` como con `Veterinario`(un turno pertenece a una mascota y a un veterinario, pero ellos pueden tener multiples turnos). A su vez, el `Turno` contiene una coleccion de `Servicios`, lo que permite que una sola cita incluya mas de un solo sericion
*  Separación del Control de Vacunaciones: Se decidió separar el concepto en dos clases distintas. Por un lado, la clase `Vacuna` funciona como un catálogo inmutable que guarda los datos necesarios para la validación y el cálculo de vencimientos (enfermedad que previene y periodicidad en meses). Por otro lado, la clase `Vacunacion` hereda de Servicio y se encarga de representar la acción física dentro de un turno, asociándose al catálogo para su utilización. Esto evita la duplicación de datos de referencia cada vez que se aplica una dosis.

## Ubicacion de las Reglas de Negocio
Según los puntos del integrador, las reglas no están en los controladores sino en el modelo de dominio.
Algunos ejemplos podrían ser:
* Dueño obligatorio: Se aplica en el constructor de `Mascota`, exigiendo un objeto de tipo `Cliente` para su instanciación y evitando mascotas sin dueño en el sistema.
* Límites de cancelación (24 hs antes): Se implementó el método `cancelarTurno(ahora: LocalDateTime)` de la clase `Turno`. Este método compara la hora actual con la hora programada y lanza una excepción propia si no se cumple el plazo de aviso previo.
* Validacion de Solapamiento de Turnos: Se tomo la decision de que la clase `Veterinario` seria la encargada de revisar su propia disponibilidad, esto en base al metodo `valudarDisponibilidad(fecha, hora, duracion)`, la cual itera sobre su propia lista de turnos para prevenir cruces de horarios, evitando delegar esta regla a algun controlador anemico

## Decisiones de Infraestructura y Persistencia
* Arquitectura de Base de Datos y Persistencia: Para la persistencia de datos mediante JPA/Hibernate, se optó por utilizar PostgreSQL en lugar de la opción embebida H2. Adicionalmente, se decidió desplegar la base de datos en un entorno en la nube utilizando el servicio Neon. Esta decisión se fundamenta en dos motivos principales:
  * Desarrollo Colaborativo: Al contar con un servidor en línea, todos los desarrolladores del equipo pueden conectarse a la misma instancia de la base de datos de manera simultánea. Esto elimina los conflictos de sincronización de datos locales y facilita enormemente las pruebas de integración durante nuestro flujo de trabajo.
  * Resiliencia y Respaldo: Operar con una base de datos remota actúa como un método de respaldo natural. Ante cualquier falla catastrófica en el entorno de ejecución local (la máquina remota) o un problema crítico en la aplicación de escritorio, la integridad histórica de los datos del sistema (clientes, mascotas, turnos y facturación) permanece asegurada en la nube.
> Para ejecutar el proyecto y conectar con la base de datos en la nube, las credenciales y la URL de conexion a Neon se encuntrar preconfiguradas en el archivo `persistence.xml`, requiriendo unicamente conexion a internet activa para utilizarla

> Por temas de seguridad se excluyo el `Password` hacia la base de datos, en caso de necesitar por favor contacte al equipo desarrollador.
## Dificultades Encontradas y Resolucion de Ellas:


## Funcionalidades implementadas y pendientes: