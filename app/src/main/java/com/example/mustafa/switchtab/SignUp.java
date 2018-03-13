package com.example.mustafa.switchtab;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class SignUp extends AppCompatActivity {

    EditText email,username,password;
    FirebaseAdapter firebaseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        email = (EditText) findViewById(R.id.signUp_etEmail);
        username = (EditText) findViewById(R.id.signUp_etUsername);
        password = (EditText) findViewById(R.id.signUp_etPassword);

        firebaseAdapter=new FirebaseAdapter();
    }

    public void signUp(View v){
        if(!email.getText().toString().equals("") && !username.getText().toString().equals("") && !password.getText().toString().equals("")){
            firebaseAdapter.uyeKayit(email.getText().toString(),password.getText().toString(),username.getText().toString(),this);
        }
        else{
            Toast.makeText(getApplicationContext(),"Lütfen Bilgileri Eksiksiz Girin", Toast.LENGTH_LONG).show();
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this,FirstActivity.class);
        startActivity(intent);
    }
}
