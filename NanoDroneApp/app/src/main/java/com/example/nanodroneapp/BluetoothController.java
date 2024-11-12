package com.example.nanodroneapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;

import com.example.nanodroneapp.Bluetooth.BluetoothDevicesList;
import com.example.nanodroneapp.Bluetooth.BluetoothListener;
import com.example.nanodroneapp.Bluetooth.BluetoothReceiver;
import com.example.nanodroneapp.Bluetooth.BluetoothScanCallback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import static androidx.core.content.ContextCompat.registerReceiver;


public class BluetoothController {

    //App context
    private Context mContext;

    // Stops scanning after 30 seconds.
    private static final long SCAN_PERIOD = 30000;

    // Scanner callback object
    private BluetoothScanCallback mBluetoothScanCallback;
    // Scanner object
    private BluetoothLeScanner mBluetoothLeScanner;
    // Scanner device list
    private BluetoothDevicesList mDeviceList;

    // device UUID
    private final UUID DEVICE_UUID = UUID.fromString("800713BC-3AF7-4CA1-9029-CA765444188F");
    // Socket used to connect the device
    private BluetoothSocket mSocket;

    // Connected device name
    private String mConnectedDeviceName;
    public String GetDeviceName(){
        if (mDeviceIsConnected)
            return mConnectedDeviceName;
        else
            return "";
    }

    //Return true if there is some device connected
    private boolean mDeviceIsConnected;
    public boolean IsConnected() { return mDeviceIsConnected; }

    //Return true if it is scanning for devices
    private boolean mIsScanning;
    public boolean IsScanning() { return mIsScanning; }

    private ArrayList<BluetoothListener> mListeners;

    // Singleton
    private static BluetoothController mController;
    public static BluetoothController Instance(){
        if (mController == null){
            mController = new BluetoothController();
        }
        return mController;
    }
    private BluetoothController(){
        mDeviceIsConnected = false;
        mConnectedDeviceName = "";

        mIsScanning = false;
    }

    @SuppressLint("MissingPermission")
    public void Init(Context aContext){
        if (mContext != null) return; // ONly can be initialized once
        mContext = aContext;

        Activity activity = (Activity) aContext;

        BluetoothManager bluetoothManager =  mContext.getSystemService(BluetoothManager.class);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();

        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            aContext.startActivity(enableBtIntent);
        }

        mBluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        IntentFilter filter = new IntentFilter();
        aContext.registerReceiver(new BluetoothReceiver(), filter);
    }

    // Scan for other bluetooth devices
    @SuppressLint("MissingPermission")
    public void Scan(){
        // If it is not scanning, start scanning
        if (!mIsScanning) {
            // Create a list where the new devices will be added
            if (mDeviceList == null) mDeviceList = new BluetoothDevicesList(){

                @Override
                public void OnAddDevice(BluetoothDevice aDevice) {
                    super.OnAddDevice(aDevice);
                    SendOnDeviceFound(aDevice);
                }
            };

            // Create a callback to update the list of devices
            if (mBluetoothScanCallback == null) mBluetoothScanCallback = new BluetoothScanCallback(mDeviceList);
            else mBluetoothScanCallback.Reset();

            mIsScanning = true;

            //Start the bluetooth scanner
            mBluetoothLeScanner.startScan(mBluetoothScanCallback);

            // Send the start event
            SendOnStartScan();

            // Stops scanning after a predefined scan period.
            new Handler().postDelayed(new Runnable() {
                @SuppressLint("MissingPermission")
                @Override
                public void run() {
                    mIsScanning = false;
                    mBluetoothLeScanner.flushPendingScanResults(mBluetoothScanCallback);
                    mBluetoothLeScanner.stopScan(mBluetoothScanCallback);
                    SendOnStopScan();
                }
            }, SCAN_PERIOD);
        }
    }

    // Connect to the selected device
    @SuppressLint("MissingPermission")
    public void Connect(String aDeviceAddress){
        mBluetoothLeScanner.stopScan(mBluetoothScanCallback);
        BluetoothDevice lDevice = mDeviceList.GetDevice(aDeviceAddress);
        try {
            // Get a BluetoothSocket to connect with the given BluetoothDevice.
            // MY_UUID is the app's UUID string, also used in the server code.
             mSocket = lDevice.createRfcommSocketToServiceRecord(DEVICE_UUID);
        } catch (IOException e) {
            System.out.println("[BLUETOOTH] Socket's create() method failed.\n" + e.getMessage());
        }

        try {
            // Connect to the remote device through the socket. This call blocks
            // until it succeeds or throws an exception.
            mSocket.connect();
        } catch (IOException connectException) {
            // Unable to connect; close the socket and return.
            System.out.println("[BLUETOOTH] Unable to connect; close the socket and return." + connectException.getMessage());
            try {
                mSocket.close();
            } catch (IOException closeException) {
                System.out.println("[BLUETOOTH] Could not close the client socket" + closeException.getMessage());
            }
        }
    }

    public void Disconnect(){

    }

    public void AddListener(BluetoothListener aListener){
        if (mListeners == null) mListeners = new ArrayList<BluetoothListener>();
        if (mListeners.contains(aListener)) return;
        mListeners.add(aListener);
    }

    public void RemoveListener(BluetoothListener aListener){
        if (mListeners != null && mListeners.contains(aListener)){
            mListeners.remove(aListener);
        }
    }

    public void ClearListeners(){
        mListeners.clear();
    }

    private void SendOnDeviceFound(BluetoothDevice aNewDevice){
        if(mListeners != null) {
            for (int i = 0; i < mListeners.size(); ++i) {
                mListeners.get(i).OnDeviceDetected(aNewDevice);
            }
        }
    }

    private void SendOnStartScan() {
        if(mListeners != null) {
            for (int i = 0; i < mListeners.size(); ++i) {
                mListeners.get(i).OnStartScan();
            }
        }
    }

    private void SendOnStopScan() {
        if(mListeners != null) {
            for (int i = 0; i < mListeners.size(); ++i) {
                mListeners.get(i).OnStopScan();
            }
        }
    }
}

