# 🔄 Tự động hóa và cảm biến

## Mô tả chung
Module này cung cấp các tính năng tự động hóa dựa trên cảm biến và ngữ cảnh, giúp ứng dụng đèn flash thông minh hơn và tiện lợi hơn cho người dùng. Các tính năng tự động hóa sẽ hoạt động dựa trên điều kiện môi trường, trạng thái thiết bị và thói quen sử dụng của người dùng.

## Thành phần cần triển khai

### 1. SensorManager
Quản lý việc truy cập và xử lý dữ liệu từ các cảm biến thiết bị.

**Chức năng**:
- Thu thập dữ liệu từ cảm biến ánh sáng
- Thu thập dữ liệu từ cảm biến gia tốc
- Thu thập dữ liệu từ cảm biến tiệm cận
- Quản lý đăng ký và hủy đăng ký cảm biến
- Xử lý và lọc dữ liệu cảm biến

**Lưu ý**:
- Tối ưu hóa việc sử dụng cảm biến để tiết kiệm pin
- Đảm bảo hoạt động ổn định trên nhiều thiết bị
- Xử lý các trường hợp thiết bị không có cảm biến cụ thể

### 2. AutomationService
Dịch vụ chạy nền để quản lý các quy tắc tự động hóa.

**Chức năng**:
- Quản lý các quy tắc tự động (rule-based automation)
- Kích hoạt hành động dựa trên điều kiện
- Lên lịch và thực thi các hành động tự động
- Duy trì hoạt động ở background khi cần

**Lưu ý**:
- Sử dụng JobScheduler/WorkManager để tối ưu pin
- Xử lý các hành động bị gián đoạn
- Đảm bảo các quy tắc không xung đột

### 3. ContextDetector
Phát hiện ngữ cảnh và môi trường xung quanh.

**Chức năng**:
- Phát hiện môi trường tối
- Phát hiện trạng thái thiết bị (cầm trên tay, trong túi, etc.)
- Phân tích dữ liệu cảm biến để xác định ngữ cảnh
- Học thói quen sử dụng của người dùng

**Lưu ý**:
- Sử dụng thuật toán hiệu quả để tiết kiệm tài nguyên
- Cân bằng giữa độ chính xác và hiệu suất
- Bảo vệ quyền riêng tư của người dùng

## Chi tiết triển khai

### Các tính năng tự động hóa cần hỗ trợ

1. **Tự động bật dựa trên ánh sáng môi trường**
   - Phát hiện môi trường tối thông qua cảm biến ánh sáng
   - Ngưỡng ánh sáng có thể tùy chỉnh
   - Xác nhận người dùng trước khi bật đèn tự động
   - Thời gian trễ có thể điều chỉnh

2. **Quản lý pin thông minh**
   - Tự động giảm độ sáng khi pin yếu
   - Tự động chuyển sang chế độ tiết kiệm pin
   - Thông báo và đề xuất cài đặt dựa trên mức pin
   - Ước tính thời gian sử dụng còn lại

3. **Tự động tắt thông minh**
   - Tắt sau khoảng thời gian không sử dụng
   - Tắt khi phát hiện điện thoại vào túi/ví
   - Tắt khi phát hiện môi trường đủ sáng
   - Hẹn giờ tắt có thể tùy chỉnh

### Cơ chế nhận diện ngữ cảnh

1. **Phát hiện môi trường tối**
   - Sử dụng cảm biến ánh sáng môi trường
   - Xác định ngưỡng sáng/tối
   - Tính đến sự thay đổi ánh sáng đột ngột
   - Mô hình dự đoán thời gian cần đèn

2. **Phát hiện chuyển động và tư thế**
   - Phân tích dữ liệu từ cảm biến gia tốc
   - Xác định khi điện thoại đang được cầm
   - Phát hiện khi điện thoại bị úp xuống
   - Nhận biết khi điện thoại đang nằm trên bàn

3. **Học thói quen người dùng**
   - Theo dõi thời gian sử dụng đèn
   - Ghi nhận môi trường khi đèn được sử dụng
   - Xây dựng hồ sơ sử dụng theo thời gian
   - Đề xuất tự động hóa dựa trên thói quen

### Cài đặt và tùy chỉnh

1. **Giao diện cài đặt tự động hóa**
   - Bật/tắt từng tính năng tự động
   - Điều chỉnh ngưỡng và độ nhạy của mỗi tính năng
   - Đặt lịch và quy tắc tùy chỉnh
   - Xem lại lịch sử hoạt động tự động

2. **Cấu hình lưu trữ**
   - Lưu cài đặt vào SharedPreferences
   - Đồng bộ cài đặt giữa các phiên
   - Cung cấp cài đặt mặc định hợp lý
   - Khôi phục cài đặt khi cần

### Xử lý background và tiết kiệm pin

1. **Các cơ chế tiết kiệm pin**
   - Đăng ký cảm biến với tần suất phù hợp
   - Sử dụng batch processing cho dữ liệu cảm biến
   - Dynamically adjust sampling rates based on context
   - Giảm thiểu wake-up periods

2. **Background Processing**
   - Sử dụng WorkManager cho các tác vụ định kỳ
   - Tận dụng Doze mode và App Standby
   - Giảm thiểu foreground services
   - Sử dụng broadcast receivers hiệu quả

## Luồng hoạt động

1. Ứng dụng cài đặt → Kích hoạt AutomationService → Đăng ký theo dõi cảm biến
2. Người dùng cấu hình các quy tắc tự động → Lưu cài đặt → Service cập nhật
3. Service nền theo dõi dữ liệu cảm biến → Phát hiện ngữ cảnh → So sánh với quy tắc
4. Điều kiện thỏa mãn → Thực hiện hành động tự động → Thông báo người dùng
5. Người dùng có thể ghi đè lên hành động tự động → Hệ thống học và điều chỉnh

## Xử lý lỗi

- Thiết bị không có cảm biến cần thiết
- Cảm biến cung cấp dữ liệu không chính xác
- Service bị kill bởi hệ thống
- Xung đột giữa các quy tắc tự động
- Quá nhiều false positives

## API chính

```java
// SensorManager
void registerLightSensor(float samplingRate, LightSensorListener listener)
void registerAccelerometer(float samplingRate, AccelerometerListener listener)
void unregisterSensor(SensorType type)
float getCurrentLightLevel()
DevicePosition getCurrentDevicePosition()

// AutomationService
void addRule(AutomationRule rule)
void removeRule(String ruleId)
List<AutomationRule> getAllRules()
void enableRule(String ruleId, boolean enabled)
boolean isRuleActive(String ruleId)

// ContextDetector
boolean isDarkEnvironment()
boolean isDeviceInPocket()
boolean isDeviceStationary(long durationMs)
float getBatteryTrendPerHour()
```

## Tích hợp với module khác

- Gọi FlashController để bật/tắt đèn
- Cập nhật UI thông qua MainActivity
- Thông báo cho người dùng thông qua NotificationManager
- Ghi log hành động tự động

## Tiêu chí hoàn thành

- Phản ứng chính xác với thay đổi môi trường (>90% chính xác)
- Tiêu thụ pin tối thiểu (<5% trong 1 giờ hoạt động nền)
- Hiệu suất ổn định trên nhiều thiết bị
- Cơ chế học thông minh cải thiện dần theo thời gian sử dụng
- Giao diện cấu hình trực quan và dễ sử dụng 