-- Tabla base para la herencia
CREATE TABLE IF NOT EXISTS Personal (
    id INTEGER PRIMARY KEY,
    nombre TEXT NOT NULL,
    apellidos TEXT NOT NULL,
    fecha_nacimiento DATE NOT NULL,
    fecha_incorporacion DATE NOT NULL,
    salario REAL NOT NULL,
    pais_origen TEXT NOT NULL,
    tipo TEXT NOT NULL CHECK (tipo IN ('ENTRENADOR', 'JUGADOR')),
    imagen_url TEXT DEFAULT '',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Tabla para Entrenadores (hereda de Personal)
CREATE TABLE IF NOT EXISTS Entrenadores (
    id INTEGER PRIMARY KEY,
    especializacion TEXT NOT NULL CHECK (
        especializacion IN ('ENTRENADOR_PRINCIPAL', 'ENTRENADOR_ASISTENTE', 'ENTRENADOR_PORTEROS')
    ),
    FOREIGN KEY (id) REFERENCES Personal(id) ON DELETE CASCADE
);

-- Tabla para Jugadores (hereda de Personal)
CREATE TABLE IF NOT EXISTS Jugadores (
    id INTEGER PRIMARY KEY,
    posicion TEXT NOT NULL CHECK (
        posicion IN ('PORTERO', 'DEFENSA', 'CENTROCAMPISTA', 'DELANTERO')
    ),
    dorsal INTEGER NOT NULL,
    altura REAL NOT NULL,
    peso REAL NOT NULL,
    goles INTEGER NOT NULL DEFAULT 0,
    partidos_jugados INTEGER NOT NULL DEFAULT 0,
    FOREIGN KEY (id) REFERENCES Personal(id) ON DELETE CASCADE
);

-- Tabla para Equipos
CREATE TABLE IF NOT EXISTS Equipos (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL,
    fecha_fundacion DATE NOT NULL,
    escudo_url TEXT DEFAULT '',
    ciudad TEXT NOT NULL,
    estadio TEXT NOT NULL,
    pais TEXT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Tabla para Convocatorias
CREATE TABLE IF NOT EXISTS Convocatorias (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    fecha DATE NOT NULL,
    descripcion TEXT NOT NULL,
    equipo_id INTEGER NOT NULL,
    entrenador_id INTEGER NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (equipo_id) REFERENCES Equipos(id) ON DELETE CASCADE,
    FOREIGN KEY (entrenador_id) REFERENCES Personal(id) ON DELETE CASCADE
);

-- Tabla para JugadoresConvocados
CREATE TABLE IF NOT EXISTS JugadoresConvocados (
    convocatoria_id INTEGER NOT NULL,
    jugador_id INTEGER NOT NULL,
    es_titular INTEGER NOT NULL DEFAULT 0,
    PRIMARY KEY (convocatoria_id, jugador_id),
    FOREIGN KEY (convocatoria_id) REFERENCES Convocatorias(id) ON DELETE CASCADE,
    FOREIGN KEY (jugador_id) REFERENCES Personal(id) ON DELETE CASCADE
);

-- Tabla para Usuarios
CREATE TABLE IF NOT EXISTS Usuarios (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT NOT NULL UNIQUE,
    password TEXT NOT NULL,
    role TEXT NOT NULL,
    created_at TEXT NOT NULL,
    updated_at TEXT NOT NULL
);
