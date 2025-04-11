# 🎨 Giao diện người dùng

## Mô tả chung
Module này xây dựng giao diện người dùng đẹp, trực quan và dễ sử dụng cho ứng dụng đèn flash. Giao diện sử dụng nền tối (dark theme) với các thành phần UI hiện đại, sắc nét và hỗ trợ đa chủ đề. Ứng dụng có hiệu ứng phát sáng cho nút nguồn, hỗ trợ chế độ ngày/đêm, và thay đổi màu sắc tương ứng với trạng thái hoạt động.

## Giao Diện 
Dựa trên mẫu giao diện [ui_thamkhao.png], ứng dụng FlashLight Alert có các đặc điểm sau:

### Thiết kế tổng thể
- Nền đen (dark theme) hiện đại, tương phản cao
- Nút nguồn lớn ở trung tâm với hiệu ứng phát sáng (đỏ khi tắt, xanh lục khi bật)
- Bố cục thẻ (card-based layout) cho các tính năng chính
- Thanh điều hướng phía dưới với 3 biểu tượng chức năng
- Giao diện tối giản, tập trung vào chức năng

### Các thành phần chính
- **Header**: Logo "Flash light Alert" với nút menu và trợ giúp
- **Nút nguồn chính**: Nút tròn lớn ở giữa màn hình với hiệu ứng phát sáng
- **Các tính năng**: Được nhóm thành 4 thẻ chính trong grid layout 2x2
- **Thanh điều chỉnh tốc độ**: Thanh trượt với chỉ số từ 0ms đến 30ms
- **Thanh điều hướng**: Biểu tượng đèn pin, trang chủ và cài đặt ở phía dưới

