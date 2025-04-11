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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Switch;

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
    private RadioGroup rgDirection;
    private SeekBar speedSeekBar;
    private SeekBar sizeSeekBar;
    private SeekBar blinkSeekBar;
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
    private boolean isBlinking = false;
    private int scrollSpeed = 50;
    private int textSize = 50;
    private TextLightView.ScrollDirection scrollDirection = TextLightView.ScrollDirection.LEFT_TO_RIGHT;

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
        rgDirection = findViewById(R.id.scroll_direction_group);
        
        // Thay thế slider bằng seekbar từ layout
        speedSeekBar = findViewById(R.id.scroll_speed_seekbar);
        sizeSeekBar = findViewById(R.id.text_size_seekbar);
        blinkSeekBar = findViewById(R.id.blink_speed_seekbar);
        
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
        sizeSeekBar.setProgress(50);
        updateSpeedText(50);
        updateSizeText(50);
        
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
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
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
        
        // Direction radio group
        rgDirection.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.direction_left_to_right) {
                scrollDirection = TextLightView.ScrollDirection.LEFT_TO_RIGHT;
            } else if (checkedId == R.id.direction_right_to_left) {
                scrollDirection = TextLightView.ScrollDirection.RIGHT_TO_LEFT;
            } else if (checkedId == R.id.direction_top_to_bottom) {
                scrollDirection = TextLightView.ScrollDirection.TOP_TO_BOTTOM;
            } else if (checkedId == R.id.direction_bottom_to_top) {
                scrollDirection = TextLightView.ScrollDirection.BOTTOM_TO_TOP;
            }
            
            if (isRunning) {
                updateTextLight();
            }
        });
        
        // Blink effect
        blinkSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                isBlinking = progress > 0;
                
                if (isRunning) {
                    if (isBlinking) {
                        textLightView.setBlinkFrequency(1000 - progress * 9);
                        textLightView.startBlinking();
                    } else {
                        textLightView.stopBlinking();
                    }
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
            // TODO: Thêm chức năng đặt làm hình nền sau
            Toast.makeText(this, "Tính năng đang phát triển", Toast.LENGTH_SHORT).show();
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
            textLightView.setupTextLight(text, textSize, currentTextColor, scrollDirection, scrollSpeed, isBlinking);
            isRunning = true;
            btnStart.setText(R.string.stop);
        } else {
            textLightView.stop();
            isRunning = false;
            btnStart.setText(R.string.start);
        }
        
        // Lưu lại cài đặt hiện tại
        saveCurrentSettings(text, scrollDirection, scrollSpeed, textSize, isBlinking, currentTextColor);
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

    private void saveCurrentSettings(String text, TextLightView.ScrollDirection direction, 
                                    float speed, int size, boolean blink, int color) {
        preferenceManager.setString("text_light_text", text);
        preferenceManager.setInt("text_light_direction", direction.ordinal());
        preferenceManager.setFloat("text_light_speed", speed);
        preferenceManager.setInt("text_light_size", size);
        preferenceManager.setBoolean("text_light_blink", blink);
        preferenceManager.setInt("text_light_color", color);
    }

    private void restoreLastSettings() {
        String text = preferenceManager.getString("text_light_text", "");
        int directionOrdinal = preferenceManager.getInt("text_light_direction", 0);
        float speed = preferenceManager.getFloat("text_light_speed", 5);
        int size = preferenceManager.getInt("text_light_size", 50);
        boolean blink = preferenceManager.getBoolean("text_light_blink", false);
        int color = preferenceManager.getInt("text_light_color", 0xFFFFFFFF);
        
        // Áp dụng cài đặt
        if (!text.isEmpty()) {
            etInputText.setText(text);
        }
        
        // Thiết lập direction
        int radioButtonId;
        switch (directionOrdinal) {
            case 0: // LEFT_TO_RIGHT
                radioButtonId = R.id.direction_left_to_right;
                scrollDirection = TextLightView.ScrollDirection.LEFT_TO_RIGHT;
                break;
            case 1: // RIGHT_TO_LEFT
                radioButtonId = R.id.direction_right_to_left;
                scrollDirection = TextLightView.ScrollDirection.RIGHT_TO_LEFT;
                break;
            case 2: // TOP_TO_BOTTOM
                radioButtonId = R.id.direction_top_to_bottom;
                scrollDirection = TextLightView.ScrollDirection.TOP_TO_BOTTOM;
                break;
            case 3: // BOTTOM_TO_TOP
                radioButtonId = R.id.direction_bottom_to_top;
                scrollDirection = TextLightView.ScrollDirection.BOTTOM_TO_TOP;
                break;
            default:
                radioButtonId = R.id.direction_left_to_right;
                scrollDirection = TextLightView.ScrollDirection.LEFT_TO_RIGHT;
        }
        
        rgDirection.check(radioButtonId);
        
        speedSeekBar.setProgress((int)speed);
        sizeSeekBar.setProgress(size);
        blinkSeekBar.setProgress(blink ? 50 : 0); // Nếu có blink, đặt giá trị ở giữa
        isBlinking = blink;
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
        if (isFullscreen) {
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
            textLightView.startScrolling();
            if (isBlinking) {
                textLightView.startBlinking();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isRunning) {
            textLightView.stopScrolling();
            textLightView.stopBlinking();
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
        
        textLightView.setupTextLight(text, textSize, currentTextColor, scrollDirection, scrollSpeed, isBlinking);
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

    private void updateBlinkEffect() {
        boolean shouldBlink = blinkSeekBar.getProgress() > 0;
        if (shouldBlink) {
            textLightView.setBlinkFrequency(1000 - blinkSeekBar.getProgress() * 9);
            textLightView.startBlinking();
        } else {
            textLightView.stopBlinking();
        }
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
                if (itemId == R.id.navigation_home) {
                    // Về trang chủ
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    finish();
                    return true;
                } else if (itemId == R.id.navigation_flash) {
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
                } else if (itemId == R.id.navigation_settings) {
                    // Về trang cài đặt
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra("navigate_to", "settings");
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    finish();
                    return true;
                }
                return false;
            });
        }
    }
} 