//Khai bao thu vien
#include <WiFi.h>
#include <WiFiClient.h>
#include <PubSubClient.h>
#include "DHT.h"

//dinh nghia protype cac ham
void callback(char*, byte*, unsigned int);

//dinh nghia cac chan ket noi
#define DHTPIN 16
#define DHTTYPE DHT22

//dinh nghia cac thong so thiet bi de ket noi IBM cloud
#define ORG "a1fssz"
#define DEVICE_TYPE "ESP32"
#define DEVICE_ID "543493B5AA8C"
#define TOKEN "POyrUBz6leTn*B29TU"

char server[] = ORG ".messaging.internetofthings.ibmcloud.com";
char authMethod[] = "use-token-auth";
char token[] = TOKEN;
char clientId[] = "d:" ORG ":" DEVICE_TYPE ":" DEVICE_ID;

//dinh nghia cac topic
char pubTopic[] = "iot-2/evt/status/fmt/json"; //topic gui du lieu
char subTopic[] = "iot-2/evt/control/fmt/json"; //topic nhan du lieu

//khai bao gia tri tk mk ket noi wifi
const char* ssid = "Abcde";
const char* password = "12340000";

//khai bao cac bien toan cuc
WiFiClient wifiClient; // ket noi wifi
PubSubClient client(server, 1883, NULL, wifiClient); //ket noi mqtt
DHT dht(DHTPIN, DHTTYPE); //ket noi sensor DHT22
unsigned long lastMsg = 0; //bien thoi gian gui du lieu
float humidity_air, temperature_air, humidity_soil, humidity_soil_sum;

void callback(char* topic, byte* payload, unsigned int length) {
  //in ra serial du lieu nhan duoc
  Serial.print("Message received: ");
  for (int i = 0; i < length; i++) {
    Serial.print((char)payload[i]);
  }
  Serial.println();

  //Xu ly du lieu nhan duoc de dieu khien den
  /*
    // Switch on the LED if an 1 was received as first character
    if ((char)payload[0] == '1') {
    digitalWrite(  , LOW);   // Turn the LED on (Note that LOW is the voltage level
    // but actually the LED is on; this is because
    // it is active low on the ESP-01)
    } else {
    digitalWrite(  , HIGH);  // Turn the LED off by making the voltage HIGH
    }
  */

}

void mqttReconnect() {
  // ket noi mqtt
  if (!client.connected()) {
    Serial.print("Reconnecting client to ");
    Serial.println(server);
    while (!client.connect(clientId, authMethod, token)) {
      Serial.print(".");
      delay(500);
    }
    //client.subscribe(subTopic); //subscribe de nhan du lieu
    Serial.println("Bluemix connected");
  }
}

void setup() {
  Serial.begin(115200);
  dht.begin();
  pinMode(14, INPUT);
  humidity_soil_sum = 0;

  // ket noi wifi
  delay(10);
  Serial.println();
  Serial.print("Connecting to ");
  Serial.print(ssid);
  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  //ket noi thanh cong
  Serial.println("");
  Serial.print("WiFi connected, IP address: ");
  Serial.println(WiFi.localIP());
}

void loop() {
  client.loop();
  if (!client.connected()) {
    mqttReconnect();
  }
  unsigned long now = millis();
  if (now - lastMsg > 3000) { //3s gui du lieu 1 lan
    lastMsg = now;

    //doc du lieu tu cac sensor
    humidity_air = dht.readHumidity();
    temperature_air = dht.readTemperature();
    for (int i = 0; i < 10; i++) { //doc 10 lan roi lay trung binh de tang do chinh xac
      humidity_soil_sum += analogRead(34);
    }
    humidity_soil = humidity_soil_sum / 10;
    humidity_soil_sum = 0;
    humidity_soil = map(humidity_soil, 0, 4095, 0, 100); //chuyen tu gia trị cua dien ap ADC(0-4095) sang %(0-100)
    humidity_soil = 100 - humidity_soil; //chuyen nguoc tu do kho sang do am

    //dinh dang chuoi du lieu dang Json de gui cho server
    String payload = "{\"d\":{\"Name\":\"" DEVICE_ID "\"";
    payload += ",\"temperature_air\":";
    payload += temperature_air;
    payload += ",\"humidity_air\":";
    payload += humidity_air;
    payload += ",\"humidity_soil\":";
    payload += humidity_soil;
    payload += "}}";

    Serial.print("Sending data: ");
    Serial.println(payload);
    //gui du lieu
    if (client.publish(pubTopic, (char*) payload.c_str())) {
      Serial.println("Publish ok");
    } else {
      Serial.println("Publish failed");
    }
  }
}
