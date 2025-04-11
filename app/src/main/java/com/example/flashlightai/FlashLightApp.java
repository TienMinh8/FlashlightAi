package com.example.flashlightai;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.example.flashlightai.utils.LocaleHelper;
import com.example.flashlightai.utils.PreferenceManager;

/**
 * Application class for the FlashLight app
 */
public class FlashLightApp extends Application {
    private static final String TAG = "FlashLightApp";
    
    @Override
    protected void attachBaseContext(Context base) {
        // Áp dụng ngôn ngữ trước khi gán context
        String language = LocaleHelper.getCurrentLanguage(base);
        Context context = LocaleHelper.setLocale(base, language);
        super.attachBaseContext(context);
    }
    
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "FlashLight App started");
        
        // Initialize any app-wide components here
    }
} 