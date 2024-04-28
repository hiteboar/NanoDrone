#include "Engine.h"
#include <map>

// ENGINE
#define PIN_FR_MOTOR 16 // Front-Right Engine pin
#define PIN_RR_MOTOR 17 // Rear-Right Engine pin
#define PIN_FL_MOTOR 5 // Front-Left Engine pin
#define PIN_RL_MOTOR 6 // Rear-Left Engine pin

class EngineController{
private:

public:
  enum EngineID {
    FR_ENGINE,
    FL_ENGINE,
    RR_ENGINE,
    RL_ENGINE
  };

  enum MovementID{
    MOVE_UP,
    MOVE_DOWN,
    MOVE_RIGHT,
    MOVE_LEFT
  };

  EngineController();

  void Init();
  void Update();

  void SetEnginePower(EngineID aEngine, int aPower);
  void AddEnginePower(EngineID aEngine, int aPower);

  void DoMove(MovementID aMove, int speed);

  private:
  std::map<EngineID, Engine*> mEngines;
};