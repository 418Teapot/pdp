
void setup()
{
Serial.begin(9600);
}

void loop()
{
while(!Serial); //wait for serial port to open
//Serial.setTimeout();
while(!Serial.find("?")); //wait for ? from host
Serial.println("!");
}
