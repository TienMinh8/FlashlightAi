package com.example.flashlightai;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.util.Log;

import com.example.flashlightai.utils.AdManager;
import com.example.flashlightai.utils.PreferenceManager;

import java.util.Locale;

/**
 * Lớp ứng dụng cho FlashLight
 */
public class FlashLightApp extends Application {
    private static final String TAG = "FlashLightApp";
    private static final String KEY_LANGUAGE = "app_language";
    private static boolean sLanguageChanged = false;
    
    /**
     * Thiết lập ngôn ngữ cho ứng dụng khi khởi tạo
     */
    @Override
    protected void attachBaseContext(Context base) {
        Context context = updateBaseContextLocale(base);
        super.attachBaseContext(context);
    }
    
    /**
     * Cập nhật Context với Locale đã lưu
     */
    private Context updateBaseContextLocale(Context context) {
        PreferenceManager preferenceManager = new PreferenceManager(context);
        String languageCode = preferenceManager.getString(KEY_LANGUAGE, null);
        
        if (languageCode == null || languageCode.isEmpty()) {
            // Set default language to English regardless of system language
            languageCode = "en";
            preferenceManager.setString(KEY_LANGUAGE, languageCode);
            Log.d(TAG, "Set default language: " + languageCode);
        }
        
        Log.d(TAG, "App using language: " + languageCode);
        
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        
        // API 17+ (Android 4.2+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            context = updateResourcesLocale(context, locale);
        } else {
            // API 16 trở xuống
            updateResourcesLocaleLegacy(context, locale); 
        }
        
        return context;
    }
    
    /**
     * Check if a language is supported
     * @param languageCode The language code to check
     * @return True if the language is supported
     */
    private boolean isSupportedLanguage(String languageCode) {
        String[] supportedLanguages = {"en", "vi", "es", "ru", "id", "uk"};
        for (String lang : supportedLanguages) {
            if (lang.equals(languageCode)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Cập nhật tài nguyên cho Android 4.2+ (API 17+)
     */
    private Context updateResourcesLocale(Context context, Locale locale) {
        Configuration configuration = new Configuration(context.getResources().getConfiguration());
        
        // API 17-24
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            configuration.setLocale(locale);
            return context.createConfigurationContext(configuration);
        }
        
        // API 25+ (Nougat+)
        return createConfigurationContextCompat(context, configuration, locale);
    }
    
    /**
     * Cập nhật tài nguyên cho phiên bản Android cũ (API 16-)
     */
    @SuppressWarnings("deprecation")
    private void updateResourcesLocaleLegacy(Context context, Locale locale) {
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
    }
    
    /**
     * Xử lý đặc biệt cho Android N trở lên
     */
    private Context createConfigurationContextCompat(Context context, Configuration configuration, Locale locale) {
        configuration.setLocale(locale);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            // Cho API 25+
            return context.createConfigurationContext(configuration);
        } else {
            // Cho API 24
            context = context.createConfigurationContext(configuration);
            return context;
        }
    }
    
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "FlashLight App started");
        
        // Khởi tạo AdMob
        initializeAdMob();
    }
    
    /**
     * Khởi tạo AdMob và tải các quảng cáo
     */
    private void initializeAdMob() {
        try {
            // Khởi tạo AdManager
            AdManager adManager = AdManager.getInstance(this);
            adManager.initialize();
            Log.d(TAG, "AdMob initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing AdMob: " + e.getMessage());
        }
    }
    
    /**
     * Cập nhật ngôn ngữ của ứng dụng và đánh dấu là đã thay đổi
     */
    public void updateLanguage(String languageCode) {
        PreferenceManager preferenceManager = new PreferenceManager(this);
        String currentLanguage = preferenceManager.getString(KEY_LANGUAGE, Locale.getDefault().getLanguage());
        
        if (languageCode != null && !languageCode.equals(currentLanguage)) {
            preferenceManager.setString(KEY_LANGUAGE, languageCode);
            sLanguageChanged = true;
            Log.d(TAG, "Language updated to: " + languageCode);
        }
    }
    
    /**
     * Kiểm tra xem ngôn ngữ đã được thay đổi chưa
     */
    public static boolean isLanguageChanged() {
        return sLanguageChanged;
    }
    
    /**
     * Đặt lại trạng thái thay đổi ngôn ngữ
     */
    public static void resetLanguageChangedFlag() {
        sLanguageChanged = false;
    }
    
    /**
     * Khởi động lại ứng dụng để áp dụng thay đổi như cài đặt ngôn ngữ
     */
    public void restartApp() {
        try {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("restart_from_language_change", true);
            startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Error restarting app: " + e.getMessage());
        }
    }
} 