### Phối màu
- Nền chính: Đen (#000000)
- Nền thẻ: Xám đậm (#202020)
- Biểu tượng: Trắng/Xám nhạt (#FFFFFF, #DDDDDD)
- Nút nguồn ON: Xanh lục (#00E5CA) với hiệu ứng phát sáng
- Nút nguồn OFF: Đỏ (#FF3333) với hiệu ứng phát sáng
- Thanh trượt: Cam/Vàng (#FFA733)
- Công tắc bật/tắt: Xanh lá (#4CD964) và Đỏ (#FF3B30)

## Thành phần cần triển khai

### 1. ThemeManager
Quản lý chủ đề và giao diện của ứng dụng.

**Chức năng**:
- Chuyển đổi giữa chế độ sáng/tối (mặc định là tối)
- Hiệu ứng phát sáng cho nút nguồn với màu sắc khác nhau (đỏ/xanh lục)
- Quản lý nhiều theme màu sắc
- Lưu trữ và áp dụng theme tùy chỉnh
- Hỗ trợ tự động chuyển chế độ theo thời gian trong ngày

**Lưu ý**:
- Sử dụng Material Design 3
- Hỗ trợ dynamic theming trên Android 12+
- Lưu theme đã chọn và áp dụng khi khởi động ứng dụng

### 2. UIComponents
Các thành phần UI tái sử dụng trong ứng dụng.

**Chức năng**:
- Nút nguồn tròn với hiệu ứng phát sáng
- Thẻ chức năng (Feature Card) với tiêu đề, biểu tượng và điều khiển
- Công tắc bật/tắt tùy chỉnh với màu sắc theo chức năng
- Thanh điều chỉnh (slider) cho tốc độ nhấp nháy
- Bảng điều khiển cho các chế độ

**Lưu ý**:
- Đảm bảo responsive trên nhiều kích thước màn hình
- Hỗ trợ tương tác cảm ứng và bàn phím
- Tối ưu hiệu năng render
- Hiệu ứng chuyển tiếp mượt mà khi thay đổi trạng thái

### 3. LightTextRenderer
Hiển thị văn bản phát sáng trên màn hình.

**Chức năng**:
- Render văn bản với hiệu ứng phát sáng
- Hỗ trợ nhiều font và kích thước chữ
- Hiệu ứng chuyển động cho văn bản
- Tùy chỉnh màu sắc và hiệu ứng

**Lưu ý**:
- Tối ưu hiệu năng rendering
- Hỗ trợ văn bản nhiều dòng
- Khả năng cuộn và hiệu ứng

## Chi tiết triển khai

### Thiết kế giao diện chính
1. **Màn hình chính** (như trong ui_thamkhao.png):
   - Header với tiêu đề và biểu tượng menu/trợ giúp
   - Nút nguồn lớn ở trung tâm với hiệu ứng phát sáng
   - Grid 2x2 của các thẻ tính năng:
     * Incoming Calls (với công tắc xanh lá)
     * SMS (với công tắc đỏ)
     * Flashing Type (với nút chọn)
     * App Selection (với nút chọn)
   - Thanh điều chỉnh tốc độ nhấp nháy (0ms - 30ms)
   - Thanh điều hướng phía dưới với 3 biểu tượng

2. **Màn hình cài đặt**:
   - Phần chọn theme (tối/sáng/tùy chỉnh)
   - Cài đặt màu cho hiệu ứng phát sáng
   - Tùy chỉnh widget và điều khiển
   - Cài đặt nâng cao (quyền, thông báo)

3. **Màn hình Flashing Type**:
   - Danh sách các kiểu nhấp nháy với minh họa
   - Tùy chỉnh mẫu nhấp nháy
   - Lưu/tải mẫu tùy chỉnh
   - Xem trước hiệu ứng

### Hệ thống Theme
1. **Các theme mặc định**:
   - Dark (Mặc định - như trong ui_thamkhao.png)
   - Light
   - High Contrast
   - Energy Saving (tối ưu pin)
   - Fun (màu sắc sống động)

2. **Cấu trúc theme**:
   - Bảng màu chính (đen, xám đậm, trắng)
   - Màu accent (đỏ, xanh lục, cam)
   - Màu hiệu ứng phát sáng
   - Corner radius (16dp cho thẻ)
   - Elevation và shadow

### Cơ chế Responsive
- Hỗ trợ điện thoại và tablet với layout khác nhau
- Hỗ trợ xoay màn hình (portrait/landscape)
- Tự động điều chỉnh với kích thước màn hình khác nhau
- Hỗ trợ split-screen và foldable device

## Luồng hoạt động
1. Ứng dụng khởi động → Load theme → Hiển thị splash screen → Màn hình chính
2. Người dùng nhấn nút nguồn → Thay đổi màu (đỏ sang xanh lục) → Kích hoạt đèn flash
3. Người dùng bật/tắt các tính năng → Cập nhật công tắc → Ghi nhớ trạng thái
4. Điều chỉnh tốc độ → Cập nhật cài đặt nhấp nháy → Áp dụng ngay
5. Chọn flashing type → Mở màn hình chọn kiểu → Quay về với lựa chọn mới

## Xử lý lỗi
- Lỗi load theme → Fallback về theme mặc định (dark)
- Lỗi quyền truy cập đèn flash → Hiển thị thông báo & hướng dẫn
- Thiết bị không có đèn flash → Hiển thị thông báo thay thế & tính năng thay thế
- Xử lý chuyển đổi trạng thái mượt mà khi có lỗi

## API chính
```java
// ThemeManager
void setTheme(ThemeType theme)
ThemeType getCurrentTheme()
void toggleDarkMode(boolean isDark)
void applyDynamicColors(boolean apply)

// PowerButton
void setPowerState(boolean isOn)
void setGlowColor(int color)
void setGlowIntensity(float intensity)
void animatePowerChange(boolean newState)

// FeatureCard
void setFeatureIcon(int iconRes)
void setFeatureTitle(String title)
void setSwitchState(boolean isOn)
void setSwitchColor(int activeColor, int inactiveColor)

// FlashSpeedSlider
void setSpeedRange(int minMs, int maxMs)
void setCurrentSpeed(int speedMs)
void setSliderColor(int thumbColor, int trackColor)
```

## Tuân thủ Material Design 3
- Sử dụng color system của Material 3 với dark theme
- Áp dụng elevation và shadow đúng quy cách
- Animation và transition theo tiêu chuẩn
- Các thành phần UI chuẩn (Button, Card, Dialog, etc.)
- Hỗ trợ accessibility và đọc màn hình

## Tiêu chí hoàn thành
- UI responsive chính xác như ui_thamkhao.png
- Hiệu ứng phát sáng cho nút nguồn mượt mà và đẹp mắt
- Animation chuyển đổi trạng thái mượt không bị giật
- Theme áp dụng đồng bộ trên toàn ứng dụng
- Các công tắc và điều khiển phản hồi nhanh và trực quan
- Accessibility compliance cho người dùng khuyết tật 