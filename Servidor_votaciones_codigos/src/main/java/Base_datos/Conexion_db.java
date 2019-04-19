
package Base_datos;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

public class Conexion_db {
    
    private Connection conexion;
    private ResultSet resultado;
    private CallableStatement statement;
    private final String usuario;
    private final String contrasena;
    private final ArrayList<String> cedulas_votantes;
    private final ArrayList<String> cedulas_candidatos_presidentes;

    // Constructor
    public Conexion_db(String usuario_c, char[] contrasena_c){
        this.usuario = usuario_c;
        this.contrasena = concantenar_contrasena(contrasena_c);            
        this.cedulas_votantes = new ArrayList<>();
        this.cedulas_candidatos_presidentes = new ArrayList<>();
    }             

    // Activa los enlaces para la conexion de base de datos
    private void iniciar_conexion(){
        try {
            this.conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/votaciones_cne?serverTimezone=UTC",
                    this.usuario, this.contrasena); 
        } catch (SQLException ex) {
            System.out.println("No inicio correcto");
            anular_puentes();
        }               
    }        

    // Anula las variables locales
    private void anular_puentes(){
        this.conexion = null;
        this.resultado = null;
        this.statement = null;
    }

    // Comprueba que existe el usuario en la 1ra ventana
    public boolean comprobar_conexion_interfaz_previo(){
        boolean validar = false;

        try {
            this.conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/votaciones_cne?serverTimezone=UTC",
                    this.usuario, this.contrasena);
            if (this.conexion != null){
                validar = true;
            }

            anular_puentes();

        } catch (SQLException ex) {
            anular_puentes();
            return validar;
        }
        return validar;
    }     
    
    // Genera las palabras claves de los votantes y candidatos
    public boolean generar_palabras_clave(){

        boolean validar = false;

        if( leer_cedulas_votantes() ){
        
            iniciar_conexion();

            if (this.conexion != null){

                ArrayList<String> palabras = palabras_lista();
                
                if ( !palabras.isEmpty() ){
                    
                    // El metodo palabras_lista anulo la conexion al finalizar, entonces inicio otra vez
                    iniciar_conexion();

                    for (int i = 1; i < this.cedulas_votantes.size() + 1; i++) {

                        try{

                            Random rand = new Random(); 
                            int num = rand.nextInt(palabras.size()) + 1; 

                            this.statement = conexion.prepareCall( "{call asignar_codigo_palabra(?, ?)}" );
                            this.statement.setString(1, this.cedulas_votantes.get(i - 1));
                            this.statement.setInt(2, num);
                            //Ejecutar el query unicamente
                            this.statement.execute();       

                            validar = true;

                        }catch(SQLException ex){
                            anular_puentes();
                        }
                    }
                }
            }  
        }                   

        anular_puentes();

        return validar;
    }

    // Crea lista de cedulas de votantes
    public boolean leer_cedulas_votantes(){

        boolean validar = false;

        iniciar_conexion();

        if (this.conexion != null){

            try{      
                this.statement = conexion.prepareCall( "{call consultar_cedulas_votantes()}" );
                // Para retornar un resultSet
                this.resultado = this.statement.executeQuery();
                while ( this.resultado.next() ) {
                    this.cedulas_votantes.add( this.resultado.getString("cedula") );
                }                  
                    
                validar = true;

            }catch(SQLException ex){
                anular_puentes();
            }

        }

        anular_puentes();

        return validar;
    }        

    // Genera los codigos de los candidatos a presidentes
    public boolean generar_codigos_cne(){

        boolean validar = false;

        iniciar_conexion();

        if (this.conexion != null){

            for (int i = 0; i < this.cedulas_votantes.size(); i++) {

                try{

                    this.statement = conexion.prepareCall( "{call asignar_codigo_cne(?, ?)}" );
                    this.statement.setString(1, this.cedulas_votantes.get(i));
                    this.statement.setString(2, generar_codigo());
                    this.statement.execute();       

                }catch(SQLException ex){
                    System.out.println("generar codigos");
                    anular_puentes();
                }

            }

            validar = true;

        }            

        anular_puentes();

        return validar;
    }   

    // Crea lista de cedulas de candidatos a presidentes
    public boolean leer_cedulas_candidatos_p(){

        boolean validar = false;

        iniciar_conexion();

        if (this.conexion != null){

            try{      
                this.statement = conexion.prepareCall( "{call cedulas_candidatos_presidentes()}" );
                // Para retornar un resultSet
                this.resultado = this.statement.executeQuery();
                while ( this.resultado.next() ) {
                    this.cedulas_candidatos_presidentes.add( this.resultado.getString("cedula") );
                }                  

                validar = true;

            }catch(SQLException ex){
                anular_puentes();
            }

        }

        anular_puentes();

        return validar;
    }        

    // Genera los codigos de los candidatos a presidentes
    public boolean generar_codigos_candidatos(){

        boolean validar = false;

        if( leer_cedulas_candidatos_p() ){

            iniciar_conexion();
            
            if (this.conexion != null){
                
                ArrayList<Integer> codigos_usados = new ArrayList<>();

                for (int i = 0; i < this.cedulas_candidatos_presidentes.size(); i++) {

                    try{
                        // VALES ARTA BROTHER 
                        
                        Random rand = new Random(); 
                        int num = rand.nextInt( this.cedulas_candidatos_presidentes.size() ) + 1; 
                        
                        if ( codigos_usados.contains(num) ){
                            i--;
                            continue;
                        }

                        this.statement = conexion.prepareCall( "{call asignar_codigo_candidato(?, ?)}" );
                        this.statement.setString(1, this.cedulas_candidatos_presidentes.get(i));
                        this.statement.setInt(2, num);
                        this.statement.execute();       
                        
                        codigos_usados.add(num);

                    }catch(SQLException ex){
                        System.out.println("generar codigos");
                        anular_puentes();
                    }

                }

                validar = true;

            }            
        }
        anular_puentes();

        return validar;
    }       
    
    // Genera lista para palabras clave
    private ArrayList<String> palabras_lista(){

        ArrayList<String> lista = new ArrayList<>();

        lista.add("alfa");
        lista.add("vita");
        lista.add("gama");
        lista.add("delta");
        lista.add("epsilon");
        lista.add("zita");
        lista.add("ita");
        lista.add("thita");
        lista.add("iota");
        lista.add("kapa");
        lista.add("lambda");
        lista.add("mi");
        lista.add("ni");
        lista.add("xi");
        lista.add("omicron");
        
        iniciar_conexion();

        if (this.conexion != null){

            for (int i = 1; i < lista.size() + 1; i++) {

                try{

                    this.statement = conexion.prepareCall( "{call crear_palabra(?, ?)}" );
                    this.statement.setString(1, lista.get(i - 1));
                    this.statement.setInt(2, i);
                    this.statement.execute();       

                }catch(SQLException ex){
                    anular_puentes();
                }

            }
        }  

        anular_puentes();

        return lista;

    }    

    // Concatena la contraseña
    private String concantenar_contrasena(char[] clave_m){
        String clave = "";
        for (int i = 0; i < clave_m.length; i++) {
            clave += clave_m[i];
        }
        return clave;
    }      
    
    // Genera un codigo alfanumerico de 12 caracteres
    private String generar_codigo(){
        
        String codigo = "";
        String alfanumerico = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789.-_?!+-";
        String[] alfanumerico_split = alfanumerico.split("");
        
        for (int i = 0; i < 12; i++) {
            
            Random rand = new Random(); 
            int num = rand.nextInt( alfanumerico_split.length ); 
            codigo += alfanumerico_split[num];
            
        }
        
        return codigo;
    }
    
}
