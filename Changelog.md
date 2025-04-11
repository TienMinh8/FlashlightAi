# Changelog

## [Unreleased]

### Added

- Tích hợp hệ thống lưu trữ kinh nghiệm dự án với cấu trúc thư mục experiences/
- Thêm quy tắc experience-system-workflow.mdc để quản lý việc ghi lại và sử dụng kinh nghiệm
- Tạo template và ví dụ mẫu cho file kinh nghiệm với cấu trúc chuẩn
- Cấu trúc phân loại kinh nghiệm theo frontend, backend, mobile, devops, testing, AI, và common
- Tích hợp hệ thống kinh nghiệm vào các quy trình làm việc hiện có
- Tự động hóa quy trình ghi lại kinh nghiệm sau khi giải quyết vấn đề phức tạp
- Tính năng tạo ngẫu nhiên tính cách AI cho dự án để tăng trải nghiệm thú vị
- Thêm file project-personality-generator.mdc để quản lý các tính cách
- Cập nhật quy trình tạo dự án và nâng cấp dự án để bao gồm bước chọn tính cách
- Hỗ trợ 11 loại tính cách khác nhau với trọng số ưu tiên
- Bổ sung quy trình quản lý resource (icon và rule) vào workflow tạo dự án
- Bổ sung quy trình quản lý resource (icon và rule) vào workflow nâng cấp dự án
- Tích hợp hướng dẫn sử dụng Icon Library API vào quy trình làm việc
- Mô tả chi tiết quy trình đồng bộ hóa Cursor Rules
- Tạo file resource-management.mdc để quản lý tài nguyên trong dự án
- Cập nhật README.md trong thư mục assets/icons để lưu trữ hướng dẫn từ ICON-LIBRARY-API-GUIDE
- Cập nhật .cursorrc để thêm resource-management.mdc vào rules áp dụng tự động
- Bổ sung quy trình sử dụng Supabase MCP trong chế độ nhà phát triển
- Thêm quy tắc kiểm tra và cấu hình .env cho các dự án Supabase
- Tạo file supabase-mcp-workflow.mdc chứa hướng dẫn chi tiết
- Cập nhật workflow dự án mới và nâng cấp dự án để tích hợp Supabase MCP
- Hướng dẫn cài đặt và sử dụng MCP để kiểm tra database changes
- Tích hợp DALL-E API để tạo và chuyển đổi ảnh vector
- Bộ script `scripts/dalle` để tạo ảnh từ prompt, phân tích ảnh, và chuyển đổi thành vector SVG
- Quy trình làm việc mới `dalle-workflow.mdc` cho việc tạo và quản lý ảnh
- Cấu trúc thư mục `assets/icons`, `assets/images`, và `assets/illustrations`
- Cải tiến script DALL-E với tính năng tối ưu prompt tự động cho vector, icon, app icon và UI icon set
- Thêm cảnh báo chi phí sử dụng DALL-E API trước khi tạo ảnh (~0,08$ mỗi ảnh với DALL-E 3)
- Bổ sung tham số để bỏ qua cảnh báo chi phí và lưu prompt đi kèm với ảnh đã tạo
- Cập nhật script analyze_image.js để tối ưu hóa cho việc phân tích và tạo prompt cho ảnh vector
- Cải thiện script vectorize_image.js với xử lý màu sắc thông minh hơn và hỗ trợ nhiều định dạng đầu vào
- Cập nhật mô tả giao diện người dùng theo UI mẫu (ui_thamkhao.png)
- Bổ sung chi tiết về thiết kế tổng thể, phối màu và các thành phần UI
- Thêm mô tả nút nguồn với hiệu ứng phát sáng (đỏ/xanh lục)
- Thêm chi tiết về bố cục thẻ và grid layout 2x2 cho các tính năng
- Cập nhật API với các thành phần mới như PowerButton, FeatureCard và FlashSpeedSlider

### Changed

