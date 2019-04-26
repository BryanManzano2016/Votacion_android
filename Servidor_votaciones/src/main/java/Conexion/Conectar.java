
package Conexion;

import Base_datos.Conexion_db;
import Clases.Cliente;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Conectar extends Thread{
    
    private String usuario;
    private String contrasena;
    private Conexion_db conector_db;
    private ServerSocket serverSocket = null;
    private final int port = 60000;                       
    private boolean estado_server = false;
    private Set<Cliente> ips;
    private Map<String, Integer> ips_sancionadas;
    private Set<String> peticiones;

    public Conectar(String usuario_c, String contrasena_c)
    {
        try{
            this.serverSocket = new ServerSocket(port);
            this.estado_server = true;
            this.ips = new HashSet<>();
            this.ips_sancionadas = new HashMap<>();
            
            peticiones = new HashSet<>();
            crear_solicitudes_cadenas();
            
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
        
        Thread hilo_contador = new contador_tiempo(60);
        hilo_contador.start();
        
        while(true) 
        {
            
            try(Socket server = serverSocket.accept()) {                
                
                // Verifica si existe cliente
                Cliente cliente_entrante = new Cliente( server.getInetAddress().toString() );
                boolean validar_cliente = verificar_cliente(cliente_entrante);
                // Si se excede de las 20 solicitudes lo rechaza
                if( validar_cliente )
                    continue;
                // Lectura
                DataInputStream input = new DataInputStream( server.getInputStream() );
                
                JSONObject objecto_recibir = new JSONObject();
                String peticion = "";
                boolean validar_aumento = false;
                // Intenta deserealizar sino aumenta el nro de solicitudes
                try{
                    objecto_recibir = new JSONObject( input.readUTF() );
                    peticion = objecto_recibir.getString("peticion");
                    if( ! this.peticiones.contains( peticion ) ){
                        validar_aumento = aumentar_solicitudes_erroneas(cliente_entrante);
                    }
                } catch( JSONException ex){
                    validar_aumento = aumentar_solicitudes_erroneas(cliente_entrante);
                }                
                
                if ( validar_aumento )
                    continue;
                
                // Escritura
                JSONObject objecto_enviar = new JSONObject();
                ArrayList lista_candidatos;                
                int validar = 0;
                
                iniciar_conexion_db();
                switch ( peticion ) {
                    
                    case "verificar_cedula_votante":
                        
                        // Intenta deserealizar, sino se puede reiniciar el bucle
                        String cedula_votante;
                        try{
                            cedula_votante = objecto_recibir.getString("cedula");
                        }catch(JSONException e){
                            aumentar_solicitudes_erroneas(cliente_entrante);
                            continue;
                        }
                        
                        String[] datos = this.conector_db.leer_cedula_votante( cedula_votante );
                        if( !datos[0].equals("") && !datos[1].equals("") ){
                            objecto_enviar.put("nombres", datos[0]);
                            objecto_enviar.put("apellidos", datos[1]);
                            objecto_enviar.put("estado", datos[2]);
                            validar = 1;
                        } else {
                            aumentar_solicitudes_erroneas(cliente_entrante);
                        }
                        break;
                        
                    case "verificar_datos_votante":
                        
                        String cedula_votante_2;
                        String palabra_clave_2;
                        String codigo_2;                        
                        try{
                            cedula_votante_2 = objecto_recibir.getString("cedula");
                            palabra_clave_2 = objecto_recibir.getString("palabra_clave");
                            codigo_2 = objecto_recibir.getString("codigo");                        
                        }catch(JSONException e){
                            aumentar_solicitudes_erroneas(cliente_entrante);
                            continue;
                        }                        
                        
                        validar = this.conector_db.validar_datos_votante( cedula_votante_2, palabra_clave_2, codigo_2);
                        if( validar != 1){
                            aumentar_solicitudes_erroneas(cliente_entrante);
                        }
                        break;
                        
                    case "obtener_datos_candidatos":                        
                        
                        String cedula_votante_3;
                        String palabra_clave_3;
                        String codigo_3;                        
                        try{
                            cedula_votante_3 = objecto_recibir.getString("cedula");
                            palabra_clave_3 = objecto_recibir.getString("palabra_clave");
                            codigo_3 = objecto_recibir.getString("codigo");                        
                        }catch(JSONException e){
                            aumentar_solicitudes_erroneas(cliente_entrante);
                            continue;
                        }                         
                        
                        // Valido que el individuo este en la db
                        validar = this.conector_db.validar_datos_votante( cedula_votante_3, palabra_clave_3, codigo_3);
                        // Si es positivo accedo a la db y obtengo los datos de los candidatos                                                
                        JSONArray datos_candidatos = new JSONArray();
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
                        } else {
                            aumentar_solicitudes_erroneas(cliente_entrante);
                        }
                        break;
                        
                    case "realizar_votacion_cne":
                        
                        String cedula_votante_4;
                        String palabra_clave_4;
                        String codigo_4;                        
                        String codigo_candidato_4;
                        try{
                            cedula_votante_4 = objecto_recibir.getString("cedula");
                            palabra_clave_4 = objecto_recibir.getString("palabra_clave");
                            codigo_4 = objecto_recibir.getString("codigo");                        
                            codigo_candidato_4 = objecto_recibir.getString("codigo_candidato");
                        }catch(JSONException e){
                            aumentar_solicitudes_erroneas(cliente_entrante);
                            continue;
                        }                         
                        
                        validar = this.conector_db.validar_datos_votante( cedula_votante_4, palabra_clave_4, codigo_4);
    
                        if ( validar == 1 ){
                            validar = this.conector_db.validar_votacion(cedula_votante_4, codigo_candidato_4);
                            if( validar == 1 )
                                reiniciar_solicitudes(cliente_entrante);
                        } else {
                            aumentar_solicitudes_erroneas(cliente_entrante);
                        }
                        break;
                        
                    default:
                        break;
                        
                }
                
                fin_conexion_db();
                
                objecto_enviar.put("respuesta", validar);

                DataOutputStream output = new DataOutputStream( server.getOutputStream());
                output.writeUTF(objecto_enviar.toString());
                
            }catch(IOException e){}
        }    
         
    }

    public boolean get_estado_server() {
        return this.estado_server;
    }
    
    private void crear_solicitudes_cadenas(){
        this.peticiones.add("verificar_cedula_votante");
        this.peticiones.add("verificar_datos_votante");
        this.peticiones.add("obtener_datos_candidatos");
        this.peticiones.add("realizar_votacion_cne");
    }
    
    private boolean verificar_cliente(Cliente cliente){
        boolean validar = this.ips_sancionadas.containsKey(cliente.getDireccion());
        
        if( !validar ){
            Cliente cliente_stream = (Cliente) this.ips.stream().filter(e->e.getDireccion().
                    equals(cliente.getDireccion())).findAny().orElse(null);
            // Si no existe lo añade a la lista, si ha ingresado mas de 20 veces lo expulsa
            if( cliente_stream == null )
            {
                this.ips.add( cliente );
            } else {
                // Rechazado si hay exceso de solicitudes
                boolean validar_exceso = rechazar_cliente(cliente);
                if( validar_exceso == true ){
                    this.ips_sancionadas.put(cliente.getDireccion(), 0);
                    validar = true;
                }
            }  
        }
        
        return validar;
    }
    
    private boolean aumentar_solicitudes_erroneas(Cliente cliente){
        boolean validar = false;
        for ( Cliente c: this.ips ) 
        {
            if( cliente.getDireccion().equals(c.getDireccion()) ){
                c.aumentar_solicitudes();
                validar = true;
                break;
            }   
        }     
        fin_conexion_db();
        return validar;
    }
    
    private void reiniciar_solicitudes(Cliente cliente){
        for ( Cliente c: this.ips ) 
        {
            if( cliente.getDireccion().equals(c.getDireccion())){
                c.reiniciar_solicitudes();
                break;
            }   
        }     
    }
    
    private boolean rechazar_cliente(Cliente cliente){
        boolean validar = false;
            for ( Cliente c: this.ips ) 
            {
                if( cliente.getDireccion().equals(c.getDireccion()) && c.getSolicitudes() >= 20 ){
                    validar = true;
                    break;
                }   
            }
        return validar;
    }
    
    private class contador_tiempo extends Thread{
        private int valor;
        
        public contador_tiempo(int valor){
            this.valor = valor;
        }
        @Override
        public void run(){
            while (true) {
                try {
                    Thread.sleep(this.valor * 1000);
                } catch (InterruptedException ex) {}
                
                if( ips_sancionadas.size() > 0 ){
                    for (String clave: ips_sancionadas.keySet()) {
                        
                        int segundos = ips_sancionadas.get(clave);
                        ips_sancionadas.replace(clave, segundos + this.valor); 
                        
                        for( Cliente cliente: ips ){
                            if( cliente.getDireccion().equals(clave) && cliente.getSolicitudes() >= 20 
                                    && ips_sancionadas.get(clave) >= this.valor * 60 ){
                                ips.remove(cliente);
                                ips_sancionadas.remove(clave);
                                break;
                            }    
                        }
                    }
                }
            }
        }
    }

    private void conteo_votos(){
        iniciar_conexion_db();
        this.candidatos_votos = this.conector_db.obtener_votos();
        fin_conexion_db();
        if( this.candidatos_votos.size() > 0 ){
            JSONObject json_enviar = new JSONObject();
            JSONArray datos_candidatos = new JSONArray();
            for( Candidato_resultado candidato: this.candidatos_votos){
                JSONArray candidato_array = new JSONArray();      
                candidato_array.put(candidato.getNombre_completo());
                candidato_array.put(candidato.getCodigo());
                candidato_array.put(candidato.getNro_votos());
                datos_candidatos.put(candidato_array);
            }
            json_enviar.put("votos", datos_candidatos);
            json_enviar.put("usuario", this.usuario);
            json_enviar.put("contrasena", this.contrasena);
            json_enviar.put("peticion", "envio_servidor_paquete");
            
            enviar_servidores(json_enviar);
        }
    }    
    
    
}

/*
System.out.println("-----");
for( Cliente c: this.ips){
    System.out.println(c.getDireccion() + ", " + c.getSolicitudes());
}                
System.out.println("-----");
*/
