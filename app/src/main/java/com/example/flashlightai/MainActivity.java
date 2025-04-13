package com.example.flashlightai;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.flashlightai.base.BaseActivity;
import com.example.flashlightai.FlashFragment;
import com.example.flashlightai.screen.ScreenLightActivity;
import com.example.flashlightai.textlight.TextLightActivity;
import com.example.flashlightai.utils.AdManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * MainActivity - Quản lý điều hướng chính của ứng dụng
 * Hiển thị các fragment tương ứng theo lựa chọn của người dùng
 */
public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";
    private AdManager adManager;
    private FrameLayout adContainer;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Giữ màn hình luôn sáng
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        // Thiết lập chế độ full screen
        setFullScreenMode();
        
        setContentView(R.layout.activity_main);
        
        // Khởi tạo AdManager
        adManager = AdManager.getInstance(this);
        adContainer = findViewById(R.id.ad_container);
        
        // Hiển thị quảng cáo banner
        loadBannerAd();
        
        // Bắt đầu đếm thời gian cho interstitial
        adManager.startInterstitialTimer();
        
        // Thiết lập bottom navigation
        setupBottomNavigation();
        
        // Thiết lập sự kiện cho nút settings
        setupSettingsButton();
        
        // Nạp fragment mặc định (FlashFragment) nếu là khởi động mới
        if (savedInstanceState == null) {
            loadFragment(new FlashFragment());
        }
        
        // Xử lý intent khi được mở từ các Activity khác
        handleNavigationIntent(getIntent());
    }
    
    /**
     * Tải và hiển thị quảng cáo banner
     */
    private void loadBannerAd() {
        if (adContainer != null && adManager != null) {
            adManager.showBannerAd(adContainer);
        }
    }
    
    /**
     * Kiểm tra và hiển thị quảng cáo interstitial nếu đủ điều kiện
     */
    private void checkAndShowInterstitial() {
        if (adManager != null && adManager.isInterstitialTimerReady()) {
            adManager.showInterstitialAd(this, new AdManager.InterstitialCallback() {
                @Override
                public void onAdClosed() {
                    // Đặt lại bộ đếm thời gian
                    adManager.resetInterstitialTimer();
                }
                
                @Override
                public void onAdFailed() {
                    // Thất bại, đặt lại bộ đếm thời gian
                    adManager.resetInterstitialTimer();
                }
            });
        }
    }
    
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        // Xử lý intent khi Activity đã tồn tại
        handleNavigationIntent(intent);
    }
    
    /**
     * Thiết lập BottomNavigationView
     */
    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        if (bottomNav != null) {
            // Đặt mục Flash được chọn mặc định
            bottomNav.setSelectedItemId(R.id.navigation_flash);
            
            // Thiết lập listener cho bottom navigation
            bottomNav.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.navigation_flash) {
                    loadFragment(new FlashFragment());
                    return true;
                } else if (itemId == R.id.navigation_screen) {
                    // Kiểm tra và hiển thị quảng cáo interstitial trước khi chuyển màn hình
                    checkAndShowInterstitial();
                    
                    Intent intent = new Intent(this, ScreenLightActivity.class);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    return false; // Không thay đổi item được chọn trong bottom nav
                } else if (itemId == R.id.navigation_text_light) {
                    // Kiểm tra và hiển thị quảng cáo interstitial trước khi chuyển màn hình
                    checkAndShowInterstitial();
                    
                    Intent intent = new Intent(this, TextLightActivity.class);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    return false; // Không thay đổi item được chọn trong bottom nav
                }
                return false;
            });
        }
    }
    
    /**
     * Thiết lập sự kiện cho nút Settings
     */
    private void setupSettingsButton() {
        ImageView settingsButton = findViewById(R.id.settings_button);
        if (settingsButton != null) {
            settingsButton.setOnClickListener(v -> {
                // Kiểm tra và hiển thị quảng cáo interstitial trước khi mở cài đặt
                checkAndShowInterstitial();
                
                // Mở SettingsActivity
                Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(settingsIntent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            });
        }
    }
    
    /**
     * Xử lý intent được gửi đến khi đã ở trong ứng dụng
     */
    private void handleNavigationIntent(Intent intent) {
        // Kiểm tra intent
        if (intent == null || !intent.hasExtra("navigate_to")) {
            return;
        }
        
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        
        if (bottomNavigationView != null) {
            String navigateTo = intent.getStringExtra("navigate_to");
            if ("flash".equals(navigateTo)) {
                bottomNavigationView.setSelectedItemId(R.id.navigation_flash);
                loadFragment(new FlashFragment());
            } else if ("settings".equals(navigateTo)) {
                // Mở SettingsActivity thay vì fragment
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
            }
        }
    }

    /**
     * Nạp fragment vào container
     */
    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }
    
    /**
     * Thiết lập chế độ full screen nhưng vẫn hiển thị thanh điều hướng
     */
    private void setFullScreenMode() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_FULLSCREEN; // Chỉ ẩn thanh trạng thái (status bar)
        decorView.setSystemUiVisibility(uiOptions);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Dừng bộ đếm thời gian khi Activity bị hủy
        if (adManager != null) {
            adManager.stopInterstitialTimer();
        }
    }
}