package com.example.mustafa.switchtab;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class FirstActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
    }
    public void toSignUp(View v){
        Intent intent = new Intent(this,SignUp.class);
        startActivity(intent);
    }
    public void toLogin(View v){
        Intent intent= new Intent(this, Login.class);
        startActivity(intent);
    }
}
