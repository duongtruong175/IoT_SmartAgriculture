# IoT_SmartAgriculture
Thiết bị Device ESP32 đọc và gửi dữ liệu thông số môi trường cho server IBM Watson IoT Platform

## Install

### 1. Khởi tạo project
- clone code từ github và mở bằng Arduino IDE

### 2. Cài đặt Add-on ESP32 Board trên Arduino IDE
- Thêm https://dl.espressif.com/dl/package_esp32_index.json
vào trong File -> Preferences -> Additional Boards Manager URLs
- Chọn Tools -> Board -> Boards Manager...
<img src="../images/boards_manager_esp32.png" alt="Boards Manager ESP32" height="420" width="700" />

### 3. Thêm các thư viện cần thiết
- Chọn Sketch -> Include Library -> Manager Libraries...
- các thư viện: PubSubClient, NTPClient, Arduino_JSON
<img src="../images/pubsubclient_library.png" alt="PubSubClient Library" height="400" width="700" />
<img src="../images/ntpclient_library.png" alt="NTPClient Library" height="400" width="700" />
<img src="../images/arduinojson_library.png" alt="Arduino_JSON Library" height="400" width="700" />

## Run
- Cắm thiết bị ESP vào cổng USB và xem Port trong Device Manager
- Chỉnh các thông số trong Tools
<img src="../images/run_code.png" alt="Run code" height="330" width="700" />
-> Chọn Upload
