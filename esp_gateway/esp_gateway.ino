#include <ESP8266WiFi.h>

void setup() {
  // put your setup code here, to run once:
  Serial.begin(115200);
  
}

void loop() {
  // put your main code here, to run repeatedly:
  Serial.println();
  Serial.print("MAC: ");
  Serial.println(WiFi.macAddress());
  delay(1000);
}
