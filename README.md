# CekalixVentas

Sistema web de gestión de ventas B2B de CEKALIX S.A.C., orientado a clientes empresariales y comercialización de productos ferreteros.

## Tecnologías

- Java
- Jakarta Servlet
- Apache Tomcat
- JSP y Bootstrap 5
- MySQL 8+
- JDBC con MySQL Connector/J

## Estructura actual

```text
src/main/java/
├── controlador/   # Proceso_Login, Proceso_Cliente, Proceso_Producto y Proceso_Venta
├── datos/         # ConexionBD y Mantenimiento_*
└── modelo/        # Cliente, Producto, Usuario, Venta y CarritoVenta

src/main/webapp/   # Vistas JSP, CSS y configuración web
database/          # Esquema y actualización de la base de datos
```

Se conservan los nombres y la organización que ya utiliza el proyecto. No se crean módulos independientes de inventario, importaciones, proveedores ni logística.

## Funcionalidades de ventas B2B contempladas

- Identificación del cliente empresarial mediante RUC y razón social.
- Consulta de productos por SKU o nombre.
- Consulta de cajas físicas, reservadas y disponibles.
- Indicador de stock crítico cuando hay menos de 10 cajas disponibles.
- Registro de órdenes con uno o varios SKU y cantidades en cajas.
- Aplicación de precios por volumen para cada SKU:
  - De 1 a 9 cajas: precio base.
  - De 10 a 49 cajas: 5 % de descuento.
  - Desde 50 cajas: 12 % de descuento.
- Registro inicial de la orden como Pendiente.
- Confirmación transaccional y reserva de cajas sin modificar el stock físico.
- Consulta del historial de órdenes y sus estados.

## Base de datos

### Instalación nueva

1. Inicia MySQL.
2. Importa `database/cekalix.sql`.
3. Configura URL, usuario y contraseña en `datos/ConexionBD.java`.
4. Verifica que MySQL Connector/J esté en `src/main/webapp/WEB-INF/lib`.
5. Importa el proyecto en Eclipse y ejecútalo en Tomcat.

### Base de datos existente

Antes de actualizar, realiza un backup y ejecuta una sola vez `database/actualizar_cekalix_ventas_b2b.sql`. El script conserva las existencias anteriores en `stock_fisico`, genera SKU provisionales y agrega el campo de stock reservado.

Los SKU generados tienen formato `SKU-000001`; reemplázalos por los SKU comerciales reales. El valor `unidades_por_caja` queda en cero hasta cargar la conversión correcta. No se inventa esa equivalencia: mientras no se registre, la consulta mostrará "Por definir".

Las cantidades de venta y existencias se expresan en cajas. La disponibilidad es `stock_fisico - stock_reservado`.

## Reglas de implementación

- Los controladores coordinan las peticiones y validan la sesión.
- Las clases de `datos` concentran el acceso JDBC y las transacciones.
- Las clases de `modelo` representan los datos y cálculos del proceso comercial.
- Las vistas JSP muestran información recibida desde los controladores y no ejecutan SQL.
- Las conexiones, sentencias y resultados se cierran mediante `try-with-resources`.

## Configuración y seguridad

La configuración inicial usa `localhost:3306`, base `cekalix`, usuario `root` y contraseña vacía. Ajusta los valores a tu instalación local.

El usuario administrador incluido en el SQL es solo para desarrollo. Antes de producción, se debe migrar el almacenamiento de contraseñas a hashes seguros y sacar las credenciales de base de datos del código fuente.
