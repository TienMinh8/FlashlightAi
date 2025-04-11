# 🔦 Chức năng đèn pin cốt lõi

## Mô tả chung
Module này quản lý chức năng cốt lõi của ứng dụng - điều khiển đèn flash của thiết bị. Module cần cung cấp nhiều chế độ hoạt động và đảm bảo hoạt động ổn định kể cả khi ứng dụng ở background.

## Thành phần cần triển khai

### 1. FlashController
Lớp quản lý trung tâm để điều khiển đèn flash.

**Chức năng**:
- Khởi tạo và giải phóng tài nguyên camera
- Bật/tắt đèn flash
- Điều khiển các chế độ đèn flash (nhấp nháy, SOS, disco...)
- Xử lý lỗi phần cứng và cơ chế fallback
- Kiểm soát nhiệt độ thiết bị

**Lưu ý**:
- Cần chạy trên background service
- Xử lý vòng đời ứng dụng (pause/resume/destroy)

### 2. Màn hình chính (MainActivity)
Giao diện người dùng chính để điều khiển đèn.

**Chức năng**:
- Hiển thị nút bật/tắt đèn lớn, dễ nhìn
- Bảng điều khiển để chọn chế độ đèn
- Hiển thị trạng thái (đèn bật/tắt, chế độ hiện tại)
- Kiểm tra và yêu cầu quyền camera khi cần

### 3. FlashService (Dịch vụ nền)
Service để đèn flash hoạt động ngay cả khi app ở background.

**Chức năng**:
- Duy trì kết nối camera khi cần thiết
- Hiển thị thông báo persistent khi đèn đang hoạt động
- Quản lý vòng đời service (start/stop)
- Tiết kiệm pin khi hoạt động ở background

## Chi tiết triển khai

### Các chế độ đèn cần hỗ trợ:

1. **Đèn liên tục (Continuous)**
   - Bật đèn liên tục cho đến khi tắt
   - Cần cơ chế tự ngắt khi quá nhiệt

2. **Chế độ nhấp nháy (Blink)**
   - Tần số: 0.5Hz - 20Hz (có thể tùy chỉnh)
   - Pattern nhấp nháy có thể tùy chỉnh (on time/off time)
   - Lưu và tải pattern đã cài đặt

3. **Chế độ SOS**
   - Tín hiệu chuẩn Morse SOS (... --- ...)
   - Cần tuân thủ đúng chuẩn SOS quốc tế

4. **Chế độ Stroboscope**
   - Nhấp nháy tốc độ cao (>10Hz)
   - Hiệu ứng đặc biệt cho tiệc/sự kiện

5. **Nhấp nháy Disco**
   - Các pattern nhịp điệu khác nhau
   - Khả năng tùy chỉnh theo beat

### Cơ chế kiểm soát nhiệt
- Giám sát nhiệt độ thiết bị khi đèn hoạt động liên tục
- Giảm độ sáng hoặc nhấp nháy khi phát hiện quá nhiệt
- Tự động tắt và thông báo nếu nhiệt độ vượt ngưỡng an toàn

### Cơ chế quản lý pin
- Tự động giảm cường độ khi pin yếu
- Cảnh báo người dùng khi sử dụng chế độ tốn pin

## Luồng hoạt động
1. Ứng dụng khởi động → Yêu cầu quyền camera → Khởi tạo FlashController
2. Người dùng bật đèn → Kiểm tra trạng thái camera → Bật flash → Khởi chạy service nền
3. Người dùng chọn chế độ → Áp dụng cấu hình → Cập nhật UI
4. Người dùng thoát app → Service nền duy trì đèn → Hiển thị thông báo
5. Tắt đèn → Service được dừng

## Xử lý lỗi
- Mất quyền camera khi đang hoạt động
- Camera bị sử dụng bởi ứng dụng khác
- Thiết bị không hỗ trợ đèn flash
- Lỗi phần cứng phát sinh

## API chính
```java
// Các phương thức chính cần triển khai 
// (chỉ là mẫu, cần điều chỉnh theo yêu cầu cụ thể)

void initialize() 
void release()
boolean turnOn()
boolean turnOff()
boolean isFlashOn()
void setMode(FlashMode mode)
void setBlinkFrequency(float frequencyHz)
void setStrobePattern(StrobePattern pattern)
```

## Tiêu chí hoàn thành
- Đèn flash hoạt động ổn định trên các thiết bị khác nhau
- Chuyển đổi mượt mà giữa các chế độ
- Service duy trì hoạt động khi app ở background
- Không gây quá nhiệt thiết bị
- Giao diện người dùng phản hồi nhanh 