#include "EngineController.h"
#include "BluetoothController.h"
#include "FlySensor.h"
#include "Arduino.h"
#include "LedDebug.h"

// LOOP
#define LOOP_TIME 500               // target delta time in ms
#define SERIAL_CHECK_TIMEOUT 2000  // max time to connect with the Serial Monitor

// IMU
#define SENSOR_RATE 104.00

FlySensor mFlySensor(SENSOR_RATE);
EngineController mEngineController;
BluetoothController mBluetoothController;

long mCurrentMillis = 0;
long mLastMillis = 0;

/*
bool OnBluetoothReceived(BluetoothController::ReceivedData aData) {
  mEngineController.SetEnginePower(EngineController::EngineID::FR_ENGINE, aData.Engine1);
  mEngineController.SetEnginePower(EngineController::EngineID::FL_ENGINE, aData.Engine2);
  mEngineController.SetEnginePower(EngineController::EngineID::RR_ENGINE, aData.Engine3);
  mEngineController.SetEnginePower(EngineController::EngineID::RL_ENGINE, aData.Engine4);
}
*/

void setup() {
  Serial.begin(9600);
  while (!Serial);

  LedDebug::GetInstance()->Init(13);

  mCurrentMillis = millis();
  mEngineController.Init();
  mFlySensor.Init();
  mBluetoothController.begin();
}

void loop() {
  long lDeltaTime = UpdateLoopTime();
  if (lDeltaTime < LOOP_TIME) {
    delay(LOOP_TIME - lDeltaTime);
  }

  if (mFlySensor.IsActive()){
    mFlySensor.Update();
  }
  else{
    mFlySensor.Init();
  }

  mBluetoothController.statusUpdate();
  mEngineController.Update();

  LedDebug::GetInstance()->Update();

  mBluetoothController.UpdateDataToSend(mFlySensor.Yaw(), mFlySensor.Pitch(), mFlySensor.Roll(), 0);
}

long UpdateLoopTime() {
  mLastMillis = mCurrentMillis;
  mCurrentMillis = millis();
  return mCurrentMillis - mLastMillis;
}