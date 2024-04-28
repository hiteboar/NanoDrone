#include "EngineController.h"
#include "BluetoothController.h"
#include "FlySensor.h"
#include "Arduino.h"

// LOOP
#define LOOP_TIME 10 // target delta time in ms

// IMU
#define SENSOR_RATE 104.00

FlySensor mFlySensor(SENSOR_RATE);
EngineController mEngineController;
BluetoothController mBluetoothController("NanoDrone");

long mCurrentMillis = 0;
long mLastMillis = 0;

void setup() {
  Serial.begin(9600);
  while (!Serial);
  mCurrentMillis = millis();
  mEngineController.Init();
  mBluetoothController.Init();

  //Init fly sensor
  Serial.println("Init fly sensor...");
  mFlySensor.Init();
}

void loop() {
  long lDeltaTime = UpdateLoopTime();
  if (lDeltaTime < LOOP_TIME){
    delay(LOOP_TIME - lDeltaTime);
  }

  mFlySensor.Update();
  mEngineController.Update();
  mBluetoothController.Update();

}

long UpdateLoopTime(){
  mLastMillis = mCurrentMillis;
  mCurrentMillis = millis();
  return mCurrentMillis - mLastMillis;
}