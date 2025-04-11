# 📱 Chức năng màn hình phát sáng

## Mô tả chung
Module này biến màn hình điện thoại thành nguồn sáng phụ, cung cấp nhiều tùy chọn màu sắc, hiệu ứng và văn bản phát sáng. Tính năng này đặc biệt hữu ích khi cần ánh sáng mềm hơn đèn flash hoặc ánh sáng với màu sắc cụ thể.

## Thành phần cần triển khai

### 1. ScreenLightController
Lớp chính quản lý hiển thị ánh sáng từ màn hình.

**Chức năng**:
- Điều khiển độ sáng màn hình
- Ngăn màn hình tắt khi đang sử dụng
- Quản lý các chế độ hiển thị 
- Theo dõi nhiệt độ và pin khi màn hình sáng liên tục

**Lưu ý**:
- Tối ưu hiệu năng và pin
- Xử lý việc ngăn chặn sleep mode
- Hoạt động trên nhiều phiên bản Android

### 2. LightEffectsManager
Quản lý các hiệu ứng ánh sáng trên màn hình.

**Chức năng**:
- Tạo các hiệu ứng ánh sáng đa dạng
- Quản lý animation và chuyển tiếp
- Cung cấp thư viện hiệu ứng có sẵn
- Cho phép tùy chỉnh hiệu ứng mới

**Lưu ý**:
- Tối ưu hiệu năng render
- Xử lý mượt mà các animation phức tạp
- Hỗ trợ tùy chỉnh tốc độ và tham số hiệu ứng

### 3. ColorPicker
Công cụ chọn và quản lý màu sắc.

**Chức năng**:
- Giao diện chọn màu trực quan
- Lưu trữ màu đã sử dụng gần đây
- Hỗ trợ bảng màu và gradient
- Tùy chỉnh màu bằng RGB/HSV/Hex

**Lưu ý**:
- Giao diện thân thiện người dùng
- Hiển thị trực quan màu đã chọn
- Lưu lịch sử màu đã sử dụng

## Chi tiết triển khai

### Các chế độ màn hình phát sáng

1. **Đèn đơn sắc**
   - Hiển thị một màu duy nhất trên toàn màn hình
   - Điều chỉnh độ sáng và màu sắc
   - Lưu trữ màu yêu thích
   - Chế độ gradient (transition giữa hai màu)

2. **Hiệu ứng ánh sáng**
   - Pulse: Nhịp đập theo tốc độ tùy chỉnh
   - Wave: Hiệu ứng sóng chuyển động
   - Strobe: Nhấp nháy với tần số tùy chỉnh
   - Rainbow: Chuyển màu cầu vồng
   - Visualizer: Phản ứng với âm thanh môi trường

3. **Light Text**
   - Hiển thị văn bản phát sáng
   - Hỗ trợ cuộn văn bản
   - Tùy chỉnh font, kích cỡ, màu
   - Hiệu ứng đặc biệt cho text (glow, blink, wave)

### Điều khiển hiệu suất và bảo vệ

1. **Quản lý nhiệt độ**
   - Giám sát nhiệt độ thiết bị
   - Tự động giảm độ sáng khi quá nhiệt
   - Thông báo cảnh báo nếu nhiệt độ tăng cao

2. **Quản lý pin**
   - Hiển thị ước tính thời gian sử dụng còn lại
   - Tự động giảm độ sáng khi pin yếu
   - Cảnh báo sử dụng pin cao
   - Chế độ tiết kiệm pin

3. **Bảo vệ màn hình**
   - Cảnh báo độ sáng cao kéo dài
   - Di chuyển nội dung định kỳ để tránh burn-in
   - Hẹn giờ tự động tắt

### Màn hình điều khiển

1. **Giao diện chính**
   - Thanh điều chỉnh độ sáng
   - Bộ chọn màu
   - Thư viện hiệu ứng có sẵn
   - Nút tùy chỉnh và lưu chế độ

2. **Màn hình cài đặt**
   - Hẹn giờ tắt
   - Chế độ pin
   - Cài đặt mặc định
   - Tùy chỉnh giao diện điều khiển

3. **Màn hình Light Text**
   - Trình soạn thảo văn bản
   - Tùy chỉnh kiểu hiển thị
   - Bộ sưu tập văn bản mẫu
   - Cài đặt animation

## Luồng hoạt động

1. Người dùng vào màn hình đèn → Chọn chế độ → Điều chỉnh cài đặt → Kích hoạt
2. Hiển thị màn hình phát sáng → Giữ màn hình luôn bật → Giám sát nhiệt độ/pin
3. Người dùng tùy chỉnh → Áp dụng thay đổi real-time → Có thể lưu cài đặt
4. Thoát chế độ hoặc hẹn giờ tắt → Trở về giao diện chính

## Xử lý lỗi

- Màn hình không duy trì độ sáng tối đa
- Vấn đề hiệu năng với hiệu ứng phức tạp
- Quản lý vòng đời Activity khi sử dụng mode toàn màn hình
- Tối ưu hóa bộ nhớ và tài nguyên GPU

## API chính

```java
// ScreenLightController
void setBrightness(float level) // 0.0 - 1.0
void setKeepScreenOn(boolean keepOn)
void setScreenColor(int color)
void startEffect(EffectType effect, EffectConfig config)
void stopEffect()

// LightEffectsManager
void loadEffect(String effectName)
void setEffectParameters(Map<String, Object> params)
void setEffectSpeed(float speed)
EffectConfig createCustomEffect(EffectConfig baseEffect, Map<String, Object> customizations)

// ColorPicker
int getSelectedColor()
void setColorChangeListener(ColorChangeListener listener)
void setGradientMode(boolean enabled, int startColor, int endColor)
void saveCustomColor(int color, String name)
```

## Tối ưu hiệu năng

- Sử dụng SurfaceView hoặc TextureView cho hiệu ứng phức tạp
- Tối ưu animators và frame rate
- Giảm độ phức tạp khi pin yếu hoặc nhiệt độ cao
- Sử dụng hardware acceleration khi có thể

## Tiêu chí hoàn thành

- Ánh sáng màn hình đều và ổn định
- Màn hình không bị tắt khi đang sử dụng
- Các hiệu ứng chạy mượt mà, không giật lag
- Tiêu thụ pin hợp lý
- Giao diện điều khiển trực quan và dễ sử dụng
- Hiệu năng ổn định trên nhiều thiết bị khác nhau 