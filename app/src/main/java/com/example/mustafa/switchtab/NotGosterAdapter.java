package com.example.mustafa.switchtab;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by Tolga on 12.04.2018.
 */

public class NotGosterAdapter extends BaseAdapter {

    Context context;
    LayoutInflater layoutInflater = null;


    ArrayList<String> basliklar = new ArrayList<String>();
    ArrayList<String> icerikler = new ArrayList<String>();
    /*String basliklar[];
    String icerikler[];*/

    public NotGosterAdapter(Context context,  ArrayList<String> basliklar, ArrayList<String> icerikler){
        this.context = context;
        this.basliklar = basliklar;
        this.icerikler = icerikler;
    }

    @Override
    public int getCount() {
        return basliklar.size();
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

        baslik.setText(basliklar.get(position));
        icerik.setText(icerikler.get(position));

        return view;
    }
}
