# Hướng Dẫn Sử Dụng Tài Liệu

Thư mục này chứa tất cả các tài liệu hướng dẫn chi tiết cho dự án FlashLightAi. Tài liệu được tổ chức theo cấu trúc phân cấp để dễ điều hướng và tham khảo.

## Cấu trúc thư mục

```
instructions/
├── README.md                 # File này
├── modules/                  # Module chi tiết của ứng dụng
│   ├── 1-flash-core.md       # Chức năng đèn pin cốt lõi
│   ├── 2-smart-control.md    # Điều khiển thông minh
│   ├── 3-user-interface.md   # Giao diện người dùng
│   ├── 4-screen-light.md     # Chức năng màn hình phát sáng
│   ├── 5-automation.md       # Tự động hóa và cảm biến
│   ├── 6-special-features.md # Tính năng đặc biệt
│   └── 7-monetization.md     # Monetization
└── API_Docs.md               # Tài liệu API (nếu có)
```

## Cách sử dụng tài liệu

### Để bắt đầu
1. Đọc [Instruction.md](../Instruction.md) ở thư mục gốc để hiểu tổng quan các nhiệm vụ
2. Xem [Project.md](../Project.md) để nắm bắt mục tiêu và kiến trúc tổng thể
3. Sau đó, chọn một tính năng cần triển khai (được đánh dấu ❌ trong Instruction.md)

### Quy trình làm việc
1. Chọn một tính năng từ Instruction.md chưa được hoàn thành
2. Đánh dấu tính năng đó là "đang tiến hành" (⏳)
3. Vào thư mục `modules/` và đọc file chi tiết tương ứng với tính năng đó
4. Triển khai theo hướng dẫn trong file
5. Sau khi hoàn thành, cập nhật trạng thái tính năng thành "hoàn thành" (✅) trong Instruction.md
6. Cập nhật Changelog.md và Codebase.md

### Khi làm việc với mỗi module
- Mỗi file trong thư mục `modules/` chứa thông tin chi tiết về một thành phần cụ thể
- Các file được đánh số theo thứ tự ưu tiên triển khai
- Mỗi file chứa:
  * Mô tả tổng quan
  * Thành phần cần triển khai
  * Chi tiết cài đặt
  * API chính
  * Luồng hoạt động
  * Tiêu chí hoàn thành

## Quy tắc chỉnh sửa tài liệu
- Không xóa các file hướng dẫn gốc
- Nếu cần cập nhật hoặc mở rộng, tạo phiên bản mới và lưu vào thư mục phù hợp
- Ghi chú rõ ràng những thay đổi trong Changelog.md
- Đảm bảo các liên kết giữa các tài liệu vẫn hoạt động sau khi chỉnh sửa

## Legend (Ký hiệu)
- ✅ Completed - Tính năng đã hoàn thành
- ⏳ In Progress - Đang tiến hành
- ❌ Not Started - Chưa bắt đầu 