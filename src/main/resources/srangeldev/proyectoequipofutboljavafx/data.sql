DELETE
FROM Personal;
DELETE
FROM Entrenadores;
DELETE
FROM Jugadores;

-- Primero insertamos en la tabla base Personal
INSERT INTO Personal (id, nombre, apellidos, fecha_nacimiento,
                      fecha_incorporacion, salario, pais_origen, tipo)
VALUES (1,
        'Pep',
        'Guardiola',
        '1971-01-18',
        '2020-07-01',
        1500000.0,
        'España',
        'ENTRENADOR');

-- Luego en la tabla específica Entrenadores
INSERT INTO Entrenadores (id, especializacion)
VALUES (1, 'ENTRENADOR_PRINCIPAL');

-- Tabla base
INSERT INTO Personal (
    id, nombre, apellidos, fecha_nacimiento,
    fecha_incorporacion, salario, pais_origen, tipo
) VALUES (
             2,
             'Lionel',
             'Messi',
             '1987-06-24',
             '2021-08-10',
             30000000.0,
             'Argentina',
             'JUGADOR'
         );

-- Tabla específica Jugadores
INSERT INTO Jugadores (
    id, posicion, dorsal, altura, peso, goles, partidos_jugados
) VALUES (
             2,
             'DELANTERO',
             30,
             1.70,
             72.0,
             805,
             1034
         );