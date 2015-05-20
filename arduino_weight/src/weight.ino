#include "HX711.h"

// HX711.DOUT	- pin #A1
// HX711.PD_SCK	- pin #A0

HX711 scale(2, 3);		// parameter "gain" is ommited; the default value 128 is used by the library
double toGram = 5.68;

void setup() {
  Serial1.begin(19200);
  Serial.begin(19200);

  //scale.read_average(20); // average of 20 readings from the ADC
  //scale.get_value(5);		// print the average of 5 readings from the ADC minus the tare weight (not set yet)
  scale.get_units(5);	//The average of 5 readings from the ADC minus tare weight (not set) divided
						// by the SCALE parameter (not set yet)

  scale.set_scale(2280.f);    // this value is obtained by calibrating the scale with known weights; see the README for details
  scale.tare();			      // reset the scale to 0

  //scale.read());              // print a raw reading from the ADC
  //scale.read_average(20);     // print the average of 20 readings from the ADC
  //scale.get_value(5);			// print the average of 5 readings from the ADC minus the tare weight, set with tare()
  //scale.get_units(5);        	// print the average of 5 readings from the ADC minus tare weight, divided
								// by the SCALE parameter set with set_scale
}

void loop() {

//TO DO: State machine all the things:
//PROTOCOL:
// '1' = start
// '0' = stop
// 't' = tare

  //while(Serial1.find('t'));
  char TempString[10];  //  Hold The Convert Data

  double weight = scale.get_units()*-1*toGram;
  if(weight<0) weight=0;

  dtostrf(weight,3,0,TempString);
  // dtostrf( [doubleVar] , [sizeBeforePoint] , [sizeAfterPoint] , [WhereToStoreIt] )
  // String complexString = String(TempString);  // cast it to string from char

/*
//printout to PC
Serial.write(TempString[0]);
Serial.write(TempString[1]);
Serial.write(TempString[2]);
Serial.write(TempString[3]);
Serial.write(TempString[4]);
Serial.write(TempString[5]);
Serial.write(0xd);
Serial.write(0xa);
*/

  Serial1.write(TempString[0]);
  Serial1.write(TempString[1]);
  Serial1.write(TempString[2]);
  Serial1.write(TempString[3]);
  //Serial1.write(TempString[4]);
  //Serial1.write(TempString[5]);

  //scale.power_down();			        // put the ADC in sleep mode
  //delay(5000);
  //scale.power_up();
  delay(200);
}

