package com.example.flashlightai.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.util.Log;

import com.example.flashlightai.R;
import com.example.flashlightai.model.Language;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Language management class for the application
 */
public class LanguageManager {
    
    private static final String TAG = "LanguageManager";
    private static final String KEY_LANGUAGE = "app_language";
    private PreferenceManager preferenceManager;
    private Context context;
    
    public LanguageManager(Context context) {
        this.context = context;
        this.preferenceManager = new PreferenceManager(context);
    }
    
    /**
     * Get list of supported languages
     */
    public List<Language> getLanguages() {
        List<Language> languages = new ArrayList<>();
        
        // Get current language code
        String currentLanguageCode = getCurrentLanguageCode();
        
        // Add supported languages
        languages.add(new Language("en", context.getString(R.string.language_english), 
                R.drawable.ic_flag_us, "en".equals(currentLanguageCode)));
        
        languages.add(new Language("vi", context.getString(R.string.language_vietnamese), 
                R.drawable.ic_flag_vietnam, "vi".equals(currentLanguageCode)));
        
        languages.add(new Language("es", context.getString(R.string.language_spanish), 
                R.drawable.ic_flag_es, "es".equals(currentLanguageCode)));
        
        languages.add(new Language("ru", context.getString(R.string.language_russian), 
                R.drawable.ic_flag_russia, "ru".equals(currentLanguageCode)));
        
        languages.add(new Language("id", context.getString(R.string.language_indonesia), 
                R.drawable.ic_flag_indo, "id".equals(currentLanguageCode)));
        
        languages.add(new Language("uk", context.getString(R.string.language_ukrainian), 
                R.drawable.ic_flag_ukraine, "uk".equals(currentLanguageCode)));
        
        languages.add(new Language("hi", context.getString(R.string.language_india), 
                R.drawable.ic_flag_india, "hi".equals(currentLanguageCode)));
        
        languages.add(new Language("ar", context.getString(R.string.language_arabic), 
                R.drawable.ic_flag_arab, "ar".equals(currentLanguageCode)));
        
        languages.add(new Language("bn", context.getString(R.string.language_bangladesh), 
                R.drawable.ic_flag_banglades, "bn".equals(currentLanguageCode)));
        
        return languages;
    }
    
    /**
     * Get current language code
     */
    public String getCurrentLanguageCode() {
        String languageCode = preferenceManager.getString(KEY_LANGUAGE, null);
        
        // If no language is saved, use English as default
        if (languageCode == null || languageCode.isEmpty()) {
            languageCode = "en"; // Always default to English
            
            // Save for future use
            preferenceManager.setString(KEY_LANGUAGE, languageCode);
            Log.d(TAG, "Default language set to: " + languageCode);
        }
        
        return languageCode;
    }
    
    /**
     * Check if a language is supported
     */
    private boolean isLanguageSupported(String languageCode) {
        // List of fully supported languages
        String[] supportedLanguages = {"en", "vi", "es", "ru", "id", "uk"};
        
        for (String supported : supportedLanguages) {
            if (supported.equals(languageCode)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Save selected language code (does not apply immediately)
     */
    public void saveLanguageCode(String languageCode) {
        // Save language code for future use
        preferenceManager.setString(KEY_LANGUAGE, languageCode);
        Log.d(TAG, "Language saved: " + languageCode);
    }
    
    /**
     * Apply language to specific context
     */
    public Context applyLanguage(Context context, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        
        // Create configuration with new locale
        Resources resources = context.getResources();
        Configuration config = new Configuration(resources.getConfiguration());
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // Android 7.0+
            config.setLocale(locale);
            return context.createConfigurationContext(config);
        } else {
            // Android 6.0 and below
            config.locale = locale;
            resources.updateConfiguration(config, resources.getDisplayMetrics());
            return context;
        }
    }
} 