package com.example.mustafa.switchtab;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class KonumActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    private MapView mapView;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private LatLng anlikKonum,sonKoordinat, isaretliKoordinat;
    private Location sonKonum;
    private Geocoder adresBilgisi;
    private String isaretliAdresBilgisi;
    private List<Address> adresSorgu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_konum);

        anlikKonum=null;
        adresBilgisi=null;
        isaretliKoordinat=null;

        mapView = (MapView) findViewById(R.id.konum_mapView);

        if(mapView!=null){
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getApplicationContext());
        mMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                mMap.clear();

                if(NotEkle.eklenecekNot.getAdresKoordinat()!=null){
                    notKonumunuAl();
                    //Toast.makeText(getApplicationContext(),"aa",Toast.LENGTH_SHORT).show();
                }
                //Toast.makeText(getApplicationContext(),"bb",Toast.LENGTH_SHORT).show();
                anlikKonum = new LatLng(location.getLatitude(),location.getLongitude());
                mMap.addMarker(new MarkerOptions()
                        .position(anlikKonum)
                        .title("Buradasınız")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                isaretliKonumGoster();
                //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(anlikKonum,16));
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
        };

        mMap.setOnMapLongClickListener(this);

        // İzin Verilmemişse, İzin İstiyor
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION},1);
        }
        else{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,1,locationListener);
            sonKonum = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(sonKonum!=null){
                sonKoordinat = new LatLng(sonKonum.getLatitude(),sonKonum.getLongitude());
                mMap.addMarker(new MarkerOptions()
                        .position(sonKoordinat)
                        .title("Buradasınız")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sonKoordinat,16));

                if(NotEkle.eklenecekNot.getAdresKoordinat()!=null){
                    notKonumunuAl();
                    //Toast.makeText(getApplicationContext(),"cc",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void konumunaGit(View v){
        konumuGoster();
    }

    public void haritaSifirla(View v){
        haritayıYenile();
        konumuGoster();
    }

    public void hedefeGit(View v){
        hedefiGoster();
    }

    public void konumuKaydet(View v){
        this.finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(grantResults.length>0){
            if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,1,locationListener);
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        haritayıYenile();
        adresBilgisi= new Geocoder(getApplicationContext(),Locale.getDefault());
        isaretliAdresBilgisi="";
        isaretliKoordinat=latLng;

        try {
            adresSorgu = adresBilgisi.getFromLocation(isaretliKoordinat.latitude,isaretliKoordinat.longitude,1);
            if(adresSorgu!=null && adresSorgu.size()>0){
                isaretliAdresBilgisi= adresSorgu.get(0).getAddressLine(0);
                NotEkle.eklenecekNot.setAdresKoordinat(isaretliKoordinat);
                NotEkle.eklenecekNot.setAdresBilgisi(adresSorgu);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMap.addMarker(new MarkerOptions().position(isaretliKoordinat).title(isaretliAdresBilgisi));
    }

    private void haritayıYenile(){
        mMap.clear();
        if(anlikKonum != null){
            mMap.addMarker(new MarkerOptions()
                    .position(anlikKonum)
                    .title("Buradasınız")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
            //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(anlikKonum,16));
        }
        else {
            mMap.addMarker(new MarkerOptions()
                    .position(sonKoordinat)
                    .title("Buradasınız")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
            //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sonKoordinat,16));
        }
        isaretliKoordinat=null;
        NotEkle.eklenecekNot.setAdresKoordinat(new LatLng(0,0));
    }
    private void konumuGoster(){
        if(anlikKonum != null){
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(anlikKonum,16));
        }
        else if (sonKoordinat!=null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sonKoordinat,16));
        }
        else{
            Toast.makeText(getApplicationContext(),"Konum Verinize Ulaşamıyoruz",Toast.LENGTH_SHORT).show();
        }
    }
    private void isaretliKonumGoster(){
        if(isaretliKoordinat!=null){
            mMap.addMarker(new MarkerOptions().position(isaretliKoordinat).title(isaretliAdresBilgisi));
        }
    }
    private void hedefiGoster(){
        if(isaretliKoordinat!=null && isaretliKoordinat.latitude!=0 && isaretliKoordinat.longitude!=0){
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(isaretliKoordinat,16));
        }
        else{
            Toast.makeText(getApplicationContext(),"Bir Hedef Belirtmediniz",Toast.LENGTH_SHORT).show();
        }
    }
    private void notKonumunuAl(){
        //haritayıYenile();
        adresBilgisi= new Geocoder(getApplicationContext(),Locale.getDefault());
        isaretliAdresBilgisi="";
        isaretliKoordinat=NotEkle.eklenecekNot.getAdresKoordinat();

        try {
            adresSorgu = adresBilgisi.getFromLocation(isaretliKoordinat.latitude,isaretliKoordinat.longitude,1);
            if(adresSorgu!=null && adresSorgu.size()>0){
                isaretliAdresBilgisi= adresSorgu.get(0).getAddressLine(0);
                /*NotEkle.eklenecekNot.setAdresKoordinat(isaretliKoordinat);
                NotEkle.eklenecekNot.setAdresBilgisi(adresSorgu);*/
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMap.addMarker(new MarkerOptions().position(isaretliKoordinat).title(isaretliAdresBilgisi));
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.finish();
    }
}
