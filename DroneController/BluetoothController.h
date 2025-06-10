#ifndef BLUETOOTHCONTROLLER_H
#define BLUETOOTHCONTROLLER_H

#include <ArduinoBLE.h>

class BluetoothController {
public:
    BluetoothController();
    void begin();
    void statusUpdate();
    void UpdateDataToSend(uint8_t yaw, uint8_t pitch, uint8_t roll, uint8_t altitude);

private:
    void tryInitBLE();
    void sendFlightData();
    void processIncomingData();

    uint8_t _yaw;
    uint8_t _pitch;
    uint8_t _roll;
    uint8_t _altitude;

    BLEService mService;
    BLECharacteristic mCharacteristic;
    BLEDevice central;
    bool connected;
    bool bleAvailable;
};

#endif
