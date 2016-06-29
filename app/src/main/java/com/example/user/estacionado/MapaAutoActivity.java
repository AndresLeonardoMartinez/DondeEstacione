package com.example.user.estacionado;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.RequestResult;
import com.example.android.multidex.estacionado.R;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class MapaAutoActivity extends AppCompatActivity implements BotonesFragment.OnFragmentInteractionListener, OnMapReadyCallback {
    private GoogleMap mMap;
    SharedPreferences sharedpreferences;
    private Intent s;
    private PosicionGPSService mService;
    private LatLng posicionAuto;
    private Marker marcadorPosicionUsuario;
    private Polyline ruta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapa_layout);

        sharedpreferences = getSharedPreferences(getString(R.string.MyPREFERENCES), Context.MODE_PRIVATE);

        Fragment newFragment = BotonesFragment.newInstance();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.fragmento_boton_nuevo_estacionamiento, newFragment).commit();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapa);
        mapFragment.getMapAsync(this);

    }

    @Override
    protected void onRestart() {
        Fragment newFragment = BotonesFragment.newInstance();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.fragmento_boton_nuevo_estacionamiento, newFragment).commitAllowingStateLoss();
        super.onRestart();
    }

    private void metodoInicial(){
        Intent i = getIntent();
        double lat = i.getDoubleExtra(getString(R.string.latitud), 0);
        double longi= i.getDoubleExtra(getString(R.string.longitud), 0);
        posicionAuto = new LatLng(lat,longi);
        s = new Intent(this,PosicionGPSService.class);
        startService(s);
        bindService(s, mConnection, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            PosicionGPSService.LocalBinder binder = (PosicionGPSService.LocalBinder) service;
            mService = binder.getService();
        }
        @Override
        public void onServiceDisconnected(ComponentName arg0) {}
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
        stopService(s);
    }

    public void MostrarPosicionAuto(){
        Geocoder geocoder = new Geocoder(this);
        double latitudeAuto = posicionAuto.latitude;
        double longitudeAuto = posicionAuto.longitude;
        List<Address> addresses;
        String addressText="";
        try {
            addresses = geocoder.getFromLocation(latitudeAuto, longitudeAuto,1);
            if(addresses != null && addresses.size() > 0 ){
                Address address = addresses.get(0);
                addressText = String.format("%s", address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "");

            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        mMap.addMarker(new MarkerOptions()
                        .position(posicionAuto).
                                title(getString(R.string.posicionActual) + addressText)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.auto_icono))
        );
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(posicionAuto, 15));
    }

    @Override public void mostrarPosicion() {
        if (mService != null && mMap!=null) {
            LatLng posicionUsuarioActual = mService.mostrar();
            double latitude = posicionUsuarioActual.latitude;
            double longitude = posicionUsuarioActual.longitude;
            if (!(latitude == 0 && longitude == 0)){
                if ( marcadorPosicionUsuario != null ){
                    marcadorPosicionUsuario.remove();
                }
                marcadorPosicionUsuario =mMap.addMarker(new MarkerOptions().position(posicionUsuarioActual).title(getString(R.string.posicionUsuario)));
                obtenerRuta(posicionUsuarioActual, posicionAuto);
            }
        }
    }

    @SuppressLint("CommitPrefEdits")
    @Override
    public void NuevaPosicion() {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.clear();
        editor.commit();
        unbindService(mConnection);
        stopService(s);
        this.finish();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        MostrarPosicionAuto();
    }

    private void EnableGPSIfPossible(){
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
        }
    }

    private void buildAlertMessageNoGps(){
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

    @SuppressWarnings("unchecked")
    private DirectionCallback retornoObtenerRuta = new DirectionCallback(){
        @Override
        public void onDirectionSuccess(Direction direction, String rawBody) {
            String status = direction.getStatus();
            if(status.equals(RequestResult.OK)) {
                Route route = direction.getRouteList().get(0);
                Leg leg = route.getLegList().get(0);
                ArrayList lista_ruta = leg.getSectionPoint();
                PolylineOptions recorrido = new PolylineOptions();
                recorrido.color(Color.RED);
                recorrido.addAll(lista_ruta);
                if (ruta!= null)
                    ruta.remove();
                 ruta = mMap.addPolyline(recorrido);
            }
        }

        @Override
        public void onDirectionFailure(Throwable t){}
    };

    private ArrayList obtenerRuta(LatLng inicio, LatLng destino) {
        ArrayList lista_ruta = new ArrayList();
        String serverKey;
        serverKey = getString(R.string.google_maps_direction_server_key);
        GoogleDirection.withServerKey(serverKey)
                .from(inicio)
                .to(destino)
                .transportMode(TransportMode.WALKING)
                .execute(retornoObtenerRuta);
        return lista_ruta;
    }
}
