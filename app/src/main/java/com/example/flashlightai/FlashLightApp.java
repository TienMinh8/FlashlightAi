package com.example.flashlightai;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.flashlightai.utils.LanguageManager;
import com.example.flashlightai.utils.PreferenceManager;

import java.util.Locale;

/**
 * Application class for the FlashLight app
 */
public class FlashLightApp extends Application {
    private static final String TAG = "FlashLightApp";
    private static final String KEY_LANGUAGE = "app_language";
    
    @Override
    protected void attachBaseContext(Context base) {
        // Khôi phục lại ngôn ngữ đã lưu
        PreferenceManager preferenceManager = new PreferenceManager(base);
        
        // Nếu chưa có ngôn ngữ được thiết lập, sử dụng tiếng Anh làm mặc định thay vì ngôn ngữ hệ thống
        String languageCode = preferenceManager.getString(KEY_LANGUAGE, "en");
        
        // Áp dụng ngôn ngữ
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Configuration config = new Configuration(base.getResources().getConfiguration());
        config.setLocale(locale);
        
        super.attachBaseContext(base.createConfigurationContext(config));
    }
    
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "FlashLight App started");
        
        // Áp dụng ngôn ngữ cho toàn bộ ứng dụng
        PreferenceManager preferenceManager = new PreferenceManager(this);
        
        // Sử dụng tiếng Anh làm mặc định thay vì ngôn ngữ hệ thống
        String languageCode = preferenceManager.getString(KEY_LANGUAGE, "en");
        setAppLocale(languageCode);
        
        // Initialize any app-wide components here
    }
    
    /**
     * Thay đổi ngôn ngữ cho toàn bộ ứng dụng
     */
    private void setAppLocale(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        
        Configuration config = getResources().getConfiguration();
        config.setLocale(locale);
        
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }
    
    /**
     * Khởi động lại ứng dụng để áp dụng thay đổi ngôn ngữ
     */
    public void restartApp() {
        try {
            // Tạo Intent để khởi động lại ứng dụng từ MainActivity
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            
            // Thêm một chút độ trễ để cho phép hoạt động hiện tại kết thúc
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                startActivity(intent);
                
                // Thêm animation mềm mại khi khởi động lại
                if (intent.resolveActivity(getPackageManager()) != null) {
                    // Đảm bảo intent có thể được xử lý
                    startActivity(intent);
                } else {
                    Log.e(TAG, "Failed to restart app: MainActivity not found");
                }
            }, 200);
        } catch (Exception e) {
            Log.e(TAG, "Error restarting app", e);
        }
    }
} 