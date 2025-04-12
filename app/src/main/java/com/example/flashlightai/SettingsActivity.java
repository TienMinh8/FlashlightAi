package com.example.flashlightai;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.flashlightai.fragment.LanguageSelectionFragment;
import com.example.flashlightai.fragment.SettingsFragment;
import com.example.flashlightai.utils.LanguageManager;

/**
 * Activity hiển thị giao diện cài đặt của ứng dụng
 */
public class SettingsActivity extends AppCompatActivity implements LanguageSelectionFragment.OnLanguageSelectedListener {
    
    private static final String TAG_LANGUAGE_FRAGMENT = "language_fragment";
    private boolean languageChanged = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        
        // Không cần xử lý ActionBar vì đã có nút back trong layout
        
        // Kiểm tra xem đây có phải là tạo activity mới hay khôi phục trạng thái
        if (savedInstanceState == null) {
            // Thêm SettingsFragment vào container
            SettingsFragment fragment = new SettingsFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.settings_container, fragment);
            transaction.commit();
        }
    }
    
    /**
     * Hiển thị Fragment chọn ngôn ngữ
     */
    public void showLanguageSelectionFragment() {
        LanguageSelectionFragment fragment = LanguageSelectionFragment.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.settings_container, fragment);
        transaction.addToBackStack(TAG_LANGUAGE_FRAGMENT);
        transaction.commit();
    }
    
    /**
     * Xử lý sự kiện click vào nút back trong layout
     */
    public void onBackButtonClick(View view) {
        onBackPressed();
    }
    
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Xử lý nút Back trên thanh tiêu đề
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    public void onBackPressed() {
        // Nếu có fragment trong stack, xử lý back bình thường
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            // Nếu không có fragment nào trong stack, thoát activity
            finish();
        }
    }
    
    @Override
    public void onLanguageSelected(String languageCode) {
        // Ngôn ngữ đã được thay đổi, quay lại Fragment cài đặt
        getSupportFragmentManager().popBackStack();
        
        // Hiển thị thông báo về việc khởi động lại ứng dụng
        Toast.makeText(this, R.string.language_changed_restart, Toast.LENGTH_LONG).show();
        
        // Đánh dấu đã thay đổi ngôn ngữ
        languageChanged = true;
        
        // Khởi động lại Activity ngay để áp dụng ngôn ngữ mới
        recreate();
    }
    
    @Override
    public void onLanguageSelectionCancelled() {
        // Người dùng đã hủy chọn ngôn ngữ, quay lại Fragment cài đặt
        getSupportFragmentManager().popBackStack();
    }
    
    @Override
    protected void onDestroy() {
        // Khi thoát khỏi Activity, nếu đã thay đổi ngôn ngữ thì
        // khởi động lại MainActivity để áp dụng ngôn ngữ mới
        if (isFinishing() && languageChanged) {
            // Khởi động lại MainActivity để áp dụng ngôn ngữ mới
            FlashLightApp app = (FlashLightApp) getApplication();
            app.restartApp();
        }
        super.onDestroy();
    }
} 