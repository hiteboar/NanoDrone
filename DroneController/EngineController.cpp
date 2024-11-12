#include <utility>
#include "variant.h"
#include "EngineController.h"
#include "Arduino.h"

EngineController::EngineController(){
  mEngines.insert(std::make_pair(EngineID::FR_ENGINE, new Engine(PIN_FR_MOTOR)));
  mEngines.insert(std::make_pair(EngineID::FL_ENGINE, new Engine(PIN_FL_MOTOR)));
  mEngines.insert(std::make_pair(EngineID::RR_ENGINE, new Engine(PIN_RR_MOTOR)));
  mEngines.insert(std::make_pair(EngineID::RL_ENGINE, new Engine(PIN_RL_MOTOR)));
}

void EngineController::Init(){
  //Serial.println("[ENGINE CONTROLLER] Init Engines...");
  mEngines[EngineID::FR_ENGINE]->Init();
  mEngines[EngineID::FL_ENGINE]->Init();
  mEngines[EngineID::RR_ENGINE]->Init();
  mEngines[EngineID::RL_ENGINE]->Init();
  //Serial.println("[ENGINE CONTROLLER] Init Done!");
}

void EngineController::Update(){
  mEngines[EngineID::FR_ENGINE]->Update();
  mEngines[EngineID::FL_ENGINE]->Update();
  mEngines[EngineID::RR_ENGINE]->Update();
  mEngines[EngineID::RL_ENGINE]->Update();
}

void EngineController::SetEnginePower(EngineID aEngine, int aPower){
  mEngines[aEngine]->SetPower(aPower);
}

void EngineController::AddEnginePower(EngineID aEngine, int aPower){
  mEngines[aEngine]->AddPower(aPower);
}

void EngineController::DoMove(MovementID aMove, int speed){
  
}