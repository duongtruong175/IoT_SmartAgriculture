//Khai bao thu vien
#include <WiFi.h>
#include <WiFiClient.h>
#include <PubSubClient.h>
#include "DHT.h"


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
//char pubTopic1[] = "iot-2/evt/status1/fmt/json";
//char pubTopic2[] = "iot-2/evt/status2/fmt/json";
char pubTopic[] = "iot-2/evt/status/fmt/json";


//khai bao gia tri tk mk ket noi wifi
const char* ssid = "Abcde";
const char* password = "12340000";


//khai bao cac bien toan cuc
WiFiClient wifiClient; // ket noi wifi
PubSubClient client(server, 1883, NULL, wifiClient); //ket noi mqtt
DHT dht(DHTPIN, DHTTYPE); //ket noi sensor DHT22


//ham ket noi wifi
void wifiConnect () {
  Serial.println();
  Serial.print("Connecting to "); Serial.print(ssid);
  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  //ket noi thanh cong
  Serial.println("");
  Serial.print("WiFi connected, IP address: "); Serial.println(WiFi.localIP());
}


//ham ket noi mqtt broker (IBM)
void mqttConnect () {
  if (WiFi.status() != WL_CONNECTED) {
    wifiConnect();
  }
  Serial.print("Reconnecting client to ");
  Serial.println(server);
  while (!client.connect(clientId, authMethod, token)) {
    Serial.print(".");
    delay(500);
  }
  Serial.println("Bluemix connected");
}


void setup() {
  Serial.begin(115200);
  dht.begin();
  pinMode(14, INPUT);

  wifiConnect();
  mqttConnect();

}

//bien thoi gian gui du lieu
long lastMsg = 0;

void loop() {
  if (!client.connected()) {
    mqttConnect();
  }

  client.loop();
  long now = millis();
  if (now - lastMsg > 3000) { //3s gui du lieu 1 lan
    lastMsg = now;

    //doc du lieu tu cac sensor
    float humidity_air = dht.readHumidity();
    float temperature_air = dht.readTemperature();
    int humidity_soil = analogRead(34);
    Serial.print("Do am dat: "); Serial.println(humidity_soil);

    //dinh dang chuoi du lieu dang Json de gui cho server
    String payload = "{\"d\":{\"Name\":\"" DEVICE_ID "\"";
    payload += ",\"temperature_air\":";
    payload += temperature_air;
    payload += ",\"humidity_air\":";
    payload += humidity_air;
    payload += "}}";

    //gui du lieu
    if (client.publish(pubTopic, (char*) payload.c_str())) {
      Serial.println("Publish ok");
      Serial.print("Sending data: "); Serial.println(payload);
    } else {
      Serial.println("Publish failed");
    }
  }

}
