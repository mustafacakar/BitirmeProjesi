package com.example.mustafa.switchtab;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

class FirebaseAdapter {
    private FirebaseAuth mAuth; //Authentication İşlemlerini Yapan Değişken
    private FirebaseAuth.AuthStateListener mAuthListener;   // Authentication İşlemlerini Takip Eden Dinleyici
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference myRef;
    private String uuid;

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
    void uyeKayit(final String email, final String password, final String username, final Context c){
        firebaseDatabase=FirebaseDatabase.getInstance();
        myRef=firebaseDatabase.getReference();


        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener((Activity) c, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    myRef.child("KeepNoteApp").child("Kullanicilar").child(username).child("KullaniciAdi").setValue(username);
                    myRef.child("KeepNoteApp").child("Kullanicilar").child(username).child("E-Mail").setValue(email);
                    myRef.child("KeepNoteApp").child("Kullanicilar").child(username).child("Parola").setValue(password);

                    otoLoginBilgiKayit(email,password,username);

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

    public  void  otoLoginYap(Context c){
        if(FirstActivity.autoLogin.getBoolean("misafir",false) == true){
            Intent intent = new Intent(c,MainActivity.class);
            c.startActivity(intent);
        }else if(FirstActivity.autoLogin.getBoolean("girisYapildi",false) == true){
            //LoginAnimasyon Ekranı
            Intent intent = new Intent(c,LoginAnimasyon.class);
            c.startActivity(intent);
        }
    }

    void otoLoginBilgiKayit(String email, String password, String username){

        FirstActivity.autoLogin.edit().putString("email",email).apply();
        FirstActivity.autoLogin.edit().putString("password",password).apply();
        FirstActivity.autoLogin.edit().putString("username",username).apply();
        FirstActivity.autoLogin.edit().putBoolean("girisYapildi",true).apply();
        FirstActivity.autoLogin.edit().putBoolean("misafir",false).apply();
    }
    void otoLoginBilgiTemizle(){

        FirstActivity.autoLogin.edit().remove("email").apply();
        FirstActivity.autoLogin.edit().remove("password").apply();
        FirstActivity.autoLogin.edit().remove("username").apply();
        FirstActivity.autoLogin.edit().remove("girisYapildi").apply();
        FirstActivity.autoLogin.edit().remove("misafir").apply();
    }

    // Üye Programa Girişini Yapan Method
    void uyeGirisYap(final String email, final String password, final Context c){
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener((Activity) c, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    if(FirstActivity.autoLogin.getBoolean("girisYapildi",false) != true){
                        girisYapanKullaniciAdiDondur(email);
                    }

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

    void girisYapanKullaniciAdiDondur(final String email){
        firebaseDatabase= FirebaseDatabase.getInstance();
        myRef=firebaseDatabase.getReference("KeepNoteApp/Kullanicilar");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    HashMap<String,String> hashMap = (HashMap<String, String>) ds.getValue();

                    if(email.equals(hashMap.get("E-Mail").toString())){
                        FirstActivity.autoLogin.edit().putString("email",hashMap.get("E-Mail").toString()).apply();
                        FirstActivity.autoLogin.edit().putString("password",hashMap.get("Parola").toString()).apply();
                        FirstActivity.autoLogin.edit().putString("username",hashMap.get("KullaniciAdi").toString()).apply();
                        FirstActivity.autoLogin.edit().putBoolean("girisYapildi",true).apply();
                        FirstActivity.autoLogin.edit().putBoolean("misafir",false).apply();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
