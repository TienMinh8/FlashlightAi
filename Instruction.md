# Hướng dẫn phát triển FlashLightAi

## Tổng quan

Tài liệu này chứa hướng dẫn cho ứng dụng FlashLightAi - ứng dụng đèn flash với nhiều tính năng nâng cao. Đọc [Project.md](Project.md) để hiểu tổng quan dự án.

## Cài đặt môi trường phát triển

1. Cài đặt công cụ:
   - Android Studio
   - JDK 11+
   - Gradle 7.0+

2. Thiết lập cấu hình:
   - Clone repository
   - Mở project trong Android Studio
   - Sync Gradle

3. Khởi động ứng dụng:
   - Run trên emulator hoặc thiết bị thật

## Các tính năng cần triển khai

### 1. 🔦 Chức năng đèn pin cốt lõi ✅

**Mô tả**: Điều khiển đèn flash với nhiều chế độ.

**Thành phần**:
- FlashController
- Màn hình chính
- Dịch vụ nền

**Chi tiết triển khai**: [Xem hướng dẫn chi tiết](instructions/modules/1-flash-core.md)

**Yêu cầu chức năng**:
- Bật/tắt đèn cơ bản
- Chế độ nhấp nháy tùy chỉnh tần số
- Chế độ SOS theo chuẩn Morse
- Chế độ stroboscope
- Chế độ nhấp nháy disco

**Ràng buộc**:
- Hoạt động ngay cả khi app ở background
- Kiểm soát quá nhiệt thiết bị
- Yêu cầu quyền truy cập camera

**Tiêu chí hoàn thành**:
- Đèn flash hoạt động ổn định
- Chuyển đổi mượt mà giữa các chế độ

### 2. 🎮 Điều khiển thông minh ❌

**Mô tả**: Cung cấp nhiều cách điều khiển đèn flash.

**Thành phần**:
- VoiceController
- GestureDetector
- Widget và QuickSettings

**Chi tiết triển khai**: [Xem hướng dẫn chi tiết](instructions/modules/2-smart-control.md)

**Yêu cầu chức năng**:
- Điều khiển bằng giọng nói
- Điều khiển bằng cử chỉ (lắc)
- Phát hiện vỗ tay
- Widget màn hình chính
- Shortcut từ thanh thông báo

**Ràng buộc**:
- Tiết kiệm pin
- Độ chính xác nhận diện cao

**Tiêu chí hoàn thành**:
- Nhận diện chính xác các lệnh
- Phản hồi nhanh không quá 0.5s

### 3. 🎨 Giao diện người dùng ⏳

**Mô tả**: UI/UX đẹp và dễ sử dụng.

**Thành phần**:
- ThemeManager
- UIComponents
- LightTextRenderer

**Chi tiết triển khai**: [Xem hướng dẫn chi tiết](instructions/modules/3-user-interface.md)

**Yêu cầu chức năng**:
- Giao diện ngày/đêm
- Nhiều theme màu sắc
- Giao diện tiết kiệm pin
- Light Text hiển thị văn bản phát sáng

**Ràng buộc**:
- Material Design 3
- Hỗ trợ cả tablet và phone
- Tối ưu hiệu năng

**Tiêu chí hoàn thành**:
- UI responsive trên nhiều kích thước màn hình
- Animation mượt và nhẹ

## Tính năng chính
- ✅ Bật/tắt đèn flash khi nhận cuộc gọi 
- ✅ Bật/tắt đèn flash khi nhận tin nhắn SMS
- ✅ Bật/tắt đèn flash khi nhận thông báo từ ứng dụng được chọn
- ⏳ Chọn kiểu nhấp nháy (đơn, kép, SOS, disco, v.v)
- ⏳ Điều chỉnh tốc độ nhấp nháy
- ✅ Hoạt động cả khi ứng dụng đang ở background
- ✅ Giao diện người dùng trực quan, dễ sử dụng

