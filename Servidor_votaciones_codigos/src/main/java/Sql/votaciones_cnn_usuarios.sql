CREATE USER 'alcon'@'localhost' IDENTIFIED BY 'alcon1';
GRANT SELECT ON votaciones_cne.votantes TO 'alcon'@'localhost';
GRANT UPDATE ON votaciones_cne.votantes TO 'alcon'@'localhost';
GRANT SELECT ON votaciones_cne.candidatos_presidente TO 'alcon'@'localhost';
GRANT UPDATE ON votaciones_cne.candidatos_presidente TO 'alcon'@'localhost';
GRANT SELECT ON votaciones_cne.partidos_politicos TO 'alcon'@'localhost';
GRANT INSERT ON votaciones_cne.votos TO 'alcon'@'localhost';

CREATE USER 'aguila'@'localhost' IDENTIFIED BY 'aguila1';
GRANT SELECT ON votaciones_cne.votantes TO 'aguila'@'localhost';
GRANT UPDATE ON votaciones_cne.votantes TO 'aguila'@'localhost';
GRANT SELECT ON votaciones_cne.candidatos_presidente TO 'aguila'@'localhost';
GRANT UPDATE ON votaciones_cne.candidatos_presidente TO 'aguila'@'localhost';
GRANT SELECT ON votaciones_cne.partidos_politicos TO 'aguila'@'localhost';
GRANT INSERT ON votaciones_cne.votos TO 'aguila'@'localhost';

CREATE USER 'panda'@'localhost' IDENTIFIED BY 'panda1';
GRANT UPDATE ON votaciones_cne.votantes TO 'panda'@'localhost';
GRANT INSERT ON votaciones_cne.palabras_clave TO 'panda'@'localhost';