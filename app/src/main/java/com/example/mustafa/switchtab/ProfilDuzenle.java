package com.example.mustafa.switchtab;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class ProfilDuzenle extends AppCompatActivity {

    TextView adSoyad, email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil_duzenle);

        adSoyad = (TextView) findViewById(R.id.profilDuzenle_etIsim);
        email = (TextView) findViewById(R.id.profilDuzenle_etEmail);

        adSoyad.setText(FirstActivity.kullanici.getAdSoyad());
        email.setText(FirstActivity.kullanici.getEmail());
    }

    public void ppDegistir(View v){
        Toast.makeText(this,"ImageView'a Tıkladın",Toast.LENGTH_SHORT).show();
    }

    public void duzenlemeyiBitir(View v){
        this.finish();
    }

    public void duzenle(View v){
        this.finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.finish();
    }

}
