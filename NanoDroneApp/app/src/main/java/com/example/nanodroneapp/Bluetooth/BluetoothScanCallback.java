package com.example.nanodroneapp.Bluetooth;

import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;

import java.util.List;

public class BluetoothScanCallback extends ScanCallback {

    private BluetoothDevicesList mDeviceList;

    public BluetoothScanCallback(BluetoothDevicesList aDeviceList){
        mDeviceList = aDeviceList;
    }

    @Override
    public void onBatchScanResults(List<ScanResult> results) {
        super.onBatchScanResults(results);
    }

    @Override
    public void onScanResult(int callbackType, ScanResult result) {
        super.onScanResult(callbackType, result);
        mDeviceList.OnAddDevice(result.getDevice());
    }

    public void Reset(){
        mDeviceList.ClearList();
    }
}
