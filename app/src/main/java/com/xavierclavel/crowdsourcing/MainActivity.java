package com.xavierclavel.crowdsourcing;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static MainActivity instance;
    public static List<ScanResult> scanResultsMemory;
    static ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.buttonWrite).setOnClickListener(this);
        findViewById(R.id.buttonPlot).setOnClickListener(this);
        findViewById(R.id.buttonRead).setOnClickListener(this);
        Log.d("__________________________________________", "start");

        instance = this;
        scanResultsMemory = new ArrayList<>();

        ArrayList wordList = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, wordList);
        ListView lv = (ListView) findViewById(R.id.listview1);
        lv.setAdapter(adapter);

        //Start foreground service that will schedule the various Jobs.
        ContextCompat.startForegroundService(this, new Intent(this, ForegroundService.class));
    }

    public static void DisplayData(List<ScanResult> scanResults) {
        //adapter.add("_________________________________________________");
        adapter.clear();
        for (ScanResult scanResult : scanResults) {
            //adapter.add(scanResult.toString());
            String ssid = scanResult.SSID.equals("") ? "SSID unknown" : scanResult.SSID;
            adapter.add(ssid + "\n" + scanResult.BSSID);
            adapter.add("_________________________________________________");
        }
        scanResultsMemory = scanResults;
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonWrite:
                XmlManager.Write();
                break;

            case R.id.buttonPlot:
                Intent intent = new Intent(this, PlotActivity.class);
                startActivity(intent);
                break;

            case R.id.buttonRead:
                Log.d("main activity", "read button pressed");
                XmlManager.Read();
                break;
        }
        Log.d("main activity", "button clicked");


    }
}