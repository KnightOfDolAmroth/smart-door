#include "InformationTask.h"
#include "Config.h"

#define MSG_INFORMATION "I:"

InformationTask::InformationTask(Door* pDoor, TempSensor* temp, LedExt* ledValue){
  this->pDoor = pDoor;
  this->temp = temp;
  this->ledValue = ledValue;
}
  
void InformationTask::init(int period){
  Task::init(period);
}
  
void InformationTask::tick(){
  if (pDoor->isSystemInfo()) {
    pDoor->sendToSerial(MSG_INFORMATION + String(temp->readTemperature()) + ":" + String(map(ledValue->getCurrentIntensity(),0,255,0,100)));
    pDoor->setSystemInfo(false);
  }
}

