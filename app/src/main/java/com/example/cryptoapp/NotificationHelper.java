package com.example.cryptoapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Random;

public class NotificationHelper extends ContextWrapper {
    private final int ACTIVITY_REQUEST_CODE=127;
    public NotificationHelper(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {
            createTargetChannels();
        }
    }
    private String CHANNEL_TARGET_NAME="Target Price Channel";
    private String CHANNEL_TARGET_ID="Target ChannelId";
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createTargetChannels(){
        NotificationChannel notificationChannel=new NotificationChannel(CHANNEL_TARGET_ID,CHANNEL_TARGET_NAME, NotificationManager.IMPORTANCE_HIGH);
        notificationChannel.enableLights(true);
        notificationChannel.enableVibration(true);
        notificationChannel.setLightColor(Color.BLUE);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        NotificationManager manager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(notificationChannel);

    }
    public void sendTargetNotification(String title,String body,Class activityName)
    {
        Intent intent=new Intent(this,activityName);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,ACTIVITY_REQUEST_CODE,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification=new NotificationCompat.Builder(this , CHANNEL_TARGET_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.ic_baseline_attach_money_24)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setStyle(new NotificationCompat.BigTextStyle().setBigContentTitle(title).bigText(body))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();
        NotificationManagerCompat.from(this).notify(new Random().nextInt(),notification);
    }

}
