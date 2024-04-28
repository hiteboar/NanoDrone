#include <MadgwickAHRS.h>
#include <Arduino_LSM6DS3.h>

class FlySensor{
  private:
    float mSampleFreq = 0; // Update freq of the sensor
    float mYaw, mPitch, mRoll; // Yaw, pitch and roll values
    Madgwick filter; // filter needed to calculate the yaw pitch and roll of the borad

    void ReadIMUValues(float &aPitch, float &aRoll, float &aHeading);

  public:
    FlySensor(float aSampleFreq);

    void Init();
    void Update();

    float Pitch();
    float Yaw();
    float Roll();
};