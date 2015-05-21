#include "HX711.h"

//TO DO: State machine all the things:
//PROTOCOL:
#define CMD_START '1'
#define CMD_STOP '0'
#define CMD_TARE 't'

// HX711.DOUT	- pin #A1
// HX711.PD_SCK	- pin #A0

HX711 scale(2, 3);		// parameter "gain" is ommited; the default value 128 is used by the library
double toGram = 5.68;	//hardware dependent calibration factor, TBD.

void setup() {
  Serial1.begin(19200); //for the BLE module, custom baud rate, see: https://github.com/RedBearLab/Biscuit/wiki/BLEMini_BiscuitCompile#characteristics
  scale.set_scale(2280.f);    // this value is obtained by calibrating the scale with known weights; see the README for details
  scale.tare();			      // reset the scale to 0
}

//enum to hold different states:
enum states{
  TO_SLEEP,
  SLEEPING,
  AWAKEN,
  TARE,
  AWAKE
};

states state=SLEEPING; //create instance of state enum.

void loop() {
double weight=0; //contains measured weight. Is reset at each loop.

delay(200); //for each loop cycle

switch(state)
	{
	case TO_SLEEP:
		scale.power_down();			        // put the ADC in sleep mode
		state=SLEEPING;
	case SLEEPING:
		//PUT ARDUINO IN SLEEP MODE WITH WDT AND UART INTERUPT ACTIVE ??? - could conserve power.
		while(Serial1.available()<1); //wait for incoming data
		if(Serial1.read()==CMD_START) state=AWAKEN;
		break;
	case AWAKEN:
		//WAKE UP ARDUINO IF PUT TO SLEEP.
		scale.power_up();
		state=TARE;
		break;
	case TARE:
		scale.tare();
		state=AWAKE;
		break;
	case AWAKE:
		if (Serial1.available()>0) //anything from BLE module?
			{
			char rx=Serial1.read();
			if(rx==CMD_STOP) state=TO_SLEEP;
			else if(rx==CMD_TARE) state=TARE;
			}
		/////////////////TODO: IS FOUR BYTES in the TempString TOO SMALL?? CHECK!!!
  		char TempString[4];  //  Hold The Convert Data
  		weight = scale.get_units()*-1*toGram; //get data from scale, and factor in calibration data.
  		if(weight<0) weight=0; //eliminate negative numbers
		dtostrf(weight,3,0,TempString); //dtostrf creates a char array from a float
  		//syntax: dtostrf( [doubleVar] , [sizeBeforePoint] , [sizeAfterPoint] , [WhereToStoreIt] )
		//send first four chars of string
		Serial1.write(TempString[0]);
		Serial1.write(TempString[1]);
		Serial1.write(TempString[2]);
		Serial1.write(TempString[3]);
		break;
	default:
		break;
	}
}

