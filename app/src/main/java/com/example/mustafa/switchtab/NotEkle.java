package com.example.mustafa.switchtab;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
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

        //Toast.makeText(getApplicationContext(),FirstActivity.kullanici.notSayisi(),Toast.LENGTH_SHORT).show();
        eklenecekNot=new NotClass();
        firebaseAdapter = new FirebaseAdapter();

        baslik= (EditText) findViewById(R.id.notEkle_etBaslik);
        icerik = (EditText) findViewById(R.id.notEkle_etIcerik);
    }

    public void olustur(View v){
        if(!baslik.getText().toString().equals("") && !icerik.getText().toString().equals("")){
            notOlustur();
            if (firebaseAdapter.notuUploadEt(eklenecekNot)){
                basariliKayit=true;
                FirstActivity.kullanici.notEkle(eklenecekNot);

                AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                Intent myIntent;
                PendingIntent pendingIntent;

                myIntent = new Intent(this,BildirimAlici.class);
                BildirimAlici.baslik = baslik.getText().toString();
                BildirimAlici.icerik = icerik.getText().toString();
                BildirimAlici.context = getApplicationContext();
                pendingIntent=PendingIntent.getBroadcast(this,0,myIntent,0);

                manager.set(AlarmManager.RTC_WAKEUP, eklenecekNot.getTakvim().getTimeInMillis(),pendingIntent);

                //FirstActivity.kullanici.notEkle(eklenecekNot);
                //Toast.makeText(getApplicationContext(),eklenecekNot.getNotResmi(),Toast.LENGTH_SHORT).show();
                this.finish();
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
        eklenecekNot.setNotResmi("Eklenmedi");
    }

    public void takvimAc(View v){
        Toast.makeText(getApplicationContext(),"Şuan "+eklenecekNot.getStringEklendigiTarih(),Toast.LENGTH_SHORT).show();
        takvimKur();
     }

    public void takvimKur(){
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
