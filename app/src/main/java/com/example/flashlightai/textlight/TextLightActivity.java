package com.example.flashlightai.textlight;

import android.app.AlertDialog;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Switch;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.flashlightai.FlashLightApp;
import com.example.flashlightai.customviews.ColorSliderView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.example.flashlightai.R;
import com.example.flashlightai.MainActivity;
import com.example.flashlightai.screen.ScreenLightActivity;
import com.example.flashlightai.textlight.TextLightView.ScrollDirection;
import com.example.flashlightai.utils.PreferenceManager;
import com.example.flashlightai.SettingsActivity;
import com.google.android.material.slider.Slider;

import com.example.flashlightai.base.BaseActivity;


import java.util.HashMap;
import java.util.Map;
import java.util.Locale;

/**
 * Activity for Text Light display feature
 */
public class TextLightActivity extends BaseActivity {
    private static final String TAG = "TextLightActivity";

    // UI Components
    private TextLightView textLightView;
    private EditText etInputText;
    private Button btnStart;
    private Button btnWallpaper;
    private SeekBar speedSeekBar;
    private SeekBar sizeSeekBar;
    private TextView tvSpeedValue;
    private TextView tvSizeValue;
    private ImageButton btnColorRed;
    private ImageButton btnColorGreen;
    private ImageButton btnColorBlue;
    private ImageButton btnColorWhite;
    private ImageButton btnColorYellow;
    private ScrollView settingsPanel;
    private int currentTextColor = Color.WHITE;
    private boolean isRunning = false;
    private boolean isFullscreen = false;
    private PreferenceManager preferenceManager;
    private int scrollSpeed = 10;
    private int textSize = 100;
    private TextLightView.ScrollDirection scrollDirection = TextLightView.ScrollDirection.RIGHT_TO_LEFT;
    private TextView colorPreview;
    private com.example.flashlightai.customviews.ColorSliderView colorSlider;
    private FrameLayout adContainer;
    private com.example.flashlightai.utils.AdManager adManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Giữ màn hình luôn sáng và thiết lập chế độ full screen giống với ScreenLightActivity
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // Luôn thiết lập chế độ fullscreen khi mở ứng dụng
        setFullScreenMode();
        
        setContentView(R.layout.activity_text_light);

        preferenceManager = new PreferenceManager(this);

        initViews();
        setupListeners();
        setupBottomNavigation();

        // Khôi phục cài đặt từ lần cuối
        restoreLastSettings();
        
