package com.example.mustafa.switchtab;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executor;

class FirebaseAdapter {
    private FirebaseAuth mAuth; //Authentication İşlemlerini Yapan Değişken
    private FirebaseAuth.AuthStateListener mAuthListener;   // Authentication İşlemlerini Takip Eden Dinleyici
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference myRef;
    private StorageReference storageReference;
    private String uuid;
    private KullaniciClass arkadas;

    // Authentication İşlemlerini Yapacak Nesneler Oluşturuluyor.
    FirebaseAdapter(){

        uyelikIslemleri();
        databaseIslemleri();
        depolamaIslemleri();
    }

    private void uyelikIslemleri(){
        mAuth=FirebaseAuth.getInstance();
        mAuthListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

            }
        };
    }

    private void databaseIslemleri(){
        firebaseDatabase=FirebaseDatabase.getInstance();
        myRef=firebaseDatabase.getReference();
    }

    private void depolamaIslemleri(){
        storageReference = FirebaseStorage.getInstance().getReference();
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

        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener((Activity) c, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    myRef.child("KeepNoteApp").child("Kullanicilar").child(username).child("KullaniciAdi").setValue(username);
                    myRef.child("KeepNoteApp").child("Kullanicilar").child(username).child("E-Mail").setValue(email);
                    myRef.child("KeepNoteApp").child("Kullanicilar").child(username).child("Parola").setValue(password);
                    myRef.child("KeepNoteApp").child("Kullanicilar").child(username).child("Isim").setValue("İsim Girilmedi");
                    myRef.child("KeepNoteApp").child("Kullanicilar").child(username).child("Fotograf").setValue("Eklenmedi");
                    myRef.child("KeepNoteApp").child("Kullanicilar").child(username).child("Arkadas_Listesi").child("Arkadas_Sayisi").setValue(0);

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
    public void uyeBilgiGuncelle(String isim, String resim){
        myRef.child("KeepNoteApp").child("Kullanicilar").child(FirstActivity.kullanici.getKullaniciAdi()).child("Isim").setValue(isim);
        myRef.child("KeepNoteApp").child("Kullanicilar").child(FirstActivity.kullanici.getKullaniciAdi()).child("Fotograf").setValue(resim);
    }
    void  otoLoginYap(Context c){
        if(FirstActivity.autoLogin.getBoolean("misafir",false) == true){
            Intent intent = new Intent(c,MainActivity.class);
            c.startActivity(intent);
        }else if(FirstActivity.autoLogin.getBoolean("girisYapildi",false) == true){
            //LoginAnimasyon Ekranı
            Intent intent = new Intent(c,LoginAnimasyon.class);
            intent.putExtra("login",false); // ilkLogin değeri = false (Oto Login Gerçekleştiriliyor)
            c.startActivity(intent);
        }
    }

    void otoLoginBilgiKayit(String email, String password, String username){
        FirstActivity.kullanici.setAdSoyad("Girilmedi");
        FirstActivity.kullanici.setEmail(email);
        FirstActivity.kullanici.setKullaniciAdi(username);
        FirstActivity.kullanici.setSifre(password);

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
                    girisYapanKullaniciAdiDondur(email,c);

                }
            }
        }).addOnFailureListener((Activity) c, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if(e!=null){
                    Toast.makeText(c,e.getLocalizedMessage().toString(),Toast.LENGTH_LONG).show();
                    FirstActivity.otoLoginBilgiTemizle();
                    Intent intent2 = new Intent(c,Login.class);
                    c.startActivity(intent2);

                    if(FirstActivity.autoLogin.getBoolean("girisYapildi",false) == true){
                        FirstActivity.otoLoginBilgiTemizle();
                        Intent intent = new Intent(c,FirstActivity.class);
                        c.startActivity(intent);
                    }
                }
            }
        });
    }

    void girisYapanKullaniciAdiDondur(final String email, final Context c){
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

                        FirstActivity.kullanici.setSifre(hashMap.get("Parola").toString());
                        FirstActivity.kullanici.setKullaniciAdi(hashMap.get("KullaniciAdi").toString());
                        FirstActivity.kullanici.setEmail(hashMap.get("E-Mail").toString());
                        FirstActivity.kullanici.setAdSoyad(hashMap.get("Isim").toString());
                        FirstActivity.kullanici.setProfilFotografi(hashMap.get("Fotograf").toString());
                        Intent intent = new Intent(c,MainActivity.class);
                        c.startActivity(intent);
                        myRef.removeEventListener(this);
                    }
                }
                myRef.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    boolean notuUploadEt(NotClass not){
        //String anlikZaman = not.getStringEklendigiTarih();
        //int notSayisi = (0-FirstActivity.kullanici.notSayisi());
        int notSira = 0;
        if(FirstActivity.kullanici.notSayisi()>0){
            notSira = FirstActivity.kullanici.notuBul(0).getNotSira() -1;
        }
        myRef.child("KeepNoteApp").child("Notlar").child(FirstActivity.autoLogin.getString("username",null)).child(String.valueOf(notSira)).child("Not_Baslik").setValue(not.getNotBaslik());
        myRef.child("KeepNoteApp").child("Notlar").child(FirstActivity.autoLogin.getString("username",null)).child(String.valueOf(notSira)).child("Not_Icerik").setValue(not.getNotIcerik());
        myRef.child("KeepNoteApp").child("Notlar").child(FirstActivity.autoLogin.getString("username",null)).child(String.valueOf(notSira)).child("Not_Sahibi").setValue(not.getNotSahibi());
        myRef.child("KeepNoteApp").child("Notlar").child(FirstActivity.autoLogin.getString("username",null)).child(String.valueOf(notSira)).child("Not_Hedefi").setValue(not.getNotHedefi());
        myRef.child("KeepNoteApp").child("Notlar").child(FirstActivity.autoLogin.getString("username",null)).child(String.valueOf(notSira)).child("Not_Resmi").setValue(not.getNotResmi());
        myRef.child("KeepNoteApp").child("Notlar").child(FirstActivity.autoLogin.getString("username",null)).child(String.valueOf(notSira)).child("Not_Sira").setValue(notSira);
        myRef.child("KeepNoteApp").child("Notlar").child(FirstActivity.autoLogin.getString("username",null)).child(String.valueOf(notSira)).child("Not_Tarih_Yil").setValue(not.getYil());
        myRef.child("KeepNoteApp").child("Notlar").child(FirstActivity.autoLogin.getString("username",null)).child(String.valueOf(notSira)).child("Not_Tarih_Ay").setValue(not.getAy());
        myRef.child("KeepNoteApp").child("Notlar").child(FirstActivity.autoLogin.getString("username",null)).child(String.valueOf(notSira)).child("Not_Tarih_Gun").setValue(not.getGun());
        myRef.child("KeepNoteApp").child("Notlar").child(FirstActivity.autoLogin.getString("username",null)).child(String.valueOf(notSira)).child("Not_Tarih_Saat").setValue(not.getSaat());
        myRef.child("KeepNoteApp").child("Notlar").child(FirstActivity.autoLogin.getString("username",null)).child(String.valueOf(notSira)).child("Not_Tarih_Dakika").setValue(not.getDakika());
        myRef.child("KeepNoteApp").child("Notlar").child(FirstActivity.autoLogin.getString("username",null)).child(String.valueOf(notSira)).child("Not_Konum_Lat").setValue(not.getAdresKoordinat().latitude);
        myRef.child("KeepNoteApp").child("Notlar").child(FirstActivity.autoLogin.getString("username",null)).child(String.valueOf(notSira)).child("Not_Konum_Lng").setValue(not.getAdresKoordinat().longitude);
        return true;
    }

    void notlarıDownloadEt(final Context c, final ListView listView, final NotGosterAdapter notAdapter){
        firebaseDatabase= FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference("KeepNoteApp/Notlar/"+FirstActivity.kullanici.getKullaniciAdi());
        FirstActivity.kullanici.notlariTemizle();

        final ArrayList<String> baslik = new ArrayList<String>();
        final ArrayList<String> icerik = new ArrayList<String>();
        myRef.orderByChild("Not_Sira").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                /*System.out.println("----------->      " +dataSnapshot.getValue().toString());
                System.out.println("----------->      " +dataSnapshot.getRef().toString());*/
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    Map<String,Object> hashMapNotlar = (HashMap<String, Object>) ds.getValue();
                    final NotClass indirilenNot = new NotClass();

                    baslik.add(hashMapNotlar.get("Not_Baslik").toString());
                    icerik.add(hashMapNotlar.get("Not_Icerik").toString());
                    indirilenNot.setNotBaslik(hashMapNotlar.get("Not_Baslik").toString());
                    indirilenNot.setNotIcerik(hashMapNotlar.get("Not_Icerik").toString());
                    indirilenNot.setNotSahibi(hashMapNotlar.get("Not_Sahibi").toString());
                    indirilenNot.setNotHedefi(hashMapNotlar.get("Not_Hedefi").toString());
                    indirilenNot.setNotResmi(hashMapNotlar.get("Not_Resmi").toString());

                    indirilenNot.setNotSira(Integer.valueOf(hashMapNotlar.get("Not_Sira").toString()));
                    indirilenNot.setYil(Integer.valueOf(hashMapNotlar.get("Not_Tarih_Yil").toString()));
                    indirilenNot.setAy(Integer.valueOf(hashMapNotlar.get("Not_Tarih_Ay").toString()));
                    indirilenNot.setGun(Integer.valueOf(hashMapNotlar.get("Not_Tarih_Gun").toString()));
                    indirilenNot.setSaat(Integer.valueOf(hashMapNotlar.get("Not_Tarih_Saat").toString()));
                    indirilenNot.setDakika(Integer.valueOf(hashMapNotlar.get("Not_Tarih_Dakika").toString()));

                    indirilenNot.setAdresKoordinat(Double.valueOf(hashMapNotlar.get("Not_Konum_Lat").toString()),Double.valueOf(hashMapNotlar.get("Not_Konum_Lng").toString()));

                    FirstActivity.kullanici.notEkle(indirilenNot);
                    listView.setAdapter(notAdapter);
                    // Bu Kod Yazılmazsa, devamlı çekmeye çalışıyor. Ekleme İşlemi yaptığımızda da
                    // database okuma işlemiyle meşgul olduğu için, 2.kere çekme işlemi başlatamıyor anladığım kadarıyla

                }
                myRef.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    void notuDuzenle(NotClass not){
        myRef.child("KeepNoteApp").child("Notlar").child(FirstActivity.autoLogin.getString("username",null)).child(String.valueOf(not.getNotSira())).child("Not_Baslik").setValue(not.getNotBaslik());
        myRef.child("KeepNoteApp").child("Notlar").child(FirstActivity.autoLogin.getString("username",null)).child(String.valueOf(not.getNotSira())).child("Not_Icerik").setValue(not.getNotIcerik());
        myRef.child("KeepNoteApp").child("Notlar").child(FirstActivity.autoLogin.getString("username",null)).child(String.valueOf(not.getNotSira())).child("Not_Sahibi").setValue(not.getNotSahibi());
        myRef.child("KeepNoteApp").child("Notlar").child(FirstActivity.autoLogin.getString("username",null)).child(String.valueOf(not.getNotSira())).child("Not_Hedefi").setValue(not.getNotHedefi());
        myRef.child("KeepNoteApp").child("Notlar").child(FirstActivity.autoLogin.getString("username",null)).child(String.valueOf(not.getNotSira())).child("Not_Resmi").setValue(not.getNotResmi());
        myRef.child("KeepNoteApp").child("Notlar").child(FirstActivity.autoLogin.getString("username",null)).child(String.valueOf(not.getNotSira())).child("Not_Sira").setValue(not.getNotSira());
        myRef.child("KeepNoteApp").child("Notlar").child(FirstActivity.autoLogin.getString("username",null)).child(String.valueOf(not.getNotSira())).child("Not_Tarih_Yil").setValue(not.getYil());
        myRef.child("KeepNoteApp").child("Notlar").child(FirstActivity.autoLogin.getString("username",null)).child(String.valueOf(not.getNotSira())).child("Not_Tarih_Ay").setValue(not.getAy());
        myRef.child("KeepNoteApp").child("Notlar").child(FirstActivity.autoLogin.getString("username",null)).child(String.valueOf(not.getNotSira())).child("Not_Tarih_Gun").setValue(not.getGun());
        myRef.child("KeepNoteApp").child("Notlar").child(FirstActivity.autoLogin.getString("username",null)).child(String.valueOf(not.getNotSira())).child("Not_Tarih_Saat").setValue(not.getSaat());
        myRef.child("KeepNoteApp").child("Notlar").child(FirstActivity.autoLogin.getString("username",null)).child(String.valueOf(not.getNotSira())).child("Not_Tarih_Dakika").setValue(not.getDakika());
        myRef.child("KeepNoteApp").child("Notlar").child(FirstActivity.autoLogin.getString("username",null)).child(String.valueOf(not.getNotSira())).child("Not_Konum_Lat").setValue(not.getAdresKoordinat().latitude);
        myRef.child("KeepNoteApp").child("Notlar").child(FirstActivity.autoLogin.getString("username",null)).child(String.valueOf(not.getNotSira())).child("Not_Konum_Lng").setValue(not.getAdresKoordinat().longitude);

    }

    void notuSil(NotClass not){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        final Query silinecekNot = ref.child("KeepNoteApp/Notlar/"+FirstActivity.autoLogin.getString("username",null).toString()).orderByChild("Not_Sira").equalTo(not.getNotSira());

        silinecekNot.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot notSnapshot: dataSnapshot.getChildren()) {
                    notSnapshot.getRef().removeValue();
                }
                silinecekNot.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    void resimUploadEt(Uri uri, final Context context, final NotClass not){
        uuid = UUID.randomUUID().toString();
        StorageReference storage = storageReference.child("images/"+uuid+".jpg");
        storage.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                not.setNotResmi(taskSnapshot.getDownloadUrl().toString());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context,"Resim Yüklenirken Hata Oluştu!",Toast.LENGTH_SHORT).show();
            }
        });
    }
    void arkadasEkle(KullaniciClass arkadas){
        String uuid;
        uuid =  UUID.randomUUID().toString();
        myRef.child("KeepNoteApp").child("Kullanicilar").child(FirstActivity.kullanici.getKullaniciAdi()).child("Arkadas_Listesi").child(arkadas.getKullaniciAdi()).child("Arkadas_KullaniciAdi").setValue(arkadas.getKullaniciAdi());
        myRef.child("KeepNoteApp").child("Kullanicilar").child(FirstActivity.kullanici.getKullaniciAdi()).child("Arkadas_Listesi").child(arkadas.getKullaniciAdi()).child("Arkadas_Isim").setValue(arkadas.getAdSoyad());
        myRef.child("KeepNoteApp").child("Kullanicilar").child(FirstActivity.kullanici.getKullaniciAdi()).child("Arkadas_Listesi").child(arkadas.getKullaniciAdi()).child("Arkadas_Fotograf").setValue(arkadas.getProfilFotografi());
        myRef.child("KeepNoteApp").child("Kullanicilar").child(FirstActivity.kullanici.getKullaniciAdi()).child("Arkadas_Listesi").child("Arkadas_Sayisi").setValue(FirstActivity.kullanici.arkadasSayisi());
    }
    private int kontrol;
    void arkadaslariCek(final Context c,final ListView arkadasListesi, final ArkadasGosterAdapter arkadasGosterAdapter){
        FirstActivity.kullanici.arkadaslariTemizle();
        kontrol=0;
        firebaseDatabase= FirebaseDatabase.getInstance();
        myRef=firebaseDatabase.getReference("KeepNoteApp/Kullanicilar/"+FirstActivity.kullanici.getKullaniciAdi()+"/Arkadas_Listesi");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot :dataSnapshot.getChildren()){
                    if(dataSnapshot.child("Arkadas_Sayisi").getValue(Integer.class)!=0 && kontrol>0){
                        arkadas=new KullaniciClass();
                        arkadas.setProfilFotografi(snapshot.child("Arkadas_Fotograf").getValue(String.class));
                        arkadas.setKullaniciAdi(snapshot.child("Arkadas_KullaniciAdi").getValue(String.class));
                        arkadas.setAdSoyad(snapshot.child("Arkadas_Isim").getValue(String.class));
                        FirstActivity.kullanici.arkadasEkle(arkadas);
                        arkadasListesi.setAdapter(arkadasGosterAdapter);
                    }
                    kontrol++;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    void arkadasSil(final KullaniciClass arkadas){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        final Query silinecekArkadas = ref.child("KeepNoteApp/Kullanicilar/"+FirstActivity.kullanici.getKullaniciAdi()+"/Arkadas_Listesi").orderByChild("Arkadas_KullaniciAdi").equalTo(arkadas.getKullaniciAdi());
        silinecekArkadas.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot arkadasSnapshot: dataSnapshot.getChildren()) {
                    arkadasSnapshot.getRef().removeValue();
                    myRef.child("Arkadas_Sayisi").setValue(FirstActivity.kullanici.arkadasSayisi());
                }
                silinecekArkadas.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
