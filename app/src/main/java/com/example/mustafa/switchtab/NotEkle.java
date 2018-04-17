package com.example.mustafa.switchtab;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class NotEkle extends AppCompatActivity {

    EditText baslik,icerik;
    FirebaseAdapter firebaseAdapter;
    boolean basariliKayit;
    NotClass eklenecekNot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_not_ekle);

        firebaseAdapter = new FirebaseAdapter();

        baslik= (EditText) findViewById(R.id.notEkle_etBaslik);
        icerik = (EditText) findViewById(R.id.notEkle_etIcerik);

        eklenecekNot=new NotClass();
    }

    public void olustur(View v){
        if(!baslik.getText().toString().equals("") && !icerik.getText().toString().equals("")){
            notOlustur();
            if (firebaseAdapter.notuUploadEt(eklenecekNot,this)){
                basariliKayit=true;
                Intent intent = new Intent(this,MainActivity.class);
                startActivity(intent);
            }
        }
        else{
            Toast.makeText(getApplicationContext(),"Başlık veya İçerik Doldurulmamış",Toast.LENGTH_LONG).show();
        }
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
        int saat = eklenecekNot.getTakvim().get(Calendar.HOUR_OF_DAY);
        int dakika = eklenecekNot.getTakvim().get(Calendar.MINUTE);
        final TimePickerDialog timePicker; //Time Picker referansımızı oluşturduk


        //TimePicker objemizi oluşturuyor ve click listener ekliyoruz
        timePicker = new TimePickerDialog(this, R.style.Theme_AppCompat_DayNight_Dialog, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                eklenecekNot.setSaat(selectedHour);
                eklenecekNot.setDakika(selectedMinute);

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

    @Override
    protected void onStop() {
        super.onStop();
        if(basariliKayit){
            this.finish();
        }
    }
}
