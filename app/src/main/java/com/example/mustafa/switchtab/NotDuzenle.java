package com.example.mustafa.switchtab;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class NotDuzenle extends AppCompatActivity {
    int notIndex;
    EditText baslik,icerik;
    FirebaseAdapter firebaseAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_not_duzenle);

        firebaseAdapter = new FirebaseAdapter();
        notIndex=getIntent().getExtras().getInt("NotSira");

        baslik = (EditText) findViewById(R.id.notDuzenle_etNotBaslik);
        icerik = (EditText) findViewById(R.id.notDuzenle_etNotIcerik);

        baslik.setText(FirstActivity.kullanici.notuBul(notIndex).getNotBaslik());
        icerik.setText(FirstActivity.kullanici.notuBul(notIndex).getNotIcerik());
    }

    public void notOlustur(){
        FirstActivity.kullanici.notuBul(notIndex).setNotBaslik(baslik.getText().toString());
        FirstActivity.kullanici.notuBul(notIndex).setNotIcerik(icerik.getText().toString());
        FirstActivity.kullanici.notuBul(notIndex).setNotSahibi(FirstActivity.kullanici.getKullaniciAdi());
        FirstActivity.kullanici.notuBul(notIndex).setNotHedefi(FirstActivity.kullanici.getKullaniciAdi());
        FirstActivity.kullanici.notuBul(notIndex).setNotResmi("Eklenmedi");
    }

    public void duzenle(View v){
        notOlustur();
        firebaseAdapter.notuDuzenle(FirstActivity.kullanici.notlariAl().get(notIndex));
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
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
}
