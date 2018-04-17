package com.example.mustafa.switchtab;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

public class NotEkle extends AppCompatActivity {

    EditText baslik,icerik;
    FirebaseAdapter firebaseAdapter;
    boolean basariliKayit;
    Button takvimButton;
    Calendar mcurrentTime;
    int secilensaat;
    int secilendakika;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_not_ekle);

        firebaseAdapter = new FirebaseAdapter();
        mcurrentTime = Calendar.getInstance();

        baslik= (EditText) findViewById(R.id.notEkle_etBaslik);
        icerik = (EditText) findViewById(R.id.notEkle_etIcerik);
        takvimButton = (Button) findViewById(R.id.notEkle_btnTakvim);
        //tarihButton = (Button) findViewById(R.id.notEkle_btnKonum);

    }

    public void olustur(View v){
        if(!baslik.getText().toString().equals("") && !icerik.getText().toString().equals("")){

            if (firebaseAdapter.notuUploadEt(baslik.getText().toString(),icerik.getText().toString(),this)){
                basariliKayit=true;


                AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                Intent myIntent;
                PendingIntent pendingIntent;

                myIntent = new Intent(this,AlarmBildirimAlici.class);
                pendingIntent=PendingIntent.getBroadcast(this,0,myIntent,0);

                manager.set(AlarmManager.RTC_WAKEUP, mcurrentTime.getTimeInMillis(),pendingIntent);


                Intent intent = new Intent(this,MainActivity.class);
                startActivity(intent);
            }
        }
        else{
            Toast.makeText(getApplicationContext(),"Başlık veya İçerik Doldurulmamış",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(basariliKayit){
            this.finish();
        }
    }

    public void takvimAc(View w){
        final int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);//Güncel saati aldık
        final int minute = mcurrentTime.get(Calendar.MINUTE);//Güncel dakikayı aldık
        final TimePickerDialog timePicker; //Time Picker referansımızı oluşturduk


        //TimePicker objemizi oluşturuyor ve click listener ekliyoruz
        timePicker = new TimePickerDialog(this, R.style.Theme_AppCompat_DayNight_Dialog, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                //Toast.makeText(getApplicationContext(),selectedHour + ":" + selectedMinute,Toast.LENGTH_LONG).show();//Ayarla butonu tıklandığında textview'a yazdırıyoruz

                secilensaat=selectedHour;
                secilendakika=selectedMinute;

                mcurrentTime.set(Calendar.HOUR_OF_DAY, selectedHour);
                mcurrentTime.set(Calendar.MINUTE, selectedMinute);
                mcurrentTime.set(Calendar.SECOND, 0);
                mcurrentTime.set(Calendar.MILLISECOND, 0);

            }
        }, hour, minute, true);//true 24 saatli sistem için
        timePicker.setTitle("Saat Seçiniz");
        timePicker.setButton(DatePickerDialog.BUTTON_POSITIVE, "Ayarla", timePicker);
        timePicker.setButton(DatePickerDialog.BUTTON_NEGATIVE, "İptal", timePicker);

        final int currentYear = mcurrentTime.get(Calendar.YEAR);//Güncel Yılı alıyoruz
        int month = mcurrentTime.get(Calendar.MONTH);//Güncel Ayı alıyoruz
        int day = mcurrentTime.get(Calendar.DAY_OF_MONTH);//Güncel Günü alıyoruz

        DatePickerDialog datePicker;//Datepicker objemiz
        datePicker = new DatePickerDialog(this, R.style.Theme_AppCompat_DayNight_Dialog, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub

                if(hour>secilendakika)
                {
                    dayOfMonth = dayOfMonth +1 ;
                    if(dayOfMonth==30)
                    {
                        monthOfYear++;
                    }
                }
                else if(hour==secilensaat)
                {
                    if(secilendakika<minute)
                    {
                        dayOfMonth=dayOfMonth +1;
                        if(dayOfMonth==30)
                        {
                            monthOfYear++;
                        }
                    }
                }

                mcurrentTime.set(Calendar.YEAR, year);
                mcurrentTime.set(Calendar.MONTH, monthOfYear);
                mcurrentTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                timePicker.show();
            }
        },currentYear,month,day);//başlarken set edilcek değerlerimizi atıyoruz
        datePicker.setTitle("Tarih Seçiniz");
        datePicker.setButton(DatePickerDialog.BUTTON_POSITIVE, "Ayarla", datePicker);
        datePicker.setButton(DatePickerDialog.BUTTON_NEGATIVE, "İptal", datePicker);

        datePicker.show();
    }
}
