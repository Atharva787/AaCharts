package com.example.aacharts;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;


public class ForegroundService extends JobIntentService{

    public static final String CHANNEL_ID = "ForegroundServiceChannel";

    private static final int JOB_ID = 1;
    public static void enqueueWork(Context context, Intent intent) {
        Log.e("In","EnqueueWork");
        enqueueWork(context, ForegroundService.class, JOB_ID, intent);
    }

    @Override
    public void onCreate() {
        Log.e("In","onCreate");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("In","onStartCommand");
        onHandleWork(intent);
        return START_NOT_STICKY;
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Log.e("In","onHandleWork");

        Log.e("In","Service Start");
        String input = intent.getStringExtra("inputExtra");
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Foreground Service")
                .setContentText(input)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1, notification);

        MainActivity.logging();
        Log.e("In","Service End");
    }

    public void onDestroy()
    {
        super.onDestroy();
        Log.e("In","OnDestroy");
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(CHANNEL_ID,"Foreground Service Channel",NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
}