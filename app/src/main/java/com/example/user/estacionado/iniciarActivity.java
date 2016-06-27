package com.example.user.estacionado;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.content.SharedPreferences;

import com.example.android.multidex.estacionado.R;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;

public class iniciarActivity extends AppCompatActivity {
    public static final String MyPREFERENCES = "MyPrefs" ;
   // public static final String BORRADOR = "BORRADOR" ;

    public static final String ubicacionLatitud = "latKEY";
    public static final String ubicacionLongitud = "longKEY";
    SharedPreferences sharedpreferences;

    //SharedPreferences sharedpreferences2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.iniciar_layout);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

    }
    private void verificacion () {
        //verificar si el gps esta activado
        //verificar si existe posicion
        //prueba
        double latitud;
        double longitud;
        latitud = Double.parseDouble(sharedpreferences.getString(ubicacionLatitud,"0"));
        longitud = Double.parseDouble(sharedpreferences.getString(ubicacionLongitud, "0"));
//        Log.d("prueba", "valor shared latitud " + latitud);
      //  Log.d("prueba", "valor shared longitud " + longitud);
        Log.d("prueba", "iniciar.verificacion(): antes del if");
        if (((latitud == 0 && longitud==0) )) //|| borrar== 1)) //no existe una ubicacion previa
        {
            Intent i;
            Log.d("prueba", "iniciar.verificacion(): desde Inicial voy a lanzar act1");
            i = new Intent(this,guardarPosicionAutoActivity.class);
            startActivity(i);
        }
        else //existe ubicacion
        {
            Intent i;
            Log.d("prueba", "iniciar.verificacion(): desde Inicial voy a lanzar act2");
            i = new Intent(this, mapaAutoActivity.class);
            i.putExtra("latitud",latitud);
            i.putExtra("longitud",longitud);
           // Log.d("prueba", "INICIAR inicia A2 con latitud" + latitud);
          //  Log.d("prueba", "INICIAR inicia A2 con longitud" + longitud);
            startActivity(i);

        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        verificacion();
    }

}
