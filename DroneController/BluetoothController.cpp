#include "BluetoothController.h"

BluetoothController::BluetoothController()
  : mService("19B10001-E8F2-537E-4F6C-D104768A1214"),
    mCharacteristic("19B10001-E8F2-537E-4F6C-D104768A1214", BLERead | BLENotify | BLEWrite, 4),
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

    mService.addCharacteristic(mCharacteristic);
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
  if (!bleAvailable || !connected) {
    return;  // No enviar si BLE no está listo o nadie está conectado
  }
  _yaw = yaw;
  _pitch = pitch;
  _roll = roll;
  _altitude = altitude;
}
  
void BluetoothController::sendFlightData() {
  if (!bleAvailable || !connected) {
    return;  // No enviar si BLE no está listo o nadie está conectado
  }
  
  // Empaquetar en orden: YAW | PITCH | ROLL | ALTITUDE
  uint8_t buffer[4];
  buffer[0] = _yaw;
  buffer[1] = _pitch;
  buffer[2] = _roll;
  buffer[3] = _altitude;

  mCharacteristic.writeValue(buffer, sizeof(buffer));
}

// BluetoothController.cpp
void BluetoothController::processIncomingData() {
  if (!connected) return;

  // ¿El central escribió algo en esta characteristic?
  if (mCharacteristic.written()) {
    int len = mCharacteristic.valueLength();      // (1) longitud real recibida
    const uint8_t* data = mCharacteristic.value(); // (2) puntero al buffer interno

    if (len >= 4) {
      uint8_t yaw      = data[0];
      uint8_t pitch    = data[1];
      uint8_t roll     = data[2];
      uint8_t altitude = data[3];

      // Ahora haces lo que necesites con estos 4 bytes:
      Serial.print("From central → ");
      Serial.print(yaw);      Serial.print('-');
      Serial.print(pitch);    Serial.print('-');
      Serial.print(roll);     Serial.print('-');
      Serial.println(altitude);
    }
  }
}


