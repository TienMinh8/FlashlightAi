# Codebase Structure

## Main Components

### Application
- **FlashLightApp**: Application class chính, khởi động các component toàn cục.

### Activities
- **MainActivity**: Activity chính của ứng dụng hiển thị giao diện điều khiển đèn pin.

### Controllers
- **FlashController**: Quản lý trực tiếp đèn flash của thiết bị với các phương thức:
  - `turnOnFlash()`, `turnOffFlash()`, `toggleFlash()`
  - `setFlashMode()`: Đặt chế độ đèn (Normal, Blink, SOS, Strobe, Disco)
  - `setBlinkFrequency()`: Thiết lập tần số nhấp nháy
  - Các phương thức private để thực hiện các chế độ đèn khác nhau

### Services
- **FlashlightService**: Service chạy ở background để giữ đèn pin hoạt động:
  - Triển khai Foreground Service với thông báo
  - Cung cấp các phương thức điều khiển đèn từ bên ngoài
  - Tương tác với FlashController để điều khiển phần cứng

### UI Components
- **activity_main.xml**: Layout chính với các thành phần:
  - Power button lớn ở giữa màn hình
  - Hiệu ứng phát sáng xung quanh nút
  - Cards chọn chế độ đèn pin
  - Thanh trượt điều chỉnh tần số
  - Text hiển thị trạng thái

### Drawables
- **feature_card_bg.xml**: Background cho các thẻ tính năng, với góc bo tròn và đường viền nhẹ
- **select_button_bg.xml**: Nền cho nút "Select" với màu nền bán trong suốt và góc bo tròn
- **icon_call.xml**: Biểu tượng cuộc gọi với màu xanh lá
- **icon_sms.xml**: Biểu tượng tin nhắn SMS với màu xanh dương
- **icon_flash.xml**: Biểu tượng đèn flash với màu vàng
- **icon_apps.xml**: Biểu tượng ứng dụng cho phần App Selection
- **icon_home.xml**: Biểu tượng trang chủ cho thanh navigation
- **icon_settings.xml**: Biểu tượng cài đặt cho thanh navigation
- **icon_help.xml**: Biểu tượng trợ giúp
- **icon_menu.xml**: Biểu tượng menu
- **bottom_nav_bg.xml**: Nền cho thanh navigation phía dưới
- **speed_slider_thumb.xml**: Thumb cho thanh trượt điều chỉnh tốc độ
- **icon_speed.xml**: Biểu tượng tốc độ
- **icon_bolt.xml**: Biểu tượng điện
- **switch_thumb.xml**: Thumb cho công tắc
- **switch_track.xml**: Track cho công tắc
- **glow_effect.xml**: Hiệu ứng phát sáng quanh nút nguồn
- **power_button_bg.xml**: Nền cho nút nguồn
- **power_icon.xml**: Biểu tượng nguồn

## Permissions
- Camera: Để truy cập đèn flash
- Foreground Service: Cho phép service chạy ở nền

## Package Structure
```
com.example.flashlightai
├── FlashLightApp.java
├── MainActivity.java
├── controller
│   └── FlashController.java
└── service
    └── FlashlightService.java
```

# Codebase Overview

## UI Components (Giao diện người dùng)

### Core UI
- **ThemeManager**: Quản lý chủ đề và giao diện của ứng dụng, với nền tối làm mặc định
- **PowerButton**: Nút nguồn ở giữa màn hình với hiệu ứng phát sáng, thay đổi màu theo trạng thái (đỏ/xanh lục)
- **FeatureCard**: Thẻ hiển thị cho các tính năng với biểu tượng, tiêu đề và điều khiển (công tắc/nút)
- **FlashSpeedSlider**: Thanh trượt điều chỉnh tốc độ nhấp nháy với phạm vi 0-30ms

### Screens
- **MainActivity**: Màn hình chính với bố cục Grid 2x2 cho các tính năng và nút điều khiển chính
- **FlashingTypeActivity**: Màn hình cấu hình kiểu nhấp nháy đèn flash
- **AppSelectionActivity**: Màn hình chọn ứng dụng sẽ kích hoạt đèn flash
- **SettingsActivity**: Màn hình cài đặt cho ứng dụng

### Layouts
- **activity_main.xml**: Layout chính của ứng dụng, bao gồm:
  - Top Bar: Thanh trên cùng với nút menu, tiêu đề và nút trợ giúp
  - Power Button: Nút nguồn chính ở giữa màn hình
  - Feature Grid: Lưới 2x2 chứa các tính năng (Cuộc gọi, SMS, Kiểu nhấp nháy, Chọn ứng dụng)
  - Speed Container: Phần điều chỉnh tốc độ nhấp nháy
  - Bottom Navigation: Thanh điều hướng phía dưới với 3 tab (Flash, Home, Settings)
- **feature_card.xml**: Layout cho thẻ tính năng với biểu tượng, tiêu đề và điều khiển
- **power_button.xml**: Layout cho nút nguồn với hiệu ứng phát sáng

### Styles và Themes
- **themes.xml**: Định nghĩa theme chính (dark theme) và các theme tuỳ chỉnh
- **colors.xml**: Định nghĩa bảng màu của ứng dụng (đen, xám đậm, trắng, đỏ, xanh lục, cam)
- **styles.xml**: Định nghĩa styles cho các component UI (công tắc, nút, thanh trượt)

## Controllers & Logic

// ... existing code or additional sections ...
