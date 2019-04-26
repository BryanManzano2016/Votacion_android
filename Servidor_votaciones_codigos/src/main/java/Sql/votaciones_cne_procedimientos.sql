# 1er servidor ------------------------------------------------------------------------------------------------------

# Consulta las cedulas de los votantes

DELIMITER $$
	CREATE PROCEDURE consultar_cedulas_votantes()
		BEGIN
			SELECT vot.cedula_votante AS cedula
			FROM votantes vot
			WHERE vot.estado_votacion = 0;
		END$$			
DELIMITER ;

# Consulta las cedulas de los candidatos a presidentes

DELIMITER $$
	CREATE PROCEDURE cedulas_candidatos_presidentes()
		BEGIN
			SELECT can.cedula_candidato_presidente AS cedula
			FROM candidatos_presidente can
			WHERE can.codigo_candidato_presidente = 0;
		END$$
DELIMITER ;

# Crear palabras clave

DELIMITER $$
	CREATE PROCEDURE crear_palabra(IN palabra_p VARCHAR(20), IN codigo_palabra_p INT)
		BEGIN
			UPDATE palabras_clave pal SET pal.palabra = palabra_p WHERE pal.codigo_palabra = codigo_palabra_p;			
        END$$
DELIMITER ;

# Asignar palabra clave

DELIMITER $$
	CREATE PROCEDURE asignar_codigo_palabra(IN cedula_votante_p VARCHAR(12), IN codigo_palabra_p INT)
		BEGIN
            UPDATE votantes vot
				SET vot.palabra_clave = codigo_palabra_p
				WHERE vot.cedula_votante = cedula_votante_p AND vot.estado_votacion = 0;
		END$$
DELIMITER ;

# Asignar codigos cne

DELIMITER $$
	CREATE PROCEDURE asignar_codigo_cne(IN cedula_votante_p VARCHAR(12), IN codigo_cne_p VARCHAR(20))
		BEGIN
            UPDATE votantes vot
				SET vot.codigo_cne = codigo_cne_p
				WHERE vot.cedula_votante = cedula_votante_p AND vot.estado_votacion = 0;
		END$$
DELIMITER ;

# Asignar codigo a candidato a presidente

DELIMITER $$
	CREATE PROCEDURE asignar_codigo_candidato(IN cedula_candidato_p VARCHAR(12), IN codigo_candidato_p INT)
		BEGIN
            UPDATE candidatos_presidente cand
				SET cand.codigo_candidato_presidente = codigo_candidato_p
				WHERE cand.cedula_candidato_presidente = cedula_candidato_p AND cand.codigo_candidato_presidente = 0;
		END$$
DELIMITER ;

# --------------------------------------------------------------------------------------------------------


# Ingresa un numero de cedula y devuelve un resultSet (ver posibilidad de devolver el nro 1

DELIMITER $$
	CREATE PROCEDURE consultar_cedula_votante(IN cedula_votante_p VARCHAR(12))
		BEGIN
			SELECT vot.cedula_votante AS cedula, vot.nombres_votante AS nombres, vot.apellidos_votante AS apellidos,
            vot.estado_votacion AS estado
				FROM votantes vot
				WHERE vot.cedula_votante = cedula_votante_p
                LIMIT 1;                
		END$$
DELIMITER ;  
 
# Ingresa un numero de cedula, codigo y keyWord, entonces devuelve un resultSet (ver posibilidad de devolver el nro 1)

DELIMITER $$
	CREATE PROCEDURE consultar_datos_votante(IN cedula_votante_p VARCHAR(12), IN palabra_clave_p VARCHAR(20), 
		IN codigo_cne_p VARCHAR(20))
		BEGIN
			SELECT vot.cedula_votante AS cedula, pal.palabra AS palabra_p, vot.codigo_cne AS codigo
				FROM votantes vot, palabras_clave pal
				WHERE vot.estado_votacion = 0 AND vot.cedula_votante = cedula_votante_p
					AND vot.codigo_cne = codigo_cne_p AND pal.codigo_palabra = vot.palabra_clave
				LIMIT 1;
		END$$
DELIMITER ;

# Consultar candidatos

DELIMITER $$
	CREATE PROCEDURE consultar_datos_candidatos()
		BEGIN			
			SELECT can.codigo_candidato_presidente AS codigo, can.nombres_candidatos_presidente AS nombres, 
				can.apellidos_candidatos_presidente AS apellidos, par.nombre_partido AS partido
				FROM candidatos_presidente can, partidos_politicos par
				WHERE can.codigo_partido = par.codigo_partido;		
		END$$
DELIMITER ;

# Inserta un voto

DELIMITER $$
	CREATE PROCEDURE realizar_voto(IN cedula_votante_p VARCHAR(12), IN nro_candidato_presidente_p INT)
		BEGIN
			INSERT INTO votos(nro_candidato_presidente) VALUES 
				(nro_candidato_presidente_p);
            UPDATE votantes vot
				SET vot.estado_votacion = 1
				WHERE vot.cedula_votante = cedula_votante_p AND vot.estado_votacion = 0;
		END$$
DELIMITER ;

# Contar votos					

DELIMITER $$
	CREATE PROCEDURE obtener_votacion()
		BEGIN			
			SELECT can.nombres_candidatos_presidente AS nombres, can.apellidos_candidatos_presidente AS apellidos,
				nro AS numero, can.codigo_candidato_presidente AS codigo
				FROM votos_candidato vot, candidatos_presidente can
				WHERE vot.codigo = can.codigo_candidato_presidente;
		END$$
DELIMITER ;



