package com.example.flashlightai;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.flashlightai.adapter.IntroSliderAdapter;
import com.example.flashlightai.model.IntroSlide;
import com.example.flashlightai.utils.AdManager;
import com.example.flashlightai.utils.DepthPageTransformer;
import com.example.flashlightai.utils.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity hiển thị màn hình giới thiệu khi người dùng lần đầu tiên truy cập ứng dụng
 */
public class IntroActivity extends AppCompatActivity {

    private ViewPager2 introViewPager;
    private IntroSliderAdapter introSliderAdapter;
    private PreferenceManager preferenceManager;
    private AdManager adManager;

    private static final String KEY_FIRST_RUN = "is_first_run";
    private static final int INTERSTITIAL_SLIDE_POSITION = 2; // Hiển thị interstitial khi chuyển đến slide thứ 3

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Ẩn thanh trạng thái, full screen
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(R.layout.activity_intro);

        // Khởi tạo views
        introViewPager = findViewById(R.id.intro_view_pager);
        
        // Khởi tạo PreferenceManager và AdManager
        preferenceManager = new PreferenceManager(this);
        adManager = AdManager.getInstance(this);

        // Thiết lập danh sách slide giới thiệu
        setupIntroSlides();
    }
    
    private void setupIntroSlides() {
        // Tạo danh sách các slide giới thiệu
        List<IntroSlide> introSlides = new ArrayList<>();
        
        // Thêm 4 slide giới thiệu
        introSlides.add(new IntroSlide(
                getString(R.string.intro_welcome_title),
                getString(R.string.intro_welcome_desc),
                R.drawable.intro_slide1));
        
        introSlides.add(new IntroSlide(
                getString(R.string.intro_modes_title),
                getString(R.string.intro_modes_desc),
                R.drawable.intro_slide2));
        
        introSlides.add(new IntroSlide(
                getString(R.string.intro_scanning_title),
                getString(R.string.intro_scanning_desc),
                R.drawable.intro_slide3));
        
        introSlides.add(new IntroSlide(
                getString(R.string.intro_config_title),
                getString(R.string.intro_config_desc),
                R.drawable.intro_slide4));
        
        // Thiết lập adapter cho ViewPager - truyền adManager vào adapter
        introSliderAdapter = new IntroSliderAdapter(introSlides, adManager);
        introViewPager.setAdapter(introSliderAdapter);
        
        // Thiết lập listener cho sự kiện nhấn nút Next/Finish trong adapter
        introSliderAdapter.setOnButtonClickListener(new IntroSliderAdapter.OnButtonClickListener() {
            @Override
            public void onNextClick(int position) {
                if (position < introSliderAdapter.getItemCount() - 1) {
                    // Chưa phải trang cuối, chuyển đến trang tiếp theo
                    introViewPager.setCurrentItem(position + 1);
                    
                    // Hiển thị quảng cáo interstitial khi chuyển đến slide đã định
                    if (position == INTERSTITIAL_SLIDE_POSITION - 1) {
                        showInterstitialAd();
                    }
                } else {
                    // Đã đến trang cuối, chuyển đến màn hình chọn ngôn ngữ
                    startLanguageSelectionActivity();
                }
            }
        });
        
        // Cải thiện hiệu ứng chuyển slide bằng DepthPageTransformer
        introViewPager.setPageTransformer(new DepthPageTransformer());
        
        // Giảm độ nhạy khi vuốt
        introViewPager.setOffscreenPageLimit(2);
        
        // Theo dõi sự thay đổi trang để hiển thị quảng cáo interstitial
        introViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                // Hiển thị quảng cáo interstitial khi chuyển sang trang thứ 3 (index 2)
                if (position == INTERSTITIAL_SLIDE_POSITION) {
                    showInterstitialAd();
                }
            }
        });
    }
    
    /**
     * Hiển thị quảng cáo interstitial
     */
    private void showInterstitialAd() {
        if (adManager != null) {
            adManager.showInterstitialAd(this, new AdManager.InterstitialCallback() {
                @Override
                public void onAdClosed() {
                    // Quảng cáo đã đóng, tiếp tục
                }
                
                @Override
                public void onAdFailed() {
                    // Không hiển thị được quảng cáo, bỏ qua
                }
            });
        }
    }
    
    private void startLanguageSelectionActivity() {
        // Chuyển đến màn hình chọn ngôn ngữ
        Intent intent = new Intent(IntroActivity.this, LanguageSelectionActivity.class);
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