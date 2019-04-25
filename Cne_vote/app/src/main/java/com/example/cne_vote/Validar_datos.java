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

public class Validar_datos extends AppCompatActivity {

    public static final String EXTRA_MESSAGE_2 = "com.example.cne_vote.MESSAGE";
    private TextView cedula_votante;
    private TextView nombres_votante;
    private TextView apellidos_votante;
    private EditText clave_votante;
    private EditText codigo_cne_votante;
    private TextView mensaje;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validar_datos);
        iniciar_elementos();
        // Obtiene el intent y establece los datos
        Intent intent = getIntent();
        String[] message = intent.getStringArrayExtra(Validar_cedula.EXTRA_MESSAGE);
        cedula_votante.setText(message[0]);
        nombres_votante.setText(message[1]);
        apellidos_votante.setText(message[2]);
    }

    protected void onResume() {
        super.onResume();
        // Limpia el contenido
        this.clave_votante.setText("");
        this.codigo_cne_votante.setText("");
    }
    // Evento
    public void verificar_datos(View view) {
        new validar_datos().execute( this.cedula_votante.getText().toString(), this.clave_votante.getText().toString(),
                this.codigo_cne_votante.getText().toString() );
    }
    private void iniciar_elementos(){
        cedula_votante = findViewById(R.id.ver_cedula_3);
        nombres_votante = findViewById(R.id.ver_nombres_3);
        apellidos_votante = findViewById(R.id.ver_apellidos_3);
        clave_votante = findViewById(R.id.insertar_clave_3);
        codigo_cne_votante = findViewById(R.id.insertar_codigo_3);
        mensaje = findViewById(R.id.alerta_texto_3);
    }
    // Tareas de multihilo
    @SuppressLint("StaticFieldLeak")
    private class validar_datos extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... datos) {
            return new Conector().validar_datos_3( datos[0], datos[1], datos[2] );
        }
        protected void onPostExecute(String resultado) {
            iniciar_votacion(resultado);
        }
    }
    // Cambio activity
    private void iniciar_votacion(String resultado){
        if ( resultado.equals("") ){
            String[] datos_i = { cedula_votante.getText().toString(), clave_votante.getText().toString(),
                    codigo_cne_votante.getText().toString() };

            Intent intent = new Intent(this, Inicio_votacion.class );
            intent.putExtra( EXTRA_MESSAGE_2, datos_i );
            startActivity(intent);
            finish();
        } else {
            mensaje.setText(resultado);
        }
    }
}

// '