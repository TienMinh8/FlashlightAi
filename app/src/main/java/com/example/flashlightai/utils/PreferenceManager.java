package com.example.flashlightai.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Utility class to handle preference storage and retrieval
 */
public class PreferenceManager {
    private static final String PREF_NAME = "FlashLightAiPrefs";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public PreferenceManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void setString(String key, String value) {
        editor.putString(key, value);
        editor.apply();
    }

    public String getString(String key, String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }

    public void setInt(String key, int value) {
        editor.putInt(key, value);
        editor.apply();
    }

    public int getInt(String key, int defaultValue) {
        return sharedPreferences.getInt(key, defaultValue);
    }

    public void setBoolean(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.apply();
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    public void setFloat(String key, float value) {
        editor.putFloat(key, value);
        editor.apply();
    }

    public float getFloat(String key, float defaultValue) {
        return sharedPreferences.getFloat(key, defaultValue);
    }

    public void setLong(String key, long value) {
        editor.putLong(key, value);
        editor.apply();
    }

    public long getLong(String key, long defaultValue) {
        return sharedPreferences.getLong(key, defaultValue);
    }

    public void remove(String key) {
        editor.remove(key);
        editor.apply();
    }

    public void clear() {
        editor.clear();
        editor.apply();
    }
} 