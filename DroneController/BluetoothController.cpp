#include <ArduinoBLE.h>
#include "BluetoothController.h"
#include "LedDebug.h"

BluetoothController::BluetoothController(const char* aDeviceName)
{
  mDeviceName = aDeviceName;
}

void BluetoothController::ValueReceivedCallback(bool (*aCallback)(unsigned int)){
  mCallback = aCallback;
}

void BluetoothController::Init(){
  
  //Serial.println("[BLUETOOTH CONTROLLER] Initializing device...");
  mIsConnected = false;
  mIsActive = false;
  // begin initialization
  BLE.end();
  if (!BLE.begin()) {
    //Serial.println("[BLUETOOTH CONTROLLER] Low Energy module failed!");
    LedDebug::GetInstance()->AddError(LedDebug::ErrorType::BLUETOOTH_ERROR);
    return;
  }

  if (LedDebug::GetInstance()->ContainsError(LedDebug::ErrorType::BLUETOOTH_ERROR)){
    LedDebug::GetInstance()->RemoveError(LedDebug::ErrorType::BLUETOOTH_ERROR);
  }
  
  BLE.setLocalName(mDeviceName);// set the local name peripheral advertises
  BLE.setAdvertisedService(mBluetoothService);// set the UUID for the service this peripheral advertises:

  mBluetoothService.addCharacteristic(mEngineCharacteristic);// add the characteristics to the service

  BLE.addService(mBluetoothService);

  mEngineCharacteristic.writeValue(0);

  mIsActive = true;

  // start advertising
  BLE.advertise();
  LedDebug::GetInstance()->AddError(LedDebug::ErrorType::BLUETOOTH_DISCONNECTED);
  //Serial.println("[BLUETOOTH CONTROLLER] Device active, waiting for connections.");
}
  
void BluetoothController::Update(){
  if (BLE.connected()){
    // poll for Bluetooth® Low Energy events
    BLE.poll();
    if (!mIsConnected){
      mIsConnected = true;
    LedDebug::GetInstance()->RemoveError(LedDebug::ErrorType::BLUETOOTH_DISCONNECTED);
    }

    if (mEngineCharacteristic.written()){
      //Serial.println("RECEIVED MESSAGE:");
      //Serial.println(mEngineCharacteristic.value());
      mCallback(mEngineCharacteristic.value());
    }

  }
  else if (mIsConnected){
    mIsConnected = false;
    if (!LedDebug::GetInstance()->ContainsError(LedDebug::ErrorType::BLUETOOTH_DISCONNECTED)){
      LedDebug::GetInstance()->AddError(LedDebug::ErrorType::BLUETOOTH_DISCONNECTED);
    }
    //Serial.println("[BLUETOOTH CONTROLLER] Bluetooth Disconnected");
  }
}

bool BluetoothController::IsActive(){
  return mIsActive;
}
