#include "LedDebug.h"
#include "Arduino.h"

LedDebug* LedDebug::mInstance = nullptr;

LedDebug* LedDebug::GetInstance(){
  if (mInstance == nullptr){
    mInstance = new LedDebug();
  }
  return mInstance;
}

void LedDebug::Init(int aLedPin){
  mLedPin = aLedPin;
  mStartMillis = 0;
  pinMode(mLedPin, OUTPUT);
}

void LedDebug::Update(){
  if (mCurrentErrors.size() == 0){
    mStartMillis = 0;
    digitalWrite(mLedPin, HIGH);
  }
  else{
    UpdateCurrentError();
  }
}

void LedDebug::AddError(ErrorType aError){
  if (!ContainsError(aError)){
    mCurrentErrors.push_back(aError);
  }
}

void LedDebug::RemoveError(ErrorType aError){
  auto it = std::find(mCurrentErrors.begin(), mCurrentErrors.end(), aError);
  if (it == mCurrentErrors.end()) return;
  mCurrentErrors.erase(it);
}

bool LedDebug::ContainsError(ErrorType aError){
  return (std::find(mCurrentErrors.begin(), mCurrentErrors.end(), aError) != mCurrentErrors.end());
}

int LedDebug::GetCurrentErrorEffectTime(){
  int lErrorValue = *mErrorIterator;
  return (2*lErrorValue - 1)*ACTIVE_TIME + RESET_TIME;
}

void LedDebug::UpdateCurrentError(){
  if (mCurrentErrors.size() > 0){
    if (mStartMillis == 0){
      mErrorIterator = mCurrentErrors.begin();
      mStartMillis = millis();
    }
    else{
      long lElapsedMillis = millis() - mStartMillis;
      int lErrorTime = GetCurrentErrorEffectTime();
      if (lElapsedMillis >= lErrorTime - RESET_TIME){
        digitalWrite(mLedPin, LOW);
        if (lElapsedMillis >= lErrorTime){
          mStartMillis = millis();
          ++mErrorIterator;
          if (mErrorIterator == mCurrentErrors.end()){ // if there is no more errors, restart the list
            mErrorIterator = mCurrentErrors.begin();
          }
        }
      }
      else{
        int lCurrentState = std::floor(lElapsedMillis / ACTIVE_TIME);
        bool lActiveState = lCurrentState % 2 == 0;
        if (lActiveState){
          digitalWrite(mLedPin, HIGH);
        }
        else{
          digitalWrite(mLedPin, LOW);
        }
      }
    }
  }
}