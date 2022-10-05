package com.xavierclavel.crowdsourcing;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

public class ForegroundService extends Service {

    public static JobScheduler scheduler;
    public static ForegroundService instance;
    static int wiFiJobId = 112;
    public static final String CHANNEL_ID = "ForegroundServiceChannel";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        instance = this;
        String input = intent.getStringExtra("inputExtra");
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Foreground Service")
                .setContentText(input)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1, notification);
        //do heavy work on a background thread
        //stopSelf();

        scheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);

        Log.d("foreground service", "here");
        // Lancer ici le job de monitoring dans une async task
        scheduleJobWiFi();
        //stopSelf();
        return START_NOT_STICKY;
    }

    public static void scheduleJobWiFi() {
        Log.d("foreground service", "about to start job");
        ComponentName serviceName = new ComponentName(instance, WiFiJobService.class);
        JobInfo jobInfo = new JobInfo.Builder(wiFiJobId, serviceName)
                //.setPeriodic(1000)
                .setMinimumLatency(5000)
                .build();
        int result = scheduler.schedule(jobInfo);
        Log.d("foreground service", "success");
        if (result == JobScheduler.RESULT_SUCCESS) {
            //Toast.makeText(instance, "successfully scheduled job", Toast.LENGTH_LONG).show();
        }
    }

    public static void displayToast() {
        Toast.makeText(instance, "scan successful", Toast.LENGTH_LONG).show();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("foreground service", "foreground service destroyed");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
