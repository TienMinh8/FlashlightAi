# 🚨 Tính năng đặc biệt

## Mô tả chung
Module này cung cấp các tính năng cao cấp và đặc biệt cho ứng dụng đèn flash, bao gồm các tín hiệu cứu nạn, hiệu ứng ánh sáng đặc biệt, và các tiện ích bổ sung. Các tính năng này giúp mở rộng chức năng của ứng dụng ngoài việc sử dụng đèn pin cơ bản.

## Thành phần cần triển khai

### 1. EmergencySignalManager
Quản lý các tín hiệu cứu nạn và khẩn cấp.

**Chức năng**:
- Tạo tín hiệu SOS chuẩn theo Morse code
- Tạo tín hiệu Mayday và các tín hiệu cứu nạn khác
- Cung cấp hướng dẫn sử dụng trong tình huống khẩn cấp
- Tùy chỉnh cường độ và tần số tín hiệu

**Lưu ý**:
- Tuân thủ chuẩn quốc tế về tín hiệu cứu nạn
- Đảm bảo độ tin cậy cao
- Tối ưu pin cho thời gian sử dụng lâu

### 2. SpecialEffectsController
Điều khiển các hiệu ứng đèn flash đặc biệt.

**Chức năng**:
- Tạo hiệu ứng đèn UV ảo qua camera
- Đồng bộ đèn flash với nhạc/âm thanh
- Tạo hiệu ứng đèn xoay (rotating beacon)
- Quản lý các hiệu ứng tùy chỉnh

**Lưu ý**:
- Tối ưu hiệu năng khi xử lý hiệu ứng phức tạp
- Cân bằng giữa tính thẩm mỹ và tiêu thụ pin
- Hỗ trợ trên đa dạng thiết bị

### 3. LightPatternManager
Quản lý và điều khiển các mẫu nhấp nháy.

**Chức năng**:
- Thư viện các mẫu nhấp nháy có sẵn
- Tạo và lưu mẫu nhấp nháy tùy chỉnh
- Nhập/xuất và chia sẻ mẫu nhấp nháy
- Chuyển đổi văn bản thành tín hiệu Morse

**Lưu ý**:
- Cung cấp giao diện dễ sử dụng để tạo mẫu
- Tối ưu lưu trữ mẫu nhấp nháy
- Hỗ trợ chia sẻ qua các phương tiện thông dụng

## Chi tiết triển khai

### Tín hiệu cứu nạn

1. **Tín hiệu SOS**
   - Chuẩn Morse: ... --- ... (3 ngắn, 3 dài, 3 ngắn)
   - Tốc độ tiêu chuẩn và tùy chỉnh
   - Lặp liên tục cho đến khi tắt
   - Hiển thị hướng dẫn sử dụng

2. **Tín hiệu Mayday**
   - Nhấp nháy theo chuẩn quốc tế
   - Tùy chọn kết hợp với âm thanh
   - Giảm độ sáng tự động để tiết kiệm pin
   - Chế độ tiết kiệm pin tối đa

3. **Các tín hiệu khác**
   - Tín hiệu cứu thương
   - Tín hiệu hỏa hoạn
   - Tín hiệu cầu cứu trên núi
   - Tùy chỉnh theo khu vực/quốc gia

### Hiệu ứng đặc biệt

1. **Đèn UV ảo**
   - Sử dụng camera và filter để tạo hiệu ứng UV
   - Hiển thị hình ảnh camera với bộ lọc UV
   - Điều chỉnh cường độ và màu sắc
   - Giải thích giới hạn của hiệu ứng ảo

2. **Nhấp nháy theo nhạc**
   - Phân tích âm thanh qua microphone
   - Đồng bộ đèn flash theo beat
   - Các chế độ nhạy: nhịp, âm lượng, tần số
   - Tùy chỉnh độ nhạy và pattern

3. **Đèn báo hiệu xoay**
   - Mô phỏng đèn cảnh báo quay
   - Nhiều màu sắc (qua màn hình): đỏ, xanh, vàng
   - Tốc độ quay tùy chỉnh
   - Kết hợp đèn flash và màn hình

### La bàn tích hợp

1. **Chế độ la bàn**
   - Sử dụng cảm biến từ trường để xác định hướng
   - Hiển thị la bàn trên màn hình
   - Bật đèn flash khi hướng về phía Bắc
   - Kết hợp với bản đồ mini (tùy chọn)

