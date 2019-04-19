package com.example.cne_vote;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.example.cne_vote.Clases.Candidato_presidente;
import com.example.cne_vote.Conexion_servidor.Conector;
import java.util.ArrayList;

public class Inicio_votacion extends AppCompatActivity {

    private TableLayout tabla;
    private ArrayList<Candidato_presidente> candidatos_presidentes;
    private String eleccion;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_votacion);
        this.tabla = findViewById(R.id.tabla_candidatos_presidentes_4);

        Intent intent = getIntent();
        String[] message = intent.getStringArrayExtra(Validar_datos.EXTRA_MESSAGE_2);
        obtener_datos_candidatos(message[0], message[1], message[2]);

    }

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
    // Cambio activity
    private void generar_contenido(ArrayList<Candidato_presidente> resultado){
        this.candidatos_presidentes = resultado;
        generar_contenido_tabla();
    }

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
                    System.out.println(eleccion);
                }
            });
        } else{
            fila.setBackgroundColor(Color.YELLOW);
        }
        return fila;
    }

    private void eleccion_candidato(String codigo){
        this.eleccion = codigo;
    }

    private void reiniciar_tabla(){
        this.tabla.removeAllViews();
        generar_contenido_tabla();
    }

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
}