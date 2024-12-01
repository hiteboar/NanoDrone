package com.example.nanodroneapp.Bluetooth;

import android.bluetooth.BluetoothDevice;

public abstract class BluetoothLEListener {
    public abstract void OnDeviceDetected(BluetoothDevice aDevice);
    public abstract void OnStartScan();
    public abstract void OnStopScan();
    public abstract void OnStartConnection();
    public abstract void OnConnect();
    public abstract void OnDisconnect();

}
