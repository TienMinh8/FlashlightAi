package com.example.flashlightai.base;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Lớp Activity cơ sở
 * Tất cả các Activity khác nên kế thừa từ lớp này
 */
public abstract class BaseActivity extends AppCompatActivity {
    
    @Override
    protected void attachBaseContext(Context newBase) {
        // Sử dụng context mặc định
        super.attachBaseContext(newBase);
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