- Cập nhật cách cài đặt Supabase MCP: sử dụng npm global thay vì cài đặt từ GitHub
- Bổ sung các tham số cụ thể khi chạy mcp-server-postgrest
- Nâng cấp quy trình tương tác tích hợp APK
- Cập nhật tài liệu hướng dẫn
- Cập nhật README.md để giới thiệu cấu trúc tài liệu mới và hệ thống kinh nghiệm
- Nâng cấp phiên bản lên 2.0.0 do thay đổi lớn trong cấu trúc tài liệu
- Cải thiện quy trình làm việc để tập trung vào documentation-first approach
- Tích hợp cảnh báo chi phí trong quy trình tạo ảnh DALL-E để tránh chi phí không cần thiết
- Cải thiện UX của các script DALL-E với giao diện dòng lệnh thân thiện và đầy màu sắc
- Nâng cấp dalle-workflow.mdc với hướng dẫn chi tiết về tối ưu prompt cho từng loại ảnh
- Chuyển tính năng Settings từ fragment trong bottom navigation thành một activity riêng biệt
- Cập nhật nút Settings trên thanh điều hướng để mở SettingsActivity
- Đơn giản hóa menu điều hướng phía dưới, chỉ giữ lại các tính năng chính
- Làm nhỏ gọn lại hộp thoại đánh giá với thiết kế đơn giản và nút tùy chỉnh

### Deprecated

- Quy trình làm việc cũ không sử dụng cấu trúc "6 Docs"

## [1.0.1] - 2024-03-23

### Added

- Bổ sung quy trình nâng cấp APK từ project Android nguồn
- Hỗ trợ build APK trực tiếp từ project Android với key debug
- Cậi tiến quy trình tích hợp package từ APK nguồn sang APK đích
- Tài liệu hướng dẫn cho quy trình tích hợp mới

### Changed

- Nâng cấp quy trình tương tác tích hợp APK
- Cập nhật tài liệu hướng dẫn

## [2.0.0] - 2024-05-24

### Added

- Cấu trúc tài liệu "6 Docs" mới để giảm thiểu AI hallucination
- Templates cho 6 tài liệu chính (PRD, App Flow, Tech Stack, Frontend Guidelines, Backend Structure, Implementation Plan)
- Quy trình tạo dự án mới (project-creation-workflow.mdc)
- Quy trình nâng cấp dự án (project-upgrade-workflow.mdc)
- Thư mục docs/ với README.md giải thích về cấu trúc mới

### Changed

- Cập nhật README.md để giới thiệu cấu trúc tài liệu mới
- Nâng cấp phiên bản lên 2.0.0 do thay đổi lớn trong cấu trúc tài liệu
- Cải thiện quy trình làm việc để tập trung vào documentation-first approach

### Deprecated

- Quy trình làm việc cũ không sử dụng cấu trúc "6 Docs"

## 2024-04-08 - Triển khai chức năng đèn pin cốt lõi

### Yêu cầu:
- Triển khai chức năng đèn pin cốt lõi theo hướng dẫn trong Instruction.md

### Thực hiện:
- Tạo lớp `FlashController` để quản lý các chức năng đèn flash với các chế độ: 
  - Normal (bật/tắt)
  - Blink (nhấp nháy tùy chỉnh tần số)
  - SOS (tín hiệu Morse)
  - Strobe (nhấp nháy nhanh)
  - Disco (nhấp nháy ngẫu nhiên)
- Tạo `FlashlightService` để giữ đèn hoạt động khi ứng dụng ở chế độ nền
- Thiết kế giao diện người dùng với:
  - Nút nguồn lớn ở chính giữa với hiệu ứng phát sáng
  - Card tính năng cho các chế độ đèn khác nhau
  - Thanh trượt điều chỉnh tần số nhấp nháy
  - Giao diện nền tối mặc định
- Cập nhật AndroidManifest để đăng ký service và yêu cầu quyền cần thiết
- Tổ chức code theo các thành phần riêng biệt:
  - Controller: Quản lý đèn flash
  - Service: Duy trì hoạt động ở chế độ nền
  - UI: Giao diện người dùng trực quan

### Tính năng đã triển khai:
- Bật/tắt đèn flash cơ bản
- Chế độ nhấp nháy với tần số tùy chỉnh
- Chế độ SOS theo chuẩn Morse
- Chế độ stroboscope
- Chế độ nhấp nháy disco
- Hoạt động ngay cả khi app ở chế độ background

## [0.3.0] - 2023-11-XX
### Added
- Cập nhật giao diện với thiết kế mới hoàn chỉnh
- Thêm các biểu tượng vector: call, sms, flash, apps, home, settings, help, menu, speed, bolt
- Tạo layout mới cho MainActivity với cấu trúc rõ ràng:
  - Top Bar với menu và help
  - Nút nguồn chính ở giữa màn hình
  - Lưới tính năng 2x2 (Cuộc gọi, SMS, Kiểu nhấp nháy, Chọn ứng dụng)
  - Thanh điều chỉnh tốc độ nhấp nháy
  - Thanh điều hướng phía dưới
