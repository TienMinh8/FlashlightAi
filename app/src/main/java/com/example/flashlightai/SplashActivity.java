package com.example.flashlightai;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.flashlightai.utils.AdManager;
import com.example.flashlightai.utils.PreferenceManager;

/**
 * Màn hình splash hiển thị khi ứng dụng khởi động
 */
public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 5000; // 5 giây
    private static final String KEY_FIRST_RUN = "is_first_run";
    private static final String KEY_INTRO_COMPLETED = "is_intro_completed";
    
    private ImageView splashLogo;
    private ProgressBar progressBar;
    private TextView appName;
    private FrameLayout adContainer;
    private PreferenceManager preferenceManager;
    private AdManager adManager;
    private boolean adsPreloaded = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Ẩn thanh trạng thái và điều hướng, full screen
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(R.layout.activity_splash);
        
        // Khởi tạo views
        splashLogo = findViewById(R.id.splash_logo);
        progressBar = findViewById(R.id.splash_progress);
        appName = findViewById(R.id.splash_text);
        adContainer = findViewById(R.id.splash_ad_container);
        
        // Khởi tạo PreferenceManager và AdManager
        preferenceManager = new PreferenceManager(this);
        adManager = AdManager.getInstance(this);
        
        // Khởi tạo SDK AdMob
        adManager.initialize();
        
        // Hiển thị quảng cáo banner và load trước các quảng cáo khác
        preloadAllAds();
        
        // Delay để hiển thị splash screen
        new Handler(Looper.getMainLooper()).postDelayed(this::checkAdsPreloadStatus, SPLASH_DURATION / 2);
    }
    
    /**
     * Tải trước tất cả các loại quảng cáo và hiển thị banner
     */
    private void preloadAllAds() {
        // Hiển thị quảng cáo banner ngay lập tức
        if (adContainer != null) {
            adManager.showBannerAd(adContainer);
        }
        
        // Tải trước interstitial ads
        adManager.preloadInterstitialAd();
        
        // Tải trước rewarded ads
        adManager.preloadRewardedAd();
        
        // Tải trước app open ads
        adManager.preloadAppOpenAd();
    }
    
    /**
     * Kiểm tra trạng thái tải quảng cáo trước khi chuyển màn hình
     */
    private void checkAdsPreloadStatus() {
        if (adsPreloaded) {
            // Đã load xong quảng cáo trước đó, chuyển màn hình
            navigateToNextScreen();
            return;
        }
        
        // Đánh dấu là đã tải xong
        adsPreloaded = true;
        
        // Chờ thêm thời gian còn lại để các quảng cáo có thể load xong
        long remainingTime = SPLASH_DURATION / 2;
        new Handler(Looper.getMainLooper()).postDelayed(this::navigateToNextScreen, remainingTime);
    }
    
    /**
     * Chuyển đến màn hình tiếp theo dựa trên trạng thái của ứng dụng
     */
    private void navigateToNextScreen() {
        // Kiểm tra xem đây có phải lần đầu chạy ứng dụng không
        boolean isFirstRun = preferenceManager.getBoolean(KEY_FIRST_RUN, true);
        
        Intent intent;
        if (isFirstRun) {
            // Lần đầu chạy, hiển thị màn hình giới thiệu
            intent = new Intent(SplashActivity.this, IntroActivity.class);
        } else {
            // Không phải lần đầu chạy, chuyển đến MainActivity
            intent = new Intent(SplashActivity.this, MainActivity.class);
        }
        
        startActivity(intent);
        finish();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Ẩn thanh điều hướng
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
    }
} 