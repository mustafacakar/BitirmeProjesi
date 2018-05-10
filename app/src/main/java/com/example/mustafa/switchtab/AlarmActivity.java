package com.example.mustafa.switchtab;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class AlarmActivity extends AppCompatActivity {

    MediaPlayer mediaSong;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        mediaSong= MediaPlayer.create(this,R.raw.alarm);
        mediaSong.start();
        TextView baslik = (TextView) findViewById(R.id.alarm_tvBaslik);
        TextView icerik = (TextView) findViewById(R.id.alarm_tvIcerik);

        baslik.setText("Not Başlığı: "+getIntent().getExtras().getString("baslik"));
        icerik.setText("Not İçeriği: "+getIntent().getExtras().getString("icerik"));
    }

    public void alarmKapat(View v){
        mediaSong.stop();
        Intent intent= new Intent(this,KonumKontrolServisi.class);
        KonumKontrolServisi.calisiyor=false;
        stopService(intent);
        this.finish();
    }
}