- Thêm các strings mới cho UI

### Fixed
- Sửa lỗi "cannot find symbol" trong MainActivity
- Cập nhật MainActivity để phù hợp với layout mới
- Thay thế các tham chiếu đến UI elements không còn tồn tại
- Cập nhật logic điều khiển UI phù hợp với thiết kế mới
- Khắc phục lỗi thiếu biến statusText
- Sửa lỗi liên quan đến SwitchCompat
- Sửa lỗi import sai đường dẫn (androidx.core.widget.SwitchCompat → androidx.appcompat.widget.SwitchCompat)
- Sửa lỗi MissingForegroundServiceTypeException trên Android 14 (API 34) bằng cách thêm thuộc tính foregroundServiceType="camera" vào khai báo service
- Sửa lỗi SecurityException trên Android 14 (API 34) bằng cách thêm quyền FOREGROUND_SERVICE_CAMERA vào AndroidManifest.xml
- Sửa lỗi "attribute android:clipPath not found" bằng cách thay đổi cách triển khai ngôi sao đánh giá
- Cải thiện khả năng tương thích của RatingBar trên các phiên bản Android cũ
- Sửa lỗi màu sắc ngôi sao đánh giá không hiển thị màu vàng như mong muốn
- Sửa lỗi "cannot find symbol import android.graphics.ColorStateList" bằng cách sử dụng đúng package android.content.res.ColorStateList

## [1.1.0] - 2023-XX-XX

### Added
- Tính năng Settings (Cài đặt) cho ứng dụng, bao gồm:
  - Quản lý thông báo đèn flash cho cuộc gọi và SMS
  - Tùy chọn Dark Mode (Chế độ tối)
  - Thay đổi ngôn ngữ (Tiếng Anh/Tiếng Việt)
  - Các tùy chọn về ứng dụng (đánh giá, chia sẻ, chính sách bảo mật, điều khoản sử dụng)
- Thêm activity Settings riêng biệt với giao diện đồng nhất
- Tích hợp Settings với các màn hình Screen Light và Text Light

### Changed
- Cải thiện điều hướng giữa các màn hình trong ứng dụng
- Lưu trữ thiết lập người dùng bằng SharedPreferences

### Fixed
- Sửa lỗi khi chuyển đổi giữa các tab

## [1.0.0] - 2023-XX-XX

### Initial Release
- Tính năng đèn pin cơ bản
- Tính năng nhấp nháy và đèn SOS
- Thông báo đèn flash cho cuộc gọi và SMS
- Tính năng Screen Light (Đèn màn hình)
- Tính năng Text Light (Hiển thị văn bản)

## [Chưa phát hành]

### Thêm mới
- Hộp thoại đánh giá thông minh khi người dùng chọn "Rate this app"
  - Cho phép đánh giá từ 1-5 sao và gửi góp ý
  - Tự động chuyển đến Google Play chỉ khi đánh giá từ 4-5 sao
  - Lưu lại phản hồi người dùng trong SharedPreferences
  - Hiển thị ngôi sao màu vàng thay vì màu mặc định
- Tính năng đa ngôn ngữ nâng cao với giao diện chọn ngôn ngữ riêng biệt
  - Thay thế hình ảnh cờ vector với hình ảnh webp thực tế từ thư mục upload
  - Cập nhật danh sách ngôn ngữ được hỗ trợ với 9 ngôn ngữ thực tế: Anh, Tây Ban Nha, Bồ Đào Nha, Hindi, Nga, Ả Rập, Bengali, Indonesia và Việt Nam
  - Tạo LanguageActivity với giao diện chọn ngôn ngữ trực quan
  - Hỗ trợ đa ngôn ngữ với tính năng lưu trữ cài đặt ngôn ngữ
  - Tự động áp dụng ngôn ngữ đã chọn khi khởi động ứng dụng
  - Cải tiến giao diện chọn ngôn ngữ với cấu trúc RadioGroup đảm bảo chỉ chọn được một ngôn ngữ
  - Thêm nút xác nhận (dấu ✓) và nút quay lại cho màn hình chọn ngôn ngữ
  - Tạo file strings.xml cho tiếng Việt và tiếng Tây Ban Nha để hỗ trợ đa ngôn ngữ

