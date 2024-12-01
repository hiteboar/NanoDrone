package com.example.nanodroneapp.Bluetooth;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.IntentFilter;
import android.util.Log;

import com.example.nanodroneapp.Bluetooth.Communicatoin.BluetoothCommunication;
import com.example.nanodroneapp.Bluetooth.Communicatoin.BluetoothCommunicationListener;
import com.example.nanodroneapp.Bluetooth.Scanner.BluetoothDevicesList;
import com.example.nanodroneapp.Bluetooth.Scanner.BluetoothScanCallback;
import com.example.nanodroneapp.Bluetooth.Scanner.BluetoothScanner;
import com.example.nanodroneapp.R;

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

    // Scanner device list
    private BluetoothDevicesList mDeviceList;

    // Bluetooth Connection controller
    private BluetoothScanner mScanner;
    // Bluetooth Gatt Communication controller
    private BluetoothCommunication mBluetoothCommunication;

    // device UUID
    private final UUID DEVICE_UUID = UUID.fromString("800713BC-3AF7-4CA1-9029-CA765444188F");
    private static final String TAG = "BluetoothLeService";

    //Return true if it is scanning for devices
    private boolean mIsScanning;
    public boolean IsScanning() { return mIsScanning; }

    private BluetoothLEListener mListener;

    // Singleton
    private static BluetoothController mController;
    public static BluetoothController Instance(){
        if (mController == null){
            mController = new BluetoothController();
        }
        return mController;
    }

    private BluetoothController(){
        mIsScanning = false;
    }

    @SuppressLint("MissingPermission")
    public void Init(Context aContext){
        if (mContext != null) return; // Only can be initialized once
        mContext = aContext;

        mDeviceList = new BluetoothDevicesList() {
            @Override
            public void OnAddDevice(BluetoothDevice aDevice) {
                super.OnAddDevice(aDevice);
                SendOnDeviceFound(aDevice);
            }
        };

        mBluetoothScanCallback = new BluetoothScanCallback(mDeviceList);

        Activity activity = (Activity) aContext;
        mScanner = new BluetoothScanner(activity, mBluetoothScanCallback);
    }

    // Scan for other bluetooth devices
    public void Scan(){
        mScanner.StartScan();
        SendOnStartScan();
    }

    public void StopScan() {
        mScanner.StopScan();
        SendOnStopScan();
    }

    public void SetListener(BluetoothLEListener aListener){
        mListener = aListener;
    }

    public void ClearListeners(){
        mListener = null;
    }

    private void SendOnDeviceFound(BluetoothDevice aNewDevice){
        if(mListener != null) {
            mListener.OnDeviceDetected(aNewDevice);
        }
    }

    private void SendOnStartScan() {
        if(mListener != null) {
            mListener.OnStartScan();
        }
    }

    private void SendOnStopScan() {
        if(mListener != null) {
            mListener.OnStopScan();
        }
    }

    // Connect to the selected device
    @SuppressLint("MissingPermission")
    public void Connect(String aDeviceAddress){
        if (mBluetoothCommunication == null) {
            StopScan();
            mBluetoothCommunication = new BluetoothCommunication(mContext, mDeviceList.GetDevice(aDeviceAddress), mBluetoothCommunicationListener);
            mBluetoothCommunication.StartCommunication();
        }
    }


    @SuppressLint("MissingPermission")
    public void Disconnect(){
        if (mBluetoothCommunication != null) {
            mBluetoothCommunication.EndCommunication();
            if (mListener != null)
                mListener.OnDisconnect();
        }
    }

    @SuppressLint("MissingPermission")
    public String GetDeviceName(){
        if (mBluetoothCommunication != null && mBluetoothCommunication.IsConnected() && mBluetoothCommunication.GetDevice() != null)
            return mBluetoothCommunication.GetDevice().getName();
        else
            return "";
    }

    //Return true if there is some device connected
    public boolean IsConnected() { return mBluetoothCommunication != null && mBluetoothCommunication.IsConnected(); }

    private BluetoothCommunicationListener mBluetoothCommunicationListener = new BluetoothCommunicationListener() {
        @Override
        public void OnStartCommunication() {
            if (mListener != null)
                mListener.OnStartConnection();
        }

        @Override
        public void OnConnectionEstablished() {
            if (mListener != null)
                mListener.OnConnect();
        }

        @Override
        public void OnDataReceived(byte[] aData) {
            // IMPLEMENT ON DATA RECEIVED
        }

        @Override
        public void OnConnectionLost() {
            Disconnect();
        }
    };
}

