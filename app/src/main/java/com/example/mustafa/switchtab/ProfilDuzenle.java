package com.example.mustafa.switchtab;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.soundcloud.android.crop.Crop;
import com.squareup.picasso.Picasso;

import java.io.File;

public class ProfilDuzenle extends AppCompatActivity {

    TextView adSoyad;
    ImageView profil;
    Uri secilenResimURI;
    FirebaseAdapter firebaseAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil_duzenle);

        secilenResimURI = null;
        firebaseAdapter = new FirebaseAdapter();

        adSoyad = (TextView) findViewById(R.id.profilDuzenle_etIsim);
        profil = (ImageView) findViewById(R.id.profilDuzenle_ivFotograf);

        adSoyad.setText(FirstActivity.kullanici.getAdSoyad());


        if(FirstActivity.kullanici.getProfilFotografi()!=null && !FirstActivity.kullanici.getProfilFotografi().equals("Eklenmedi")){
            Picasso.get().load(FirstActivity.kullanici.getProfilFotografi()).into(profil);
        }else{
            profil.setImageDrawable(getResources().getDrawable(R.drawable.icon_profile_man));
        }
    }

    public void ppDegistir(View v){
        if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},2);
        }else{
            Crop.pickImage(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 2){
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Crop.pickImage(this);
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == Crop.REQUEST_PICK){
                Uri kaynak = data.getData();
                Uri hedef = Uri.fromFile(new File(getCacheDir(),"cropped"));

                Crop.of(kaynak,hedef).asSquare().start(this);
                secilenResimURI = Crop.getOutput(data);
                //imageView.setImageURI(Crop.getOutput(data));
            }else if(requestCode == Crop.REQUEST_CROP){
                handle_crop(resultCode,data);
            }
        }
    }

    private void handle_crop(int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            //imageView.setImageURI(Crop.getOutput(data));
            secilenResimURI = Crop.getOutput(data);
            profil.setImageURI(secilenResimURI);
        }else if(resultCode == Crop.RESULT_ERROR){
            Toast.makeText(getApplicationContext(),"Hata",Toast.LENGTH_SHORT).show();
        }
    }

    public void duzenlemeyiBitir(View v){
        if(!adSoyad.getText().toString().equals("")){
            FirstActivity.kullanici.setAdSoyad(adSoyad.getText().toString());
            if(secilenResimURI!=null){
                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("profil/"+FirstActivity.kullanici.getKullaniciAdi()+".jpg");
                storageReference.putFile(secilenResimURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        FirstActivity.kullanici.setProfilFotografi(taskSnapshot.getDownloadUrl().toString());

                        firebaseAdapter.uyeBilgiGuncelle(adSoyad.getText().toString(),taskSnapshot.getDownloadUrl().toString());
                        geriDon();
                    }
                });
            }
            else{
                firebaseAdapter.uyeBilgiGuncelle(adSoyad.getText().toString(),FirstActivity.kullanici.getProfilFotografi());
                geriDon();
            }
        }
        else{
            Toast.makeText(this,"Bir İsim Giriniz",Toast.LENGTH_SHORT).show();
        }


    }
    private void geriDon(){
        this.finish();
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    public void duzenle(View v){
        this.finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


}
