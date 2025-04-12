package com.example.flashlightai;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.flashlightai.fragment.LanguageSelectionFragment;
import com.example.flashlightai.utils.LanguageManager;
import com.example.flashlightai.utils.PreferenceManager;
import com.example.flashlightai.model.Language;

import java.util.List;
import java.util.Locale;

/**
 * Activity hiển thị giao diện chọn ngôn ngữ
 * Chỉ được hiển thị khi lần đầu khởi chạy ứng dụng
 */
public class LanguageSelectionActivity extends AppCompatActivity implements LanguageSelectionFragment.OnLanguageSelectedListener {

    private static final String KEY_FIRST_RUN = "is_first_run";
    private PreferenceManager preferenceManager;
    private LanguageManager languageManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_selection_container);

        // Khởi tạo PreferenceManager và LanguageManager
        preferenceManager = new PreferenceManager(this);
        languageManager = new LanguageManager(this);

        // Kiểm tra xem đây có phải lần đầu chạy ứng dụng không
        boolean isFirstRun = preferenceManager.getBoolean(KEY_FIRST_RUN, true);

        if (isFirstRun) {
            // Đây là lần đầu chạy, hiển thị fragment chọn ngôn ngữ
            
            // Thiết lập tiếng Anh làm mặc định
            languageManager.saveLanguageCode("en");
            
            // Load fragment chọn ngôn ngữ
            loadLanguageSelectionFragment();
        } else {
            // Không phải lần đầu chạy, chuyển đến MainActivity
            startMainActivity();
        }
    }

    private void loadLanguageSelectionFragment() {
        LanguageSelectionFragment fragment = LanguageSelectionFragment.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onLanguageSelected(String languageCode) {
        // Đánh dấu rằng ứng dụng đã được chạy
        preferenceManager.setBoolean(KEY_FIRST_RUN, false);
        
        // Áp dụng ngôn ngữ mới
        languageManager.saveLanguageCode(languageCode);
        
        // Chuyển đến MainActivity
        startMainActivity();
    }

    @Override
    public void onLanguageSelectionCancelled() {
        // Xử lý trường hợp người dùng hủy chọn ngôn ngữ
        // Trong trường hợp này, chúng ta có thể sử dụng ngôn ngữ mặc định và tiếp tục
        preferenceManager.setBoolean(KEY_FIRST_RUN, false);
        startMainActivity();
    }
} 