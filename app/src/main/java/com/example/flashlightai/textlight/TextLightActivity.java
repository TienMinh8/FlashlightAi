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

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.example.flashlightai.R;
import com.example.flashlightai.MainActivity;
import com.example.flashlightai.screen.ScreenLightActivity;
import com.example.flashlightai.textlight.TextLightView.ScrollDirection;
import com.example.flashlightai.utils.PreferenceManager;
import com.google.android.material.slider.Slider;

import java.util.HashMap;
import java.util.Map;

/**
 * Activity for Text Light display feature
 */
public class TextLightActivity extends AppCompatActivity {
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
    private int scrollSpeed = 50;
    private int textSize = 100;
    private TextLightView.ScrollDirection scrollDirection = TextLightView.ScrollDirection.LEFT_TO_RIGHT;
    private boolean isFullBackground = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Giữ màn hình luôn sáng và thiết lập chế độ full screen giống với ScreenLightActivity
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setFullScreenMode();
        
        setContentView(R.layout.activity_text_light);

        preferenceManager = new PreferenceManager(this);

        initViews();
        setupListeners();
        setupBottomNavigation();

        // Khôi phục cài đặt từ lần cuối
        restoreLastSettings();
    }

    /**
     * Thiết lập chế độ full screen giống với ScreenLightActivity
     */
    private void setFullScreenMode() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    private void initViews() {
        textLightView = findViewById(R.id.text_light_view);
        etInputText = findViewById(R.id.input_text);
        btnStart = findViewById(R.id.start_button);
        btnWallpaper = findViewById(R.id.set_wallpaper_button);
        // Xóa tham chiếu đến RadioGroup direction vì đã loại bỏ khỏi layout
        // rgDirection = findViewById(R.id.scroll_direction_group);
        
        // Đặt hướng mặc định là LEFT_TO_RIGHT
        scrollDirection = TextLightView.ScrollDirection.LEFT_TO_RIGHT;
        
        // Thay thế slider bằng seekbar từ layout
        speedSeekBar = findViewById(R.id.scroll_speed_seekbar);
        sizeSeekBar = findViewById(R.id.text_size_seekbar);
        
        // Giới hạn giá trị tối đa của seekbar size là 150
        sizeSeekBar.setMax(150);
        
        tvSpeedValue = findViewById(R.id.tv_speed_value);
        tvSizeValue = findViewById(R.id.tv_size_value);
        
        settingsPanel = findViewById(R.id.settings_panel);

        btnColorRed = findViewById(R.id.color_red);
        btnColorGreen = findViewById(R.id.color_green);
        btnColorBlue = findViewById(R.id.color_blue);
        btnColorWhite = findViewById(R.id.color_white);
        btnColorYellow = findViewById(R.id.color_yellow);

        // Thiết lập giá trị mặc định
        speedSeekBar.setProgress(50);
        sizeSeekBar.setProgress(100);
        updateSpeedText(50);
        updateSizeText(100);
        
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
            private final int MAX_TEXT_SIZE = 150;
            
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
        btnStart.setOnClickListener(v -> startTextLight());
        
        btnWallpaper.setOnClickListener(v -> {
            toggleFullBackground();
        });
        
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
        
        // Thiết lập listeners cho các nút màu
        setupColorButtons();
    }

    private void setupColorButtons() {
        View.OnClickListener colorClickListener = v -> {
            int color = 0;
            
            if (v.getId() == R.id.color_red) {
                color = ContextCompat.getColor(this, R.color.red);
            } else if (v.getId() == R.id.color_green) {
                color = ContextCompat.getColor(this, R.color.green);
            } else if (v.getId() == R.id.color_blue) {
                color = ContextCompat.getColor(this, R.color.blue);
            } else if (v.getId() == R.id.color_white) {
                color = ContextCompat.getColor(this, R.color.white);
            } else if (v.getId() == R.id.color_yellow) {
                color = ContextCompat.getColor(this, R.color.yellow);
            } else if (v.getId() == R.id.color_purple) {
                color = ContextCompat.getColor(this, R.color.purple);
            }
            
            if (color != 0) {
                currentTextColor = color;
                
                // Cập nhật màu nếu đang chạy
                if (isRunning) {
                    textLightView.setTextColor(color);
                }
                
                // Đổi màu preview nếu có
                View colorPreview = findViewById(R.id.color_preview);
                if (colorPreview != null) {
                    colorPreview.setBackgroundColor(color);
                }
            }
        };
        
        btnColorRed.setOnClickListener(colorClickListener);
        btnColorGreen.setOnClickListener(colorClickListener);
        btnColorBlue.setOnClickListener(colorClickListener);
        btnColorWhite.setOnClickListener(colorClickListener);
        btnColorYellow.setOnClickListener(colorClickListener);
        
        // Thêm button purple nếu có trong layout
        ImageButton btnColorPurple = findViewById(R.id.color_purple);
        if (btnColorPurple != null) {
            btnColorPurple.setOnClickListener(colorClickListener);
        }
    }

    private void startTextLight() {
        String text = etInputText.getText().toString();
        if (text.isEmpty()) {
            text = getString(R.string.default_text); // "I LOVE YOU" hoặc giá trị mặc định khác
        }
        
        // Sau khi start, kiểm tra xem đã chạy chưa
        if (!isRunning) {
            // Đặt kích thước lớn để hiển thị tốt
            int enhancedTextSize = textSize * 3;
            
            textLightView.setupTextLight(text, enhancedTextSize, currentTextColor, scrollDirection, scrollSpeed, false);
            isRunning = true;
            btnStart.setText(R.string.stop);
            
            // Kích hoạt chế độ toàn màn hình và quay ngang chỉ khi người dùng đã chọn Full Background
            if (isFullBackground) {
                enableFullScreenLandscape();
            }
        } else {
            textLightView.stop();
            isRunning = false;
            btnStart.setText(R.string.start);
            
            // Trở về chế độ bình thường nếu đang trong chế độ fullscreen landscape
            if (getRequestedOrientation() == android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                disableFullScreenLandscape();
            }
        }
        
        // Lưu lại cài đặt hiện tại
        saveCurrentSettings(text, scrollSpeed, textSize, currentTextColor);
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
        String text = preferenceManager.getString("text_light_text", "");
        // Không cần đọc direction từ preferences vì chúng ta đã loại bỏ tính năng chọn direction
        // int directionOrdinal = preferenceManager.getInt("text_light_direction", 0);
        float speed = preferenceManager.getFloat("text_light_speed", 5);
        int size = preferenceManager.getInt("text_light_size", 100);
        int color = preferenceManager.getInt("text_light_color", 0xFFFFFFFF);
        
        // Áp dụng cài đặt
        if (!text.isEmpty()) {
            etInputText.setText(text);
        }
        
        // Thiết lập direction luôn là LEFT_TO_RIGHT
        scrollDirection = TextLightView.ScrollDirection.LEFT_TO_RIGHT;
        
        // Không cần thiết lập RadioGroup vì đã loại bỏ
        // int radioButtonId;
        // switch (directionOrdinal) { ... }
        // rgDirection.check(radioButtonId);
        
        speedSeekBar.setProgress((int)speed);
        sizeSeekBar.setProgress(size);
        currentTextColor = color;
        
        updateSpeedText(speed);
        updateSizeText(size);
    }

    private void toggleFullscreen() {
        isFullscreen = !isFullscreen;
        
        if (isFullscreen) {
            // Ẩn settings panel
            settingsPanel.setVisibility(View.GONE);
            
            // Thiết lập chế độ full screen với API tương tự như ScreenLightActivity
            setFullScreenMode();
            
            // Cập nhật nút
            btnWallpaper.setText(R.string.exit_fullscreen);
            
            Toast.makeText(this, "Nhấn vào màn hình để thoát chế độ toàn màn hình", Toast.LENGTH_SHORT).show();
            
            // Thêm listener để bắt touch event và thoát full screen
            textLightView.setOnClickListener(v -> toggleFullscreen());
        } else {
            // Hiện settings panel
            settingsPanel.setVisibility(View.VISIBLE);
            
            // Hiện lại thanh trạng thái nhưng vẫn ẩn thanh điều hướng để tạo trải nghiệm đồng nhất
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            
            // Cập nhật nút
            btnWallpaper.setText(R.string.fullscreen);
            
            // Xóa listener khỏi textLightView khi không còn ở chế độ full screen
            textLightView.setOnClickListener(null);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        // Khi cửa sổ có focus, đảm bảo trạng thái full screen được duy trì
        if (hasFocus && isFullscreen) {
            setFullScreenMode();
        }
    }

    @Override
    public void onBackPressed() {
        if (isRunning && getRequestedOrientation() == android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            // Nếu đang chạy text light ở chế độ landscape, dừng lại và quay về portrait mode
            textLightView.stop();
            isRunning = false;
            btnStart.setText(R.string.start);
            disableFullScreenLandscape();
            
            // Reset trạng thái full background nếu cần
            if (isFullBackground) {
                isFullBackground = false;
                btnWallpaper.setText(R.string.full_background);
            }
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
        if (textLightView != null && textLightView.isRunning()) {
            textLightView.stop();
            btnStart.setText(R.string.start);
            isRunning = false;
            
            // Đảm bảo quay về portrait mode khi ứng dụng đi vào background
            if (getRequestedOrientation() == android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                disableFullScreenLandscape();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Đảm bảo cài đặt full screen khi quay lại màn hình
        if (isFullscreen) {
            setFullScreenMode();
        }
        
        if (isRunning) {
            // Kiểm tra xem có đang ở chế độ landscape không
            if (getRequestedOrientation() == android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                // Đảm bảo UI ở chế độ fullscreen landscape
                enableFullScreenLandscape();
            }
            
            textLightView.startScrolling();
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
        String text = etInputText.getText().toString();
        if (text.isEmpty()) {
            text = getString(R.string.default_text);
        }
        
        // Đặt kích thước lớn để hiển thị tốt
        int enhancedTextSize = textSize * 3;
        
        textLightView.setupTextLight(text, enhancedTextSize, currentTextColor, scrollDirection, scrollSpeed, false);
    }

    private void showColorPickerDialog() {
        // Tạo một danh sách các màu để chọn
        final int[] colors = new int[] {
            ContextCompat.getColor(this, R.color.red),
            ContextCompat.getColor(this, R.color.green),
            ContextCompat.getColor(this, R.color.blue),
            ContextCompat.getColor(this, R.color.yellow),
            ContextCompat.getColor(this, R.color.purple),
            ContextCompat.getColor(this, R.color.white),
            ContextCompat.getColor(this, R.color.black)
        };
        
        // Tên các màu tương ứng
        final String[] colorNames = new String[] {
            "Đỏ", "Xanh lá", "Xanh dương", "Vàng", "Tím", "Trắng", "Đen"
        };
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chọn màu chữ");
        builder.setItems(colorNames, (dialog, which) -> {
            // Cập nhật màu khi người dùng chọn
            currentTextColor = colors[which];
            
            // Cập nhật màu nếu đang chạy
            if (isRunning) {
                textLightView.setTextColor(currentTextColor);
            }
            
            // Đổi màu preview nếu có
            View colorPreview = findViewById(R.id.color_preview);
            if (colorPreview != null) {
                colorPreview.setBackgroundColor(currentTextColor);
            }
        });
        
        builder.show();
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
        }
    }

    /**
     * Chuyển đổi chế độ full background (toàn màn hình và quay ngang)
     */
    private void toggleFullBackground() {
        isFullBackground = !isFullBackground;
        
        if (isFullBackground) {
            btnWallpaper.setText(R.string.exit_fullscreen);
            Toast.makeText(this, "Nhấn Start để kích hoạt chế độ toàn màn hình và quay ngang", Toast.LENGTH_SHORT).show();
            
            // Nếu đang chạy thì kích hoạt ngay
            if (isRunning) {
                enableFullScreenLandscape();
            }
        } else {
            btnWallpaper.setText(R.string.full_background);
            
            // Nếu đang ở chế độ landscape thì quay về portrait
            if (getRequestedOrientation() == android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                disableFullScreenLandscape();
            }
        }
    }

    /**
     * Kích hoạt chế độ toàn màn hình và tự động quay ngang
     */
    private void enableFullScreenLandscape() {
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
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN);
        
        // Đặt orientation thành landscape (quay ngang)
        setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        
        // Thêm listener để bắt touch event và thoát full screen
        textLightView.setOnClickListener(v -> {
            if (isRunning) {
                disableFullScreenLandscape();
                textLightView.stop();
                isRunning = false;
                btnStart.setText(R.string.start);
            }
        });
        
        Toast.makeText(this, "Nhấn vào màn hình để thoát chế độ toàn màn hình", Toast.LENGTH_SHORT).show();
    }
    
    /**
     * Tắt chế độ toàn màn hình và quay ngang
     */
    private void disableFullScreenLandscape() {
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
        
        // Hiện lại thanh trạng thái nhưng vẫn ẩn thanh điều hướng
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        
        // Đặt orientation thành portrait (quay dọc)
        setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
        // Xóa listener khỏi textLightView
        textLightView.setOnClickListener(null);
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
} 