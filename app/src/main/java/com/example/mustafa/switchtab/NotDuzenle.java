package com.example.mustafa.switchtab;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class NotDuzenle extends AppCompatActivity {
    private int notIndex;
    private EditText baslik,icerik;
    private FirebaseAdapter firebaseAdapter;
    private Uri secilenResimURI;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_not_duzenle);

        secilenResimURI = null;
        firebaseAdapter = new FirebaseAdapter();
        notIndex=getIntent().getExtras().getInt("NotSira");

        baslik = (EditText) findViewById(R.id.notDuzenle_etNotBaslik);
        icerik = (EditText) findViewById(R.id.notDuzenle_etNotIcerik);

        baslik.setText(FirstActivity.kullanici.notuBul(notIndex).getNotBaslik());
        icerik.setText(FirstActivity.kullanici.notuBul(notIndex).getNotIcerik());
    }

    private void notOlustur(){
        FirstActivity.kullanici.notuBul(notIndex).setNotBaslik(baslik.getText().toString());
        FirstActivity.kullanici.notuBul(notIndex).setNotIcerik(icerik.getText().toString());
        FirstActivity.kullanici.notuBul(notIndex).setNotSahibi(FirstActivity.kullanici.getKullaniciAdi());
        FirstActivity.kullanici.notuBul(notIndex).setNotHedefi(FirstActivity.kullanici.getKullaniciAdi());
    }

    private void anaSayfayaDon(){
        this.finish();
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    public void duzenle(View v){
        if(!baslik.getText().toString().equals("") && !icerik.getText().toString().equals("")){
            if(secilenResimURI!=null){
                String uuid = UUID.randomUUID().toString();
                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("images/"+uuid+".jpg");
                storageReference.putFile(secilenResimURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        FirstActivity.kullanici.notuBul(notIndex).setNotResmi(taskSnapshot.getDownloadUrl().toString());
                        notOlustur();
                        firebaseAdapter.notuDuzenle(FirstActivity.kullanici.notlariAl().get(notIndex));
                        anaSayfayaDon();
                    }
                });
            }else{
                notOlustur();
                firebaseAdapter.notuDuzenle(FirstActivity.kullanici.notlariAl().get(notIndex));
                this.finish();
                Intent intent = new Intent(this,MainActivity.class);
                startActivity(intent);
            }
        }
        else{
            Toast.makeText(this,"Başlık ve İçerik Değerlerini Doldurunuz",Toast.LENGTH_SHORT).show();
        }
    }

    public void takvimKur(View v){
        final int saat = FirstActivity.kullanici.notuBul(notIndex).getSaat();
        final int dakika = FirstActivity.kullanici.notuBul(notIndex).getDakika();
        final TimePickerDialog timePicker; //Time Picker referansımızı oluşturduk


        //TimePicker objemizi oluşturuyor ve click listener ekliyoruz
        timePicker = new TimePickerDialog(this, R.style.Theme_AppCompat_DayNight_Dialog, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                FirstActivity.kullanici.notuBul(notIndex).setSaat(selectedHour);
                FirstActivity.kullanici.notuBul(notIndex).setDakika(selectedMinute);

                if(saat>FirstActivity.kullanici.notuBul(notIndex).getSaat())
                {
                    FirstActivity.kullanici.notuBul(notIndex).setGun((FirstActivity.kullanici.notuBul(notIndex).getGun()+1));
                    if(FirstActivity.kullanici.notuBul(notIndex).getGun()==30)
                    {
                        FirstActivity.kullanici.notuBul(notIndex).setYil((FirstActivity.kullanici.notuBul(notIndex).getYil()+1));
                    }
                }
                else if(saat==FirstActivity.kullanici.notuBul(notIndex).getSaat())
                {
                    if(dakika>FirstActivity.kullanici.notuBul(notIndex).getDakika())
                    {
                        FirstActivity.kullanici.notuBul(notIndex).setGun((FirstActivity.kullanici.notuBul(notIndex).getGun()+1));
                        if(FirstActivity.kullanici.notuBul(notIndex).getGun()==30)
                        {
                            FirstActivity.kullanici.notuBul(notIndex).setYil((FirstActivity.kullanici.notuBul(notIndex).getYil()+1));
                        }
                    }
                }

                Toast.makeText(getApplicationContext(),"Ayarlanan Tarih "+FirstActivity.kullanici.notuBul(notIndex).getTakvim().get(Calendar.DAY_OF_MONTH)+"."+FirstActivity.kullanici.notuBul(notIndex).getTakvim().get(Calendar.MONTH)+"."+FirstActivity.kullanici.notuBul(notIndex).getTakvim().get(Calendar.YEAR)+" Saat:"+FirstActivity.kullanici.notuBul(notIndex).getTakvim().get(Calendar.HOUR_OF_DAY)+":"+FirstActivity.kullanici.notuBul(notIndex).getTakvim().get(Calendar.MINUTE),Toast.LENGTH_LONG).show();
            }
        }, saat, dakika, true);//true 24 saatli sistem için
        //timePicker.setTitle("Saat Seçin");
        timePicker.setButton(DatePickerDialog.BUTTON_POSITIVE, "Ayarla", timePicker);
        timePicker.setButton(DatePickerDialog.BUTTON_NEGATIVE, "Vazgeç", timePicker);


        int yil = FirstActivity.kullanici.notuBul(notIndex).getYil();
        int ay = FirstActivity.kullanici.notuBul(notIndex).getAy();
        int gun = FirstActivity.kullanici.notuBul(notIndex).getGun();

        DatePickerDialog datePicker;//Datepicker objemiz
        datePicker = new DatePickerDialog(this, R.style.Theme_AppCompat_DayNight_Dialog, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                FirstActivity.kullanici.notuBul(notIndex).setYil(year);
                FirstActivity.kullanici.notuBul(notIndex).setAy(monthOfYear);
                FirstActivity.kullanici.notuBul(notIndex).setGun(dayOfMonth);
                timePicker.show();
            }
        },yil,ay,gun);//başlarken set edilcek değerlerimizi atıyoruz
        //datePicker.setTitle("Tarih Seçiniz");
        datePicker.setButton(DatePickerDialog.BUTTON_POSITIVE, "Ayarla", datePicker);
        datePicker.setButton(DatePickerDialog.BUTTON_NEGATIVE, "Vazgeç", datePicker);

        datePicker.show();
    }

    public void fotografDuzenle(View v){
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
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void handle_crop(int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            //imageView.setImageURI(Crop.getOutput(data));
            secilenResimURI = Crop.getOutput(data);
        }else if(resultCode == Crop.RESULT_ERROR){
            Toast.makeText(getApplicationContext(),"Hata",Toast.LENGTH_SHORT).show();
        }
    }


    public void konumDuzenle(View v){
        Intent konumAc = new Intent(this, KonumDuzenle.class);
        konumAc.putExtra("notIndex", notIndex);
        startActivity(konumAc);
    }
}
