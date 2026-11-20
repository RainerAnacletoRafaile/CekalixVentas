# CekalixVentas

Sistema web de gestión de clientes, productos y ventas B2B.

## Tecnologías

- Java 25
- Jakarta Servlet 6
- Apache Tomcat 11
- JSP y Bootstrap 5
- MySQL 8+
- JDBC con MySQL Connector/J

## Estructura del proyecto

```text
src/main/java/
├── controlador/   # Servlets: reciben peticiones y coordinan el flujo MVC
├── datos/         # DAO y acceso a MySQL
│   ├── ConexionBD.java
│   └── Mantenimiento_*.java
└── modelo/        # Entidades y objetos del dominio
    ├── Cliente.java
    ├── Producto.java
    ├── Usuario.java
    ├── Venta.java
    └── CarritoVenta.java

src/main/webapp/   # Vistas JSP, CSS y configuración web
database/          # Script de creación de la base de datos
```

## Puesta en marcha local

1. Inicia MySQL.
2. Importa `database/cekalix.sql` en MySQL.
3. Revisa URL, usuario y contraseña de MySQL en `datos/ConexionBD.java`.
4. Verifica que MySQL Connector/J esté en `src/main/webapp/WEB-INF/lib`.
5. Importa el proyecto en Eclipse y ejecútalo en Tomcat 11.

La configuración predeterminada usa `localhost:3306`, base de datos `cekalix`, usuario `root` y contraseña vacía. Ajusta esos valores si tu instalación es diferente.

## Responsabilidades MVC

- **Controlador:** valida la petición, la sesión y coordina los DAO.
- **Modelo:** representa los datos y reglas del dominio. `CarritoVenta` administra productos y cálculos sin abrir conexiones.
- **Datos:** ejecuta consultas y transacciones. `ConexionBD` centraliza la creación de conexiones JDBC.
- **Vistas:** muestran información recibida desde los controladores; no deben ejecutar consultas SQL.

Los DAO usan `try-with-resources` para cerrar automáticamente conexiones, sentencias y resultados.

## Nota de seguridad

El usuario administrador incluido en el script SQL es solo para desarrollo local. Antes de publicar el sistema, se debe migrar el almacenamiento de contraseñas a hashes seguros y sacar las credenciales de base de datos del código fuente.
