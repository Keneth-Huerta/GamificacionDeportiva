-- ============================================
-- Sistema de Gamificación Deportiva
-- Script de Creación de Base de Datos
-- ============================================

-- Crear base de datos
CREATE DATABASE IF NOT EXISTS gamificacion_deportiva 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE gamificacion_deportiva;

-- ============================================
-- Tabla: Usuarios
-- Almacena información de los usuarios del sistema
-- ============================================
CREATE TABLE IF NOT EXISTS Usuarios (
    id VARCHAR(50) PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    nivel INT DEFAULT 1,
    puntosTotales INT DEFAULT 0,
    experiencia INT DEFAULT 0,
    fotoPerfil BLOB,
    esAdmin BOOLEAN DEFAULT FALSE,
    fechaRegistro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ultimaActualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_nombre (nombre),
    INDEX idx_nivel (nivel),
    INDEX idx_puntos (puntosTotales)
) ENGINE=InnoDB;

-- ============================================
-- Tabla: Actividades
-- Registra las actividades deportivas realizadas
-- ============================================
CREATE TABLE IF NOT EXISTS Actividades (
    id VARCHAR(50) PRIMARY KEY,
    usuarioId VARCHAR(50) NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    fecha DATE NOT NULL,
    hora TIME NOT NULL,
    duracionMinutos INT NOT NULL,
    distanciaKm DOUBLE DEFAULT 0,
    caloriasQuemadas INT DEFAULT 0,
    esCompetencia BOOLEAN DEFAULT FALSE,
    competenciaId VARCHAR(50) DEFAULT NULL,
    fechaRegistro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (usuarioId) REFERENCES Usuarios(id) ON DELETE CASCADE,
    INDEX idx_usuario (usuarioId),
    INDEX idx_fecha (fecha),
    INDEX idx_tipo (tipo)
) ENGINE=InnoDB;

-- ============================================
-- Tabla: Logros
-- Define los logros disponibles en el sistema
-- ============================================
CREATE TABLE IF NOT EXISTS Logros (
    id VARCHAR(50) PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    tipo VARCHAR(50) NOT NULL,
    puntosRecompensa INT DEFAULT 0,
    metaRequerida INT DEFAULT 0,
    icono VARCHAR(255) DEFAULT NULL,
    fechaCreacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_tipo (tipo)
) ENGINE=InnoDB;

-- ============================================
-- Tabla: LogrosCompletados
-- Registra los logros completados por los usuarios
-- ============================================
CREATE TABLE IF NOT EXISTS LogrosCompletados (
    usuarioId VARCHAR(50),
    logroId VARCHAR(50),
    fechaCumplimiento TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (usuarioId, logroId),
    FOREIGN KEY (usuarioId) REFERENCES Usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (logroId) REFERENCES Logros(id) ON DELETE CASCADE,
    INDEX idx_fecha (fechaCumplimiento)
) ENGINE=InnoDB;

-- ============================================
-- Tabla: Competencias
-- Define las competencias disponibles
-- ============================================
CREATE TABLE IF NOT EXISTS Competencias (
    id VARCHAR(50) PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    fechaInicio DATE NOT NULL,
    fechaFin DATE NOT NULL,
    tipoDeporte VARCHAR(50) NOT NULL,
    estado VARCHAR(20) DEFAULT 'ACTIVA',
    puntosRecompensa INT DEFAULT 0,
    fechaCreacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_fechas (fechaInicio, fechaFin),
    INDEX idx_estado (estado),
    INDEX idx_tipo (tipoDeporte)
) ENGINE=InnoDB;

-- ============================================
-- Tabla: CompetenciasParticipacion
-- Registra la participación de usuarios en competencias
-- ============================================
CREATE TABLE IF NOT EXISTS CompetenciasParticipacion (
    competenciaId VARCHAR(50),
    usuarioId VARCHAR(50),
    puntuacion DOUBLE DEFAULT 0,
    estado VARCHAR(20) DEFAULT 'INSCRITO',
    fechaInscripcion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (competenciaId, usuarioId),
    FOREIGN KEY (competenciaId) REFERENCES Competencias(id) ON DELETE CASCADE,
    FOREIGN KEY (usuarioId) REFERENCES Usuarios(id) ON DELETE CASCADE,
    INDEX idx_puntuacion (puntuacion)
) ENGINE=InnoDB;

