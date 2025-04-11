# 💰 Monetization

## Mô tả chung
Module này triển khai các cơ chế kiếm tiền trong ứng dụng đèn flash, bao gồm hiển thị quảng cáo, phiên bản premium không quảng cáo, và mua các tính năng/chủ đề bổ sung. Chiến lược monetization cần đảm bảo cân bằng giữa doanh thu và trải nghiệm người dùng tích cực.

## Thành phần cần triển khai

### 1. AdManager
Quản lý hiển thị quảng cáo trong ứng dụng.

**Chức năng**:
- Tích hợp các loại quảng cáo (banner, interstitial, rewarded)
- Lên lịch hiển thị quảng cáo phù hợp
- Theo dõi hiệu suất quảng cáo
- Đảm bảo tuân thủ chính sách của Google

**Lưu ý**:
- Quảng cáo không nên xâm phạm trải nghiệm chính
- Tránh hiển thị quảng cáo khi người dùng đang trong tình huống khẩn cấp
- Tối ưu hóa tỷ lệ fill rate và eCPM

### 2. PremiumManager
Quản lý tính năng và đặc quyền của người dùng premium.

**Chức năng**:
- Xác minh trạng thái đăng ký premium
- Quản lý các quyền lợi của người dùng premium
- Xử lý các gói đăng ký khác nhau
- Nhắc nhở gia hạn và quản lý hết hạn

**Lưu ý**:
- Lưu trữ an toàn trạng thái người dùng
- Xử lý các trường hợp hết hạn đăng ký
- Đảm bảo trải nghiệm liền mạch khi chuyển đổi giữa các loại tài khoản

### 3. IAPController
Quản lý mua hàng trong ứng dụng.

**Chức năng**:
- Tích hợp Google Play Billing Library
- Xử lý mua và khôi phục giao dịch
- Mở khóa nội dung sau khi mua thành công
- Xác minh giao dịch và xử lý lỗi

**Lưu ý**:
- Đảm bảo an toàn và xác thực giao dịch
- Xử lý các trường hợp giao dịch thất bại
- Cung cấp cơ chế khôi phục mua hàng

## Chi tiết triển khai

### Chiến lược quảng cáo

1. **Banner Ads**
   - Hiển thị ở cuối màn hình chính
   - Không hiển thị trong chế độ đèn flash chính
   - Refresh định kỳ (30-60 giây)
   - Tự động ẩn trong tình huống khẩn cấp

2. **Interstitial Ads**
   - Hiển thị khi thoát khỏi một tính năng đặc biệt
   - Giới hạn tần suất (tối đa 1 lần/5 phút)
   - Không hiển thị khi pin dưới 20%
   - Không hiển thị khi đang sử dụng tính năng SOS

3. **Rewarded Ads**
   - Cung cấp để mở khóa tính năng premium tạm thời (30 phút)
   - Mở khóa theme hoặc hiệu ứng đặc biệt
   - Mở khóa các pattern nhấp nháy cao cấp
   - Xem quảng cáo để tránh thời gian chờ giữa các tính năng

### Gói Premium

1. **Premium Lite**
   - Loại bỏ quảng cáo banner
   - Giảm tần suất quảng cáo interstitial
   - Mở khóa 5 theme cơ bản
   - Giá: $0.99/tháng hoặc $9.99/năm

2. **Premium Plus**
   - Loại bỏ tất cả quảng cáo
   - Mở khóa tất cả theme và hiệu ứng
   - Mở khóa tính năng Light Text nâng cao
   - Giá: $2.99/tháng hoặc $19.99/năm

3. **Premium Lifetime**
   - Tất cả quyền lợi của Premium Plus
   - Mua một lần, sử dụng vĩnh viễn
   - Cập nhật miễn phí trong tương lai
   - Giá: $29.99 (một lần)

### In-App Purchases

1. **Theme Packs**
   - Gói theme theo chủ đề (Neon, Minimalist, Party, etc.)
   - Mỗi gói 5-10 theme
   - Giá: $0.99 - $1.99 mỗi gói

2. **Hiệu ứng đặc biệt**
   - Gói hiệu ứng cao cấp
   - Mẫu nhấp nháy độc quyền
   - Hiệu ứng theo mùa/sự kiện
   - Giá: $0.99 - $2.99 mỗi gói

