//Khai bao thu vien
#include <WiFi.h>
#include <PubSubClient.h>
#include <Arduino_JSON.h>
#include <NTPClient.h>
#include <WiFiUdp.h>
#include "DHT.h"


//dinh nghia protype cac ham
void callback(char*, byte*, unsigned int);

//dinh nghia cac chan ket noi
#define DHTPIN 16
#define DHTTYPE DHT22
#define LED_LAMP 25
#define LED_PUMP 26
#define DIGITAL_SOIL_SENSOR 14
#define ANALOG_SOIL_SENSOR 34

//dinh nghia cac thong so thiet bi de ket noi IBM cloud
#define ORG "i4tud6"
#define DEVICE_TYPE "ESP32"
#define DEVICE_ID "543493B5AA8C"
#define TOKEN "?if0gLeQXXxbe+DGWd"

char server[] = ORG ".messaging.internetofthings.ibmcloud.com";
char authMethod[] = "use-token-auth";
char token[] = TOKEN;
char clientId[] = "d:" ORG ":" DEVICE_TYPE ":" DEVICE_ID;

//dinh nghia cac topic
char pubTopic[] = "iot-2/evt/status/fmt/json"; //topic gui du lieu
char subTopic[] = "iot-2/cmd/control/fmt/json"; //topic nhan du lieu

//khai bao gia tri tk mk ket noi wifi
const char* ssid = "Abcde";
const char* password = "12340000";

//khai bao cac bien toan cuc
WiFiClient wifiClient; // ket noi wifi
PubSubClient client(server, 1883, callback, wifiClient); //ket noi mqtt
DHT dht(DHTPIN, DHTTYPE); //ket noi sensor DHT22
unsigned long lastMsg = 0; //bien thoi gian gui du lieu
float humidity_air, temperature_air, humidity_soil, humidity_soil_sum;

//bien NTP lay time
WiFiUDP ntpUDP;
NTPClient timeClient(ntpUDP, "1.asia.pool.ntp.org", 0*60*60, 60*1000);
unsigned long time_send;

void callback(char* topic, byte* payload, unsigned int length) {
  //in ra serial du lieu nhan duoc
  Serial.print("Message received: ");
  for (int i = 0; i < length; i++) {
    Serial.print((char)payload[i]);
  }
  Serial.println();

  //Xu ly du lieu nhan duoc de dieu khien den
  JSONVar myObject = JSON.parse((const char*)payload);
  if (JSON.typeof(myObject) == "undefined") {
    Serial.println("Parsing payload failed!");
    return;
  }
  if (myObject.hasOwnProperty("lamp")) {
    int lampCmd = (int)myObject["lamp"];
    if (lampCmd == 1) {
      digitalWrite(LED_LAMP, HIGH);   // turn the LED on (HIGH is the voltage level)
      delay(1000);                       // wait for a second
    }
    else if (lampCmd == 0) {
      digitalWrite(LED_LAMP, LOW);    // turn the LED off by making the voltage LOW
      delay(1000);                       // wait for a second
    }
  }
  else if (myObject.hasOwnProperty("pump")) {
    int pumpCmd = (int)myObject["pump"];
    if (pumpCmd == 1) {
      digitalWrite(LED_PUMP, HIGH);   // turn the LED on (HIGH is the voltage level)
      delay(1000);                       // wait for a second
    }
    else if (pumpCmd == 0) {
      digitalWrite(LED_PUMP, LOW);    // turn the LED off by making the voltage LOW
      delay(1000);                       // wait for a second
    }
  }

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
    client.subscribe(subTopic); //subscribe de nhan du lieu
    Serial.println("Bluemix connected");
  }
}

void setup() {
  Serial.begin(115200);
  dht.begin();
  pinMode(DIGITAL_SOIL_SENSOR, INPUT);
  pinMode(LED_LAMP, OUTPUT); //den mo phong light
  pinMode(LED_PUMP, OUTPUT); //den mo phong may bom
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

  timeClient.begin();
}

void loop() {
  client.loop();
  if (!client.connected()) {
    mqttReconnect();
  }
  timeClient.update();
  
  unsigned long now = millis();
  if (now - lastMsg > 3000) { //3s gui du lieu 1 lan
    lastMsg = now;

    //doc du lieu tu cac sensor
    humidity_air = dht.readHumidity();
    temperature_air = dht.readTemperature();
    for (int i = 0; i < 10; i++) { //doc 10 lan roi lay trung binh de tang do chinh xac
      humidity_soil_sum += analogRead(ANALOG_SOIL_SENSOR);
    }
    humidity_soil = humidity_soil_sum / 10;
    humidity_soil_sum = 0;
    humidity_soil = map(humidity_soil, 0, 4095, 0, 100); //chuyen tu gia trị cua dien ap ADC(0-4095) sang %(0-100)
    humidity_soil = 100 - humidity_soil; //chuyen nguoc tu do kho sang do am

    time_send = timeClient.getEpochTime(); // timestamp theo second
    
    //dinh dang chuoi du lieu dang Json de gui cho server
    String payload = "{\"d\":{\"device_id\":\"" DEVICE_ID "\"";
    payload += ",\"temperature_air\":";
    payload += temperature_air;
    payload += ",\"humidity_air\":";
    payload += humidity_air;
    payload += ",\"humidity_soil\":";
    payload += humidity_soil;
    payload += ",\"time_send\":";
    payload += time_send;
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
