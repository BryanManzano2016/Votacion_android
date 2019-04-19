package com.example.cne_vote;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void ingresar_validar_cedula(View view) {
        Intent intent = new Intent(this, Validar_cedula.class);
        startActivity(intent);
    }

    public void solicitar_ayuda(View view) {
        Intent intent = new Intent(this, Validar_cedula.class);
        startActivity(intent);
    }

}

