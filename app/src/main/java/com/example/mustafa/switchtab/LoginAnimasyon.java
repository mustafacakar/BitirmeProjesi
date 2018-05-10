package com.example.mustafa.switchtab;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;

import java.util.Timer;
import java.util.TimerTask;

public class LoginAnimasyon extends AppCompatActivity {

    FirebaseAdapter firebaseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_animasyon);


        firebaseAdapter=new FirebaseAdapter();

        if(getIntent().getExtras().getBoolean("login")){
            firebaseAdapter.uyeGirisYap(getIntent().getExtras().getString("email"),getIntent().getExtras().getString("password"),this);
        }else{

            //firebaseAdapter.girisYapanKullaniciAdiDondur(FirstActivity.autoLogin.getString("email",null).toString());
            firebaseAdapter.uyeGirisYap(FirstActivity.autoLogin.getString("email",null).toString(),FirstActivity.autoLogin.getString("password",null).toString(),this);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAdapter.authStateListenerEkle();
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseAdapter.authStateListenerTemizle();
        this.finish();
    }
}
