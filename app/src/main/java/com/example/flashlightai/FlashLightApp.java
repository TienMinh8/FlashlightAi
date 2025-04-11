package com.example.flashlightai;

import android.app.Application;
import android.util.Log;

/**
 * Application class for the FlashLight app
 */
public class FlashLightApp extends Application {
    private static final String TAG = "FlashLightApp";
    
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "FlashLight App started");
        
        // Initialize any app-wide components here
    }
} 