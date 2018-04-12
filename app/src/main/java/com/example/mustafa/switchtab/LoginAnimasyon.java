package com.example.mustafa.switchtab;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class LoginAnimasyon extends AppCompatActivity {

    FirebaseAdapter firebaseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_animasyon);

        firebaseAdapter=new FirebaseAdapter();
        firebaseAdapter.uyeGirisYap(FirstActivity.autoLogin.getString("email",null).toString(),FirstActivity.autoLogin.getString("password",null).toString(),this);
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