2. **Tính năng định hướng**
   - Đánh dấu vị trí và hướng đi
   - Hiển thị độ cao (nếu có cảm biến)
   - Lưu điểm đánh dấu
   - Đèn flash theo hướng đã đánh dấu

### Thư viện mẫu nhấp nháy

1. **Mẫu nhấp nháy có sẵn**
   - Các mẫu cơ bản (chậm, vừa, nhanh)
   - Các mẫu thông dụng (SOS, cảnh báo, vui nhộn)
   - Mẫu theo chủ đề (lễ hội, concert, thể thao)
   - Mẫu theo nhịp điệu (vals, disco, techno)

2. **Trình tạo mẫu nhấp nháy**
   - Giao diện trực quan để tạo pattern
   - Chỉnh sửa timing của từng bước
   - Xem trước mẫu đã tạo
   - Lưu và đặt tên cho mẫu

3. **Chuyển đổi Morse**
   - Nhập văn bản để chuyển thành tín hiệu Morse
   - Hiển thị bản dịch Morse
   - Điều chỉnh tốc độ và khoảng dừng
   - Lưu tin nhắn Morse yêu thích

## Màn hình và giao diện người dùng

1. **Màn hình tín hiệu cứu nạn**
   - Nút khẩn cấp lớn và dễ nhận biết
   - Hướng dẫn sử dụng nhanh
   - Tùy chọn bao gồm "Không tắt màn hình"
   - Hiển thị thời lượng pin và thời gian còn lại

2. **Màn hình hiệu ứng đặc biệt**
   - Thư viện hiệu ứng với xem trước
   - Điều khiển tinh chỉnh cho mỗi hiệu ứng
   - Tùy chọn lưu và chia sẻ cài đặt
   - Cảnh báo tiêu thụ pin cao

3. **Màn hình trình tạo mẫu nhấp nháy**
   - Timeline trực quan để tạo pattern
   - Công cụ chỉnh sửa dễ sử dụng
   - Thư viện mẫu đã lưu
   - Tùy chọn chia sẻ và nhập mẫu

## Luồng hoạt động

1. Người dùng chọn tính năng đặc biệt → Hiển thị màn hình tương ứng → Cấu hình tham số
2. Kích hoạt tính năng → Hiển thị hướng dẫn nếu cần → Thực thi tính năng
3. Giám sát pin và nhiệt độ → Điều chỉnh nếu cần → Thông báo người dùng
4. Người dùng lưu cài đặt → Hiển thị trong danh sách yêu thích → Có thể truy cập nhanh sau này

## Xử lý lỗi

- Thiết bị không hỗ trợ một số tính năng đặc biệt
- Tiêu thụ pin quá mức khi sử dụng hiệu ứng nặng
- Độ chính xác của cảm biến (la bàn, gia tốc)
- Xung đột với ứng dụng khác khi sử dụng camera/microphone

## API chính

```java
// EmergencySignalManager
void startSOSSignal(float speed)
void startMaydaySignal()
void startCustomEmergencySignal(EmergencySignalType type)
void stopEmergencySignal()

// SpecialEffectsController
void startUVEffect(float intensity)
void startMusicSync(MusicSyncMode mode, float sensitivity)
void startRotatingBeacon(int color, float rotationSpeed)
void stopEffect()

// LightPatternManager
void playPattern(String patternId)
void savePattern(FlashPattern pattern, String name)
List<FlashPattern> getBuiltInPatterns()
String convertTextToMorse(String text)
```

## Tích hợp với module khác

- Sử dụng FlashController để điều khiển đèn
- Tích hợp với ScreenLightController cho hiệu ứng kết hợp
- Sử dụng SensorManager để lấy dữ liệu cho la bàn
- Tương tác với UI để hiển thị thông tin và hướng dẫn

## Tiêu chí hoàn thành

- Tín hiệu SOS và cứu nạn chuẩn xác theo quy định quốc tế
- Hiệu ứng đặc biệt hoạt động mượt mà, không lag
- La bàn có độ chính xác cao (sai số < 5 độ)
- Trình tạo mẫu nhấp nháy dễ sử dụng và linh hoạt
- Hoạt động ổn định trong thời gian dài
- Tiêu thụ pin hợp lý cho từng tính năng 