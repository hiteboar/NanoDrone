#include <ArduinoBLE.h>
#include <stdio.h>

class BluetoothController{

  private:
  BLEService mBluetoothService{"800713BC-3AF7-4CA1-9029-CA765444188F"};
  BLEByteCharacteristic mEngineCharacteristic{"800713BC-3AF7-4CA1-9029-CA765444188F", BLERead | BLEWrite};
  bool mIsConnected = false;
  const char* mDeviceName;
  //bool (*mCallback)(unsigned char);

  public:
  BluetoothController(const char* aDeviceName);
  
  //void ValueReceivedCallback(bool (*aCallback)(unsigned char));

  void Init();
  void Update();

};