#include "api/Common.h"
class Engine {
  private:
    int mCurrentPower = 0;
    pin_size_t mPin;

  public:
    Engine(pin_size_t aEnginePin);

    void Init();
    void Update();

    void SetPower(int aPower);
    void AddPower(int aPower);
    int GetPower();
};