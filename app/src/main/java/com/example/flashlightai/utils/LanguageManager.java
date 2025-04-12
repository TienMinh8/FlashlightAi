package com.example.flashlightai.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;

import com.example.flashlightai.R;
import com.example.flashlightai.model.Language;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Lớp quản lý ngôn ngữ ứng dụng
 */
public class LanguageManager {
    
    private static final String KEY_LANGUAGE = "app_language";
    private PreferenceManager preferenceManager;
    private Context context;
    
    public LanguageManager(Context context) {
        this.context = context;
        this.preferenceManager = new PreferenceManager(context);
    }
    
    /**
     * Lấy danh sách ngôn ngữ hỗ trợ
     */
    public List<Language> getLanguages() {
        List<Language> languages = new ArrayList<>();
        
        // Lấy language code đang được chọn
        String currentLanguageCode = getCurrentLanguageCode();
        
        // Thêm các ngôn ngữ được hỗ trợ
        languages.add(new Language("en", context.getString(R.string.language_english), 
                R.drawable.ic_flag_us, "en".equals(currentLanguageCode)));
        
        languages.add(new Language("vi", context.getString(R.string.language_vietnamese), 
                R.drawable.ic_flag_vietnam, "vi".equals(currentLanguageCode)));
        
        languages.add(new Language("es", context.getString(R.string.language_spanish), 
                R.drawable.ic_flag_es, "es".equals(currentLanguageCode)));
        
        languages.add(new Language("pt", context.getString(R.string.language_portuguese), 
                R.drawable.ic_flag_portugal, "pt".equals(currentLanguageCode)));
        
        languages.add(new Language("ru", context.getString(R.string.language_russian), 
                R.drawable.ic_flag_russia, "ru".equals(currentLanguageCode)));
        
        languages.add(new Language("id", context.getString(R.string.language_indonesia), 
                R.drawable.ic_flag_indo, "id".equals(currentLanguageCode)));
        
        languages.add(new Language("hi", context.getString(R.string.language_india), 
                R.drawable.ic_flag_india, "hi".equals(currentLanguageCode)));
        
        languages.add(new Language("ar", context.getString(R.string.language_arabic), 
                R.drawable.ic_flag_arab, "ar".equals(currentLanguageCode)));
        
        languages.add(new Language("bn", context.getString(R.string.language_bangladesh), 
                R.drawable.ic_flag_banglades, "bn".equals(currentLanguageCode)));
        
        return languages;
    }
    
    /**
     * Lấy mã ngôn ngữ hiện tại
     */
    public String getCurrentLanguageCode() {
        return preferenceManager.getString(KEY_LANGUAGE, Locale.getDefault().getLanguage());
    }
    
    /**
     * Áp dụng ngôn ngữ cho ứng dụng
     */
    public void setLanguage(String languageCode) {
        preferenceManager.setString(KEY_LANGUAGE, languageCode);
        updateResources(languageCode);
    }
    
    /**
     * Cập nhật tài nguyên theo ngôn ngữ
     */
    private void updateResources(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLocale(locale);
        } else {
            configuration.locale = locale;
        }
        
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
    }
} 