-- ============================================
-- Tabla: Desafios
-- Define los desafíos disponibles
-- ============================================
CREATE TABLE IF NOT EXISTS Desafios (
    id VARCHAR(50) PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    tipoDeporte VARCHAR(50) NOT NULL,
    metaRequerida INT NOT NULL,
    puntosRecompensa INT DEFAULT 0,
    fechaInicio DATE NOT NULL,
    fechaFin DATE NOT NULL,
    estado VARCHAR(20) DEFAULT 'ACTIVO',
    fechaCreacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_fechas (fechaInicio, fechaFin),
    INDEX idx_estado (estado),
    INDEX idx_tipo (tipoDeporte)
) ENGINE=InnoDB;

-- ============================================
-- Tabla: DesafiosParticipacion
-- Registra la participación de usuarios en desafíos
-- ============================================
CREATE TABLE IF NOT EXISTS DesafiosParticipacion (
    desafioId VARCHAR(50),
    usuarioId VARCHAR(50),
    progreso INT DEFAULT 0,
    completado BOOLEAN DEFAULT FALSE,
    fechaInscripcion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fechaCompletado TIMESTAMP NULL,
    PRIMARY KEY (desafioId, usuarioId),
    FOREIGN KEY (desafioId) REFERENCES Desafios(id) ON DELETE CASCADE,
    FOREIGN KEY (usuarioId) REFERENCES Usuarios(id) ON DELETE CASCADE,
    INDEX idx_completado (completado)
) ENGINE=InnoDB;

-- ============================================
-- Datos Iniciales - Usuario Administrador
-- ============================================
INSERT INTO Usuarios (id, nombre, nivel, puntosTotales, experiencia, esAdmin) 
VALUES ('admin001', 'Administrador', 1, 0, 0, TRUE)
ON DUPLICATE KEY UPDATE nombre = 'Administrador';

-- ============================================
-- Datos Iniciales - Logros Básicos
-- ============================================

-- Logros de Actividad Total
INSERT INTO Logros (id, nombre, descripcion, tipo, puntosRecompensa, metaRequerida) VALUES
('logro_act_5', 'Primer Paso', 'Completa 5 actividades deportivas', 'ACTIVIDAD_TOTAL', 50, 5),
('logro_act_10', 'En Movimiento', 'Completa 10 actividades deportivas', 'ACTIVIDAD_TOTAL', 100, 10),
('logro_act_25', 'Deportista Activo', 'Completa 25 actividades deportivas', 'ACTIVIDAD_TOTAL', 250, 25),
('logro_act_50', 'Atleta Dedicado', 'Completa 50 actividades deportivas', 'ACTIVIDAD_TOTAL', 500, 50),
('logro_act_100', 'Leyenda del Deporte', 'Completa 100 actividades deportivas', 'ACTIVIDAD_TOTAL', 1000, 100)
ON DUPLICATE KEY UPDATE nombre = VALUES(nombre);

-- Logros de Calorías
INSERT INTO Logros (id, nombre, descripcion, tipo, puntosRecompensa, metaRequerida) VALUES
('logro_cal_1000', 'Quemador Novato', 'Quema 1000 calorías totales', 'CALORIAS', 100, 1000),
('logro_cal_5000', 'Quemador Experto', 'Quema 5000 calorías totales', 'CALORIAS', 300, 5000),
('logro_cal_10000', 'Incinerador', 'Quema 10000 calorías totales', 'CALORIAS', 600, 10000),
('logro_cal_25000', 'Horno Humano', 'Quema 25000 calorías totales', 'CALORIAS', 1500, 25000)
ON DUPLICATE KEY UPDATE nombre = VALUES(nombre);

-- Logros de Distancia (Correr/Ciclismo)
INSERT INTO Logros (id, nombre, descripcion, tipo, puntosRecompensa, metaRequerida) VALUES
('logro_dist_10', 'Primeros 10K', 'Recorre 10 kilómetros totales', 'DISTANCIA', 100, 10),
('logro_dist_50', 'Media Centena', 'Recorre 50 kilómetros totales', 'DISTANCIA', 250, 50),
('logro_dist_100', 'Centenario', 'Recorre 100 kilómetros totales', 'DISTANCIA', 500, 100),
('logro_dist_250', 'Maratonista', 'Recorre 250 kilómetros totales', 'DISTANCIA', 1200, 250),
('logro_dist_500', 'Ultramaratonista', 'Recorre 500 kilómetros totales', 'DISTANCIA', 2500, 500)
ON DUPLICATE KEY UPDATE nombre = VALUES(nombre);

