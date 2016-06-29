package com.example.user.estacionado;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.content.SharedPreferences;
import com.example.android.multidex.estacionado.R;
import android.support.v7.app.AppCompatActivity;


public class IniciarActivity extends AppCompatActivity {

    public String ubicacionLatitud=null;
    public String ubicacionLongitud=null;
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.iniciar_layout);

        ubicacionLatitud = getString(R.string.ubicacionLatitud);
        ubicacionLongitud = getString(R.string.ubicacionLongitud);
        sharedpreferences = getSharedPreferences(getString(R.string.MyPREFERENCES), Context.MODE_PRIVATE);

    }
    private void verificacion () {
        double latitud;
        double longitud;
        latitud = Double.parseDouble(sharedpreferences.getString(ubicacionLatitud,"0"));
        longitud = Double.parseDouble(sharedpreferences.getString(ubicacionLongitud, "0"));
        if (latitud == 0 && longitud==0){
            Intent i;
            i = new Intent(this,GuardarPosicionAutoActivity.class);
            startActivity(i);
        }
        else{ //existe ubicacion
            Intent i;
            i = new Intent(this, MapaAutoActivity.class);
            i.putExtra(getString(R.string.latitud),latitud);
            i.putExtra(getString(R.string.longitud),longitud);
            startActivity(i);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        verificacion();
    }
}
