#include <ArduinoBLE.h>
#include <stdio.h>

class BluetoothController{

  private:
  BLEService mBluetoothService{"800713BC-3AF7-4CA1-9029-CA765444188F"};
  BLEByteCharacteristic mEngineCharacteristic{"C2422EFF-4C8B-49C2-9116-196FA98101E7", BLERead | BLEWrite};
  bool mIsConnected = false;
  const char* mDeviceName;

  public:
  BluetoothController(const char* aDeviceName);
  
  void Init();
  void Update();

};