#include "BluetoothController.h"

BluetoothController::BluetoothController(const char* aDeviceName)
{
  mDeviceName = aDeviceName;
}

void BluetoothController::ValueReceivedCallback(bool (*aCallback)(unsigned int)){
  mCallback = aCallback;
}

void BluetoothController::Init(){
  
  Serial.println("[BLUETOOTH CONTROLLER] Initializing device...");
  mIsConnected = false;
  
  // begin initialization
  if (!BLE.begin()) {
    Serial.println("[BLUETOOTH CONTROLLER] Low Energy module failed!");

    while (1);
  }

  BLE.setLocalName(mDeviceName);// set the local name peripheral advertises
  BLE.setAdvertisedService(mBluetoothService);// set the UUID for the service this peripheral advertises:

  mBluetoothService.addCharacteristic(mEngineCharacteristic);// add the characteristics to the service

  BLE.addService(mBluetoothService);

  mEngineCharacteristic.writeValue(0);

  // start advertising
  BLE.advertise();
  Serial.println("[BLUETOOTH CONTROLLER] Device active, waiting for connections.");
}
  
void BluetoothController::Update(){
  if (BLE.connected()){
    // poll for Bluetooth® Low Energy events
    BLE.poll();
    if (!mIsConnected){
      mIsConnected = true;
      Serial.println("[BLUETOOTH CONTROLLER] Bluetooth Connected");
    }

    if (mEngineCharacteristic.written()){
      mCallback(mEngineCharacteristic.value());
      //__raise OnActionReceived(mEngineCharacteristic.value);
    }

  }
  else if (mIsConnected){
    mIsConnected = false;
    Serial.println("[BLUETOOTH CONTROLLER] Bluetooth Disconnected");
  }
}
