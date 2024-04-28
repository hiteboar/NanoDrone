#include "BluetoothController.h"

BluetoothController::BluetoothController(const char* aDeviceName)
{
  mDeviceName = aDeviceName;
}
  
void BluetoothController::Init(){
  mIsConnected = false;

  BLE.setLocalName(mDeviceName);// set the local name peripheral advertises
  BLE.setAdvertisedService(mBluetoothService);// set the UUID for the service this peripheral advertises:
  mBluetoothService.addCharacteristic(mEngineCharacteristic);// add the characteristics to the service

  BLE.addService(mBluetoothService);

  // start advertising
  BLE.advertise();
  Serial.println("Bluetooth® device active, waiting for connections...");
}
  
void BluetoothController::Update(){
  if (BLE.connected()){
    // poll for Bluetooth® Low Energy events
    BLE.poll();
    if (!mIsConnected){
      mIsConnected = true;
      Serial.println("Bluetooth Connected");
    }

    if (mEngineCharacteristic.written()){
     // __raise OnActionReceived(mEngineCharacteristic.value);
    }

  }
  else if (mIsConnected){
    mIsConnected = false;
    Serial.println("Bluetooth Disconnected");
  }
}