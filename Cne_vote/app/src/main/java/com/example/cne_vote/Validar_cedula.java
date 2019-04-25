package com.example.cne_vote;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.cne_vote.Conexion_servidor.Conector;

public class Validar_cedula extends AppCompatActivity {
    // Variables
    public static final String EXTRA_MESSAGE = "com.example.cne_vote.MESSAGE";
    private EditText cedula_votante;
    private TextView mensaje;
    // Metodos de activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validar_cedula);
        iniciar_elementos();
    }
    protected void onResume() {
        super.onResume();
        // Limpia el contenido
        this.cedula_votante.setText("");
        this.mensaje.setText("");
    }
    // Eventos
    public void verificar_cedula(View view) {
        if ( !this.cedula_votante.getText().toString().equals("") ) {
            new validar_cedula().execute( this.cedula_votante.getText().toString() );
        }
    }
    private void iniciar_elementos(){
        cedula_votante = findViewById( R.id.input_cedula_2 );
        mensaje = findViewById( R.id.alerta_texto_2 );
    }
    // Tareas de multihilo
    @SuppressLint("StaticFieldLeak")
    private class validar_cedula extends AsyncTask<String, Void, String[]> {
        protected String[] doInBackground(String... cedula) {
            return new Conector().validar_cedula_2( cedula[0] );
        }
        protected void onPostExecute(String[] resultado) {
            iniciar_validar_datos(resultado);
        }
    }
    // Cambio activity
    private void iniciar_validar_datos(String[] datos){
        if ( datos[0].length() == 0 ){
            if( datos[4].equals("1")){
                mensaje.setText( R.string.votacion_finalizada );
            } else {
                String[] datos_i = {datos[1], datos[2], datos[3]};
                Intent intent = new Intent(this, Validar_datos.class);
                intent.putExtra(EXTRA_MESSAGE, datos_i);
                startActivity(intent);
                finish();
            }
        } else {
            mensaje.setText(datos[0]);
        }
    }
}