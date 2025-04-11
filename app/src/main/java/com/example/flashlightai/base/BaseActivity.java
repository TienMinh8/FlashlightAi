package com.example.flashlightai.base;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.flashlightai.utils.LocaleHelper;

/**
 * Lớp Activity cơ sở xử lý việc thiết lập ngôn ngữ
 * Tất cả các Activity khác nên kế thừa từ lớp này
 */
public abstract class BaseActivity extends AppCompatActivity {
    
    @Override
    protected void attachBaseContext(Context newBase) {
        // Áp dụng ngôn ngữ đã lưu cho context
        String language = LocaleHelper.getCurrentLanguage(newBase);
        Context context = LocaleHelper.setLocale(newBase, language);
        super.attachBaseContext(context);
    }
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
    }
} 