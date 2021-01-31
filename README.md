# IoT_SmartAgriculture
Hệ thống IoT cho giám sát và chăm sóc cây trồng

## Mô hình chi tiết
<img src="../images/mohinh.JPG" alt="Mo hinh"/>

Mô hình chi tiết các thành phần trong hệ thống:
- Cảm biến độ ẩm đất, cảm biến DHT22 giúp đọc thông số môi trường như nhiệt độ, độ ẩm và độ ẩm đất.
- Các thiết bị điều khiển là đèn và máy bơm (trong nội dung bài tập lớn này, nhóm sử dụng đèn LED để mô phỏng).
- ESP32 để giúp đọc dữ liệu từ cảm biến rồi gửi cho server IBM Watson IoT Platform, đồng thời nhận lệnh để điều khiển thiết bị đèn LED.
- Service IBM Watson IoT Platform thuộc IBM Cloud đóng vai trò là server trao đổi dữ liệu sử dụng giao thức chính là MQTT và cách giao thức bảo mật rất tốt. Đây là dịch vụ rất tốt cho việc xây dựng nhanh một hệ thống IoT.
- Ứng dụng Node-RED vừa có vai trò chức năng điều khiển thiết bị, vừa là nơi để lấy dữ liệu từ server rồi đẩy vào database. Trong nội dung bài tập lớn này, ứng dụng Node-RED được nhóm deploy lên Cloud Foundry. Vì thế, chỉ cần dùng trình duyệt từ điện thoại hoặc máy tính đều có thể truy cập vào để sử dụng.
- Database Db2 cũng là một dịch vụ có sẵn của IBM Cloud giúp tạo nhanh một hệ cơ sở dữ liệu cho việc lưu trữ dữ liệu.
- Ứng dụng Android đóng vai trò phân quyền người dùng cho các khu vực cây trồng, trực quan dữ liệu lấy từ database bằng biểu đồ và cũng có chức năng điều khiển thiết bị như ứng dụng Node-RED.
