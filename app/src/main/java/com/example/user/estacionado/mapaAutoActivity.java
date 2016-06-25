package com.example.user.estacionado;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

import com.example.user.estacionado.botonesFragment;

public class mapaAutoActivity extends AppCompatActivity implements botonesFragment.OnFragmentInteractionListener, OnMapReadyCallback {
    private GoogleMap mMap;
    SharedPreferences sharedpreferences;
    private Intent s;
    private Fragment F;
    private PosicionGPSService mService;
    private LatLng posicion;
    private Marker miPosicion;
    public static final String MyPREFERENCES = "MyPrefs" ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_fragmento_actividad2);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        //mapa
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapa);
        mapFragment.getMapAsync(this);
    }
    private void metodoInicial(){
        Intent i = getIntent();
        double lat = i.getDoubleExtra("latitud",0);
        double longi= i.getDoubleExtra("longitud",0);
        posicion = new LatLng(lat,longi);
        //indico en el mapa la pos del auto
        //MostrarPosicionAuto();
        Log.d("prueba", "A2.metodoInicial(): intent lat: "+lat);
        Log.d("prueba", "A2.metodoInicial(): intent longi: " + longi);
        //creo y me bindeo para acceder al metodo mostrar del servicio
        s = new Intent(this,PosicionGPSService.class);
        startService(s);
        bindService(s, mConnection, Context.BIND_AUTO_CREATE);
    }
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get
            // LocalService instance
            PosicionGPSService.LocalBinder binder = (PosicionGPSService.LocalBinder) service;
            mService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        EnableGPSIfPossible();
        metodoInicial();

    }

    @Override
    protected void onStop() {
        super.onStop();
        //unbindService(mConnection);
        stopService(s);
    }

    public void MostrarPosicionAuto(){
        Geocoder geocoder = new Geocoder(this);
        double latitude = posicion.latitude;
        double longitude = posicion.longitude;
        List<Address> addresses;
        String addressText="";
        try {
            addresses = geocoder.getFromLocation(latitude, longitude,1);
            if(addresses != null && addresses.size() > 0 ){
                Address address = addresses.get(0);
                addressText = String.format("%s",
                        address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "");
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        Log.i("prueba", "addressText " + addressText);
        //miPosicion=
                mMap.addMarker(new MarkerOptions().position(posicion).title("Su vehículo: " + addressText));


    }

    @Override public void mostrarPosicion() {
        //obtenemos posicion actual
        //preguntamos por gps

        if (mService != null && mMap!=null) {
            LatLng miPosicion = mService.mostrar();
            Geocoder geocoder = new Geocoder(this);
            double latitude = miPosicion.latitude;
            double longitude = miPosicion.longitude;
            if (!(latitude == 0 && longitude == 0))
            {
                List<Address> addresses;
                String addressText="";
                try {
                    addresses = geocoder.getFromLocation(latitude, longitude,1);
                    if(addresses != null && addresses.size() > 0 ){
                        Address address = addresses.get(0);
                        addressText = String.format("%s",
                                address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "");
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                Log.i("prueba", "addressText " + addressText);
                //mMap.setContentDescription(addressText);

                mMap.addMarker(new MarkerOptions().position(miPosicion).title("Ud esta aquí"));

                //mMap.addMarker(new MarkerOptions().position(posicion).title("Su vehículo: "+addressText));
                //if (veces==0)
                //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(miPosicion,12));
                //aca seria deseable calcular la ruta
                //veces++;
            }
        }
        else
        {
            Log.d("prueba", "El servicio es nulo o el mapa aun no fue creado");
        }

    }
    @Override
    public void NuevaPosicion() {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.clear();
        editor.commit();
        this.finish();
        //vuelvo a I
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng BahiaBlanca = new LatLng(-38.7167,-62.2833);
        mMap = googleMap;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(BahiaBlanca,12));
        MostrarPosicionAuto();
        Log.d("MAPA", "ONmapReady");
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

}
