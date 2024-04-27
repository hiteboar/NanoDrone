#include <Arduino_LSM6DS3.h>
#include <MadgwickAHRS.h>

// LOOP
#define LOOP_TIME 10 // target delta time in ms

// IMU
#define SENSOR_RATE 104.00

// ENGINE
#define PIN_FR_MOTOR 16 // Front-Right Engine pin
#define PIN_RR_MOTOR 17 // Rear-Right Engine pin
#define PIN_FL_MOTOR 5 // Front-Left Engine pin
#define PIN_RL_MOTOR 6 // Rear-Left Engine pin


class Engine {
  private:
    int mCurrentPower = 0;
    pin_size_t mPin;

  public:
    Engine(pin_size_t aEnginePin){
      mPin = aEnginePin;
    }

    void Init(){
      pinMode(mPin, OUTPUT);
    }

    void Update(){
      analogWrite(mPin, mCurrentPower);
    }

    void SetPower(int aPower){
      mCurrentPower = aPower;
    }

    void AddPower(int aPower){
      mCurrentPower += aPower;
    }

    int GetPower(){
      return mCurrentPower;
    }
};

class FlySensor{
  private:
    float mSampleFreq = 0;
    Madgwick filter; // initialize a Madgwick filter:
    float mYaw, mPitch, mRoll;

    void ReadIMUValues(float &aPitch, float &aRoll, float &aHeading){
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

  public:
    FlySensor(float aSampleFreq){
      mSampleFreq = aSampleFreq;
      
    }

    void Init(){
      Serial.println("Starting IMU...");
      if (!IMU.begin()) {
        while (1);
      }
      
      // start the filter to run at the sample rate:
      filter.begin(SENSOR_RATE);
      Serial.println("IMU sensor rate set at " + String(mSampleFreq));

      Serial.print("Gyroscope sample rate = ");
      Serial.print(IMU.gyroscopeSampleRate());
      Serial.println("Hz");

      Serial.print("Accelerometer sample rate = ");
      Serial.print(IMU.accelerationSampleRate());
      Serial.println("Hz");

      Serial.println("IMU Start: DONE!");
    }

    void Update(){
      ReadIMUValues(mPitch, mRoll, mYaw);
    }

    float Pitch(){
      return mPitch;
    }

    float Yaw(){
      return mYaw;
    }

    float Roll(){
      return mRoll;
    }
};


Engine mFREngine(PIN_FR_MOTOR);
Engine mFLEngine(PIN_FL_MOTOR);
Engine mRREngine(PIN_RR_MOTOR);
Engine mRLEngine(PIN_RL_MOTOR);

FlySensor mFlySensor(SENSOR_RATE);

long mCurrentMillis = 0;
long mLastMillis = 0;

void setup() {
  Serial.begin(9600);
  while (!Serial);
  mCurrentMillis = millis();

  // Init Engines
  Serial.println("Init Engines...");
  mFREngine.Init();
  mFLEngine.Init();
  mRREngine.Init();
  mRLEngine.Init();
  
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
  
  mFREngine.Update();
  mFLEngine.Update();
  mRREngine.Update();
  mRLEngine.Update();

  Serial.println("Yaw: "+ String(mFlySensor.Yaw()) + " Pitch: " + String(mFlySensor.Pitch()) + " Roll: " + String(mFlySensor.Roll()));
}

long UpdateLoopTime(){
  mLastMillis = mCurrentMillis;
  mCurrentMillis = millis();
  return mCurrentMillis - mLastMillis;
}