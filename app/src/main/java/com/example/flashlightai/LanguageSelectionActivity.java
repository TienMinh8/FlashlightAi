package com.example.flashlightai;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.flashlightai.fragment.LanguageSelectionFragment;
import com.example.flashlightai.utils.AdManager;
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
    private AdManager adManager;
    private boolean isAppOpenAdShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_selection_container);

        // Khởi tạo PreferenceManager, LanguageManager và AdManager
        preferenceManager = new PreferenceManager(this);
        languageManager = new LanguageManager(this);
        adManager = AdManager.getInstance(this);

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
    
    /**
     * Hiển thị quảng cáo App Open sau khi chọn ngôn ngữ
     * và trước khi chuyển đến MainActivity
     */
    private void showAppOpenAdAndStartMainActivity() {
        if (isAppOpenAdShown) {
            startMainActivity();
            return;
        }

        isAppOpenAdShown = true;
        adManager.showAppOpenAd(this, new AdManager.AppOpenAdCallback() {
            @Override
            public void onAdClosed() {
                // Quảng cáo đã đóng, tiếp tục chuyển đến MainActivity
                startMainActivity();
            }

            @Override
            public void onAdFailed() {
                // Không hiển thị được quảng cáo, chuyển thẳng đến MainActivity
                startMainActivity();
            }
        });
    }

    @Override
    public void onLanguageSelected(String languageCode) {
        // Đánh dấu rằng ứng dụng đã được chạy
        preferenceManager.setBoolean(KEY_FIRST_RUN, false);
        
        // Áp dụng ngôn ngữ mới
        languageManager.saveLanguageCode(languageCode);
        
        // Hiển thị quảng cáo App Open và sau đó chuyển đến MainActivity
        showAppOpenAdAndStartMainActivity();
    }

    @Override
    public void onLanguageSelectionCancelled() {
        // Xử lý trường hợp người dùng hủy chọn ngôn ngữ
        // Trong trường hợp này, chúng ta có thể sử dụng ngôn ngữ mặc định và tiếp tục
        preferenceManager.setBoolean(KEY_FIRST_RUN, false);
        
        // Hiển thị quảng cáo App Open và sau đó chuyển đến MainActivity
        showAppOpenAdAndStartMainActivity();
    }
} 