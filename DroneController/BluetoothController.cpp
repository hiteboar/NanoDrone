#include "BluetoothController.h"

BluetoothController::BluetoothController()
  : mService("19B10000-E8F2-537E-4F6C-D104768A1214"), // Servicio base
    rxCharacteristic("19B10001-E8F2-537E-4F6C-D104768A1214", BLEWrite, 4),   // Central → Arduino
    txCharacteristic("19B10002-E8F2-537E-4F6C-D104768A1214", BLENotify, 4),  // Arduino → Central
    connected(false),
    bleAvailable(false)
{}

void BluetoothController::begin() {
  tryInitBLE();
}

void BluetoothController::tryInitBLE() {
  if (BLE.begin()) {
    BLE.setLocalName("NanoDrone");
    BLE.setAdvertisedService(mService);

    mService.addCharacteristic(rxCharacteristic);
    mService.addCharacteristic(txCharacteristic);
    BLE.addService(mService);

    BLE.advertise();
    Serial.println("BLE started. Advertising as NanoDrone...");
    bleAvailable = true;
  } else {
    Serial.println("BLE init failed. Will retry...");
    bleAvailable = false;
  }
}

void BluetoothController::statusUpdate() {
  if (!bleAvailable) {
    tryInitBLE(); // Reintentar si BLE aún no está disponible
    return;
  }

  BLEDevice newCentral = BLE.central();

  if (newCentral && !connected) {
    central = newCentral;
    connected = true;
    Serial.print("Device connected: ");
    Serial.println(central.address());
    return;
  }

  if (connected && !central.connected()) {
    Serial.println("Device disconnected.");
    connected = false;
    return;
  }

  if (!connected) return;

  sendFlightData();
  processIncomingData();
}

void BluetoothController::UpdateDataToSend(uint8_t yaw, uint8_t pitch, uint8_t roll, uint8_t altitude) {
  if (!bleAvailable || !connected) return;

  _yaw = yaw;
  _pitch = pitch;
  _roll = roll;
  _altitude = altitude;
}

void BluetoothController::sendFlightData() {
  if (!bleAvailable || !connected) return;

  uint8_t buffer[4] = { _yaw, _pitch, _roll, _altitude };
  txCharacteristic.writeValue(buffer, sizeof(buffer));
}

void BluetoothController::processIncomingData() {
  if (!connected) return;

  if (rxCharacteristic.written()) {
    const uint8_t* data = rxCharacteristic.value();
    int len = rxCharacteristic.valueLength();

    if (len >= 4) {
      uint8_t yaw      = data[0];
      uint8_t pitch    = data[1];
      uint8_t roll     = data[2];
      uint8_t altitude = data[3];

      Serial.print("From central → ");
      Serial.print(yaw);      Serial.print('-');
      Serial.print(pitch);    Serial.print('-');
      Serial.print(roll);     Serial.print('-');
      Serial.println(altitude);
    }
  }
}
