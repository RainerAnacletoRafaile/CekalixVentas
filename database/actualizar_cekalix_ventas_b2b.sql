-- =====================================================
-- Actualización del esquema existente para ventas B2B.
-- Ejecutar UNA SOLA VEZ después de realizar un backup.
-- Los SKU generados son identificadores temporales.
-- unidades_por_caja queda en 0 hasta cargar el dato real.
-- =====================================================
USE cekalix;

ALTER TABLE producto
  ADD COLUMN sku VARCHAR(40) NULL AFTER id_producto,
  ADD COLUMN unidades_por_caja INT NOT NULL DEFAULT 0 AFTER precio,
  ADD COLUMN stock_fisico INT NOT NULL DEFAULT 0 AFTER unidades_por_caja,
  ADD COLUMN stock_reservado INT NOT NULL DEFAULT 0 AFTER stock_fisico;

UPDATE producto
SET sku = CONCAT('SKU-', LPAD(id_producto, 6, '0')),
    stock_fisico = stock;

ALTER TABLE producto
  MODIFY sku VARCHAR(40) NOT NULL,
  ADD CONSTRAINT uq_producto_sku UNIQUE (sku),
  ADD CONSTRAINT chk_producto_unidades_caja CHECK (unidades_por_caja >= 0),
  ADD CONSTRAINT chk_producto_stock CHECK (
    stock_fisico >= 0 AND stock_reservado >= 0 AND stock_reservado <= stock_fisico
  );

ALTER TABLE venta
  MODIFY estado VARCHAR(30) NOT NULL DEFAULT 'Pendiente';

UPDATE venta SET estado = 'Confirmada' WHERE estado = 'Registrada';
