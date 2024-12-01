package com.example.nanodroneapp;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.nanodroneapp.Bluetooth.BluetoothController;
import com.example.nanodroneapp.Bluetooth.BluetoothScannerListener;

import java.util.ArrayList;
import java.util.Hashtable;


public class BluetoothConnectionFragment extends Fragment {

    // Adapter to create the device list
    BluetoothItemAdapter mAdapter;

    // Bluetooth devices
    // - KEY : device address
    // - VALUE: device name
    private Hashtable<String, String> mDevices;

    //Used to pass Bluetooth item info to the adapter
    private class BluetoothItem{
        private String mName;
        private String mAddress;
        public BluetoothItem(String aName, String aAddress){
            mName = aName;
            mAddress = aAddress;
        }

        public String getName(){ return mName;}
        public String getAddress(){ return mAddress;}
    }

    private TextView mScannerStatus;
    private BluetoothItem mSelectedBluetoothDevice = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bluetooth, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Intitialize the buttons
        Button lScanButton = (Button) view.findViewById(R.id.BluetoothScanButton);
        Button lConnectButton = (Button) view.findViewById(R.id.BluetoothConnectButton);

        lScanButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                if (mAdapter != null){
//                    mSelectedBluetoothDevice = null;
//                    mAdapter.clear();
//                    mAdapter.notifyDataSetChanged();
                    BluetoothController.Instance().Scan();
                }
            }
        });

        lConnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Connect to the selected device int list
                if (mSelectedBluetoothDevice != null){
                    BluetoothController.Instance().Connect(mSelectedBluetoothDevice.getAddress());
                }
            }
        });

        //Get scan status text view
        mScannerStatus = (TextView) view.findViewById(R.id.ScannerStatus);


        // Initilialize the list
        ListView lList = (ListView) view.findViewById(R.id.BluetoothDevicesList);
        mDevices = new Hashtable<String, String>();
        mAdapter = new BluetoothItemAdapter(getContext(), R.layout.bluetoothdeviceitem, new ArrayList<BluetoothItem>());

        // Set the adapter to the list
        lList.setAdapter(mAdapter);

        // Initialize the Bluetooth Controller and set the callback listener
        BluetoothFragmentListener lBluetoothFragmentListener = new BluetoothFragmentListener();
        BluetoothController.Instance().Init(getContext());
        BluetoothController.Instance().AddListener(lBluetoothFragmentListener);

    }

    private class BluetoothFragmentListener extends BluetoothScannerListener {

        @SuppressLint("MissingPermission")
        @Override
        public void OnDeviceDetected(BluetoothDevice aDevice) {
            if (aDevice.getName() != null && !mDevices.containsKey(aDevice.getAddress())){
                BluetoothItem lDevice = new BluetoothItem(aDevice.getName(), aDevice.getAddress());
                mDevices.put(lDevice.getAddress(), lDevice.getName());
                mAdapter.add(lDevice);
                mAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void OnStartScan() {
            mScannerStatus.setText(getString(R.string.Bluetooth_settings_scanning));
        }

        @Override
        public void OnStopScan() {
            mScannerStatus.setText(getString(R.string.Bluetooth_settings_scan_completed));
        }
    }


    // Custrom ArrayAdapter to show all the bluetooth device info
    class BluetoothItemAdapter extends ArrayAdapter<BluetoothItem> {

        private Context mContext;
        private int mResourceID;

        public BluetoothItemAdapter(Context aContext, int aResourceID, ArrayList<BluetoothItem> aItems){
            super(aContext, aResourceID, aItems);
            mContext = aContext;
            mResourceID = aResourceID;
        }

        @NonNull
        @Override
        public View getView(int aPosition, @Nullable View aConvertView, @NonNull ViewGroup aParent) {
            View lView = aConvertView;
            if (lView == null) {
                lView = LayoutInflater.from(mContext).inflate(mResourceID, aParent, false);
            }

            // Get the new item of the Adapter and set all the values
            BluetoothItem lItem = getItem(aPosition);

            TextView lDeviceNameTextView = lView.findViewById(R.id.BluetoothDeviceName);
            TextView lDeviceAdressTextView = lView.findViewById(R.id.BluetoothDeviceAddress);

            lDeviceNameTextView.setText(lItem.getName());
            lDeviceAdressTextView.setText(lItem.getAddress());

            //Set OnClick listener to the item
            lView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    BluetoothItem lItem = getItem(aPosition);
                    mSelectedBluetoothDevice = lItem;
                }
            });

            return lView;
        }
    }
}

