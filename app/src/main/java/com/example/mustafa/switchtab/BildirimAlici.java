package com.example.mustafa.switchtab;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;


public class BildirimAlici extends BroadcastReceiver {
    public static Context alarmContext;
    public static String baslik;
    public static String icerik;
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        builder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(baslik+" ")
                .setContentText(icerik+" ")
                .setDefaults(Notification.DEFAULT_LIGHTS)
                .setContentInfo("Bildirim Bilgi");

        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1,builder.build());
        Intent alarmAc = new Intent(context,AlarmActivity.class);
        alarmAc.putExtra("baslik",baslik);
        alarmAc.putExtra("icerik",icerik);
        alarmContext.startActivity(alarmAc);
    }
}