### Thay đổi
- Chuyển tính năng Settings từ fragment trong bottom navigation thành một activity riêng biệt
- Cập nhật nút Settings trên thanh điều hướng để mở SettingsActivity
- Đơn giản hóa menu điều hướng phía dưới, chỉ giữ lại các tính năng chính
- Làm nhỏ gọn lại hộp thoại đánh giá với thiết kế đơn giản và nút tùy chỉnh
- Nâng cấp cơ chế thay đổi ngôn ngữ từ tùy chọn đơn giản thành màn hình chọn ngôn ngữ riêng biệt
- Cải thiện cơ chế chọn ngôn ngữ để yêu cầu xác nhận trước khi áp dụng thay đổi
- Thay đổi danh sách ngôn ngữ được hỗ trợ từ 11 xuống còn 9 ngôn ngữ, bỏ các ngôn ngữ Korean, Japanese, German, French và Italian, thêm ngôn ngữ Russian, Arabic và Bengali

### Fixed
- Sửa lỗi "attribute android:clipPath not found" bằng cách thay đổi cách triển khai ngôi sao đánh giá
- Cải thiện khả năng tương thích của RatingBar trên các phiên bản Android cũ
- Sửa lỗi màu sắc ngôi sao đánh giá không hiển thị màu vàng như mong muốn
- Sửa lỗi "cannot find symbol import android.graphics.ColorStateList" bằng cách sử dụng đúng package android.content.res.ColorStateList
- Sửa lỗi không thể chọn các chức năng Language và các tính năng trong phần About
- Cập nhật SettingsFragment để thêm xử lý sự kiện cho các nút trong màn hình Settings
- Thêm chức năng chia sẻ ứng dụng qua mạng xã hội
- Chuẩn bị nền tảng cho các tính năng đánh giá, chính sách bảo mật và điều khoản sử dụng

## [1.1.0] - 2024-07-xx

### Fixed
- Sửa lỗi chức năng chọn ngôn ngữ không hoạt động đúng cách
- Ngôn ngữ bây giờ được duy trì nhất quán trong toàn bộ ứng dụng
- Chuyển đổi ngôn ngữ không yêu cầu khởi động lại ứng dụng

### Changed
- Cải thiện cấu trúc code với thiết kế BaseActivity
- Cập nhật cách áp dụng ngôn ngữ theo cách tiếp cận hiện đại hơn
- Xử lý ngôn ngữ từ cấp độ ứng dụng với Application class

### Added
- Thêm lớp LocaleHelper để quản lý ngôn ngữ một cách nhất quán
- Xử lý tốt hơn cho cấu hình locale thay đổi trong AndroidManifest

### Removed
- Đã loại bỏ hoàn toàn tính năng đổi ngôn ngữ do gặp nhiều lỗi không khắc phục được
- Xóa các file liên quan: LanguageActivity.java, activity_language.xml, LocaleHelper.java
- Đơn giản hóa BaseActivity và loại bỏ mã xử lý ngôn ngữ trong MainActivity

## [1.0.0] - 2023-11-20

### Added
- Tính năng đèn pin cơ bản với các chế độ: Thường, Nhấp nháy, SOS
- Hiệu ứng glow khi bật đèn
- Điều chỉnh tốc độ nhấp nháy
- Hỗ trợ đa ngôn ngữ
- Thông báo đèn flash khi có cuộc gọi/SMS
- Thêm các tính năng: đèn màn hình, đèn văn bản

### Fixed
- Sửa lỗi tham chiếu đến LocaleHelper còn sót lại sau khi loại bỏ tính năng ngôn ngữ
- Cập nhật AndroidManifest để loại bỏ các thuộc tính liên quan đến xử lý ngôn ngữ
- Cập nhật FlashLightApp để không còn sử dụng LocaleHelper
- Sửa lỗi MissingForegroundServiceTypeException khi khởi động NotificationMonitorService
- Thêm thuộc tính android:foregroundServiceType="dataSync" cho NotificationMonitorService
- Thêm quyền FOREGROUND_SERVICE_DATA_SYNC trong AndroidManifest
- Cải thiện xử lý lỗi khi khởi động foreground service
