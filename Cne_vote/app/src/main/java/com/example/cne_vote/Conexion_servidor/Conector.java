package com.example.cne_vote.Conexion_servidor;

import com.example.cne_vote.Clases.Candidato_presidente;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class Conector {
    private Socket client;
    private OutputStream output;
    private InputStream input;
    final private String hostName;
    final private int portNumber;
    // Constructor
    public Conector() {
        this.hostName = "192.168.100.133";
        this.portNumber = 60000;
    }
    // Abre sockets para enviar datos serializados
    private void iniciar_conexion(){
        try {
            this.client = new Socket(hostName, portNumber);
            this.output = this.client.getOutputStream();
            this.input = this.client.getInputStream();
        }catch(IOException ignored){}
    }
    // Cierra las conexiones con el servidor
    private void anular_conexion(){
        this.client = null;
        this.output = null;
        this.input = null;
    }
    // Envio objeto serializado mediante el OutputStream
    public String[] validar_cedula_2(String cedula){

        String[] datos = {"", cedula, "", ""};
        iniciar_conexion();
        try {
            // ESCRITURA: cedula y comando
            DataOutputStream out = new DataOutputStream(this.output);

            JSONObject objecto_enviar = new JSONObject();
            objecto_enviar.put("peticion", "verificar_cedula_votante");
            objecto_enviar.put("cedula", cedula);

            out.writeUTF( objecto_enviar.toString() );

            // LECTURA: codigo cne y clave
            DataInputStream input = new DataInputStream( client.getInputStream() );

            JSONObject objecto_recibir = new JSONObject( input.readUTF() );
            if ( objecto_recibir.getInt("respuesta") == 0){
                datos[0] = "Ciudadano no encontrado en padron electoral";
            } else if ( objecto_recibir.getInt("respuesta") == 1 ) {
                datos[2] = objecto_recibir.getString("nombres");
                datos[3] = objecto_recibir.getString("apellidos");
            }

        }catch (IOException | JSONException ignored){}
        anular_conexion();

        return datos;
    }

    public String validar_datos_3(String cedula, String palabra_clave, String codigo){

        String validar = "";
        iniciar_conexion();
        try {
            // ESCRITURA: cedula y comando
            DataOutputStream out = new DataOutputStream(this.output);

            JSONObject objecto_enviar = new JSONObject();
            objecto_enviar.put("peticion", "verificar_datos_votante");
            objecto_enviar.put("cedula", cedula);
            objecto_enviar.put("palabra_clave", palabra_clave);
            objecto_enviar.put("codigo", codigo);

            out.writeUTF( objecto_enviar.toString() );

            // LECTURA: codigo cne y clave
            DataInputStream input = new DataInputStream( client.getInputStream() );

            JSONObject objecto_recibir = new JSONObject( input.readUTF() );
            if ( objecto_recibir.getInt("respuesta") != 1 ){
                validar = "Los datos ingresados son erroneos";
            }

        }catch (IOException | JSONException ignored){}
        anular_conexion();

        return validar;
    }

    public ArrayList<Candidato_presidente> obtener_datos_4(String cedula, String palabra_clave, String codigo){

        ArrayList<Candidato_presidente> candidatos = new ArrayList<>();

        iniciar_conexion();
        try {
            // ESCRITURA: cedula y comando
            DataOutputStream out = new DataOutputStream(this.output);

            JSONObject objecto_enviar = new JSONObject();
            objecto_enviar.put("peticion", "obtener_datos_candidatos");
            objecto_enviar.put("cedula", cedula);
            objecto_enviar.put("palabra_clave", palabra_clave);
            objecto_enviar.put("codigo", codigo);

            out.writeUTF( objecto_enviar.toString() );

            // LECTURA: codigo cne y clave
            DataInputStream input = new DataInputStream( client.getInputStream() );

            JSONObject objecto_recibir = new JSONObject( input.readUTF() );
            JSONArray json_total = new JSONArray( objecto_recibir.get("candidatos").toString() );
            for ( int i = 0; i < json_total.length(); i++){
                JSONArray json_parcial = new JSONArray( json_total.get(i).toString() );
                Candidato_presidente candidato = new Candidato_presidente( json_parcial.get(0).toString(), json_parcial.get(1).toString(),
                        json_parcial.get(2).toString(), json_parcial.get(3).toString());
                candidatos.add(candidato);
            }
        }catch (IOException | JSONException ignored){}

        anular_conexion();

        return candidatos;
    }
}
