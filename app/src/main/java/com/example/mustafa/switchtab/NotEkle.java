package com.example.mustafa.switchtab;

import android.*;
import android.Manifest;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
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
import java.net.URI;
import java.util.Calendar;
import java.util.UUID;

import static com.example.mustafa.switchtab.R.id.imageView;

public class NotEkle extends AppCompatActivity {

    EditText baslik,icerik;
    FirebaseAdapter firebaseAdapter;
    boolean basariliKayit;
    Uri secilenResimURI;
    public static NotClass eklenecekNot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_not_ekle);

        //Toast.makeText(getApplicationContext(),FirstActivity.kullanici.notSayisi(),Toast.LENGTH_SHORT).show();
        eklenecekNot=new NotClass();
        firebaseAdapter = new FirebaseAdapter();

        baslik= (EditText) findViewById(R.id.notEkle_etBaslik);
        icerik = (EditText) findViewById(R.id.notEkle_etIcerik);
    }

    public void olustur(View v){
        if(!baslik.getText().toString().equals("") && !icerik.getText().toString().equals("")){
            notOlustur();
            if(secilenResimURI!=null){
                String uuid = UUID.randomUUID().toString();
                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("images/"+uuid+".jpg");
                storageReference.putFile(secilenResimURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //noinspection VisibleForTests
                        eklenecekNot.setNotResmi(taskSnapshot.getDownloadUrl().toString());

                        if (firebaseAdapter.notuUploadEt(eklenecekNot)){
                            basariliKayit=true;
                            FirstActivity.kullanici.notEkle(eklenecekNot);

                            alarmKur();
                        }
                    }
                });
            }else{
                if (firebaseAdapter.notuUploadEt(eklenecekNot)){
                    basariliKayit=true;
                    FirstActivity.kullanici.notEkle(eklenecekNot);

                    alarmKur();
                }
            }
        }
        else{
            Toast.makeText(getApplicationContext(),"Başlık veya İçerik Doldurulmamış",Toast.LENGTH_LONG).show();
        }
    }

    private void alarmKur(){
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent myIntent;
        PendingIntent pendingIntent;

        myIntent = new Intent(this,BildirimAlici.class);
        BildirimAlici.baslik = baslik.getText().toString();
        BildirimAlici.icerik = icerik.getText().toString();
        BildirimAlici.alarmContext=this;
        pendingIntent=PendingIntent.getBroadcast(this,0,myIntent,0);

        manager.set(AlarmManager.RTC_WAKEUP, eklenecekNot.getTakvim().getTimeInMillis(),pendingIntent);

        //FirstActivity.kullanici.notEkle(eklenecekNot);
        //Toast.makeText(getApplicationContext(),eklenecekNot.getNotResmi(),Toast.LENGTH_SHORT).show();
        Intent konumServisi = new Intent(this,KonumKontrolServisi.class);
        KonumKontrolServisi.LAT = eklenecekNot.getAdresKoordinat().latitude;
        KonumKontrolServisi.LNG = eklenecekNot.getAdresKoordinat().longitude;
        KonumKontrolServisi.konumContext = this;
        KonumKontrolServisi.calisiyor=true;
        startService(konumServisi);
        this.finish();
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    public void notOlustur(){
        eklenecekNot.setNotBaslik(baslik.getText().toString());
        eklenecekNot.setNotIcerik(icerik.getText().toString());
        eklenecekNot.setNotSahibi(FirstActivity.kullanici.getKullaniciAdi());
        eklenecekNot.setNotHedefi(FirstActivity.kullanici.getKullaniciAdi());
    }

    public void takvimAc(View v){
        Toast.makeText(getApplicationContext(),"Şuan "+eklenecekNot.getStringEklendigiTarih(),Toast.LENGTH_SHORT).show();
        takvimKur();
     }

    public void takvimKur(){
        eklenecekNot.getTakvim().set(Calendar.MILLISECOND,0);
        eklenecekNot.getTakvim().set(Calendar.SECOND,0);
        final int saat = eklenecekNot.getTakvim().get(Calendar.HOUR_OF_DAY);
        final int dakika = eklenecekNot.getTakvim().get(Calendar.MINUTE);
        final TimePickerDialog timePicker; //Time Picker referansımızı oluşturduk


        //TimePicker objemizi oluşturuyor ve click listener ekliyoruz
        timePicker = new TimePickerDialog(this, R.style.Theme_AppCompat_DayNight_Dialog, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                eklenecekNot.setSaat(selectedHour);
                eklenecekNot.setDakika(selectedMinute);

                if(saat>eklenecekNot.getSaat())
                {
                    eklenecekNot.setGun((eklenecekNot.getGun()+1));
                    if(eklenecekNot.getGun()==30)
                    {
                        eklenecekNot.setYil((eklenecekNot.getYil()+1));
                    }
                }
                else if(saat==eklenecekNot.getSaat())
                {
                    if(dakika>eklenecekNot.getDakika())
                    {
                        eklenecekNot.setGun((eklenecekNot.getGun()+1));
                        if(eklenecekNot.getGun()==30)
                        {
                            eklenecekNot.setYil((eklenecekNot.getYil()+1));
                        }
                    }
                }

                Toast.makeText(getApplicationContext(),"Ayarlanan Tarih "+eklenecekNot.getTakvim().get(Calendar.DAY_OF_MONTH)+"."+eklenecekNot.getTakvim().get(Calendar.MONTH)+"."+eklenecekNot.getTakvim().get(Calendar.YEAR)+" Saat:"+eklenecekNot.getTakvim().get(Calendar.HOUR_OF_DAY)+":"+eklenecekNot.getTakvim().get(Calendar.MINUTE),Toast.LENGTH_LONG).show();
            }
        }, saat, dakika, true);//true 24 saatli sistem için
        //timePicker.setTitle("Saat Seçin");
        timePicker.setButton(DatePickerDialog.BUTTON_POSITIVE, "Ayarla", timePicker);
        timePicker.setButton(DatePickerDialog.BUTTON_NEGATIVE, "Vazgeç", timePicker);


        int yil = eklenecekNot.getTakvim().get(Calendar.YEAR);
        int ay = eklenecekNot.getTakvim().get(Calendar.MONTH);
        int gun = eklenecekNot.getTakvim().get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePicker;//Datepicker objemiz
        datePicker = new DatePickerDialog(this, R.style.Theme_AppCompat_DayNight_Dialog, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                eklenecekNot.setYil(year);
                eklenecekNot.setAy(monthOfYear);
                eklenecekNot.setGun(dayOfMonth);
                timePicker.show();
            }
        },yil,ay,gun);//başlarken set edilcek değerlerimizi atıyoruz
        //datePicker.setTitle("Tarih Seçiniz");
        datePicker.setButton(DatePickerDialog.BUTTON_POSITIVE, "Ayarla", datePicker);
        datePicker.setButton(DatePickerDialog.BUTTON_NEGATIVE, "Vazgeç", datePicker);

        datePicker.show();
    }

    public void fotografSec(View v){
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
        }else if(resultCode == Crop.RESULT_ERROR){
            Toast.makeText(getApplicationContext(),"Hata",Toast.LENGTH_SHORT).show();
        }
    }

    public void konumSec(View v){
        Intent konumAc = new Intent(this, KonumActivity.class);
        startActivity(konumAc);
    }

    @Override
    protected void onStop() {
        super.onStop();
        /*if(basariliKayit){
            this.finish();
        }*/
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }
}
