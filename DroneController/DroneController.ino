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

bool OnBluetoothReceived(unsigned int aData){

  unsigned int lMode = (aData >> 31);
  unsigned int lEngine1 = (aData >> 24) & 127;
  unsigned int lEngine2 = (aData >> 17) & 127;
  unsigned int lEngine3 = (aData >> 10) & 127;
  unsigned int lEngine4 = (aData >> 3) & 127;
  
  // CHECK DATA MODE
  if(lMode == 0){ // ADD MODE
   Serial.print(" -ADD MODE- ");
  }
  else{ // SET MODE
   Serial.print(" -SET MODE- ");
  }

  Serial.print(" Engine1: ");
  Serial.print(lEngine1);
  Serial.print(" Engine2: ");
  Serial.print(lEngine2);
  Serial.print(" Engine3: ");
  Serial.print(lEngine3);
  Serial.print(" Engine4: ");
  Serial.println(lEngine4);

  mEngineController.SetEnginePower(EngineController::EngineID::FR_ENGINE, (lEngine1/100) * 255);
  mEngineController.SetEnginePower(EngineController::EngineID::FL_ENGINE, (lEngine2/100) * 255);
  mEngineController.SetEnginePower(EngineController::EngineID::RR_ENGINE, (lEngine3/100) * 255);
  mEngineController.SetEnginePower(EngineController::EngineID::RL_ENGINE, (lEngine4/100) * 255);
}

void setup() {
  Serial.begin(9600);
  while (!Serial);
  mCurrentMillis = millis();
  mBluetoothController.Init();
  mEngineController.Init();
  mBluetoothController.ValueReceivedCallback(OnBluetoothReceived);

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
  mBluetoothController.Update();
  mEngineController.Update();

}

long UpdateLoopTime(){
  mLastMillis = mCurrentMillis;
  mCurrentMillis = millis();
  return mCurrentMillis - mLastMillis;
}