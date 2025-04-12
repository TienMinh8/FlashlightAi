# Codebase Structure

## Main Components

### Application
- **FlashLightApp**: Application class chính, khởi động các component toàn cục.

### Activities
- **MainActivity**: Activity chính của ứng dụng hiển thị giao diện điều khiển đèn pin.
- **SettingsActivity**: Activity riêng biệt chứa các thiết lập ứng dụng
  - onCreate(): Khởi tạo activity và nạp SettingsFragment
  - onBackPressed(), finish(): Xử lý hiệu ứng animation khi quay lại
  - layout: activity_settings.xml với FrameLayout để chứa fragment

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

## SettingsFragment

- Module/Package: com.example.flashlightai.fragment
- **SettingsFragment**: Quản lý thiết lập ứng dụng
  - onCreateView(), onViewCreated(): Tạo và cấu hình view của fragment
  - initViews(): Khởi tạo các thành phần UI
  - setupInitialState(): Thiết lập trạng thái ban đầu từ preferences
  - setupEventListeners(): Thiết lập các sự kiện cho các thành phần UI
  - showLanguageDialog(): Hiển thị dialog chọn ngôn ngữ
  - rateApp(), shareApp(), openPrivacyPolicy(), openTermsOfUse(): Các chức năng About

## PreferenceManager

- Module/Package: com.example.flashlightai.utils
- PreferenceManager (Quản lý thiết lập người dùng)
  - setString(), getString(): Lưu trữ và lấy giá trị chuỗi
  - setInt(), getInt(): Lưu trữ và lấy giá trị số nguyên
  - setBoolean(), getBoolean(): Lưu trữ và lấy giá trị boolean
  - setFloat(), getFloat(): Lưu trữ và lấy giá trị số thực
  - setLong(), getLong(): Lưu trữ và lấy giá trị số nguyên dài
  - remove(), clear(): Xóa một thiết lập hoặc tất cả thiết lập

## MainActivity

- Module/Package: com.example.flashlightai
- MainActivity (Giao diện chính của ứng dụng)
  - setupBottomNavigation(): Cấu hình thanh điều hướng dưới cùng, hỗ trợ tab Settings
  - handleNavigationIntent(): Xử lý điều hướng từ các màn hình khác

## Tổng quan về cấu trúc codebase

### LanguageActivity

**Package**: com.example.flashlightai

**Chức năng chính**: Cho phép người dùng chọn và thay đổi ngôn ngữ của ứng dụng

**Các thành phần chính**:
- Quản lý UI hiển thị 9 ngôn ngữ được hỗ trợ với các thẻ thông tin
- Xử lý sự kiện chọn ngôn ngữ thông qua RadioGroup
- Lưu trữ ngôn ngữ đã chọn vào SharedPreferences
- Áp dụng thay đổi ngôn ngữ cho toàn bộ ứng dụng
- Khởi động lại MainActivity khi ngôn ngữ được thay đổi

**Phương thức chính**:
- setLocale(Context, String): Thay đổi ngôn ngữ của ứng dụng
- applyLanguage(Context, PreferenceManager): Áp dụng ngôn ngữ đã lưu
- confirmLanguageSelection(): Xác nhận và áp dụng ngôn ngữ đã chọn
- updateSelectedLanguage(String): Cập nhật UI dựa trên ngôn ngữ hiện tại

### SettingsFragment

**Package**: com.example.flashlightai.fragment

**Chức năng chính**: Hiển thị và quản lý các cài đặt của ứng dụng

**Các thành phần chính**:
- Quản lý các tùy chọn và cài đặt: thông báo, ngôn ngữ, chế độ tối
- Xử lý các tùy chọn trong phần About: đánh giá, chia sẻ, chính sách bảo mật, điều khoản
- Lưu trữ và áp dụng các thiết lập của người dùng

**Phương thức chính**:
- showRatingDialog(): Hiển thị hộp thoại đánh giá ứng dụng
- shareApp(): Chia sẻ ứng dụng qua các nền tảng khác
- showPrivacyPolicy(): Mở trang chính sách riêng tư
- showTermsOfUse(): Mở trang điều khoản sử dụng

### PreferenceManager

**Package**: com.example.flashlightai.utils

**Chức năng chính**: Quản lý các thiết lập và cài đặt người dùng

**Phương thức chính**:
- setString(String, String): Lưu giá trị chuỗi
- getString(String, String): Lấy giá trị chuỗi
- setBoolean(String, boolean): Lưu giá trị boolean
- getBoolean(String, boolean): Lấy giá trị boolean
- setFloat(String, float): Lưu giá trị float
- getFloat(String, float): Lấy giá trị float