-- Logros de Racha
INSERT INTO Logros (id, nombre, descripcion, tipo, puntosRecompensa, metaRequerida) VALUES
('logro_racha_3', 'Racha Inicial', 'Entrena 3 días seguidos', 'RACHA_DIAS', 150, 3),
('logro_racha_7', 'Semana Perfecta', 'Entrena 7 días seguidos', 'RACHA_DIAS', 300, 7),
('logro_racha_14', 'Dos Semanas', 'Entrena 14 días seguidos', 'RACHA_DIAS', 700, 14),
('logro_racha_30', 'Mes Completo', 'Entrena 30 días seguidos', 'RACHA_DIAS', 1500, 30)
ON DUPLICATE KEY UPDATE nombre = VALUES(nombre);

-- Logros por Deporte Específico
INSERT INTO Logros (id, nombre, descripcion, tipo, puntosRecompensa, metaRequerida) VALUES
('logro_correr_10', 'Corredor Novato', 'Completa 10 sesiones de carrera', 'CORRER', 200, 10),
('logro_natacion_10', 'Nadador Novato', 'Completa 10 sesiones de natación', 'NATACION', 200, 10),
('logro_ciclismo_10', 'Ciclista Novato', 'Completa 10 sesiones de ciclismo', 'CICLISMO', 200, 10),
('logro_futbol_10', 'Futbolista Novato', 'Completa 10 sesiones de fútbol', 'FUTBOL', 200, 10),
('logro_baloncesto_10', 'Basketbolista Novato', 'Completa 10 sesiones de baloncesto', 'BALONCESTO', 200, 10)
ON DUPLICATE KEY UPDATE nombre = VALUES(nombre);

-- ============================================
-- Vistas útiles
-- ============================================

-- Vista de estadísticas de usuarios
CREATE OR REPLACE VIEW VistaEstadisticasUsuarios AS
SELECT 
    u.id,
    u.nombre,
    u.nivel,
    u.puntosTotales,
    u.experiencia,
    COUNT(DISTINCT a.id) as totalActividades,
    COALESCE(SUM(a.caloriasQuemadas), 0) as totalCalorias,
    COALESCE(SUM(a.distanciaKm), 0) as totalDistancia,
    COUNT(DISTINCT lc.logroId) as logrosCompletados
FROM Usuarios u
LEFT JOIN Actividades a ON u.id = a.usuarioId
LEFT JOIN LogrosCompletados lc ON u.id = lc.usuarioId
GROUP BY u.id, u.nombre, u.nivel, u.puntosTotales, u.experiencia;

-- Vista de clasificación en competencias
CREATE OR REPLACE VIEW VistaClasificacionCompetencias AS
SELECT 
    c.id as competenciaId,
    c.nombre as competenciaNombre,
    u.id as usuarioId,
    u.nombre as usuarioNombre,
    cp.puntuacion,
    cp.estado,
    RANK() OVER (PARTITION BY c.id ORDER BY cp.puntuacion DESC) as posicion
FROM Competencias c
INNER JOIN CompetenciasParticipacion cp ON c.id = cp.competenciaId
INNER JOIN Usuarios u ON cp.usuarioId = u.id
WHERE c.estado = 'ACTIVA';

-- ============================================
-- Procedimientos almacenados útiles
-- ============================================

DELIMITER //

-- Procedimiento para actualizar nivel de usuario
CREATE PROCEDURE ActualizarNivelUsuario(IN p_usuarioId VARCHAR(50))
BEGIN
    DECLARE v_experiencia INT;
    DECLARE v_nuevoNivel INT;
    
    SELECT experiencia INTO v_experiencia FROM Usuarios WHERE id = p_usuarioId;
    
    -- Fórmula simple: nivel = experiencia / 1000 + 1
    SET v_nuevoNivel = FLOOR(v_experiencia / 1000) + 1;
    
    UPDATE Usuarios SET nivel = v_nuevoNivel WHERE id = p_usuarioId;
END //

-- Procedimiento para obtener logros próximos a completar
CREATE PROCEDURE LogrosProximosCompletar(IN p_usuarioId VARCHAR(50))
BEGIN
    -- Esta consulta debe implementarse según la lógica específica de cada tipo de logro
    SELECT 
        l.id,
        l.nombre,
        l.descripcion,
        l.tipo,
        l.metaRequerida,
        l.puntosRecompensa
    FROM Logros l
    WHERE NOT EXISTS (
        SELECT 1 FROM LogrosCompletados lc 
        WHERE lc.logroId = l.id AND lc.usuarioId = p_usuarioId
    )
    ORDER BY l.puntosRecompensa ASC
    LIMIT 5;
END //

DELIMITER ;

-- ============================================
-- Fin del script
-- ============================================

-- Mensaje de confirmación
SELECT 'Base de datos creada exitosamente' as mensaje;
