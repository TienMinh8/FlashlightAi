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

### Deprecated

- Quy trình làm việc cũ không sử dụng cấu trúc "6 Docs"

## [1.0.1] - 2024-03-23

### Added

- Bổ sung quy trình nâng cấp APK từ project Android nguồn
- Hỗ trợ build APK trực tiếp từ project Android với key debug
- Cải tiến quy trình tích hợp package từ APK nguồn sang APK đích
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
