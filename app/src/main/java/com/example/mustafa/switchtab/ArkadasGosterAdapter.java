package com.example.mustafa.switchtab;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;



public class ArkadasGosterAdapter extends BaseAdapter {

    Context context;
    LayoutInflater layoutInflater = null;


    public ArkadasGosterAdapter(Context context){
        this.context = context;
    }

    @Override
    public int getCount() {
        return FirstActivity.kullanici.arkadasSayisi();
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
            view = layoutInflater.inflate(R.layout.arkadas_liste_gorunum,null);
        }

        TextView isim = (TextView) view.findViewById(R.id.kisiler_tvAdSoyad);
        TextView kullaniciAdi = (TextView) view.findViewById(R.id.kisiler_tvKullaniciAdi);
        ImageView profil = (ImageView) view.findViewById(R.id.kisiler_ivProfil);
        isim.setTypeface(null, Typeface.BOLD);


        isim.setText(FirstActivity.kullanici.arkadasiBul(position).getAdSoyad());
        kullaniciAdi.setText("@"+FirstActivity.kullanici.arkadasiBul(position).getKullaniciAdi());

        if(position%2==0){
            profil.setImageDrawable(view.getResources().getDrawable(R.drawable.profil_pic1));
        }else{
            profil.setImageDrawable(view.getResources().getDrawable(R.drawable.profil_pic2));
        }


        return view;
    }
}
