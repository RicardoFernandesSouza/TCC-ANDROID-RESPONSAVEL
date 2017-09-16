package com.example.ricardofernandes.tohomeresponsavel;

/**
 * Created by RicardoFernandes on 07/06/2017.
 */


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class Etapas extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setTitle( "Etapas" );
        setContentView(R.layout.activity_etapas);

        Button mButtonResponsavel = (Button) findViewById(R.id.btnResponsavel);
        Button mButtonCliente = (Button) findViewById(R.id.btnCliente);




        mButtonResponsavel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent it = new Intent(Etapas.this, EtapasResponsavel.class);
                startActivity(it);
            }
        });

        mButtonCliente.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent it1 = new Intent(Etapas.this, EtapasCliente.class);
                startActivity(it1);
            }
        });
    }
}