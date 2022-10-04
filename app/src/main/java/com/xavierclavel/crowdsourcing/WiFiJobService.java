package com.xavierclavel.crowdsourcing;

import android.Manifest;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.app.job.JobWorkItem;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import java.util.List;

public class WiFiJobService extends JobService  {
    public static boolean shouldReschedule = true;
    WiFiJob wiFiJob;
    public static WiFiJobService instance;
    public static JobParameters wifiJobParameters;
    WifiManager wifiMan;

    // Méthode appelée quand la tâche est lancée
    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d("wifi job", "onStartJob id=" + params.getJobId());
        // ***** Lancer ici la mesure dans un thread à part *****
        wifiMan = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        instance = this;
        wifiJobParameters = params;
        wiFiJob = wiFiJob == null ? new WiFiJob() : wiFiJob;
        wiFiJob.doInBackground();
        return true;
    }

    // Méthode appelée quand la tâche est arrêtée par le scheduler
// Retourne vrai si le scheduler doit relancer la tâche
    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d("wifi job", "onStopJob id=" + params.getJobId());
// ***** Arrêter le thread du job ici ******
        return shouldReschedule;
    }





    class WiFiJob extends AsyncTask<String, Integer, String> {
        // This is run in a background thread
        @Override
        protected String doInBackground(String... params) {
            Log.d("wifi job", "wifi job started");
            try {
                List<ScanResult> scanResults = wifiMan.getScanResults();
                Log.d("wifi job", "successfully read wifi data");
            } catch (Exception e) {
                Log.d("wifi job", "failed to read wifi data");
            } finally {
                WiFiJobService.instance.jobFinished(WiFiJobService.wifiJobParameters, WiFiJobService.shouldReschedule);
            }
            return "Done";
        }
    }

}
