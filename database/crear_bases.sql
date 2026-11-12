-- =============================================================
-- Laboratorio: Comunicación entre Servicios (Eureka + OpenFeign)
-- Crea una base de datos independiente por microservicio
-- (patrón "Database per Service": ningún servicio consulta la BD de otro).
-- Las tablas las crea Hibernate (ddl-auto: update) al iniciar cada servicio.
-- =============================================================

CREATE DATABASE IF NOT EXISTS bd_categoria_dev CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS bd_producto_dev  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS bd_cliente_dev   CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS bd_pedido_dev    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

SHOW DATABASES LIKE 'bd_%_dev';
