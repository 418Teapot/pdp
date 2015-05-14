#define BLEMini Serial1

void setup()
{
    BLEMini.begin(57600);
    Serial.begin(57600);
}

unsigned char buf[16] = {0};
unsigned char len = 0;

void loop()
{
  while ( BLEMini.available())
  {
    Serial.write( BLEMini.read() );
  }

  while ( Serial.available() )
  {
    unsigned char c = Serial.read();
    if (c != 0x0A)
    {
      if (len < 16)
        buf[len++] = c;
    }
    else
    {
      buf[len++] = 0x0A;

      for (int i = 0; i < len; i++)
      {
         BLEMini.write(buf[i]);
    	delay(10);
     }
      len = 0;
    }
  }
}

