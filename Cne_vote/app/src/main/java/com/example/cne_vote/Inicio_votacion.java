package com.example.cne_vote;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.example.cne_vote.Clases.Candidato_presidente;
import com.example.cne_vote.Conexion_servidor.Conector;
import java.util.ArrayList;

public class Inicio_votacion extends AppCompatActivity {

    private String cedula_votante;
    private String palabra_clave;
    private String codigo_cne;
    private TableLayout tabla;
    private ArrayList<Candidato_presidente> candidatos_presidentes;
    private String eleccion = "";
    private TextView mensaje;
    private boolean voto_realizado;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_votacion);
        this.tabla = findViewById(R.id.tabla_candidatos_presidentes_4);
        this.mensaje = findViewById(R.id.mensaje_4);
        this.voto_realizado = false;
        Intent intent = getIntent();
        String[] message = intent.getStringArrayExtra(Validar_datos.EXTRA_MESSAGE_2);
        this.cedula_votante = message[0];
        this.palabra_clave = message[1];
        this.codigo_cne = message[2];

        obtener_datos_candidatos(this.cedula_votante, this.palabra_clave, this.codigo_cne);
    }

    // Presiona el boton de votar
    public void ejecutar_votacion(View view){
        if( ! this.eleccion.equals("") && !voto_realizado ){
            new eleccion_votacion().execute(this.cedula_votante, this.palabra_clave, this.codigo_cne, this.eleccion);
        }
    }

    // Llama a ejecutar la tarea que obtiene la lista de candidatos
    private void obtener_datos_candidatos(String cedula, String palabra_clave, String codigo){
        new datos_candidatos().execute(cedula, palabra_clave, codigo);
    }
    // Tareas de multihilo
    @SuppressLint("StaticFieldLeak")
    private class datos_candidatos extends AsyncTask<String, Void, ArrayList<Candidato_presidente>> {
        protected ArrayList<Candidato_presidente> doInBackground(String... datos) {
            return new Conector().obtener_datos_4( datos[0], datos[1], datos[2] );
        }
        protected void onPostExecute(ArrayList<Candidato_presidente> resultado) {
            generar_contenido(resultado);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class eleccion_votacion extends AsyncTask<String, Void, Integer> {
        protected Integer doInBackground(String... datos) {
            return new Conector().realizar_votacion_4( datos[0], datos[1], datos[2], datos[3]);
        }
        protected void onPostExecute(Integer resultado) {
            validar_votacion(resultado);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class contador_segundos extends AsyncTask<Integer, Void, Void> {
        protected Void doInBackground(Integer... segundos) {
            int segundos_local = 0;
            while ( segundos_local < segundos[0] ){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {}
                segundos_local++;
            }
            return null;
        }
        protected void onPostExecute(Void none) {
            finish();
        }
    }
    // Llama al metod que crea la tabla con la lista  que es dada por la tarea
    private void generar_contenido(ArrayList<Candidato_presidente> resultado){
        this.candidatos_presidentes = resultado;
        generar_contenido_tabla();
    }
    // Genera la tabla
    private void generar_contenido_tabla(){

        TableRow fila_encabezado = obtener_fila("Nombre", "Partido político", "-1");
        this.tabla.addView(fila_encabezado, 0);

        int contador = 1;
        for( Candidato_presidente candidato: this.candidatos_presidentes){
            TableRow fila_tabla = obtener_fila(candidato.getNombres() + " " +  candidato.getApellidos(), candidato.getPartido(),
                    candidato.getCodigo());
            this.tabla.addView(fila_tabla, contador);
            contador++;
        }
    }
    // Genera una fila de la tabla
    private TableRow obtener_fila(String seccion_nombre_f, String seccion_partido_f, final String codigo) {

        final TableRow fila = new TableRow(this);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT,
                0.3f);
        fila.setLayoutParams(lp);

        TextView seccion_nombre = obtener_texto(seccion_nombre_f);
        TextView seccion_partido = obtener_texto(seccion_partido_f);

        fila.addView(seccion_nombre, 0);
        fila.addView(seccion_partido, 1);

        if( ! codigo.equals("-1") ) {
            if (!codigo.equals(this.eleccion)) {
                fila.setBackgroundColor(Color.WHITE);
            } else {
                fila.setBackgroundColor(Color.CYAN);
            }

            fila.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!codigo.equals(eleccion)) {
                        eleccion_candidato(codigo);
                        reiniciar_tabla();
                    }
                }
            });
        } else{
            fila.setBackgroundColor(Color.YELLOW);
        }
        return fila;
    }
    // Solo establece cual es la eleccion del votante
    private void eleccion_candidato(String codigo){
        this.eleccion = codigo;
    }
    // Borra los elementos de la tabla
    private void reiniciar_tabla(){
        this.tabla.removeAllViews();
        generar_contenido_tabla();
    }
    // Genera una celda de la tabla
    private TextView obtener_texto(String texto){

        TextView vista_texto = new TextView(this);
        vista_texto.setText(texto);

        vista_texto.setWidth(400);
        vista_texto.setHeight(150);
        vista_texto.setPadding(0,100,25,0);
        vista_texto.setBackgroundResource(R.drawable.border);
        vista_texto.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        vista_texto.setTextSize(16);

        return vista_texto;
    }
    // Verifica que la votacion sea con exito
    private void validar_votacion(Integer resultado){
        System.out.println(resultado);
        if ( resultado == 1 ) {
            this.voto_realizado = true;
            mensaje.setText(R.string.mensaje_validacion_voto);
            new contador_segundos().execute(5);
        } else if ( resultado == 2){
            this.mensaje.setText(R.string.servidor_fuera_servicio);
        } else {
            this.mensaje.setText(R.string.mensaje_negacion_voto);
        }
    }
}