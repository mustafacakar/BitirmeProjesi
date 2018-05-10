package com.example.mustafa.switchtab;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by misti on 7.05.2018.
 */

public class searchAdapter extends RecyclerView.Adapter<searchAdapter.SearchViewHolder> {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference myRef;
    private String uuid;
    private FirebaseAdapter firebaseAdapter;


    Context context;
    /*ArrayList<String> fullNameList;
    ArrayList<String> kullaniciAdiList;*/
    //ArrayList<String> profilePicList;
    ArrayList<KullaniciClass> insanListesi;
    EditText search;

    class SearchViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImage;
        TextView full_name, user_name;
        Button ArkadasEkleBtn;

        public SearchViewHolder(View itemView) {
            super(itemView);
            profileImage = (ImageView) itemView.findViewById(R.id.profileImage);
            full_name = (TextView) itemView.findViewById(R.id.full_name);
            user_name = (TextView) itemView.findViewById(R.id.user_name);
            ArkadasEkleBtn = (Button) itemView.findViewById(R.id.arkadasEkleBtn);
            profileImage.setImageDrawable(itemView.getResources().getDrawable(R.drawable.icon_profile_man));
            search = (EditText) itemView.findViewById(R.id.search_edit_text);
        }
    }

    public searchAdapter(Context context, ArrayList<KullaniciClass> insanListesi) {
        this.context = context;
        /*this.fullNameList = fullNameList;
        this.kullaniciAdiList = userNameList;*/
        this.insanListesi=insanListesi;
        firebaseAdapter = new FirebaseAdapter();
        //this.profilePicList = profilePicList;
    }

    @Override
    public SearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_list_items, parent, false);
        return new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SearchViewHolder holder, final int position) {
        holder.full_name.setText(insanListesi.get(position).getAdSoyad());
        holder.user_name.setText("@"+insanListesi.get(position).getKullaniciAdi());
        //Glide.with(context).load(profilePicList.get(position)).asBitmap().placeholder(R.mipmap.ic_launcher_round).into(holder.profileImage);
        if(insanListesi.get(position).getProfilFotografi()!=null && !insanListesi.get(position).getProfilFotografi().equals("Eklenmedi")){
            Picasso.get().load(insanListesi.get(position).getProfilFotografi()).into(holder.profileImage);
        }

        holder.ArkadasEkleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Firebase Arkadaş ekle.


                /*firebaseDatabase.getInstance();
                myRef.child("KeepNoteApp").child("Kullancilar").child(FirstActivity.kullanici.getKullaniciAdi())
                        .child("ArkadasListesi").child("Arkadas_KullaniciAdi").setValue(kullaniciAdiList.get(position));*/
                FirstActivity.kullanici.arkadasEkle(insanListesi.get(position));
                firebaseAdapter.arkadasEkle(insanListesi.get(position));
                Toast.makeText(context,insanListesi.get(position).getKullaniciAdi()+" İle Arkadaş Olundu!",Toast.LENGTH_SHORT).show();
                Tab2.searchBar.setText("");
                Tab3.arkadasSayisi.setText(FirstActivity.kullanici.arkadasSayisi()+"");

            }
        });
    }

    @Override
    public int getItemCount() {
        return insanListesi.size();
    }
}

