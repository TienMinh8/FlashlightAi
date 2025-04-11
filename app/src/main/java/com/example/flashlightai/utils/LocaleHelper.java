package com.example.flashlightai.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;

import java.util.Locale;

/**
 * Lớp trợ giúp cho việc quản lý ngôn ngữ trong ứng dụng
 */
public class LocaleHelper {
    private static final String KEY_LANGUAGE = "language";
    private static final String LANG_EN = "en"; // Default language

    /**
     * Lấy ngôn ngữ hiện tại từ các thiết lập
     * @param context Context hiện tại
     * @return Mã ngôn ngữ hiện tại
     */
    public static String getCurrentLanguage(Context context) {
        PreferenceManager preferenceManager = new PreferenceManager(context);
        return preferenceManager.getString(KEY_LANGUAGE, LANG_EN);
    }

    /**
     * Đặt ngôn ngữ hiện tại
     * @param context Context hiện tại
     * @param languageCode Mã ngôn ngữ
     * @return Context mới đã được cấu hình với ngôn ngữ mới
     */
    public static Context setLocale(Context context, String languageCode) {
        // Lưu ngôn ngữ đã chọn
        PreferenceManager preferenceManager = new PreferenceManager(context);
        preferenceManager.setString(KEY_LANGUAGE, languageCode);

        // Cập nhật cấu hình ngôn ngữ
        return updateResources(context, languageCode);
    }

    /**
     * Cập nhật tài nguyên ngôn ngữ cho context
     * @param context Context hiện tại
     * @param languageCode Mã ngôn ngữ
     * @return Context mới với cấu hình ngôn ngữ đã được cập nhật
     */
    private static Context updateResources(Context context, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Resources resources = context.getResources();
        Configuration config = new Configuration(resources.getConfiguration());
        config.setLocale(locale);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return context.createConfigurationContext(config);
        } else {
            resources.updateConfiguration(config, resources.getDisplayMetrics());
            return context;
        }
    }
} 