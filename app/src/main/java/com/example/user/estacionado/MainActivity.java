package com.example.user.estacionado;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.location.LocationManager;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

public class MainActivity extends AppCompatActivity {
    private MyService mService;
    LatLng posicion;
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String ubicacionLatitud = "latKEY";
    public static final String ubicacionLongitud = "longKEY";
    private Intent s;
    private int cont;
    SharedPreferences sharedpreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EnableGPSIfPossible();
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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
        Log.d("prueba", "MainActivity.guardar() antes de matar el servicio");
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
        if (posicion != null && !(posicion.latitude==0 && posicion.longitude==0)){
            editor.clear(); //limpio lo viejo
            //editor.putFloat("latKEY",(float) posicion.latitude);
            //editor.putFloat("longKEY",(float) posicion.longitude);
            double latitud = posicion.latitude;
            double longitud = posicion.longitude;
            editor.putString(ubicacionLatitud,latitud+"");
            editor.putString(ubicacionLongitud, longitud + "");
            editor.commit();
        }else{
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("No se percibe señal. Reintentar")
                    .setCancelable(false)
                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            dialog.cancel();
                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();
            Log.d("prueba", "MainActivity.guardarPosicionAuto(): DENTRO DEL ELSE");
            editor.putString(ubicacionLatitud, "0");
            editor.putString(ubicacionLongitud, "0");
            editor.commit();
            Log.d("prueba", "MainActivity.guardarPosicionAuto(): shared preferences 0 0");
        }
    }

    protected void onResume(){
        super.onResume();
        EnableGPSIfPossible();
    }


    private void EnableGPSIfPossible(){
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
        }
    }

    private  void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Es necesario tener activado el GPS para usar la aplicación. ¿Desea activarlo?")
                .setCancelable(false)
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }


    public void onBackPressed(){
        super.onBackPressed();
        onStop();
    }
}
