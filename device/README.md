# IoT_SmartAgriculture

## Install
### 1. Cài đặt Add-on ESP32 Board trên Arduino IDE
- Thêm https://dl.espressif.com/dl/package_esp32_index.json
vào trong File -> Preferences -> Additional Boards Manager URLs
- Chọn Tools -> Board -> Boards Manager...
<img src="https://user-images.githubusercontent.com/57711768/100892827-51406400-34ed-11eb-89f8-096e3733d3ef.png" alt="Boards Manager ESP32" height="420" width="700" />

### 2. Sử dụng thư viện mqtt PubSubClient cho Arduino
Nguồn: https://www.arduinolibraries.info/libraries/pub-sub-client và https://github.com/knolleary/pubsubclient
- Chọn Sketch -> Include Library -> Manager Libraries...
<img src="https://user-images.githubusercontent.com/57711768/100894956-a0879400-34ef-11eb-8c61-bbe479105063.png" alt="Boards Manager ESP32" height="400" width="700" />

## Run
- Cắm thiết bị ESP vào cổng USB và xem Port trong Device Manager
- Chỉnh các thông số trong Tools
<img src="https://user-images.githubusercontent.com/57711768/100896470-396adf00-34f1-11eb-9f61-60af5dcc6706.png" alt="Boards Manager ESP32" height="330" width="700" />
-> Chọn Upload
