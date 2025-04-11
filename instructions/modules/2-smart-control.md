# 🎮 Điều khiển thông minh

## Mô tả chung
Module này cung cấp nhiều cách điều khiển đèn flash ngoài giao diện chạm cơ bản, giúp người dùng tương tác với ứng dụng một cách trực quan và tiện lợi. Các phương thức điều khiển bao gồm giọng nói, cử chỉ, widget và phím tắt.

## Thành phần cần triển khai

### 1. VoiceController
Lớp để nhận dạng và xử lý lệnh giọng nói.

**Chức năng**:
- Lắng nghe và nhận dạng các lệnh giọng nói
- Phân tích ngữ cảnh lệnh
- Thực thi lệnh tương ứng
- Hỗ trợ đa ngôn ngữ (ưu tiên tiếng Anh và tiếng Việt)

**Lưu ý**:
- Sử dụng Speech Recognition API của Android
- Tối ưu hóa nhận dạng offline khi có thể
- Tiết kiệm pin khi hoạt động

### 2. GestureDetector
Lớp để phát hiện và xử lý cử chỉ.

**Chức năng**:
- Phát hiện cử chỉ lắc điện thoại để bật/tắt đèn
- Phát hiện vỗ tay thông qua microphone
- Xử lý tương tác cử chỉ trên màn hình
- Điều chỉnh độ nhạy phát hiện

**Lưu ý**:
- Sử dụng cảm biến gia tốc và microphone
- Lọc nhiễu để tránh nhận dạng sai
- Cung cấp khả năng tùy chỉnh độ nhạy

### 3. WidgetProvider
Widget cho màn hình chính của thiết bị.

**Chức năng**:
- Widget điều khiển đèn các kích thước khác nhau
- Hiển thị trạng thái đèn hiện tại
- Chuyển đổi nhanh giữa các chế độ
- Hỗ trợ widget có thể thay đổi kích thước

**Lưu ý**:
- Tuân thủ thiết kế Material Design
- Cập nhật trạng thái real-time
- Tối ưu hiệu năng và dung lượng

### 4. QuickSettingsProvider
Tích hợp vào thanh thông báo và cài đặt nhanh.

**Chức năng**:
- Tile trong Quick Settings để bật/tắt đèn
- Shortcut từ màn hình khóa
- Thông báo tùy chỉnh khi đèn đang bật
- Điều khiển từ thông báo

## Chi tiết triển khai

### Điều khiển bằng giọng nói
1. **Các lệnh cần hỗ trợ**:
   - "Bật đèn" / "Turn on the flashlight"
   - "Tắt đèn" / "Turn off the flashlight"
   - "Chế độ SOS" / "SOS mode"
   - "Nhấp nháy" / "Blink mode"
   - "Chế độ disco" / "Disco mode"

2. **Khả năng tùy chỉnh**:
   - Từ khóa kích hoạt (wake word) có thể tùy chỉnh
   - Thêm/xóa/sửa các lệnh tùy chỉnh
   - Điều chỉnh độ nhạy nhận dạng

### Điều khiển bằng cử chỉ
1. **Cử chỉ lắc**:
   - Lắc ngang: bật/tắt đèn
   - Lắc dọc: thay đổi chế độ
   - Lắc theo pattern: kích hoạt chế độ SOS

2. **Phát hiện vỗ tay**:
   - Nhận dạng âm thanh vỗ tay
   - Phân biệt với tiếng ồn môi trường
   - Hỗ trợ mẫu vỗ tay có thể lập trình (1 lần, 2 lần, 3 lần)

### Widget và Quick Settings
1. **Widget màn hình chính**:
   - Kích thước: 1x1, 2x1, 2x2
   - Nút bật/tắt nhanh
   - Hiển thị chế độ hiện tại
   - Tùy chọn nhanh cho các chế độ phổ biến

2. **Quick Settings Tile**:
   - Hiển thị trạng thái đèn
   - Nhấn để bật/tắt
   - Nhấn giữ để mở ứng dụng
   - Chuyển màu theo trạng thái

### Xử lý xung đột
- Ưu tiên lệnh trực tiếp từ UI hơn các lệnh gián tiếp
- Phát hiện và giải quyết xung đột giữa các phương thức điều khiển
- Cơ chế debounce để tránh kích hoạt nhiều lần

## Luồng hoạt động
1. Ứng dụng khởi động → Đăng ký các service lắng nghe
2. Người dùng sử dụng phương thức điều khiển → Phát hiện tương tác → Phân tích lệnh
3. Xử lý lệnh → Gọi FlashController để thực thi → Cập nhật UI và widget
4. Service duy trì lắng nghe ở background

## Xử lý lỗi
- Thiết bị không hỗ trợ một số cảm biến
- Nhận dạng giọng nói không chính xác
- Xung đột quyền truy cập microphone
- Các vấn đề về hiệu suất và pin

## API chính
```java
// VoiceController
void startListening()
void stopListening()
void addCustomCommand(String command, Runnable action)

// GestureDetector
void enableShakeDetection(boolean enable)
void setClapDetection(boolean enable)
void setShakeSensitivity(float sensitivity)

// WidgetProvider
void updateWidgetState(boolean isFlashOn, FlashMode currentMode)

// QuickSettingsProvider
void updateTileState(boolean isFlashOn)
```

## Tích hợp với module khác
- Gọi trực tiếp FlashController để điều khiển đèn
- Cập nhật UI từ MainActivity khi trạng thái thay đổi
- Ghi log thông qua LogManager cho mục đích gỡ lỗi

## Tiêu chí hoàn thành
- Nhận dạng chính xác các lệnh giọng nói (>90% độ chính xác)
- Phản hồi trong vòng 0.5 giây
- Widget hoạt động nhất quán với ứng dụng chính
- Tiêu thụ pin tối thiểu khi lắng nghe ở background 