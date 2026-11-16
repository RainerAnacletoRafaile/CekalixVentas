-- =====================================================
-- CEKALIX S.A.C. - Base de datos del sistema de ventas
-- Motor: MySQL / InnoDB
-- Ejecutar este archivo completo en MySQL Workbench.
-- =====================================================

CREATE DATABASE IF NOT EXISTS cekalix
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE cekalix;

-- Desactivar temporalmente las comprobaciones para permitir
-- volver a ejecutar el script sin problemas de orden.
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS detalle_venta;
DROP TABLE IF EXISTS venta;
DROP TABLE IF EXISTS detalle_cotizacion;
DROP TABLE IF EXISTS cotizacion;
DROP TABLE IF EXISTS producto;
DROP TABLE IF EXISTS cliente;
DROP TABLE IF EXISTS usuario;
DROP TABLE IF EXISTS rol;

SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE rol (
    id_rol INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(30) NOT NULL UNIQUE
) ENGINE=InnoDB;

CREATE TABLE usuario (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    id_rol INT NOT NULL,
    nombre VARCHAR(25) NOT NULL,
    usuario VARCHAR(40) NOT NULL UNIQUE,
    contrasena VARCHAR(40) NOT NULL,
    estado BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_usuario_rol
        FOREIGN KEY (id_rol) REFERENCES rol(id_rol)
) ENGINE=InnoDB;

CREATE TABLE cliente (
    id_cliente INT AUTO_INCREMENT PRIMARY KEY,
    razon_social VARCHAR(100) NOT NULL,
    ruc VARCHAR(11) NOT NULL UNIQUE,
    telefono VARCHAR(15),
    direccion VARCHAR(50)
) ENGINE=InnoDB;

CREATE TABLE producto (
    id_producto INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    medida VARCHAR(50),
    grosor VARCHAR(50),
    color VARCHAR(50),
    precio DECIMAL(8,2) NOT NULL,
    stock INT NOT NULL DEFAULT 0
) ENGINE=InnoDB;

CREATE TABLE cotizacion (
    id_cotizacion INT AUTO_INCREMENT PRIMARY KEY,
    id_cliente INT NOT NULL,
    id_usuario INT NOT NULL,
    fecha DATE NOT NULL,
    estado VARCHAR(30) NOT NULL DEFAULT 'Pendiente',
    total DECIMAL(8,2) NOT NULL DEFAULT 0.00,
    CONSTRAINT fk_cotizacion_cliente
        FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente),
    CONSTRAINT fk_cotizacion_usuario
        FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)
) ENGINE=InnoDB;

CREATE TABLE detalle_cotizacion (
    id_detalle_cotizacion INT AUTO_INCREMENT PRIMARY KEY,
    id_cotizacion INT NOT NULL,
    id_producto INT NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(8,2) NOT NULL,
    subtotal DECIMAL(8,2) NOT NULL,
    CONSTRAINT fk_detalle_cotizacion
        FOREIGN KEY (id_cotizacion) REFERENCES cotizacion(id_cotizacion)
        ON DELETE CASCADE,
    CONSTRAINT fk_detalle_cotizacion_producto
        FOREIGN KEY (id_producto) REFERENCES producto(id_producto)
) ENGINE=InnoDB;

CREATE TABLE venta (
    id_venta INT AUTO_INCREMENT PRIMARY KEY,
    id_cliente INT NOT NULL,
    id_usuario INT NOT NULL,
    id_cotizacion INT NULL,
    fecha DATE NOT NULL,
    total DECIMAL(8,2) NOT NULL,
    estado VARCHAR(30) NOT NULL DEFAULT 'Registrada',
    CONSTRAINT fk_venta_cliente
        FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente),
    CONSTRAINT fk_venta_usuario
        FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario),
    CONSTRAINT fk_venta_cotizacion
        FOREIGN KEY (id_cotizacion) REFERENCES cotizacion(id_cotizacion)
) ENGINE=InnoDB;

CREATE TABLE detalle_venta (
    id_detalle_venta INT AUTO_INCREMENT PRIMARY KEY,
    id_venta INT NOT NULL,
    id_producto INT NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(8,2) NOT NULL,
    subtotal DECIMAL(8,2) NOT NULL,
    CONSTRAINT fk_detalle_venta
        FOREIGN KEY (id_venta) REFERENCES venta(id_venta)
        ON DELETE CASCADE,
    CONSTRAINT fk_detalle_venta_producto
        FOREIGN KEY (id_producto) REFERENCES producto(id_producto)
) ENGINE=InnoDB;

-- Roles iniciales
INSERT INTO rol (id_rol, nombre) VALUES
(1, 'Administrador'),
(2, 'Vendedor');

-- Usuario administrador inicial.
-- Usuario: admin
-- Contraseña: admin123
-- Contraseña en texto simple, según el alcance académico solicitado.
INSERT INTO usuario (id_rol, nombre, usuario, contrasena, estado)
SELECT 1, 'Administrador', 'admin', 'admin123', TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM usuario WHERE usuario = 'admin'
);

-- Verificación rápida
SHOW TABLES;
SELECT id_usuario, nombre, usuario, id_rol, estado FROM usuario;
