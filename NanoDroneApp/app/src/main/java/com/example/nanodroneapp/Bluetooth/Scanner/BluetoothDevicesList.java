package com.example.nanodroneapp.Bluetooth.Scanner;

import android.bluetooth.BluetoothDevice;

import java.util.Enumeration;
import java.util.Hashtable;

public class BluetoothDevicesList {

    Hashtable<String, BluetoothDevice> mDevices;

    public BluetoothDevicesList(){
        mDevices = new Hashtable<String, BluetoothDevice>();
    }

    public void OnAddDevice(BluetoothDevice aDevice){
        String lAdress = aDevice.getAddress();
        if (!mDevices.containsKey(lAdress)){
            mDevices.put(lAdress, aDevice);
        }
    }

    public BluetoothDevice GetDevice(String aAdress){
        if (mDevices.containsKey(aAdress)) return mDevices.get(aAdress);
        return null;
    }

    public void ClearList(){
        mDevices.clear();
    }

    public int GetDeviceCount(){
        return mDevices.size();
    }

    public String[] GetDeviceList(){
        String[] lAdresses = new String[GetDeviceCount()];
        Enumeration<String> lKeys = mDevices.keys();
        int lIndex = 0;
        while (lKeys.hasMoreElements() && lIndex < lAdresses.length) {
            String lAdress = lKeys.nextElement();
            lAdresses[lIndex] = lAdress;
        }
        return lAdresses;
    }
}
