package com.example.nanodroneapp.Bluetooth.Communicatoin;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.UUID;


public class BluetoothCommunication{

    private static final String TAG = "BluetoothCommunication";

    private Context mContext;
    private BluetoothDevice mDevice;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothGattService mBluetoothGattService;
    private BluetoothGattCharacteristic mBluetoothGattCharacteristic;
    private BluetoothCommunicationListener mListener;

    private UUID CHARACTERISTIC_UUID = UUID.fromString("800713BC-3AF7-4CA1-9029-CA765444188F");
    private UUID SERVICE_UUID = UUID.fromString("800713BC-3AF7-4CA1-9029-CA765444188F");

    private boolean mIsConnected = false;
    public boolean IsConnected(){ return mIsConnected; }

    BluetoothGattCallback mBluetoothGattCallback = new BluetoothGattCallback() {

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                mBluetoothGattService = gatt.getService(SERVICE_UUID);
                if (mBluetoothGattService != null) {
                    Log.i(TAG, "Service characteristic UUID found: " + mBluetoothGattService.getUuid().toString());
                    mBluetoothGattCharacteristic = mBluetoothGattService.getCharacteristic(CHARACTERISTIC_UUID);
                    mIsConnected = true;
                    mListener.OnConnectionEstablished();
                } else {
                    Log.i(TAG, "Service characteristic not found for UUID: " + SERVICE_UUID);
                }
            }
        }

        @Override
        public void onServiceChanged(@NonNull BluetoothGatt gatt) {
            super.onServiceChanged(gatt);
        }

        @SuppressLint("MissingPermission")
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            if (status != newState && newState == BluetoothProfile.STATE_CONNECTED) {
                mBluetoothGatt.discoverServices();
            }
            else if (newState == BluetoothProfile.STATE_DISCONNECTED){
                mListener.OnConnectionLost();
            }
        }

        @Override
        public void onCharacteristicRead(@NonNull BluetoothGatt gatt, @NonNull BluetoothGattCharacteristic characteristic, @NonNull byte[] value, int status) {
            super.onCharacteristicRead(gatt, characteristic, value, status);

            if (status == BluetoothGatt.GATT_SUCCESS)
            {
                byte[] lData = characteristic.getValue();
                mListener.OnDataReceived(lData);
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            if (status == BluetoothGatt.GATT_SUCCESS)
            {
                // La escritura fue exitosa
            }
        }
    };


    public BluetoothCommunication(Context aContext, BluetoothDevice aDevice, BluetoothCommunicationListener aListener){
        mContext = aContext;
        mDevice = aDevice;
        mListener = aListener;
    }

    @SuppressLint("MissingPermission")
    public void StartCommunication(){
        mIsConnected = false;
        mBluetoothGatt = mDevice.connectGatt(mContext, false, mBluetoothGattCallback);
        mListener.OnStartCommunication();
    }

    @SuppressLint("MissingPermission")
    public void WriteData(byte[] aData){
        if (mBluetoothGattCharacteristic != null){
            mBluetoothGattCharacteristic.setValue(aData);
            mBluetoothGatt.writeCharacteristic(mBluetoothGattCharacteristic);
        }
    }

    @SuppressLint("MissingPermission")
    public void EndCommunication(){
        if (mBluetoothGatt != null)
            mBluetoothGatt.disconnect();
    }

    public BluetoothDevice GetDevice(){
        return mDevice;
    }
}
