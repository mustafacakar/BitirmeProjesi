package com.example.mustafa.switchtab;

import android.content.Context;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.example.mustafa.switchtab.R.id.text;
import static com.example.mustafa.switchtab.R.id.textView;

/**
 * Created by Tolga on 12.04.2018.
 */

public class NotGosterAdapter extends BaseAdapter {

    Context context;
    LayoutInflater layoutInflater = null;
    Geocoder adresBilgisi;
    List<Address> adresSorgu;
    LatLng latLng;


    /*ArrayList<String> basliklar = new ArrayList<String>();
    ArrayList<String> icerikler = new ArrayList<String>();
    String basliklar[];
    String icerikler[];*/

    public NotGosterAdapter(Context context){
        this.context = context;
        /*this.basliklar = basliklar;
        this.icerikler = icerikler;*/
    }

    @Override
    public int getCount() {
        return FirstActivity.kullanici.notSayisi();
    }

    @Override
    public Object getItem(int position) {
        return getItemId(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if(view == null){
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.not_gorunum,null);
        }

        TextView baslik = (TextView) view.findViewById(R.id.notGorunum_tvNotBaslik);
        TextView icerik = (TextView) view.findViewById(R.id.notGorunum_tvNotIcerik);
        TextView tarih = (TextView) view.findViewById(R.id.notGorunum_tvTakvim);
        TextView saat = (TextView) view.findViewById(R.id.notGorunum_tvSaat);
        TextView konum = (TextView) view.findViewById(R.id.notGorunum_tvKonum);
        baslik.setTypeface(null, Typeface.BOLD);

        String tarihString = FirstActivity.kullanici.notuBul(position).getGun()+"."+(FirstActivity.kullanici.notuBul(position).getAy()+1)+"."+FirstActivity.kullanici.notuBul(position).getYil();
        String saatString = FirstActivity.kullanici.notuBul(position).getSaat()+":"+FirstActivity.kullanici.notuBul(position).getDakika();
        String konumString = "Bulunamadı";
        latLng=FirstActivity.kullanici.notuBul(position).getAdresKoordinat();
        adresBilgisi = new Geocoder(context.getApplicationContext(), Locale.getDefault());
        try {
            adresSorgu = adresBilgisi.getFromLocation(latLng.latitude,latLng.longitude,1);
            konumString = adresSorgu.get(0).getSubAdminArea()+","+adresSorgu.get(0).getAdminArea();
        } catch (IOException e) {
            e.printStackTrace();
        }

        baslik.setText(FirstActivity.kullanici.notuBul(position).getNotBaslik());
        icerik.setText(FirstActivity.kullanici.notuBul(position).getNotIcerik());
        tarih.setText(tarihString);
        saat.setText(saatString);
        konum.setText(konumString);


        return view;
    }
}
