package com.example.ricardofernandes.tohomeresponsavel;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by RicardoFernandes on 07/06/2017.
 */

public class Tab3Dados extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.tab3dados, container, false);
        Button buttonDadosResp = (Button)rootView.findViewById(R.id.btn_cliente);
        Button buttonDadosResi = (Button)rootView.findViewById(R.id.btn_residencia);
        buttonDadosResp.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v){
                switch(v.getId()){

                    case R.id.btn_cliente:
                        Intent intent1 = new Intent(rootView.getContext(), DadosCliente.class);
                        rootView.getContext().startActivity(intent1);
                        break;
                }
            }
        });

        buttonDadosResi.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v){
                switch(v.getId()){

                    case R.id.btn_residencia:
                        Intent intent3 = new Intent(rootView.getContext(), DadosResidencia.class);
                        rootView.getContext().startActivity(intent3);
                        break;
                }
            }
        });



        return rootView;

    }
}