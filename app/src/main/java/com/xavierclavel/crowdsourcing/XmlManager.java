package com.xavierclavel.crowdsourcing;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.util.Log;
import android.util.Xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlSerializer;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class XmlManager {

    static Context context;
    static String filename = "scan_data";
    public static boolean autoUpdate = false;

    public static List<TimestampedData> dataMemory;

    public static void Memorize(List<ScanResult> scanResults) {
        dataMemory = dataMemory == null ? new ArrayList<>() : dataMemory;
        dataMemory.add(new TimestampedData(getTimestamp(), scanResults));
        Log.d("xml manager", "data written | total amount of data : " + dataMemory.size());

        if (autoUpdate) PlotActivity.plotData(dataMemory);
    }

    public static void Write() {
        Log.d("xml manager", "starting to write data");
        try {
            FileOutputStream fos = context.openFileOutput(filename, Context.MODE_APPEND);
            XmlSerializer serializer = Xml.newSerializer();
            serializer.setOutput(fos, "UTF-8"); //definit le fichier de sortie
            serializer.startDocument(null, Boolean.TRUE);
            serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
            writeData(serializer);
            serializer.flush();
            fos.close();
        } catch (Exception e) {
            Log.d("Xml manager", "failed to write data in xml file");
        }
    }

    public static void writeData(XmlSerializer serializer) {
        try {
            serializer.startTag("null", "root");
                for (TimestampedData timestampedData : dataMemory) {

                    serializer.startTag("null", "measurement");

                    serializer.startTag("null", "timestamp");
                    serializer.text(timestampedData.timestamp);
                    serializer.endTag("null", "timestamp");

                    serializer.startTag("null", "BSSID");
                    for (ScanResult scanResult : timestampedData.scanResults) {
                        serializer.startTag("null", "record");
                        serializer.text(scanResult.BSSID);
                        serializer.endTag("null", "record");
                    }
                    serializer.endTag("null", "BSSID");

                    serializer.endTag("null", "measurement");
                }
            serializer.endTag("null", "root");
        } catch (Exception e) {
            Log.d("xml manager", "failed to write records on xml file");
        }

    }

    public static String getTimestamp() {
        Long tsLong = System.currentTimeMillis()/1000;
        return tsLong.toString();
    }

    private String readRawData(String filename) {
        FileInputStream fis = null;
        InputStreamReader isr = null;
        String data = "";
        try {
            fis = context.openFileInput(filename);
            isr = new InputStreamReader(fis);
            char[] inputBuffer = new char[fis.available()];
            isr.read(inputBuffer);
            data = new String(inputBuffer);
            //Log.i(TAG, "Read data from file " + filename);
            isr.close();
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    public void Read(Context context) {
        //Read data string from file
        String data = readRawData(filename);
        InputStream is = null;
        try {
            is = new ByteArrayInputStream(data.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();;
        DocumentBuilder db;
        NodeList items = null;
        Document dom;
        try {
            db = dbf.newDocumentBuilder();
            dom = db.parse(is);
            dom.getDocumentElement().normalize();
            //get all measurement tags
            items = dom.getElementsByTagName("measurement");
            Log.d("xml manager", "nb of xml measurements = "+items.getLength());
            for (int i=0;i<items.getLength();i++){
                Element measure = (Element)items.item(i);
                //get timestamp
                String timestamp = measure.getElementsByTagName("timestamp").item(0).getTextContent();
                //for all elements in the document
                Log.d("xml manager","Measurement "+i+ " with timestamp "+timestamp);
                //get all APs
                NodeList aps= measure.getElementsByTagName("record");
                for (int j=0; j<aps.getLength();j++){
                    String ap = aps.item(j).getTextContent();
                    Log.d("xml manager", " "+ap);
                }
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
