package com.example.nanodroneapp.Bluetooth;

import android.bluetooth.BluetoothDevice;

public abstract class BluetoothListener{
    public abstract void OnDeviceDetected(BluetoothDevice aDevice);
    public abstract void OnStartScan();
    public abstract void OnStopScan();
}
