package Base_datos;

import Conexion.Conectar;
import Interfaz.Metodos_db;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Conexion_db {
    
        private Connection conexion;
        private CallableStatement statement;
        private ResultSet resultado;
        private final String usuario;
        private final String contrasena;
        private final char[] contrasena_char;
        
        public Conexion_db(String usuario_c, char[] contrasena_c){
            this.usuario = usuario_c;
            this.contrasena = concantenar_contrasena(contrasena_c);  
            this.contrasena_char = contrasena_c;
        }
        
        public Conexion_db(String usuario_c, String contrasena_c){
            this.usuario = usuario_c;
            this.contrasena = contrasena_c;
            this.contrasena_char = null;
        }        
        
        private String concantenar_contrasena(char[] clave_m){
            String clave = "";
            for (int i = 0; i < clave_m.length; i++) {
                clave += clave_m[i];
            }
            return clave;
        }        
        
        // Comprueba que existe el usuario en la 2da ventana
        public boolean conexion_interfaz(){
            boolean validar = false;
            try {
                this.conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/votaciones_cne?serverTimezone=UTC",
                        this.usuario, this.contrasena);
                if (this.conexion != null){
                    Conectar thread = new Conectar(this.usuario, this.contrasena);
                    if ( thread.get_estado_server() ){
                        // thread.setDaemon(true); //MANTENIENDO ESTO LA VENTANA NO SE CERRARIA PORQUE HAY UN WHILE
                        thread.start();
                        validar = true;
                    }
                }
                anular_puentes();
            } catch (SQLException ex) {
                return validar;
            }
            return validar;
        }
        
        // Comprueba que existe el usuario en la 1ra ventana
        public boolean comprobar_conexion_interfaz_previo(){
            boolean validar = false;
            try {
                this.conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/votaciones_cne?serverTimezone=UTC",
                        this.usuario, this.contrasena);
                if (this.conexion != null){
                    validar = true;
                    // Inicia la ventana inicio de votaciones
                    Metodos_db metodos = new Metodos_db(this.usuario, this.contrasena_char);
                    metodos.setVisible(true);
                }
                anular_puentes();
            } catch (SQLException ex) {
                return validar;
            }
            return validar;
        }        
        
        // Activa los enlaces para la conexion de base de datos
        private void iniciar_conexion(){
            try {
                this.conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/votaciones_cne?serverTimezone=UTC",
                        this.usuario, this.contrasena); 
            } catch (SQLException ex) {
                anular_puentes();
            }               
        }        
        
        // Anula las variables locales
        private void anular_puentes(){
            this.conexion = null;
            this.resultado = null;
        }    

        // Verifica la existencia del votante y devuelve los nombres/apellidos
        public String[] leer_cedula_votante(String cedula){
            // Nombres y apellidos en string
            String[] datos = { "", "" };
            
            iniciar_conexion();

            if (this.conexion != null){

                try{
                    
                    this.statement = conexion.prepareCall( "{call consultar_cedula_votante(?)}" );
                    // Para retornar un resultSet
                    this.statement.setString(1, cedula);
                    this.resultado = this.statement.executeQuery();
                    
                    while ( this.resultado.next() ) {
                        if( this.resultado.getString("cedula").equals(cedula) ){
                            
                            datos[0] = this.resultado.getString("nombres");
                            datos[1] = this.resultado.getString("apellidos");
                            
                        }
                    }                  

                }catch(SQLException ex){}

            }

            anular_puentes();

            return datos;
        }        
        
        // Valida los datos que permiten votar al usuario
        public int validar_datos_votante(String cedula, String clave, String codigo_cne){
            // Nombres y apellidos en string
            int validar = 0;
            
            iniciar_conexion();

            if (this.conexion != null){

                try{
                    
                    this.statement = conexion.prepareCall( "{call consultar_datos_votante(?, ?, ?)}" );
                    // Para retornar un resultSet
                    this.statement.setString(1, cedula);
                    this.statement.setString(2, clave);
                    this.statement.setString(3, codigo_cne);
                    this.resultado = this.statement.executeQuery();
                    
                    while ( this.resultado.next() ) {
                        if( this.resultado.getString("cedula").equals(cedula) && 
                                this.resultado.getString("palabra_p").equals(clave) && 
                                this.resultado.getString("codigo").equals(codigo_cne) ){
                            validar = 1;
                        }
                    }     
                    
                }catch(SQLException ex){}

            }

            anular_puentes();

            return validar;
        }  
        
        // Entrega los candidatos, es ejecutado en conjunto con validar_datos_votante
        public ArrayList obtener_datos_candidatos(){
            // Nombres y apellidos en string
            
            ArrayList lista_candidatos = new ArrayList<>();
            
            iniciar_conexion();

            if (this.conexion != null){

                try{
                    
                    this.statement = conexion.prepareCall( "{call consultar_datos_candidatos()}" );
                    // Para retornar un resultSet
                    this.resultado = this.statement.executeQuery();
                    
                    while ( this.resultado.next() ) {
                        
                        ArrayList datos = new ArrayList<>();
                        int codigo = this.resultado.getInt("codigo");
                        datos.add( Integer.toString(codigo) );
                        datos.add(this.resultado.getString("nombres"));
                        datos.add(this.resultado.getString("apellidos"));
                        datos.add(this.resultado.getString("partido"));
                        lista_candidatos.add(datos);
                        
                    } 
                    
                }catch(SQLException ex){}

            }

            anular_puentes();

            return lista_candidatos;
        }         
        
        //  Inserta en la db el voto con su validacion
        public int validar_votacion(String cedula, String codigo_candidato){
            
            int validar = 0;
                        
            iniciar_conexion();

            if (this.conexion != null){

                try{
                    
                    this.statement = conexion.prepareCall( "{call realizar_voto(?, ?)}" );
                    
                    this.statement.setString(1, cedula);
                    this.statement.setInt(2, Integer.parseInt( codigo_candidato ));
                    this.statement.execute();
                    
                    validar = 1;
                    
                }catch(SQLException ex){}

            }

            anular_puentes();
            
            
            return validar;
        }

}
// '072', 'Doris Boris', 'Anton Soria', '0', 'yMvXX9tWL!j3', '6'

