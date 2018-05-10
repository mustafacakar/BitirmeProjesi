package com.example.mustafa.switchtab;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;


public class KonumKontrolServisi extends Service {
    public static double LAT, LNG;
    public static boolean calisiyor;
    public static Context konumContext;
    Location hedef;
    LocationListener locationListener;
    LocationManager locationManager;

    @Override
    public void onCreate() {
        super.onCreate();
        hedef = new Location("Hedef Konum");
        hedef.setLongitude(LNG);
        hedef.setLatitude(LAT);

        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        //noinspection MissingPermission
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Toast.makeText(getApplicationContext(), (int) location.distanceTo(hedef) + "m Kaldı!", Toast.LENGTH_SHORT).show();
                if(location.distanceTo(hedef)<50 && LAT!=0 && LNG!=0 && calisiyor){
                    Intent intent = new Intent(getApplicationContext(),AlarmActivity.class);
                    intent.putExtra("baslik","Konumunuza Vardınız!");
                    intent.putExtra("icerik"," ");
                    konumContext.startActivity(intent);
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent konumAc = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                konumAc.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(konumAc);
            }
        });
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
