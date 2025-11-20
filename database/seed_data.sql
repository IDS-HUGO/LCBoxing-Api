-- ===========================================
-- DATOS INICIALES PARA LCBOXING
-- ===========================================

USE lcboxing_normalizada;

-- ===========================================
-- 1. ROLES
-- ===========================================
INSERT INTO roles (id_rol, nombre_rol, descripcion) VALUES
(1, 'GERENTE', 'Acceso total al sistema, gestión de usuarios y configuración'),
(2, 'STAFF', 'Registro de asistencias, consulta de información de atletas');

-- ===========================================
-- 2. TIPOS DE MEMBRESÍA
-- ===========================================
INSERT INTO tipos_membresia (nombre_tipo, descripcion, duracion_dias, precio_base, sesiones_incluidas, activo) VALUES
('Mensual', 'Membresía mensual con acceso ilimitado', 30, 500.00, NULL, TRUE),
('Trimestral', 'Membresía trimestral con acceso ilimitado', 90, 1350.00, NULL, TRUE),
('Semestral', 'Membresía semestral con acceso ilimitado', 180, 2500.00, NULL, TRUE),
('Anual', 'Membresía anual con acceso ilimitado', 365, 4500.00, NULL, TRUE),
('12 Sesiones', 'Paquete de 12 sesiones', 60, 600.00, 12, TRUE),
('24 Sesiones', 'Paquete de 24 sesiones', 90, 1100.00, 24, TRUE),
('Clase Individual', 'Una sola clase', 1, 100.00, 1, TRUE);

-- ===========================================
-- 3. MÉTODOS DE PAGO
-- ===========================================
INSERT INTO metodos_pago (nombre_metodo, requiere_referencia, activo) VALUES
('EFECTIVO', FALSE, TRUE),
('TARJETA_DEBITO', TRUE, TRUE),
('TARJETA_CREDITO', TRUE, TRUE),
('TRANSFERENCIA', TRUE, TRUE),
('PAYPAL', TRUE, TRUE),
('OTRO', FALSE, TRUE);

-- ===========================================
-- 4. ESTADOS DE MEMBRESÍA
-- ===========================================
INSERT INTO estados_membresia (id_estado_membresia, nombre_estado, descripcion) VALUES
(1, 'ACTIVA', 'Membresía vigente y utilizable'),
(2, 'VENCIDA', 'Membresía expirada'),
(3, 'SUSPENDIDA', 'Membresía temporalmente suspendida'),
(4, 'CANCELADA', 'Membresía cancelada permanentemente');

-- ===========================================
-- 5. USUARIO ADMINISTRADOR INICIAL
-- ===========================================
-- Password: admin123 (encriptado con BCrypt)
-- Nota: Deberás cambiar este password al iniciar por primera vez
INSERT INTO usuarios (id_rol, nombre, apellido_paterno, apellido_materno, email, password_hash, telefono, activo) VALUES
(1, 'Administrador', 'Sistema', 'LC', 'admin@lcboxing.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '1234567890', TRUE);

-- ===========================================
-- 6. ATLETAS DE EJEMPLO (OPCIONAL)
-- ===========================================
INSERT INTO atletas (nombre, apellido_paterno, apellido_materno, email, telefono, fecha_nacimiento, genero, id_usuario_registro, activo, notas) VALUES
('Carlos', 'Ramírez', 'Torres', 'carlos.ramirez@example.com', '5551234567', '2000-03-15', 'M', 1, TRUE, 'Principiante en boxeo'),
('María', 'González', 'López', 'maria.gonzalez@example.com', '5559876543', '1998-07-22', 'F', 1, TRUE, 'Nivel intermedio'),
('Juan', 'Martínez', 'García', 'juan.martinez@example.com', '5556789012', '2002-11-05', 'M', 1, TRUE, 'Nuevo ingreso');

-- ===========================================
-- 7. DATOS MÉDICOS DE EJEMPLO
-- ===========================================
INSERT INTO datos_medicos (id_atleta, tipo_sangre, alergias, condiciones_medicas) VALUES
(1, 'O+', 'Ninguna', 'Ninguna'),
(2, 'A+', 'Polen', 'Asma leve controlada'),
(3, 'B+', 'Ninguna', 'Ninguna');

-- ===========================================
-- 8. CONTACTOS DE EMERGENCIA
-- ===========================================
INSERT INTO contactos_emergencia (id_atleta, nombre_contacto, telefono_contacto, relacion) VALUES
(1, 'Rosa Torres', '5551112222', 'Madre'),
(2, 'Pedro González', '5553334444', 'Padre'),
(3, 'Ana García', '5555556666', 'Madre');

-- ===========================================
-- 9. MEMBRESÍAS DE EJEMPLO
-- ===========================================
INSERT INTO membresias (id_atleta, id_tipo_membresia, id_estado_membresia, fecha_inicio, fecha_vencimiento, precio_pagado, sesiones_restantes, id_usuario_registro, observaciones) VALUES
(1, 1, 1, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 30 DAY), 500.00, NULL, 1, 'Membresía mensual activa'),
(2, 5, 1, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 60 DAY), 600.00, 12, 1, 'Paquete de 12 sesiones'),
(3, 1, 1, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 30 DAY), 500.00, NULL, 1, 'Membresía mensual nueva');

-- ===========================================
-- 10. PAGOS DE EJEMPLO
-- ===========================================
INSERT INTO pagos (id_membresia, id_metodo_pago, monto, referencia, concepto, id_usuario_registro, notas) VALUES
(1, 1, 500.00, NULL, 'Pago de membresía mensual', 1, 'Pago en efectivo'),
(2, 3, 600.00, 'REF-12345', 'Pago de paquete 12 sesiones', 1, 'Pago con tarjeta de crédito'),
(3, 1, 500.00, NULL, 'Pago de membresía mensual', 1, 'Pago en efectivo');

-- ===========================================
-- 11. ASISTENCIAS DE EJEMPLO
-- ===========================================
INSERT INTO asistencias (id_atleta, id_membresia, fecha_asistencia, hora_entrada, hora_salida, id_usuario_registro_entrada, id_usuario_registro_salida, observaciones) VALUES
(1, 1, CURDATE(), '18:00:00', '20:00:00', 1, 1, 'Entrenamiento completo'),
(2, 2, CURDATE(), '19:00:00', NULL, 1, NULL, 'En entrenamiento'),
(3, 3, CURDATE() - INTERVAL 1 DAY, '17:30:00', '19:30:00', 1, 1, 'Primera clase');

-- ===========================================
-- VERIFICACIÓN DE DATOS
-- ===========================================
SELECT 'Roles insertados:' AS info, COUNT(*) AS total FROM roles;
SELECT 'Tipos de membresía insertados:' AS info, COUNT(*) AS total FROM tipos_membresia;
SELECT 'Métodos de pago insertados:' AS info, COUNT(*) AS total FROM metodos_pago;
SELECT 'Estados de membresía insertados:' AS info, COUNT(*) AS total FROM estados_membresia;
SELECT 'Usuarios insertados:' AS info, COUNT(*) AS total FROM usuarios;
SELECT 'Atletas insertados:' AS info, COUNT(*) AS total FROM atletas;

-- ===========================================
-- CREDENCIALES DE ACCESO INICIAL
-- ===========================================
-- Email: admin@lcboxing.com
-- Password: admin123
-- 
-- ¡IMPORTANTE! Cambia este password después del primer inicio de sesión
-- ===========================================
