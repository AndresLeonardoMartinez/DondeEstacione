package com.example.user.estacionado;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.model.LatLng;

public class MainActivity extends AppCompatActivity {
    private MyService mService;
    LatLng posicion;
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String ubicacionLatitud = "latKEY";
    public static final String ubicacionLongitud = "longKEY";
    private Intent s;
    SharedPreferences sharedpreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        //creamos el servicio
         s = new Intent(this,MyService.class);
        startService(s);
        //nos bindeamos al servicio para acceder al metodo que nos provee la ubicacion
        bindService(s, mConnection, Context.BIND_AUTO_CREATE);
    }
    public void guardar(View view){

        guardarPosicionAuto(); //invoca al metodo que invoca al metodo del servicio
        stopService(s); //termino el servicio
        Log.d("prueba", "antes de matar el servicio");
        //this.finish();
        this.finish();

    }
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get
            // LocalService instance
            MyService.LocalBinder binder = (MyService.LocalBinder) service;
            mService = binder.getService();
            Button botonGuardar = (Button) findViewById(R.id.botonGuardar);
            botonGuardar.setEnabled(true);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {

        }
    };
    private void guardarPosicionAuto (){
        posicion = mService.mostrar();
        SharedPreferences.Editor editor = sharedpreferences.edit();

        //guardamos la posicion mediante shared preferences
        if (posicion != null && !(posicion.latitude==0 && posicion.longitude==0)) {
            editor.clear(); //limpio lo viejo
            //editor.putFloat("latKEY",(float) posicion.latitude);
            //editor.putFloat("longKEY",(float) posicion.longitude);
            double latitud = posicion.latitude;
            double longitud = posicion.longitude;
            editor.putString(ubicacionLatitud,latitud+"");
            editor.putString(ubicacionLongitud, longitud + "");
            editor.commit();

        }
        else
        {
            editor.putString(ubicacionLatitud, "0");
            editor.putString(ubicacionLongitud, "0");
            editor.commit();
            Log.d("prueba", "guardarPosicionAuto: shared preferences 0 0");
        }
    }

}
