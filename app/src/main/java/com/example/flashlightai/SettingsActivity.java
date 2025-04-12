package com.example.flashlightai;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import android.widget.ImageButton;

import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.FragmentTransaction;

import com.example.flashlightai.base.BaseActivity;
import com.example.flashlightai.fragment.LanguageSelectionFragment;
import com.example.flashlightai.fragment.SettingsFragment;
import com.example.flashlightai.utils.PreferenceManager;

/**
 * Activity cài đặt ứng dụng
 */
public class SettingsActivity extends BaseActivity implements LanguageSelectionFragment.OnLanguageSelectedListener {
    
    private static final String TAG = "SettingsActivity";
    private boolean languageChanged = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        
        // Hiển thị nút back trên ActionBar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        
        // Xử lý nút back trong layout nếu có
        ImageButton btnBack = findViewById(R.id.btn_back);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> onBackPressed());
        }
        
        // Hiển thị Fragment cài đặt
        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.settings_container, new SettingsFragment());
            transaction.commit();
        }
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    /**
     * Xử lý khi người dùng chọn một ngôn ngữ mới
     */
    @Override
    public void onLanguageSelected(String languageCode) {
        try {
            // Đánh dấu là đã thay đổi ngôn ngữ
            languageChanged = true;
            
            // Cập nhật ngôn ngữ trong Application
            FlashLightApp app = (FlashLightApp) getApplication();
            app.updateLanguage(languageCode);
            
            // Đóng fragment chọn ngôn ngữ
            getSupportFragmentManager().popBackStack();
            
            // Hiển thị thông báo cho người dùng
            Toast.makeText(this, R.string.language_changed, Toast.LENGTH_SHORT).show();
            
            // Khởi động lại ứng dụng để áp dụng ngôn ngữ mới
            super.restartApp();
        } catch (Exception e) {
            // Xử lý lỗi an toàn
            Toast.makeText(this, "Error applying language change", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    
    /**
     * Xử lý khi người dùng hủy chọn ngôn ngữ
     */
    @Override
    public void onLanguageSelectionCancelled() {
        // Quay lại Fragment cài đặt
        getSupportFragmentManager().popBackStack();
    }
    
    @Override
    public void onBackPressed() {
        // Nếu đã thay đổi ngôn ngữ, khởi động lại MainActivity
        if (languageChanged) {
            super.restartApp();
            return;
        }
        super.onBackPressed();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Không cần xử lý logic khởi động lại ở đây nữa
    }

    /**
     * Hiển thị Fragment chọn ngôn ngữ
     */
    public void showLanguageSelectionFragment() {
        LanguageSelectionFragment fragment = LanguageSelectionFragment.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.settings_container, fragment);
        transaction.addToBackStack("language_selection");
        transaction.commit();
    }
} 