## Giao diện
- ✅ Thiết kế dark theme hiện đại  
- ✅ Nút nguồn lớn ở giữa màn hình
- ✅ Lưới tính năng 2x2 (Cuộc gọi, SMS, Kiểu nhấp nháy, Chọn ứng dụng)
- ✅ Thanh điều chỉnh tốc độ nhấp nháy
- ✅ Thanh điều hướng phía dưới (Flash, Home, Settings)
- ✅ Các biểu tượng vector rõ ràng cho từng tính năng

### 4. 📱 Chức năng màn hình phát sáng ❌

**Mô tả**: Sử dụng màn hình làm nguồn sáng phụ.

**Thành phần**:
- ScreenLightController
- Bộ hiệu ứng ánh sáng
- ColorPicker

**Chi tiết triển khai**: [Xem hướng dẫn chi tiết](instructions/modules/4-screen-light.md)

**Yêu cầu chức năng**:
- Màn hình đèn đơn sắc
- Màn hình hiệu ứng
- Điều chỉnh độ sáng
- Light Text

**Ràng buộc**:
- Kiểm soát nhiệt độ thiết bị
- Cảnh báo sử dụng pin cao

**Tiêu chí hoàn thành**:
- Ánh sáng đều và ổn định
- Màn hình không bị tắt khi đang sử dụng

### 5. 🔄 Tự động hóa và cảm biến ❌

**Mô tả**: Tính năng tự động dựa trên cảm biến và ngữ cảnh.

**Thành phần**:
- SensorManager
- AutomationService
- ContextDetector

**Chi tiết triển khai**: [Xem hướng dẫn chi tiết](instructions/modules/5-automation.md)

**Yêu cầu chức năng**:
- Tự động bật khi phát hiện môi trường tối
- Tự động giảm độ sáng khi pin yếu
- Tự động tắt sau thời gian không sử dụng

**Ràng buộc**:
- Tối ưu sử dụng pin
- Dữ liệu cảm biến chính xác

**Tiêu chí hoàn thành**:
- Phản ứng chính xác với thay đổi môi trường
- Không làm giảm thời lượng pin

### 6. 🚨 Tính năng đặc biệt ❌

**Mô tả**: Các tính năng cao cấp và tiện ích.

**Thành phần**:
- EmergencySignalManager
- SpecialEffectsController
- LightPatternManager

**Chi tiết triển khai**: [Xem hướng dẫn chi tiết](instructions/modules/6-special-features.md)

**Yêu cầu chức năng**:
- Tín hiệu cứu nạn (SOS, Mayday)
- Đèn UV ảo
- Nhấp nháy theo nhạc
- Đèn báo hiệu xoay
- La bàn tích hợp

**Ràng buộc**:
- Hướng dẫn sử dụng rõ ràng
- Tối ưu hiệu năng

**Tiêu chí hoàn thành**:
- Tín hiệu chuẩn xác
- Hoạt động ổn định trong thời gian dài

### 7. 💰 Monetization ❌

**Mô tả**: Chiến lược kiếm tiền từ ứng dụng.

**Thành phần**:
- AdManager
- PremiumManager
- IAPController

**Chi tiết triển khai**: [Xem hướng dẫn chi tiết](instructions/modules/7-monetization.md)

**Yêu cầu chức năng**:
- Quảng cáo không xâm phạm
- Phiên bản premium không quảng cáo
- Mua thêm theme và hiệu ứng

**Ràng buộc**:
- Trải nghiệm người dùng không bị gián đoạn
- Tuân thủ chính sách Google Play

**Tiêu chí hoàn thành**:
- Tích hợp quảng cáo mượt mà
- Hệ thống IAP hoạt động đúng

## Quy trình làm việc

1. Chọn tính năng chưa triển khai (❌)
2. Đánh dấu tính năng đang triển khai (⏳)
3. Triển khai theo yêu cầu chức năng
4. Kiểm tra theo tiêu chí hoàn thành
5. Đánh dấu hoàn thành (✅)
6. Cập nhật Changelog.md
7. Cập nhật Codebase.md

## Hướng dẫn chi tiết

Để biết thêm thông tin về cách sử dụng các tài liệu hướng dẫn, xem [Hướng dẫn sử dụng tài liệu](instructions/README.md).

## Legend

- ✅ Completed
- ⏳ In Progress
- ❌ Not Started
