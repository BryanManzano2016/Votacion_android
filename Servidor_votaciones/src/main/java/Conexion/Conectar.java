
package Conexion;

import Base_datos.Conexion_db;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

public class Conectar extends Thread{
    
    private String usuario;
    private String contrasena;
    private Conexion_db conector_db;
    private ServerSocket serverSocket = null;
    private final int port = 60000;                       
    private boolean estado_server = false;

    public Conectar(String usuario_c, String contrasena_c)
    {
        try{
            this.serverSocket = new ServerSocket(port);
            this.estado_server = true;
            
            this.usuario = usuario_c;
            this.contrasena = contrasena_c;
        }catch(IOException e){}
    }
    
    private void iniciar_conexion_db() { this.conector_db = new Conexion_db(this.usuario, this.contrasena); }
    
    private void fin_conexion_db() { this.conector_db = null; }
    
    @Override
    public void run() { iniciar(); }
    
    private void iniciar()
    {        
        while(true) 
        {
            try(Socket server = serverSocket.accept()) {
                
                ArrayList lista_candidatos;                
                int validar = 0;
                        
                // Lectura
                DataInputStream input = new DataInputStream( server.getInputStream() );
                JSONObject objecto_recibir = new JSONObject( input.readUTF() );
                
                // Escritura
                JSONObject objecto_enviar = new JSONObject();

                iniciar_conexion_db();
                
                System.out.println(objecto_recibir.getString("peticion"));
                
                switch ( objecto_recibir.getString("peticion") ) {
                    
                    case "verificar_cedula_votante":
                        String[] datos = this.conector_db.leer_cedula_votante( objecto_recibir.getString("cedula") );
                        if( !datos[0].equals("") && !datos[1].equals("") ){
                            objecto_enviar.put("nombres", datos[0]);
                            objecto_enviar.put("apellidos", datos[1]);
                            validar = 1;
                        } 
                        break;
                        
                    case "verificar_datos_votante":
                        String cedula = objecto_recibir.getString("cedula");
                        String palabra_clave = objecto_recibir.getString("palabra_clave");
                        String codigo = objecto_recibir.getString("codigo");
                        validar = this.conector_db.validar_datos_votante( cedula, palabra_clave, codigo);
                        break;
                        
                    case "obtener_datos_candidatos":                        
                        JSONArray datos_candidatos = new JSONArray();
                        
                        String cedula_3 = objecto_recibir.getString("cedula");
                        String palabra_clave_3 = objecto_recibir.getString("palabra_clave");
                        String codigo_3 = objecto_recibir.getString("codigo");
                        
                        // Valido que el individuo este en la db
                        validar = this.conector_db.validar_datos_votante( cedula_3, palabra_clave_3, codigo_3);
                        // Si es positivo accedo a la db y obtengo los datos de los candidatos
                        if ( validar == 1 ){
                            lista_candidatos = this.conector_db.obtener_datos_candidatos();
                            for (int i = 0; i < lista_candidatos.size(); i++) {
                                JSONArray candidato = new JSONArray();
                                candidato.put( lista_candidatos.get(i) );
                                // Agrego un jsonArray dentro del otro jsonArray mas general
                                datos_candidatos.put(candidato);
                            }
                            // Al objeto que se envia se añade el jsonArray
                            objecto_enviar.put("candidatos", lista_candidatos);
                        }
                        break;
                        
                    case "realizar_votacion_cne":
                        String cedula_4 = objecto_recibir.getString("cedula");
                        String palabra_clave_4 = objecto_recibir.getString("palabra_clave");
                        String codigo_4 = objecto_recibir.getString("codigo");
                        String codigo_candidato_4 = objecto_recibir.getString("codigo_candidato");
                        
                        validar = this.conector_db.validar_datos_votante( cedula_4, palabra_clave_4, codigo_4);
                        
                        if ( validar == 1){
                            validar = this.conector_db.validar_votacion(cedula_4, codigo_candidato_4);
                        }
                        break;
                        
                    default:
                        break;
                        
                }
                
                fin_conexion_db();
                
                objecto_enviar.put("respuesta", validar);

                DataOutputStream output = new DataOutputStream( server.getOutputStream());
                output.writeUTF(objecto_enviar.toString());

            }catch(IOException e){
                System.out.println(e.getMessage());
                System.out.println(e.getLocalizedMessage());
            }
        }    
        
    }

    public boolean get_estado_server() {
        return this.estado_server;
    }
    
}