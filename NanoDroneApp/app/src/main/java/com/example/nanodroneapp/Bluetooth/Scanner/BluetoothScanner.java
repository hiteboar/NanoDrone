package com.example.nanodroneapp.Bluetooth.Scanner;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.os.Handler;
import android.util.Log;

public class BluetoothScanner {

    private final Activity mActivity;

    // Stops scanning after 30 seconds.
    private static final long SCAN_PERIOD = 30000;
    // Bluetooth Adapter
    private final BluetoothAdapter mBluetoothAdapter;
    private final BluetoothScanCallback mScanCallback;
    private final BluetoothLeScanner mBluetoothLeScanner;

    private boolean mIsScanning;

    private static final String TAG = "BluetoothLeService";

    public BluetoothScanner(Activity aActivity, BluetoothScanCallback aScannCallback){
        mActivity = aActivity;
        mScanCallback = aScannCallback;

        BluetoothManager bluetoothManager =  mActivity.getSystemService(BluetoothManager.class);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        if (mBluetoothLeScanner == null){
            Log.e(TAG, "Unable to initialize Bluetooth");
        }
    }

    @SuppressLint("MissingPermission")
    public void StartScan(){
        // If it is not scanning, start scanning
        if (!mIsScanning && mBluetoothLeScanner != null) {
            mIsScanning = true;
            //Start the bluetooth scanner
            mBluetoothLeScanner.startScan(mScanCallback);

            // Stops scanning after a predefined scan period.
            new Handler().postDelayed(new Runnable() {
                @SuppressLint("MissingPermission")
                @Override
                public void run() {
                    StopScan();
                }
            }, SCAN_PERIOD);
        }
    }

    @SuppressLint("MissingPermission")
    public void StopScan(){
        if (mBluetoothLeScanner != null) {
            mIsScanning = false;
            mBluetoothLeScanner.flushPendingScanResults(mScanCallback);
            mBluetoothLeScanner.stopScan(mScanCallback);
            mBluetoothAdapter.cancelDiscovery();
        }
    }

    public boolean IsScanning() {
        return mIsScanning;
    }

    @SuppressLint("MissingPermission")
    public boolean IsBonded(BluetoothDevice aDevice){
        return mBluetoothAdapter.getBondedDevices().contains(aDevice);
    }
}
