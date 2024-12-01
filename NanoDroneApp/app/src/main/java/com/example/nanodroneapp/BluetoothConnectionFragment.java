package com.example.nanodroneapp;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.nanodroneapp.Bluetooth.BluetoothController;
import com.example.nanodroneapp.Bluetooth.BluetoothLEListener;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.concurrent.TimeUnit;


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
        public boolean IsSelected = false;

        private View mView;

        public BluetoothItem(String aName, String aAddress){
            mName = aName;
            mAddress = aAddress;
            IsSelected = false;
        }

        public String getName(){ return mName;}
        public String getAddress(){ return mAddress;}
    }

    private View mView;

    private TextView GetScannerStatus(){ return mView.findViewById(R.id.ScannerStatus); }
    private TextView GetConnectedDeviceName(){ return mView.findViewById(R.id.ConnectedDeviceName); }
    private Button GetConnectButton(){ return mView.findViewById(R.id.BluetoothConnectButton); }
    private Button GetScanButton(){ return mView.findViewById(R.id.BluetoothScanButton); }


    private BluetoothItem mSelectedBluetoothDevice = null;

    private static final String TAG = "BluetoothConnectionFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bluetooth, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mView = view;

        //Initilize buttons
        GetScanButton().setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                if (mAdapter != null){
                    BluetoothController.Instance().Scan();
                }
            }
        });

        GetConnectButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Connect to the selected device int list
                if (mSelectedBluetoothDevice != null){
                    BluetoothController.Instance().Connect(mSelectedBluetoothDevice.getAddress());
                }
            }
        });

        // Initilialize the list
        ListView lList = (ListView) view.findViewById(R.id.BluetoothDevicesList);
        mDevices = new Hashtable<String, String>();
        mAdapter = new BluetoothItemAdapter(getContext(), R.layout.bluetoothdeviceitem, new ArrayList<BluetoothItem>());

        // Set the adapter to the list
        lList.setAdapter(mAdapter);

        // Initialize the Bluetooth Controller and set the callback listener
        BluetoothFragmentListener lBluetoothFragmentListener = new BluetoothFragmentListener();
        BluetoothController.Instance().Init(getContext());
        BluetoothController.Instance().SetListener(lBluetoothFragmentListener);
    }

    @Override
    public void onDestroyView() {
        GetConnectButton().setOnClickListener(null);
        GetScanButton().setOnClickListener(null);
        super.onDestroyView();
    }

    private class BluetoothFragmentListener extends BluetoothLEListener {

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
            GetScannerStatus().setText(getString(R.string.Bluetooth_settings_scanning));
        }

        @Override
        public void OnStopScan() {
            GetScannerStatus().setText(getString(R.string.Bluetooth_settings_scan_completed));
        }

        @Override
        public void OnStartConnection() {
            GetScannerStatus().setText("Connecting...");
        }

        @Override
        public void OnConnect() {
            GetScannerStatus().setText("Connected");
            GetConnectedDeviceName().setText(BluetoothController.Instance().GetDeviceName());
        }

        @Override
        public void OnDisconnect() {
            //mConnectedDeviceName.setText(getString(R.string.Default_device_name));
        }
    }


    // Custrom ArrayAdapter to show all the bluetooth device info
    private class BluetoothItemAdapter extends ArrayAdapter<BluetoothItem> {

        private Context mContext;
        private int mResourceID;

        public BluetoothItemAdapter(Context aContext, int aResourceID, ArrayList<BluetoothItem> aItems){
            super(aContext, aResourceID, aItems);
            mContext = aContext;
            mResourceID = aResourceID;
        }

        public void SelectItem(int aPosition){
            int lCount = getCount();
            for (int i = 0; i < lCount; ++i) {
                getItem(i).IsSelected = (i == aPosition);
            }
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

            lView.setBackgroundColor(lItem.IsSelected ? Color.LTGRAY : Color.TRANSPARENT);

            //Set OnClick listener to the item
            lView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    BluetoothItem lItem = getItem(aPosition);
                    mSelectedBluetoothDevice = lItem;
                    SelectItem(aPosition);
                    mAdapter.notifyDataSetChanged();
                }
            });

            return lView;
        }
    }

}

