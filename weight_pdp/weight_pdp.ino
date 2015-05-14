#include "HX711.h"

// HX711.DOUT	- pin #A1
// HX711.PD_SCK	- pin #A0

HX711 scale(2, 3);		// parameter "gain" is ommited; the default value 128 is used by the library
double toGram = 5.68;

void setup() {
  Serial1.begin(38400);
  Serial.begin(38400);
  Serial.println("HX711 Demo");

  Serial.println("Before setting up the scale:");
  Serial.print("read: \t\t");
  Serial.println(scale.read());			// print a raw reading from the ADC

  Serial.print("read average: \t\t");
  Serial.println(scale.read_average(20));  	// print the average of 20 readings from the ADC

  Serial.print("get value: \t\t");
  Serial.println(scale.get_value(5));		// print the average of 5 readings from the ADC minus the tare weight (not set yet)

  Serial.print("get units: \t\t");
  Serial.println(scale.get_units(5), 1);	// print the average of 5 readings from the ADC minus tare weight (not set) divided 
						// by the SCALE parameter (not set yet)  

  scale.set_scale(2280.f);                      // this value is obtained by calibrating the scale with known weights; see the README for details
  scale.tare();				        // reset the scale to 0

  Serial.println("After setting up the scale:");

  Serial.print("read: \t\t");
  Serial.println(scale.read());                 // print a raw reading from the ADC

  Serial.print("read average: \t\t");
  Serial.println(scale.read_average(20));       // print the average of 20 readings from the ADC

  Serial.print("get value: \t\t");
  Serial.println(scale.get_value(5));		// print the average of 5 readings from the ADC minus the tare weight, set with tare()

  Serial.print("get units: \t\t");
  Serial.println(scale.get_units(5), 1);        // print the average of 5 readings from the ADC minus tare weight, divided 
						// by the SCALE parameter set with set_scale

  Serial.println("Readings:");
}

void loop() {
  
  Serial.print(" One reading: ");
  Serial.print(scale.get_units()*-1, 1);
  Serial.print(" | average: ");
  Serial.print(scale.get_units(10)*-1, 1);
  Serial.print(" | in grams: ");
  Serial.println(scale.get_units()*-1*toGram, 1);
  
  double weight = scale.get_units()*-1*toGram;
  String toString = (String)weight;
  
  Serial.println(toString);
  
  Serial1.write('H');
  Serial1.write('e');
  Serial1.write('j');

  //scale.power_down();			        // put the ADC in sleep mode
  //delay(5000);
  //scale.power_up();
  delay(200);
}