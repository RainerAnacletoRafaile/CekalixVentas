-- =====================================================
-- CEKALIX S.A.C. - Base de datos del sistema de ventas
-- MySQL 8+ / InnoDB
-- =====================================================
CREATE DATABASE IF NOT EXISTS cekalix
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE cekalix;

CREATE TABLE IF NOT EXISTS rol (
  id_rol INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(30) NOT NULL UNIQUE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS usuario (
  id_usuario INT AUTO_INCREMENT PRIMARY KEY,
  id_rol INT NOT NULL DEFAULT 2,
  nombre VARCHAR(100) NOT NULL,
  usuario VARCHAR(40) NOT NULL UNIQUE,
  contrasena VARCHAR(100) NOT NULL,
  estado BOOLEAN NOT NULL DEFAULT TRUE,
  CONSTRAINT fk_usuario_rol FOREIGN KEY (id_rol) REFERENCES rol(id_rol)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS cliente (
  id_cliente INT AUTO_INCREMENT PRIMARY KEY,
  razon_social VARCHAR(100) NOT NULL,
  ruc VARCHAR(11) NOT NULL UNIQUE,
  telefono VARCHAR(15),
  direccion VARCHAR(150)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS producto (
  id_producto INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(100) NOT NULL,
  medida VARCHAR(50),
  grosor VARCHAR(50),
  color VARCHAR(50),
  precio DECIMAL(10,2) NOT NULL,
  stock INT NOT NULL DEFAULT 0
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS cotizacion (
  id_cotizacion INT AUTO_INCREMENT PRIMARY KEY,
  id_cliente INT NOT NULL,
  id_usuario INT NOT NULL,
  fecha DATE NOT NULL,
  estado VARCHAR(30) NOT NULL DEFAULT 'Pendiente',
  total DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  CONSTRAINT fk_cotizacion_cliente FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente),
  CONSTRAINT fk_cotizacion_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS detalle_cotizacion (
  id_detalle_cotizacion INT AUTO_INCREMENT PRIMARY KEY,
  id_cotizacion INT NOT NULL,
  id_producto INT NOT NULL,
  cantidad INT NOT NULL,
  precio_unitario DECIMAL(10,2) NOT NULL,
  subtotal DECIMAL(10,2) NOT NULL,
  CONSTRAINT fk_detalle_cotizacion FOREIGN KEY (id_cotizacion)
    REFERENCES cotizacion(id_cotizacion) ON DELETE CASCADE,
  CONSTRAINT fk_detalle_cotizacion_producto FOREIGN KEY (id_producto)
    REFERENCES producto(id_producto)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS venta (
  id_venta INT AUTO_INCREMENT PRIMARY KEY,
  id_cliente INT NOT NULL,
  id_usuario INT NOT NULL,
  id_cotizacion INT NULL,
  fecha DATE NOT NULL,
  total DECIMAL(10,2) NOT NULL,
  estado VARCHAR(30) NOT NULL DEFAULT 'Registrada',
  CONSTRAINT fk_venta_cliente FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente),
  CONSTRAINT fk_venta_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario),
  CONSTRAINT fk_venta_cotizacion FOREIGN KEY (id_cotizacion) REFERENCES cotizacion(id_cotizacion)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS detalle_venta (
  id_detalle_venta INT AUTO_INCREMENT PRIMARY KEY,
  id_venta INT NOT NULL,
  id_producto INT NOT NULL,
  cantidad INT NOT NULL,
  precio_unitario DECIMAL(10,2) NOT NULL,
  subtotal DECIMAL(10,2) NOT NULL,
  CONSTRAINT fk_detalle_venta FOREIGN KEY (id_venta)
    REFERENCES venta(id_venta) ON DELETE CASCADE,
  CONSTRAINT fk_detalle_venta_producto FOREIGN KEY (id_producto)
    REFERENCES producto(id_producto)
) ENGINE=InnoDB;

-- Datos iniciales idempotentes
INSERT IGNORE INTO rol (id_rol, nombre) VALUES
  (1, 'Administrador'), (2, 'Vendedor');

-- Acceso inicial: admin / admin123. Cambiar la clave luego del primer ingreso.
INSERT INTO usuario (id_rol, nombre, usuario, contrasena, estado)
SELECT 1, 'Administrador', 'admin', 'admin123', TRUE
WHERE NOT EXISTS (SELECT 1 FROM usuario WHERE usuario = 'admin');

SHOW TABLES;
SELECT id_usuario, nombre, usuario, id_rol, estado FROM usuario;
