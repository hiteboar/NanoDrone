package com.example.nanodroneapp.Bluetooth;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BluetoothReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
         String lAction = intent.getAction();
         System.out.println("[BLUETOOTH] ACTION RECEIVED: " + lAction);
    }
}
