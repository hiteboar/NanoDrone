package com.example.nanodroneapp.Bluetooth;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.IntentFilter;
import android.util.Log;

import com.example.nanodroneapp.Bluetooth.Scanner.BluetoothDevicesList;
import com.example.nanodroneapp.Bluetooth.Scanner.BluetoothScanCallback;
import com.example.nanodroneapp.Bluetooth.Scanner.BluetoothScanner;

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

    // Connected device name
    private BluetoothDevice mConnectedDevice = null;

    @SuppressLint("MissingPermission")
    public String GetDeviceName(){
        if (mConnectedDevice != null)
            return mConnectedDevice.getName();
        else
            return "";
    }

    //Return true if there is some device connected
    public boolean IsConnected() { return mConnectedDevice != null; }

    //Return true if it is scanning for devices
    private boolean mIsScanning;
    public boolean IsScanning() { return mIsScanning; }

    private ArrayList<BluetoothScannerListener> mListeners;

    // Singleton
    private static BluetoothController mController;
    public static BluetoothController Instance(){
        if (mController == null){
            mController = new BluetoothController();
        }
        return mController;
    }

    private BluetoothController(){
        mConnectedDevice = null;
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

        IntentFilter filter = new IntentFilter();
        aContext.registerReceiver(new BluetoothReceiver(), filter);
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

    public void AddListener(BluetoothScannerListener aListener){
        if (mListeners == null) mListeners = new ArrayList<BluetoothScannerListener>();
        if (mListeners.contains(aListener)) return;
        mListeners.add(aListener);
    }

    public void RemoveListener(BluetoothScannerListener aListener){
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

    // Connect to the selected device
    @SuppressLint("MissingPermission")
    public void Connect(String aDeviceAddress){
        if (mBluetoothCommunication == null) {
            StopScan();
            mBluetoothCommunication = new BluetoothCommunication(mContext, mDeviceList.GetDevice(aDeviceAddress)) {
                @Override
                public void OnDataReceived(byte[] aData) {
                    super.OnDataReceived(aData);
                    Log.i(TAG, "Received data: " + new String(aData));
                }
            };
            mBluetoothCommunication.StartCommunication();
        }
        else{
            mBluetoothCommunication.WriteData(new byte[]{0x12, 0x34, 0x56});
        }
    }


    @SuppressLint("MissingPermission")
    public void Disconnect(){
        if (mBluetoothCommunication != null)
            mBluetoothCommunication.EndCommunication();
    }
}

