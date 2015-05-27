#include "HX711.h"

#define DEBUG

#ifdef DEBUG
 #define DEBUG_PRINT(x)     Serial.print (x)
 //#define DEBUG_PRINTDEC(x)     Serial.print (x, DEC)
 #define DEBUG_PRINTLN(x)  Serial.println (x)
#else
 #define DEBUG_PRINT(x)
 //#define DEBUG_PRINTDEC(x)
 #define DEBUG_PRINTLN(x)
#endif


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
  #ifdef DEBUG
		while(!Serial);
  		Serial.begin(19200);
  #endif
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

states state=TO_SLEEP; //create instance of state enum.

//char incoming;

void loop() {
double weight=0; //contains measured weight. Is reset at each loop.

delay(200); //for each loop cycle

DEBUG_PRINT("State: ");

switch(state)
	{
	case TO_SLEEP:
		DEBUG_PRINTLN("TO_SLEEP");
		scale.power_down();			        // put the ADC in sleep mode
		state=SLEEPING;
		break;
	case SLEEPING:
		DEBUG_PRINTLN("SLEEPING");
		//PUT ARDUINO IN SLEEP MODE WITH WDT AND UART INTERUPT ACTIVE ??? - could conserve power.
		if(Serial1.available()) //wait for incoming data
			{
			char incoming=Serial1.read();
			DEBUG_PRINT("rx: ");
			DEBUG_PRINTLN(incoming);
			if(incoming==CMD_START) state=AWAKEN;
			}
		break;
	case AWAKEN:
		DEBUG_PRINTLN("AWAKEN");
		scale.power_up();
		state=TARE;
		break;
	case TARE:
		DEBUG_PRINTLN("TARE");
		scale.tare();
		state=AWAKE;
		break;
	case AWAKE:
		DEBUG_PRINTLN("AWAKE");
		if (Serial1.available()) //anything from BLE module?
			{
			char rx=Serial1.read();
			DEBUG_PRINT("rx: ");
			DEBUG_PRINTLN(rx);
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

