package com.example.user.estacionado;

import com.example.android.multidex.estacionado.R;
import android.annotation.SuppressLint;
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
import android.widget.Button;
import com.google.android.gms.maps.model.LatLng;

public class GuardarPosicionAutoActivity extends AppCompatActivity implements BotonUbicacionAutoFragment.OnFragmentInteractionListener {
    private PosicionGPSService mService;
    public String ubicacionLatitud=null;
    public String ubicacionLongitud=null;
    private Intent s;
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guardarposautolayout);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        sharedpreferences = getSharedPreferences(getString(R.string.MyPREFERENCES), Context.MODE_PRIVATE);
        ubicacionLatitud = getString(R.string.ubicacionLatitud);
        ubicacionLongitud = getString(R.string.ubicacionLongitud);
    }

    private void metodoInicial(){
        s = new Intent(this,PosicionGPSService.class);
        startService(s);
        bindService(s, mConnection, Context.BIND_AUTO_CREATE);
    }

    public void guardar(){
        boolean resu = guardarPosicionAuto();
        if(resu) {
            unbindService(mConnection);
            stopService(s);
            this.finish();
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            PosicionGPSService.LocalBinder binder = (PosicionGPSService.LocalBinder) service;
            mService = binder.getService();
            Button botonGuardar = (Button) findViewById(R.id.botonGuardar);
            if (botonGuardar != null) {
                botonGuardar.setEnabled(true);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0){}
    };

    @SuppressLint("CommitPrefEdits")
    private boolean guardarPosicionAuto (){
        LatLng posicion = mService.mostrar();
        SharedPreferences.Editor editor = sharedpreferences.edit();
        if (posicion != null && !(posicion.latitude==0 && posicion.longitude==0)){
            editor.clear();
            double latitud = posicion.latitude;
            double longitud = posicion.longitude;
            editor.putString(ubicacionLatitud,latitud+"");
            editor.putString(ubicacionLongitud, longitud + "");
            editor.commit();
            return true;
        }else{
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getString(R.string.guardarPosicionAlertaNoSignalMsg))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.guardarPosicionAlertaMsgBtnAceptar), new DialogInterface.OnClickListener() {
                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            dialog.cancel();
                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();
            //editor.putString(ubicacionLatitud, "0");
            //editor.putString(ubicacionLongitud, "0");
            //editor.commit();
            return false;
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        EnableGPSIfPossible();
        metodoInicial();
    }

    @Override
    protected void onRestart(){
        super.onRestart();
    }

    @Override
    protected void onStop(){
        super.onStop();
        stopService(s);
    }

    private void EnableGPSIfPossible(){
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
        }
    }

    private  void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.AlertaNoGPSMsg))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.AlertaNoGPSBtnSi), new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton(getString(R.string.AlertaNoGPSBtnNo), new DialogInterface.OnClickListener() {
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

    @Override
    public void guardarPosicion() {
        guardar();
    }
}