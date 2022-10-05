package com.xavierclavel.crowdsourcing;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;

import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;

import java.util.Arrays;
import java.util.List;

public class PlotActivity extends AppCompatActivity {

    static PlotActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plot);
        instance = this;

        plotData(XmlManager.dataMemory);    //to replace by actual xml file
        XmlManager.autoUpdate = true;
    }

    public static void plotData(List<TimestampedData> dataMemory) {
        instance.setContentView(R.layout.activity_plot);    //reload plot window to update it
        if (dataMemory == null) {
            Log.d("plot activity", "null list");
            return;
        }
        // initialiser la référence sur XYPlot :
        XYPlot plot = (XYPlot) instance.findViewById(R.id.plot);

        if (dataMemory.size() == 0 ) return;

        Integer[] ids = new Integer[dataMemory.size()];
        Integer[] times = new Integer[dataMemory.size()];
        Integer[] nbAps = new Integer[dataMemory.size()];
        int i = 0;

        //lire les données
        for (TimestampedData timestampedData : dataMemory) {
            ids[i] = i;
            times[i] = Integer.parseInt(timestampedData.timestamp);
            nbAps[i] = timestampedData.scanResults.size();
            Log.d("plot activity", "Measurement id "+i+"- timestamp "+times[i] +" - nb APS "+nbAps[i]);
            i++;
        }

        // transformer les données en series XY
        XYSeries apsXY = new SimpleXYSeries(Arrays.asList(nbAps),
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Nb Aps");
        // définir le format de la courbe (ligne rouge, marqueurs bleus)
        LineAndPointFormatter series1Format = new
                LineAndPointFormatter(Color.RED, Color.BLUE, null, null);
        // ajouter la serie XY au plot:
        plot.addSeries(apsXY, series1Format);
    }

}