#include "HX711.h"

//#define DEBUG

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

int greenLed=6; //power on
int redLed=9; //low batt!!
int battPin=A0; //read battery voltage

int filter_alpha = 10;

HX711 scale(2, 3);		// parameter "gain" is ommited; the default value 128 is used by the library
//#1:
double toGram = 5.68;	//hardware dependent calibration factor, TBD.
//#2:
//double toGram = 1.0/4.78;	//hardware dependent calibration factor, TBD.


void setup() {
  #ifdef DEBUG
		while(!Serial);
  		Serial.begin(19200);
  #endif

  pinMode(greenLed,OUTPUT);
  pinMode(redLed,OUTPUT);
  pinMode(battPin,INPUT);
  vBattCheck(); //do an initial battery check, to light up the leds immediately

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

states state=AWAKE; //create instance of state enum.

unsigned int vBatt=1023;

void loop() {
double weight=0; //contains measured weight. Is reset at each loop.

vBattCheck();

delay(200); //for each loop cycle

DEBUG_PRINTLN(" ");
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

  		DEBUG_PRINT("Weight: ");
  		for(int i = 0; i<weight;i+=100) DEBUG_PRINT('-');
  		DEBUG_PRINT("| ");
  		DEBUG_PRINTLN(weight);

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

void vBattCheck()
{
	vBatt = (vBatt * filter_alpha + analogRead(battPin)) / (filter_alpha+1); //moving average filter

	DEBUG_PRINT("vBattRAW: ");
	DEBUG_PRINTLN(vBatt);
	DEBUG_PRINT("vBatt: ");
	DEBUG_PRINT((float)(float)5*((float)vBatt/(float)1023));
	DEBUG_PRINTLN(" V");

	if (vBatt<666) { //Approximately 3,25V (lipo danger-low voltage is at 3,2)
		digitalWrite(redLed,HIGH);
		digitalWrite(greenLed,LOW);
	}
	else {
		digitalWrite(redLed,LOW);
		digitalWrite(greenLed,HIGH);
	}
}