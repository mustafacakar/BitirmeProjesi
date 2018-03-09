package com.example.mustafa.switchtab;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

class FirebaseAdapter {
    private FirebaseAuth mAuth; //Authentication İşlemlerini Yapan Değişken
    private FirebaseAuth.AuthStateListener mAuthListener;   // Authentication İşlemlerini Takip Eden Dinleyici

    // Authentication İşlemlerini Yapacak Nesneler Oluşturuluyor.
    FirebaseAdapter(){
        mAuth=FirebaseAuth.getInstance();
        mAuthListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

            }
        };
    }

    // Kullanılacak Activity'de OnStart Methodu @Override Edilip İçine Yazılacak
    public void authStateListenerEkle(){
        mAuth.addAuthStateListener(mAuthListener);
    }

    // Kullanılacak Activity'de OnStop Methodu @Override Edilip İçine Yazılacak
    public void authStateListenerTemizle(){
        if(mAuthListener!=null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    // Üye Kaydı Yapan Method
    void uyeKayit(String email, String password, final Context c){
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener((Activity) c, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Intent intent = new Intent(c,MainActivity.class);
                    c.startActivity(intent);
                }
            }
        }).addOnFailureListener((Activity) c, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if(e!=null){
                    Toast.makeText(c,e.getLocalizedMessage().toString(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Üye Programa Girişini Yapan Method
    void uyeGirisYap(String email, String password, final Context c){
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener((Activity) c, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Intent intent = new Intent(c,MainActivity.class);
                    c.startActivity(intent);
                }
            }
        }).addOnFailureListener((Activity) c, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if(e!=null){
                    Toast.makeText(c,e.getLocalizedMessage().toString(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
