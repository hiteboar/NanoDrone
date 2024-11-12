#include "api/Common.h"
#include <algorithm>
#include <iostream>
#include <list>
#include <cmath>

#define ACTIVE_TIME 500
#define RESET_TIME 4000

class LedDebug {

public:
  enum ErrorType {
    NONE = 0,
    BLUETOOTH_ERROR = 1,
    ENGINE_ERROR = 2,
    IMU_ERROR = 3
  };

  void Init(int aLedPin);
  void Update();

  void AddError(ErrorType aError);
  void RemoveError(ErrorType aError);
  bool ContainsError(ErrorType aError);

  static LedDebug* GetInstance();

  LedDebug(LedDebug &other) = delete;
  void operator=(const LedDebug &) = delete;

protected:

  int mLedPin;

  LedDebug(){};
  static LedDebug* mInstance;

  std::list<ErrorType> mCurrentErrors;
  
  std::list<ErrorType>::iterator mErrorIterator;
  long mStartMillis;

  int GetCurrentErrorEffectTime();
  void UpdateCurrentError();

};