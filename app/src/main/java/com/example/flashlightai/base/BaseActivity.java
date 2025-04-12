package com.example.flashlightai.base;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.flashlightai.FlashLightApp;
import com.example.flashlightai.utils.PreferenceManager;

import java.util.Locale;

/**
 * Lớp Activity cơ sở cho tất cả các Activity trong ứng dụng
 * Đảm bảo ngôn ngữ được áp dụng nhất quán
 */
public abstract class BaseActivity extends AppCompatActivity {
    private static final String TAG = "BaseActivity";
    private static final String KEY_LANGUAGE = "app_language";
    
    @Override
    protected void attachBaseContext(Context newBase) {
        // Lấy ngôn ngữ từ Preferences
        PreferenceManager preferenceManager = new PreferenceManager(newBase);
        String languageCode = preferenceManager.getString(KEY_LANGUAGE, null);
        
        if (languageCode == null || languageCode.isEmpty()) {
            // Nếu chưa có ngôn ngữ, sử dụng ngôn ngữ hệ thống
            languageCode = Locale.getDefault().getLanguage();
            // Lưu lại để lần sau dùng
            preferenceManager.setString(KEY_LANGUAGE, languageCode);
        }
        
        // Tạo Locale từ mã ngôn ngữ
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        
        // Cập nhật cấu hình
        Context localizedContext = applyLanguage(newBase, locale);
        super.attachBaseContext(localizedContext);
    }
    
    private Context applyLanguage(Context context, Locale locale) {
        Configuration config = new Configuration(context.getResources().getConfiguration());
        
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            // Android 7.0+
            config.setLocale(locale);
            return context.createConfigurationContext(config);
        } else {
            // Các phiên bản Android cũ
            config.setLocale(locale);
            context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
            return context;
        }
    }
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Activity created with language: " + Locale.getDefault().getLanguage());
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        
        // Kiểm tra nếu ngôn ngữ đã thay đổi từ cài đặt
        if (FlashLightApp.isLanguageChanged()) {
            Log.d(TAG, "Language changed detected, recreating activity");
            FlashLightApp.resetLanguageChangedFlag();
            recreate();
        }
    }
    
    /**
     * Khởi động lại toàn bộ ứng dụng khi cần
     */
    protected void restartApp() {
        try {
            FlashLightApp app = (FlashLightApp) getApplication();
            app.restartApp();
            
            // Kết thúc Activity hiện tại để tránh Activity bị duplicated sau khi khởi động lại
            finish();
        } catch (Exception e) {
            Log.e(TAG, "Error restarting app: " + e.getMessage());
            // Vẫn kết thúc Activity để đảm bảo không bị crash
            finish();
        }
    }
} 