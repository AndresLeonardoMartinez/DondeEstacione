package com.example.user.estacionado;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;


import com.google.android.gms.maps.model.LatLng;


public class PosicionGPSService extends Service {
    private final IBinder mBinder = new LocalBinder();
    private LocationManager locationManager;
    private String locationProvider;
    private LatLng latlong;
    private GuardarPosicionAutoActivity MA;

    public class LocalBinder extends Binder {
        PosicionGPSService getService() {
            return PosicionGPSService.this;
        }
    }

    public PosicionGPSService(){}

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {}

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                latlong=new LatLng(location.getLatitude(),location.getLongitude());
                }

            public void onStatusChanged(String provider, int status, Bundle extras){}

            public void onProviderEnabled(String provider){}

            public void onProviderDisabled(String provider){}
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {}
        locationProvider = LocationManager.GPS_PROVIDER;
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 6000, 0.1f, locationListener);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy(){}

    public LatLng mostrar(){
        if (latlong  != null){
            return latlong;
        }
        else{
            return new LatLng(0,0);
        }
    }
}
