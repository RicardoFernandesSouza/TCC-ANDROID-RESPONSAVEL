package com.example.ricardofernandes.tohomeresponsavel;

/**
 * Created by RicardoFernandes on 07/06/2017.
 */


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class Tab2Status extends Fragment implements OnClickListener {

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.tab2status, container, false);
        Button button = (Button)rootView.findViewById(R.id.btnEtapas);
        Button marcaVisitaBtn = (Button)rootView.findViewById(R.id.btnMarcaVisita);
        Button tiraFoto = (Button)rootView.findViewById(R.id.btnTiraFoto);
        Button solicitaFoto = (Button)rootView.findViewById(R.id.btnSolicitaFoto);
        button.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v){
                switch(v.getId()){

                    case R.id.btnEtapas:
                        Intent intent1 = new Intent(rootView.getContext(), Etapas.class);
                        rootView.getContext().startActivity(intent1);
                        break;
                }
            }
        });

        marcaVisitaBtn.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v){
                switch(v.getId()){

                    case R.id.btnMarcaVisita:
                        Intent intent3 = new Intent(rootView.getContext(), MarcaVisita.class);
                        rootView.getContext().startActivity(intent3);
                        break;
                }
            }
        });

        tiraFoto.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v){
                switch(v.getId()){

                    case R.id.btnTiraFoto:
                        Intent intent4 = new Intent(rootView.getContext(), TiraFoto.class);
                        rootView.getContext().startActivity(intent4);
                        break;
                }
            }
        });

        solicitaFoto.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.btnSolicitaFoto:
                        sendEmail();
                }
            }
        });

        return rootView;

    }

    @Override
    public void onClick(View v) {

    }

    protected void sendEmail() {
        Log.i("Enviar E-mail", "");
        String[] TO = {"ricardo_fernandes.souza@hotmail.com"};
        String[] CC = {"ricardo_fernandes.souza@hotmail.com"};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setData(Uri.parse("ricardo_fernandes.souza@hotmail.com"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Foto da Obra");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Olá, gostaria de ter mais informaçoes da obra...Poderia me enviar uma foto?"+"\n"+"Aguardo retorno"+"\n"+"Esse foi uma mensagem automática do App To Home");


            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
           // finish();
//            Log.i("Finished sending email...", "");
//        } catch (android.content.ActivityNotFoundException ex) {
//            Toast.makeText(Tab2Status.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
//        }
//    }
}



}

