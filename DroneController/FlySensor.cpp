#include "api/Common.h"
#include "FlySensor.h"
#include "Arduino.h"

void FlySensor::ReadIMUValues(float &aPitch, float &aRoll, float &aHeading){
  // values for acceleration and rotation:
  float lXAcc, lYAcc, lZAcc;
  float lXGyro, lYGyro, lZGyro;

  // check if the IMU is ready to read:
  if (IMU.accelerationAvailable() && IMU.gyroscopeAvailable()) {
    // read accelerometer &and gyrometer:
    IMU.readAcceleration(lXAcc, lYAcc, lZAcc);
    IMU.readGyroscope(lXGyro, lYGyro, lZGyro);

    // update the filter, which computes orientation:
    filter.updateIMU(lXGyro, lYGyro, lZGyro, lXAcc, lYAcc, lZAcc);

    // print the heading, pitch and roll
    aRoll = filter.getRoll();
    aPitch = filter.getPitch();
    aHeading = filter.getYaw();
  }
}

FlySensor::FlySensor(float aSampleFreq){
  mSampleFreq = aSampleFreq;
}

void FlySensor::Init(){
  Serial.println("Starting IMU...");
  if (!IMU.begin()) {
    while (1);
  }
        
  // start the filter to run at the sample rate:
  filter.begin(mSampleFreq);
  Serial.println("IMU sensor rate set at " + String(mSampleFreq));

  Serial.print("Gyroscope sample rate = ");
  Serial.print(IMU.gyroscopeSampleRate());
  Serial.println("Hz");

  Serial.print("Accelerometer sample rate = ");
  Serial.print(IMU.accelerationSampleRate());
  Serial.println("Hz");

  Serial.println("IMU Start: DONE!");
}

void FlySensor::Update(){
  ReadIMUValues(mPitch, mRoll, mYaw);
  //Serial.println("Yaw: "+ String(mPitch) + " Pitch: " + String(mRoll) + " Roll: " + String(mYaw));
}

float FlySensor::Pitch(){
  return mPitch;
}

float FlySensor::Yaw(){
  return mYaw;
}

float FlySensor::Roll(){
  return mRoll;
}