package com.example.nanodroneapp.Bluetooth;

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

import java.util.List;
import java.util.UUID;


public class BluetoothCommunication{

    private static final String TAG = "BluetoothCommunication";

    private Context mContext;
    private BluetoothDevice mDevice;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothGattService mBluetoothGattService;
    private BluetoothGattCharacteristic mBluetoothGattCharacteristic;

    private BluetoothDevice mConnectedDevice;

    private UUID CHARACTERISTIC_UUID = UUID.fromString("800713BC-3AF7-4CA1-9029-CA765444188F");
    private UUID SERVICE_UUID = UUID.fromString("800713BC-3AF7-4CA1-9029-CA765444188F");


    BluetoothGattCallback mBluetoothGattCallback = new BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                mConnectedDevice = gatt.getDevice();
                mBluetoothGattService = gatt.getService(SERVICE_UUID);
                mBluetoothGattCharacteristic = mBluetoothGattService.getCharacteristic(CHARACTERISTIC_UUID);
                Log.i(TAG, "Connected to device: " + mConnectedDevice.getName());
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                mConnectedDevice = null;
                Log.i(TAG, "Disconnected from device");
            }
        }

        @Override
        public void onCharacteristicRead(@NonNull BluetoothGatt gatt, @NonNull BluetoothGattCharacteristic characteristic, @NonNull byte[] value, int status) {
            super.onCharacteristicRead(gatt, characteristic, value, status);

            if (status == BluetoothGatt.GATT_SUCCESS)
            {
                byte[] data = characteristic.getValue();
                OnDataReceived(data);
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


    public BluetoothCommunication(Context aContext, BluetoothDevice aDevice){
        mContext = aContext;
        mDevice = aDevice;
    }

    @SuppressLint("MissingPermission")
    public void StartCommunication(){
        mBluetoothGatt = mDevice.connectGatt(mContext, false, mBluetoothGattCallback);
    }

    @SuppressLint("MissingPermission")
    public void WriteData(byte[] aData){
        if (mBluetoothGattCharacteristic != null){
            mBluetoothGattCharacteristic.setValue(aData);
            mBluetoothGatt.writeCharacteristic(mBluetoothGattCharacteristic);
        }
    }

    public void OnDataReceived(byte[] aData){}

    @SuppressLint("MissingPermission")
    public void EndCommunication(){
        mBluetoothGatt.disconnect();
    }
}
