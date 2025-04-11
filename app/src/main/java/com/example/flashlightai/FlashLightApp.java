package com.example.flashlightai;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.example.flashlightai.utils.PreferenceManager;

/**
 * Application class for the FlashLight app
 */
public class FlashLightApp extends Application {
    private static final String TAG = "FlashLightApp";
    
    @Override
    protected void attachBaseContext(Context base) {
        // Đã loại bỏ phần xử lý ngôn ngữ
        super.attachBaseContext(base);
    }
    
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "FlashLight App started");
        
        // Initialize any app-wide components here
    }
} 