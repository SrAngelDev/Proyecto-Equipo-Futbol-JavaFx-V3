DELETE FROM Personal;
DELETE FROM Entrenadores;
DELETE FROM Jugadores;
DELETE FROM Usuarios;

-- Insert default users with hashed passwords
-- admin password: admin (hashed with SHA-256 and Base64 encoded)
-- user password: user (hashed with SHA-256 and Base64 encoded)
INSERT INTO Usuarios (username, password, role, created_at, updated_at)
VALUES ('admin', 'jGl25bVBBBW96Qi9Te4V37Fnqchz/Eu4qB9vKrRIqRg=', 'ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO Usuarios (username, password, role, created_at, updated_at)
VALUES ('user', 'BPiZbadjt6lpsQKO4wB1aerzpjVIbdqyEdUSyFud+Ps=', 'USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
