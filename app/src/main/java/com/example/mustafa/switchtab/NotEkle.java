package com.example.mustafa.switchtab;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class NotEkle extends AppCompatActivity {

    EditText baslik,icerik;
    FirebaseAdapter firebaseAdapter;
    boolean basariliKayit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_not_ekle);

        firebaseAdapter = new FirebaseAdapter();

        baslik= (EditText) findViewById(R.id.notEkle_etBaslik);
        icerik = (EditText) findViewById(R.id.notEkle_etIcerik);
    }

    public void olustur(View v){
        if(!baslik.getText().toString().equals("") && !icerik.getText().toString().equals("")){
            if (firebaseAdapter.notuUploadEt(baslik.getText().toString(),icerik.getText().toString(),this)){
                //basariliKayit=true;
                this.finish();
            }
        }
        else{
            Toast.makeText(getApplicationContext(),"Başlık veya İçerik Doldurulmamış",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        /*if(basariliKayit){
            this.finish();
        }*/
    }
}
