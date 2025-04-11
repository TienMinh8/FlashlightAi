package com.example.flashlightai;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.flashlightai.base.BaseActivity;
import com.example.flashlightai.utils.LocaleHelper;
import com.example.flashlightai.utils.PreferenceManager;

/**
 * Activity cho phép người dùng thay đổi ngôn ngữ ứng dụng
 */
public class LanguageActivity extends BaseActivity {
    private static final String TAG = "LanguageActivity";
    private static final String KEY_LANGUAGE = "language";
    
    // Danh sách các mã ngôn ngữ
    private static final String LANG_EN = "en"; // English
    private static final String LANG_ES = "es"; // Spanish
    private static final String LANG_PT = "pt"; // Portuguese
    private static final String LANG_HI = "hi"; // Hindi
    private static final String LANG_RU = "ru"; // Russian
    private static final String LANG_AR = "ar"; // Arabic
    private static final String LANG_BN = "bn"; // Bengali/Bangladeshi
    private static final String LANG_ID = "id"; // Indonesian
    private static final String LANG_VI = "vi"; // Vietnamese
    
    private PreferenceManager preferenceManager;
    private String currentLanguage;
    private String selectedLanguage;
    private RadioGroup radioGroupLanguages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);
        
        // Khởi tạo PreferenceManager
        preferenceManager = new PreferenceManager(this);
        
        // Lấy ngôn ngữ hiện tại
        currentLanguage = LocaleHelper.getCurrentLanguage(this);
        selectedLanguage = currentLanguage;
        
        // Khởi tạo UI
        initUI();
        
        // Cập nhật UI để hiển thị ngôn ngữ đã chọn
        updateSelectedLanguage(currentLanguage);
    }
    
    private void initUI() {
        // Thiết lập RadioGroup
        radioGroupLanguages = findViewById(R.id.radio_group_languages);
        
        // Thiết lập nút Back
        ImageView btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> onBackPressed());
        
        // Thiết lập nút Confirm
        ImageView btnConfirm = findViewById(R.id.btn_confirm);
        btnConfirm.setOnClickListener(v -> confirmLanguageSelection());
        
        // Thiết lập sự kiện cho RadioGroup
        radioGroupLanguages.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton selectedRadioButton = findViewById(checkedId);
            if (selectedRadioButton != null) {
                String tag = (String) selectedRadioButton.getTag();
                if (tag != null) {
                    selectedLanguage = tag;
                }
            }
        });
        
        // Thiết lập sự kiện cho từng CardView
        setupCardClickListeners();
    }
    
    private void setupCardClickListeners() {
        // English
        CardView cardEnglish = findViewById(R.id.card_english);
        cardEnglish.setOnClickListener(v -> {
            RadioButton radioButton = findViewById(R.id.radio_english);
            radioButton.setChecked(true);
        });
        
        // Spanish
        CardView cardSpanish = findViewById(R.id.card_spanish);
        cardSpanish.setOnClickListener(v -> {
            RadioButton radioButton = findViewById(R.id.radio_spanish);
            radioButton.setChecked(true);
        });
        
        // Portuguese
        CardView cardPortuguese = findViewById(R.id.card_portuguese);
        cardPortuguese.setOnClickListener(v -> {
            RadioButton radioButton = findViewById(R.id.radio_portuguese);
            radioButton.setChecked(true);
        });
        
        // Hindi
        CardView cardHindi = findViewById(R.id.card_hindi);
        cardHindi.setOnClickListener(v -> {
            RadioButton radioButton = findViewById(R.id.radio_hindi);
            radioButton.setChecked(true);
        });
        
        // Russian
        CardView cardRussian = findViewById(R.id.card_russian);
        cardRussian.setOnClickListener(v -> {
            RadioButton radioButton = findViewById(R.id.radio_russian);
            radioButton.setChecked(true);
        });
        
        // Arabic
        CardView cardArabic = findViewById(R.id.card_arabic);
        cardArabic.setOnClickListener(v -> {
            RadioButton radioButton = findViewById(R.id.radio_arabic);
            radioButton.setChecked(true);
        });
        
        // Bangladeshi
        CardView cardBangladeshi = findViewById(R.id.card_bangladeshi);
        cardBangladeshi.setOnClickListener(v -> {
            RadioButton radioButton = findViewById(R.id.radio_bangladeshi);
            radioButton.setChecked(true);
        });
        
        // Indonesian
        CardView cardIndonesian = findViewById(R.id.card_indonesian);
        cardIndonesian.setOnClickListener(v -> {
            RadioButton radioButton = findViewById(R.id.radio_indonesian);
            radioButton.setChecked(true);
        });
        
        // Vietnamese
        CardView cardVietnamese = findViewById(R.id.card_vietnamese);
        cardVietnamese.setOnClickListener(v -> {
            RadioButton radioButton = findViewById(R.id.radio_vietnamese);
            radioButton.setChecked(true);
        });
    }
    
    private void confirmLanguageSelection() {
        if (selectedLanguage.equals(currentLanguage)) {
            // Không thay đổi nếu đã chọn ngôn ngữ hiện tại
            finish();
            return;
        }
        
        try {
            // Áp dụng ngôn ngữ mới
            Context context = LocaleHelper.setLocale(this, selectedLanguage);
            
            // Thông báo cho người dùng
            String message = getString(R.string.language_changed);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            
            // Khởi động lại tất cả Activity để áp dụng thay đổi ngôn ngữ
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("language_changed", true);
            finish(); // Đóng Activity này trước
            startActivity(intent); // Khởi động lại MainActivity
        } catch (Exception e) {
            Log.e(TAG, "Error changing language: " + e.getMessage());
            Toast.makeText(this, "Error changing language", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void updateSelectedLanguage(String languageCode) {
        // Xóa tất cả các lựa chọn hiện tại
        radioGroupLanguages.clearCheck();
        
        int radioButtonId;
        switch (languageCode) {
            case LANG_ES:
                radioButtonId = R.id.radio_spanish;
                break;
            case LANG_PT:
                radioButtonId = R.id.radio_portuguese;
                break;
            case LANG_HI:
                radioButtonId = R.id.radio_hindi;
                break;
            case LANG_RU:
                radioButtonId = R.id.radio_russian;
                break;
            case LANG_AR:
                radioButtonId = R.id.radio_arabic;
                break;
            case LANG_BN:
                radioButtonId = R.id.radio_bangladeshi;
                break;
            case LANG_ID:
                radioButtonId = R.id.radio_indonesian;
                break;
            case LANG_VI:
                radioButtonId = R.id.radio_vietnamese;
                break;
            case LANG_EN:
            default:
                radioButtonId = R.id.radio_english;
                break;
        }
        
        // Chọn radio button tương ứng
        radioGroupLanguages.check(radioButtonId);
    }
    
    /**
     * Áp dụng ngôn ngữ đã lưu cho ứng dụng
     * Gọi phương thức này trong onCreate của MainActivity
     */
    public static void applyLanguage(Context context) {
        String languageCode = LocaleHelper.getCurrentLanguage(context);
        LocaleHelper.setLocale(context, languageCode);
    }
}