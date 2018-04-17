package com.example.mustafa.switchtab;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class FirstActivity extends AppCompatActivity {

    public static SharedPreferences autoLogin;
    /*
    * Key: girisYapildi     | otoLogin Aktif/Pasif Durumu (true "Aktif")
    * Key: email            | otoLogin'de email'i Saklar
    * Key: password         | otoLogin'de password'u Saklar
    * Key: username         | otoLogin'de username'i Saklar
    * Key: misafir          | Misafir Girişi Yapıldı/Yapılmadı (true "Yapıldı")
    * */


    public static KullaniciClass kullanici;
    FirebaseAdapter login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        autoLogin=this.getSharedPreferences("com.example.mustafa.switchtab", Context.MODE_PRIVATE);
        kullanici=new KullaniciClass();

        login=new FirebaseAdapter();
        login.otoLoginYap(this);
    }
    public void toSignUp(View v){
        Intent intent = new Intent(this,SignUp.class);
        startActivity(intent);
    }
    public void toLogin(View v){
        Intent intent= new Intent(this, Login.class);
        startActivity(intent);
    }

    public static void otoLoginBilgiTemizle() {
        autoLogin.edit().remove("email").apply();
        autoLogin.edit().remove("password").apply();
        autoLogin.edit().remove("username").apply();
        autoLogin.edit().remove("girisYapildi").apply();
        autoLogin.edit().remove("misafir").apply();
    }

    @Override
    protected void onStart() {
        super.onStart();
        login.authStateListenerEkle();
    }

    @Override
    protected void onStop() {
        super.onStop();
        login.authStateListenerTemizle();
        this.finish();
    }
}
