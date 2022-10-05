package com.xavierclavel.crowdsourcing;

import android.net.wifi.ScanResult;

import java.util.List;

public class TimestampedData {
    public String timestamp;
    public List<ScanResult> scanResults;

    public TimestampedData(String timestamp, List<ScanResult> scanResults) {
        this.timestamp = timestamp;
        this.scanResults = scanResults;
    }
}
