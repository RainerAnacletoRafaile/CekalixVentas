# CekalixVentas

Sistema web de gestión de ventas B2B de CEKALIX S.A.C., orientado a clientes empresariales y comercialización de productos ferreteros.

## Tecnologías

- Java
- Jakarta Servlet
- Apache Tomcat
- JSP y Bootstrap 5
- MySQL 8+
- JDBC con MySQL Connector/J

## Estructura del proyecto

```text
src/main/java/
├── controlador/   # Proceso_Login, Proceso_Cliente, Proceso_Producto y Proceso_Venta
├── datos/         # ConexionBD y Mantenimiento_*
└── modelo/        # Cliente, Producto, Usuario, Venta y CarritoVenta

src/main/webapp/   # Vistas JSP, CSS y configuración web
database/          # Esquema y actualización de la base de datos
```

Se mantienen los nombres y la organización que ya utiliza CEKALIX Ventas. El alcance es el proceso comercial B2B; no se crean módulos independientes de inventario, importaciones, proveedores ni logística.

## Funcionalidades de ventas B2B

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
3. Revisa URL, usuario y contraseña en `datos/ConexionBD.java`.
4. Verifica MySQL Connector/J en `src/main/webapp/WEB-INF/lib`.
5. Importa el proyecto en Eclipse y ejecútalo en Tomcat.

### Base de datos existente

Realiza un backup y ejecuta una sola vez `database/actualizar_cekalix_ventas_b2b.sql`. La migración copia las existencias actuales a `stock_fisico`, crea SKU provisionales y agrega el stock reservado.

Los SKU generados tienen formato `SKU-000001`; reemplázalos por los SKU comerciales reales. El valor `unidades_por_caja` queda en cero hasta cargar la equivalencia real. No se inventa esa conversión: mientras no esté definida, la consulta muestra "Por definir".

Las cantidades de venta y existencias se expresan en cajas. La disponibilidad es `stock_fisico - stock_reservado`.

## Responsabilidades del código

- **Controlador:** valida peticiones y sesión, y coordina el proceso de ventas.
- **Datos:** concentra las consultas JDBC y las transacciones.
- **Modelo:** representa clientes, productos, usuarios, ventas y cálculos comerciales.
- **Vistas:** muestran información enviada por los controladores; no ejecutan SQL.

Las conexiones, sentencias y resultados se cierran con `try-with-resources`.

## Configuración y seguridad

La configuración inicial usa `localhost:3306`, base `cekalix`, usuario `root` y contraseña vacía. Ajusta estos valores a tu instalación local.

El usuario administrador incluido en el SQL es solo para desarrollo. Antes de producción, se debe migrar el almacenamiento de contraseñas a hashes seguros y sacar las credenciales de base de datos del código fuente.
