#include <ArduinoBLE.h>
#include <stdio.h>

class BluetoothController{

  private:
  BLEService mBluetoothService{"800713BC-3AF7-4CA1-9029-CA765444188F"};
  BLEIntCharacteristic mEngineCharacteristic{"800713BC-3AF7-4CA1-9029-CA765444188F", BLERead | BLEWrite};
  bool mIsConnected = false;
  bool mIsActive = false;
  const char* mDeviceName;
  bool (*mCallback)(unsigned int);

  public:
  BluetoothController(const char* aDeviceName);
  
  void ValueReceivedCallback(bool (*aCallback)(unsigned int));

  void Init();
  void Update();
  bool IsActive();

};