3. **Tính năng cao cấp**
   - Trình tạo mẫu nhấp nháy nâng cao
   - Light Text hiệu ứng cao cấp
   - Công cụ tạo tín hiệu SOS tùy chỉnh
   - Giá: $1.99 - $3.99 mỗi tính năng

### Giao diện mua hàng

1. **Màn hình Premium**
   - So sánh các gói premium
   - Hiển thị rõ quyền lợi và giá cả
   - Nút đăng ký và xem trước
   - FAQ về tính năng premium

2. **Cửa hàng trong ứng dụng**
   - Danh sách các gói theme/hiệu ứng
   - Xem trước trước khi mua
   - Thông tin chi tiết về sản phẩm
   - Quản lý mục đã mua

### Prompting Strategy

1. **Premium Prompts**
   - Hiển thị sau 5 lần sử dụng ứng dụng
   - Khi người dùng mở một tính năng premium
   - Sau khi sử dụng tính năng cơ bản trong 2 phút
   - Giới thiệu phiên bản Premium Lite trước

2. **Sale & Discounts**
   - Giảm giá theo mùa (Black Friday, Holidays)
   - Ưu đãi cho người dùng đã có trong ứng dụng >30 ngày
   - Khuyến mãi theo sự kiện đặc biệt
   - Quảng bá gói mới với giá ưu đãi

### Phân tích và tối ưu hóa

1. **Theo dõi hiệu suất**
   - Tracking conversion rate
   - Phân tích LTV (Lifetime Value)
   - A/B testing các chiến lược hiển thị
   - Phân tích funnel mua hàng

2. **Tối ưu hóa giá**
   - Testing các mức giá khác nhau
   - Đánh giá hiệu quả các gói khuyến mãi
   - Điều chỉnh giá theo khu vực
   - Phân tích điểm chuyển đổi tối ưu

## Luồng hoạt động

1. Cài đặt ứng dụng → Cung cấp trải nghiệm đầy đủ với quảng cáo → Giới thiệu Premium sau 5 lần sử dụng
2. Người dùng mở tính năng premium → Hiển thị màn hình upgrade → Cung cấp lựa chọn xem quảng cáo hoặc mua premium
3. Người dùng chọn mua → Xử lý giao dịch → Mở khóa tính năng → Cập nhật trạng thái người dùng
4. Quản lý đăng ký → Gửi thông báo trước khi hết hạn → Xử lý gia hạn

## Xử lý lỗi

- Giao dịch thất bại hoặc bị hủy
- Không thể tải quảng cáo
- Mất kết nối internet khi xác minh giao dịch
- Sự cố trong quá trình khôi phục mua hàng
- Vấn đề với trạng thái đăng ký

## API chính

```java
// AdManager
void initializeAds(Context context)
void showBannerAd(ViewGroup container)
void showInterstitialAd(InterstitialCallback callback)
void showRewardedAd(RewardCallback callback)
void disableAds(boolean disable)

// PremiumManager
boolean isPremiumUser()
PremiumTier getPremiumTier()
boolean hasFeatureAccess(FeatureType feature)
void checkSubscriptionStatus()
void restorePurchases()

// IAPController
void initialize(Context context)
void purchaseProduct(String productId, PurchaseCallback callback)
void purchaseSubscription(String subscriptionId, PurchaseCallback callback)
List<Product> getAvailableProducts()
void consumePurchase(String token)
```

## Tuân thủ chính sách

- Tuân thủ chính sách của Google Play về mua trong ứng dụng
- Hiển thị rõ ràng các điều khoản đăng ký
- Cung cấp cơ chế hủy đăng ký dễ dàng
- Đảm bảo quảng cáo không chứa nội dung vi phạm chính sách
- Bảo vệ thông tin thanh toán của người dùng

## Tiêu chí hoàn thành

- Tích hợp quảng cáo mượt mà, không làm gián đoạn trải nghiệm chính
- Hệ thống IAP hoạt động đúng (mua, khôi phục, xác minh)
- Trải nghiệm premium rõ ràng và có giá trị
- Tỷ lệ conversion và retention đạt mục tiêu
- Doanh thu trên mỗi người dùng (ARPU) phù hợp với mục tiêu 