        // Hiển thị và chạy text ngay khi màn hình được tạo
        startTextLightAutomatically();
    }

    /**
     * Thiết lập chế độ full screen giống với ScreenLightActivity
     */
    private void setFullScreenMode() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
                
        // Đảm bảo rằng cờ được đặt thành true
        isFullscreen = true;
    }

    /**
     * Khôi phục trạng thái hiển thị bình thường của UI
     */
    private void restoreNormalScreenMode() {
        // Hiện lại thanh trạng thái và thanh điều hướng
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        
        // Đảm bảo isFullscreen được đặt thành false
        isFullscreen = false;
    }

    private void initViews() {
        textLightView = findViewById(R.id.text_light_view);
        etInputText = findViewById(R.id.input_text);
        btnStart = findViewById(R.id.start_button);
        
        // Đặt hướng mặc định là RIGHT_TO_LEFT thay vì LEFT_TO_RIGHT
        scrollDirection = TextLightView.ScrollDirection.RIGHT_TO_LEFT;
        
        // Thay thế slider bằng seekbar từ layout
        speedSeekBar = findViewById(R.id.scroll_speed_seekbar);
        sizeSeekBar = findViewById(R.id.text_size_seekbar);
        
        // Giới hạn giá trị tối đa của seekbar size là 200
        sizeSeekBar.setMax(200);
        
        settingsPanel = findViewById(R.id.settings_panel);

        // Thay thế các nút màu bằng ColorSliderView
        colorPreview = findViewById(R.id.color_preview);
        colorSlider = findViewById(R.id.color_slider);
        
        // Khởi tạo container quảng cáo và hiển thị quảng cáo
        adContainer = findViewById(R.id.ad_container);
        adManager = com.example.flashlightai.utils.AdManager.getInstance(this);
        adManager.showBannerAd(adContainer);

        // Thiết lập giá trị mặc định
        speedSeekBar.setProgress(10);
        sizeSeekBar.setProgress(160);
        updateSpeedText(10);
        updateSizeText(160);
        
        // Thêm touch listener cho layout chính để ẩn bàn phím khi người dùng chạm vào ngoài EditText
        View rootView = findViewById(android.R.id.content);
        rootView.setOnTouchListener((v, event) -> {
            clearKeyboardFocus();
            return false;
        });
        
        // Cấu hình EditText để cải thiện trải nghiệm nhập liệu
        etInputText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                // Khi EditText có focus, chỉnh lại layout để đảm bảo nó luôn hiển thị
                FrameLayout textDisplayArea = findViewById(R.id.text_display_area);
                if (textDisplayArea != null) {
                    // Giảm kích thước hiển thị khi đang nhập text
                    ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) textDisplayArea.getLayoutParams();
                    params.height = getResources().getDimensionPixelSize(R.dimen.text_display_height_when_editing);
                    textDisplayArea.setLayoutParams(params);
                }
                
                // Gọi phương thức cuộn màn hình để đảm bảo EditText hiển thị
                scrollToEditText();
            } else {
                // Khôi phục kích thước khi không nhập text
                FrameLayout textDisplayArea = findViewById(R.id.text_display_area);
                if (textDisplayArea != null) {
                    ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) textDisplayArea.getLayoutParams();
                    params.height = 0; // MATCH_CONSTRAINT
                    textDisplayArea.setLayoutParams(params);
                }
            }
        });
        
        // Thêm click listener cho EditText
        etInputText.setOnClickListener(v -> {
            // Đảm bảo EditText hiển thị khi được nhấn vào
            scrollToEditText();
        });
        
        // Listener cho seekbar speed
        speedSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                scrollSpeed = progress;
                updateSpeedText(progress);
                
                if (isRunning) {
                    updateTextLight();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Do nothing
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Do nothing
            }
        });
        
        // Listener cho seekbar size
        sizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            // Giới hạn kích thước tối đa cùng với max của seekbar
            private final int MAX_TEXT_SIZE = 200;
            
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Đảm bảo kích thước không vượt quá giới hạn
                if (progress > MAX_TEXT_SIZE) {
                    progress = MAX_TEXT_SIZE;
                    seekBar.setProgress(progress);
                }
                
                textSize = progress;
                updateSizeText(progress);
                
                if (isRunning) {
                    updateTextLight();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Do nothing
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Do nothing
            }
        });
    }

    private void setupListeners() {
        // Nút Start giờ sẽ là nút Fullscreen
        btnStart.setOnClickListener(v -> toggleFullScreenLandscape());
        
        // Xử lý nút back
        ImageButton btnBack = findViewById(R.id.btn_back);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
        
        // Xử lý nút fullscreen
        ImageButton btnFullscreen = findViewById(R.id.btn_fullscreen);
        if (btnFullscreen != null) {
            btnFullscreen.setOnClickListener(v -> toggleFullscreen());
        }
        
        // Xử lý EditText để khi người dùng nhấn Done trên bàn phím sẽ ẩn bàn phím
        etInputText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE) {
                // Ẩn bàn phím
                clearKeyboardFocus();
                return true;
            }
            return false;
        });
        
        // Thêm TextWatcher để cập nhật text trong khi người dùng gõ
        etInputText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Cập nhật văn bản hiển thị nếu đang chạy
                if (isRunning) {
                    updateTextLight();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        
        // Thiết lập các listener cho ColorSlider
        // 1. Listener khi người dùng đang kéo thanh màu (cập nhật màu ngay lập tức)
        colorSlider.setOnColorChangingListener(color -> {
            currentTextColor = color;
            updateColorPreview(color);
            
            // Cập nhật màu chữ ngay lập tức
            if (isRunning) {
                textLightView.setTextColor(color);
            }
        });
        
        // 2. Listener khi người dùng chọn xong màu (thả tay khỏi thanh màu)
        colorSlider.setOnColorSelectedListener(color -> {
            currentTextColor = color;
            updateColorPreview(color);
            
            if (isRunning) {
                updateTextLight();
            }
        });
    }

    private void updateColorPreview(int color) {
        // Cập nhật màu và chữ "A" cho colorPreview
        colorPreview.setBackgroundColor(color);
        
        // Đảm bảo chữ "A" luôn hiển thị với màu tương phản
        int textColor = isColorDark(color) ? Color.WHITE : Color.BLACK;
        colorPreview.setTextColor(textColor);
    }
    
    // Helper method để kiểm tra màu tối hay sáng
    private boolean isColorDark(int color) {
        double darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
        return darkness >= 0.5;
    }

    private void updateSpeedText(float value) {
        if (tvSpeedValue != null) {
            tvSpeedValue.setText(String.format("%.0f", value));
        }
    }

    private void updateSizeText(float value) {
        if (tvSizeValue != null) {
            tvSizeValue.setText(String.format("%.0f", value));
        }
    }

    private void saveCurrentSettings(String text, float speed, int size, int color) {
        preferenceManager.setString("text_light_text", text);
        // Không cần lưu direction vì sử dụng giá trị mặc định
        // preferenceManager.setInt("text_light_direction", direction.ordinal());
        preferenceManager.setFloat("text_light_speed", speed);
        preferenceManager.setInt("text_light_size", size);
        preferenceManager.setInt("text_light_color", color);
    }

    private void restoreLastSettings() {
        // Lấy cài đặt từ lần sử dụng trước đó
        String lastText = preferenceManager.getString("text_light_text", "");
        float speedValue = preferenceManager.getFloat("text_light_speed", 10f);
        int lastSpeed = (int)speedValue;
        int lastSize = preferenceManager.getInt("text_light_size", 160);
        int lastColor = preferenceManager.getInt("text_light_color", Color.WHITE);
        
        // Thiết lập các giá trị
        etInputText.setText(lastText);
        speedSeekBar.setProgress(lastSpeed);
        sizeSeekBar.setProgress(lastSize);
        scrollSpeed = lastSpeed;
        textSize = lastSize;
        currentTextColor = lastColor;
        
        // Cập nhật hiển thị
        updateSpeedText(lastSpeed);
        updateSizeText(lastSize);
        
        // Cập nhật ColorSliderView với màu đã lưu
        colorSlider.setSelectedColor(lastColor);
        updateColorPreview(lastColor);

        // Đặt hướng mặc định là RIGHT_TO_LEFT
        scrollDirection = TextLightView.ScrollDirection.RIGHT_TO_LEFT;
    }

    /**
     * Chuyển đổi giữa chế độ toàn màn hình và bình thường
     */
    private void toggleFullscreen() {
        isFullscreen = !isFullscreen;
        
        if (isFullscreen) {
            // Ẩn settings panel
            settingsPanel.setVisibility(View.GONE);
            
            // Ẩn thanh navigation và status bar
            setFullScreenMode();
            
            // Cập nhật nút
            btnStart.setText(R.string.exit_fullscreen);
            
            // Thêm listener để bắt touch event và thoát full screen
            textLightView.setOnClickListener(v -> toggleFullscreen());
            
            // Thông báo cho người dùng cách thoát fullscreen
            Toast.makeText(this, "Nhấn vào màn hình để thoát chế độ toàn màn hình", Toast.LENGTH_SHORT).show();
        } else {
            // Hiện settings panel
            settingsPanel.setVisibility(View.VISIBLE);
            
            // Khôi phục trạng thái bình thường của màn hình
            restoreNormalScreenMode();
            
            // Cập nhật nút
            btnStart.setText(R.string.fullscreen);
            
            // Xóa listener khỏi textLightView khi không còn ở chế độ full screen
            textLightView.setOnClickListener(null);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            // Luôn đảm bảo chế độ fullscreen khi cửa sổ có focus
            setFullScreenMode();
        }
    }

    @Override
    public void onBackPressed() {
        if (getRequestedOrientation() == android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            // Nếu đang ở chế độ landscape, quay về portrait
            toggleFullScreenLandscape();
        } else if (isFullscreen) {
            // Nếu đang ở chế độ fullscreen thông thường, thoát fullscreen
            toggleFullscreen();
        } else {
            // Thêm animation khi thoát màn hình
            super.onBackPressed();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Không cần dừng animation khi thoát ứng dụng vì chữ luôn chạy
        
        // KHÔNG tự động quay về portrait mode khi ứng dụng đi vào background
        // Việc này có thể gây ra màn hình không duy trì chế độ landscape
        /*
        if (getRequestedOrientation() == android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            // Đảm bảo quay về portrait mode khi ứng dụng đi vào background
            setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        */
    }

    @Override
    protected void onResume() {
        super.onResume();
        
        // Đảm bảo thanh điều hướng vẫn bị ẩn khi quay lại
        setFullScreenMode();
        
        // Cập nhật trạng thái của ứng dụng
        if (isRunning && textLightView != null) {
            textLightView.startScrolling();
        }
        
        // Nhắc nhở người dùng nhấn nút fullscreen nếu chưa
        if (!isFullscreen) {
            try {
                Toast.makeText(this, R.string.fullscreen_suggestion, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Log.e(TAG, "Error showing fullscreen suggestion toast", e);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isRunning) {
            textLightView.stopScrolling();
        }
    }

    @Override
    public void finish() {
        super.finish();
        // Thêm animation khi thoát màn hình để tạo trải nghiệm nhất quán
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void updateTextLight() {
        // Lấy giá trị từ giao diện người dùng
        String text = etInputText.getText().toString();
        
        // Luôn đảm bảo sử dụng hướng RIGHT_TO_LEFT
        scrollDirection = TextLightView.ScrollDirection.RIGHT_TO_LEFT;
        
        // Cập nhật TextLightView với tham số mới
        // Nhân kích thước với 3 để kích thước 1 = 3px
        textLightView.setupTextLight(
            text,
            textSize * 3, 
            currentTextColor,
            scrollDirection,
            scrollSpeed
        );
    }

    /**
     * Thiết lập BottomNavigationView
     */
    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        if (bottomNav != null) {
            // Đặt mục hiện tại là Text Light
            bottomNav.setSelectedItemId(R.id.navigation_text_light);
            
            // Thiết lập listener cho các mục
            bottomNav.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.navigation_flash) {
                    // Về trang đèn flash
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra("navigate_to", "flash");
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    finish();
                    return true;
                } else if (itemId == R.id.navigation_screen) {
                    // Chuyển đến trang Screen Light
                    Intent intent = new Intent(this, ScreenLightActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    finish();
                    return true;
                } else if (itemId == R.id.navigation_text_light) {
                    // Đã ở trang Text Light
                    return true;
                }
                return false;
            });
            
            // Thêm nút Settings vào top bar nếu có
            ImageButton btnSettings = findViewById(R.id.settings_button);
            if (btnSettings != null) {
                btnSettings.setOnClickListener(v -> {
                    // Mở SettingsActivity
                    Intent intent = new Intent(this, SettingsActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                });
            }
        }
    }

    /**
     * Chuyển đổi giữa chế độ landscape fullscreen và portrait
     */
    private void toggleFullScreenLandscape() {
        // Nếu đã đang ở chế độ landscape, chuyển về portrait
        if (getRequestedOrientation() == android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            // Hiện lại settings panel
            settingsPanel.setVisibility(View.VISIBLE);
            
            // Hiện lại thanh header
            View headerBar = findViewById(R.id.header_bar);
            if (headerBar != null) {
                headerBar.setVisibility(View.VISIBLE);
            }
            
            // Hiện lại thanh bottom navigation
            BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
            if (bottomNav != null) {
                bottomNav.setVisibility(View.VISIBLE);
            }
            
            // Hiện lại EditText
            etInputText.setVisibility(View.VISIBLE);
            
            // Khôi phục trạng thái bình thường của màn hình
            restoreNormalScreenMode();
            
            // Đặt orientation thành portrait (quay dọc)
            setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            
            // Xóa listener khỏi textLightView
            textLightView.setOnClickListener(null);
            
            // Cập nhật văn bản nút
            btnStart.setText(R.string.fullscreen);
            
            // Đảm bảo isFullscreen = false
            isFullscreen = false;
        } else {
            
            // Ẩn bàn phím nếu đang hiển thị
            android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            View currentFocus = getCurrentFocus();
            if (currentFocus != null) {
                imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
            }
            
            // Ẩn settings panel
            settingsPanel.setVisibility(View.GONE);
            
            // Ẩn thanh header
            View headerBar = findViewById(R.id.header_bar);
            if (headerBar != null) {
                headerBar.setVisibility(View.GONE);
            }
            
            // Ẩn thanh bottom navigation
            BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
            if (bottomNav != null) {
                bottomNav.setVisibility(View.GONE);
            }
            
            // Ẩn EditText khi ở chế độ fullscreen
            etInputText.setVisibility(View.GONE);
            
            // Thiết lập chế độ full screen
            setFullScreenMode();
            
            // Đặt orientation thành landscape (quay ngang)
            setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            
            // Thêm listener để bắt touch event và thoát full screen
            textLightView.setOnClickListener(v -> toggleFullScreenLandscape());
            
            // Cập nhật văn bản nút (mặc dù nó không hiển thị ở chế độ này)
            btnStart.setText(R.string.exit_fullscreen);
            
            // Đảm bảo isFullscreen = true
            isFullscreen = true;
            
            Toast.makeText(this, "Nhấn vào màn hình để thoát chế độ toàn màn hình", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Ẩn bàn phím và bỏ focus khỏi EditText
     */
    private void clearKeyboardFocus() {
        // Ẩn bàn phím
        android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        View currentFocus = getCurrentFocus();
        if (currentFocus != null) {
            imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
            currentFocus.clearFocus();
        }
        
        // Đặt focus vào view gốc để bỏ focus khỏi EditText
        View rootView = findViewById(android.R.id.content);
        rootView.requestFocus();
    }

    /**
     * Cuộn màn hình để đảm bảo EditText luôn hiển thị
     */
    private void scrollToEditText() {
        // Đảm bảo UI đã được vẽ
        etInputText.post(() -> {
            // Đặt focus vào EditText
            etInputText.requestFocus();
            
            // Hiển thị bàn phím
            android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.showSoftInput(etInputText, android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT);
            
            // Scroll đến EditText
            int[] location = new int[2];
            etInputText.getLocationOnScreen(location);
            
            // Tạo một Rect để đại diện cho vùng nhìn thấy của EditText
            android.graphics.Rect rect = new android.graphics.Rect(
                    location[0], 
                    location[1], 
                    location[0] + etInputText.getWidth(), 
                    location[1] + etInputText.getHeight());
            
            // Đề phòng khi dùng ScrollView
            View parentScrollView = findViewById(R.id.settings_panel);
            if (parentScrollView instanceof ScrollView) {
                ((ScrollView) parentScrollView).requestChildRectangleOnScreen(etInputText, rect, false);
            }
        });
    }

    /**
     * Khởi động chữ chạy tự động khi mở màn hình
     */
    private void startTextLightAutomatically() {
        String text = etInputText.getText().toString();
        if (text.isEmpty()) {
            text = getString(R.string.default_text);
            etInputText.setText(text);
        }
        
        // Sử dụng kích thước với hệ số nhân 3 để kích thước 1 = 3px
        int enhancedTextSize = textSize * 3;
        
        // Khởi động chữ chạy
        scrollDirection = TextLightView.ScrollDirection.RIGHT_TO_LEFT;
        textLightView.setupTextLight(text, enhancedTextSize, currentTextColor, scrollDirection, scrollSpeed, false);
        isRunning = true;
        
        // Lưu lại cài đặt hiện tại
        saveCurrentSettings(text, scrollSpeed, textSize, currentTextColor);
    }

    @Override
    public void onConfigurationChanged(android.content.res.Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        
        // Kiểm tra xem thay đổi cấu hình có phải là thay đổi hướng màn hình không
        if (newConfig.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE) {
            // Nếu đang ở chế độ landscape và isFullscreen là true
            // duy trì trạng thái fullscreen
            if (isFullscreen) {
                getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN);
            }
        } else if (newConfig.orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT) {
            // Nếu trở về portrait nhưng vẫn duy trì trạng thái fullscreen
            if (isFullscreen) {
                // Continue with fullscreen code
                getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            }
        }
    }
} 