CREATE DATABASE votaciones_cne;

USE votaciones_cne;

# CREATE 

CREATE TABLE palabras_clave(
	codigo_palabra INT PRIMARY KEY,
	palabra VARCHAR(20)
);

CREATE TABLE votantes(
	cedula_votante VARCHAR(12) PRIMARY KEY, /* --- */
    nombres_votante VARCHAR(30),
    apellidos_votante VARCHAR(30),
    estado_votacion INT DEFAULT 0,
    codigo_cne VARCHAR(20) DEFAULT "", # Generar
	palabra_clave INT DEFAULT -1 #Generar    
);

CREATE TABLE partidos_politicos(
	codigo_partido INT PRIMARY KEY, /* --- */
    nombre_partido VARCHAR(50)
);

CREATE TABLE candidatos_presidente(
	cedula_candidato_presidente VARCHAR(12) PRIMARY KEY, /* --- */
	codigo_candidato_presidente INT DEFAULT 0, #Generar
    nombres_candidatos_presidente VARCHAR(30),
    apellidos_candidatos_presidente VARCHAR(30),    
	codigo_partido INT,
	FOREIGN KEY (codigo_partido) REFERENCES partidos_politicos(codigo_partido) ON DELETE CASCADE
);

CREATE TABLE votos(
	id_voto INT AUTO_INCREMENT PRIMARY KEY, /*---*/
    nro_candidato_presidente INT DEFAULT -1,
    valor_encriptacion VARCHAR(50)
);

# INSERT 

INSERT INTO palabras_clave(codigo_palabra) VALUES 
	(1),
    (2),
    (3),
    (4),
    (5),
    (6),
    (7),
    (8),
    (9),
    (10),
    (11),
    (12),
    (13),
    (14),
    (15);

INSERT INTO votantes(cedula_votante, nombres_votante, apellidos_votante) VALUES
	("070", "Pablo Dario", "Hernadez Soto"),
    ("071", "Angel David", "Estacio Perez"),
    ("072", "Doris Boris", "Anton Soria"),
    ("073", "Carla", "Estacio Perez"),
    ("074", "Angel David", "Totoy"),
    ("075", "Romel", "Estacio Perez");
    
INSERT INTO partidos_politicos(codigo_partido, nombre_partido) VALUES
	(1, "PSC"),
    (3, "Sociedad"),
    (5, "Adelante"),
    (35, "Vamos");

INSERT INTO candidatos_presidente(cedula_candidato_presidente, nombres_candidatos_presidente,
	apellidos_candidatos_presidente, codigo_partido) VALUES
	("170", "Azad Dario", "Macas Soto", 5),
    ("171", "Marcos David", "Toro Perez", 35),
    ("172", "Hernan Boris", "Perazo Soria", 35),
    ("173", "Rafael Pedro", "Perazo Soria", 5),
    ("174", "Hernan Dustin", "Mariduena Solis", 3);


