# Lỗi SecurityException với Foreground Service Type Camera trên Android 14

## Metadata
- **ID**: EXP-003
- **Ngày**: 2024-05-26
- **Tags**: #android, #foreground-service, #android14, #permission, #security-exception
- **Độ phức tạp**: Trung bình
- **Thời gian giải quyết**: 30 phút
- **Status**: Đã giải quyết

## Mô tả vấn đề
Sau khi thêm thuộc tính `android:foregroundServiceType="camera"` vào khai báo service trong AndroidManifest.xml để giải quyết lỗi `MissingForegroundServiceTypeException`, ứng dụng FlashLightAi tiếp tục gặp crash với lỗi `SecurityException`:

```
FATAL EXCEPTION: main
Process: com.example.flashlightai, PID: 29079
java.lang.RuntimeException: Unable to start service com.example.flashlightai.service.FlashlightService@231148a with Intent { act=ACTION_START_FOREGROUND_SERVICE cmp=com.example.flashlightai/.service.FlashlightService }: java.lang.SecurityException: Starting FGS with type camera callerApp=ProcessRecord{8b1cc0b 29079:com.example.flashlightai/u0a786} targetSDK=34 requires permissions: all of the permissions allOf=true [android.permission.FOREGROUND_SERVICE_CAMERA] any of the permissions allOf=false [android.permission.CAMERA, android.permission.SYSTEM_CAMERA] and the app must be in the eligible state/exemptions to access the foreground only permission
```

Lỗi này xảy ra vì từ Android 14 (API 34), Google không chỉ yêu cầu khai báo loại foreground service mà còn bắt buộc phải khai báo và yêu cầu quyền tương ứng. Cụ thể, khi sử dụng `foregroundServiceType="camera"`, ứng dụng cần thêm quyền `android.permission.FOREGROUND_SERVICE_CAMERA`.

## Các giải pháp đã thử
### Giải pháp 1: Thêm quyền FOREGROUND_SERVICE_CAMERA
- Mô tả: Thêm quyền `android.permission.FOREGROUND_SERVICE_CAMERA` vào AndroidManifest.xml.
- Kết quả: Thành công, ứng dụng không còn crash khi khởi động service.
- Phân tích: Đây là quyền mới được giới thiệu trong Android 14, không được mô tả rõ trong tài liệu ban đầu về foregroundServiceType.

## Giải pháp cuối cùng
Thêm quyền `android.permission.FOREGROUND_SERVICE_CAMERA` vào AndroidManifest.xml:

```xml
<!-- Permissions -->
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.FLASHLIGHT" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_CAMERA" />
```

Từ Android 14 (API 34), mỗi loại foreground service sẽ cần một quyền tương ứng:
- `camera` → `FOREGROUND_SERVICE_CAMERA`
- `microphone` → `FOREGROUND_SERVICE_MICROPHONE`
- `location` → `FOREGROUND_SERVICE_LOCATION`
- v.v.

## Kinh nghiệm rút ra
- Từ Android 14 (API 34), foreground service với loại cụ thể cần khai báo thêm quyền tương ứng
- Thiếu quyền `FOREGROUND_SERVICE_CAMERA` khi sử dụng `foregroundServiceType="camera"` sẽ gây crash với `SecurityException`
- Ngoài quyền thiết bị (CAMERA), cũng cần quyền dịch vụ (FOREGROUND_SERVICE_CAMERA)
- Thay đổi trong Android 14 về Foreground Service được thực hiện theo hai bước: trước hết yêu cầu khai báo loại, sau đó yêu cầu quyền tương ứng
- Google đang áp dụng chính sách bảo mật và quyền riêng tư chặt chẽ hơn với mỗi phiên bản Android mới

## Phòng tránh trong tương lai
- Đọc kỹ tài liệu Android mới nhất về từng loại foreground service và quyền cần thiết
- Kiểm tra tất cả các quyền cần thiết cho mỗi loại service, bao gồm cả quyền truyền thống và quyền mới
- Tạo một bảng đối chiếu giữa `foregroundServiceType` và quyền tương ứng để tham khảo
- Kiểm tra kỹ lỗi SecurityException để xác định chính xác quyền bị thiếu
- Luôn xem lại các thay đổi bảo mật trong Android mới trước khi cập nhật targetSdkVersion

## Mối quan hệ với EXP-002
Vấn đề này có liên quan trực tiếp đến EXP-002 về MissingForegroundServiceTypeException. Đây là hai bước cần thiết khi làm việc với Foreground Service trên Android 14:
1. Khai báo loại service với thuộc tính `foregroundServiceType` (EXP-002)
2. Khai báo quyền tương ứng như `FOREGROUND_SERVICE_CAMERA` (EXP-003)

## Tài liệu tham khảo
- [Android 14 Foreground Service changes](https://developer.android.com/about/versions/14/changes/fgs-types-required)
- [Foreground Service Permissions](https://developer.android.com/guide/components/foreground-services#permissions)
- [Android Security changes in 14](https://developer.android.com/about/versions/14/behavior-changes-14#security)
- [SecurityException documentation](https://developer.android.com/reference/java/lang/SecurityException) 