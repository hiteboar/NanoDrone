#include "Engine.h"

Engine::Engine(pin_size_t aEnginePin){
  mPin = aEnginePin;
}

void Engine::Init(){
  pinMode(mPin, OUTPUT);
}

void Engine::Update(){
  analogWrite(mPin, mCurrentPower);
}

void Engine::SetPower(int aPower){
  mCurrentPower = aPower;
}

void Engine::AddPower(int aPower){
  mCurrentPower += aPower;
}

int Engine::GetPower(){
  return mCurrentPower;
}