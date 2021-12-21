# IoT_SmartAgriculture
Node-RED App

## Install
### Cách 1: Sử dụng service Node-RED của IBM Cloud

#### 1. Tạo project
- Khởi tạo node-red app trên ibm cloud và tạo các connection đến các services: IBM Watson IoT Platform, Db2

#### 2. Thêm dependencies
- Thêm vào package.json "node-red-contrib-ibm-db2": "0.x", "node-red-dashboard": "2.x", "node-red-contrib-scx-ibmiotapp": "0.x"

#### 3. Code
- Import file flows.json để generate ứng dụng (chỉnh sửa các thông số kết nối cho phù hợp)
- Ở function Create json key của flow 3, copy and paste khóa kết nối tới db2 vào

### Cách 2: Sử dụng Node-RED App local
- tương tự như trên nhưng phải tạo các kết nối thủ công
