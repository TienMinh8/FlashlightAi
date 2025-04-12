package com.example.flashlightai.screen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.example.flashlightai.R;
import com.example.flashlightai.MainActivity;
import com.example.flashlightai.textlight.TextLightActivity;

import java.util.Arrays;
import java.util.List;

/**
 * Activity để hiển thị màn hình phát sáng
 */
public class ScreenLightActivity extends AppCompatActivity {
    private static final String TAG = "ScreenLightActivity";

    // Views
    private ScreenLightView screenLightView;
    private ScreenLightController screenLightController;
    private View controlPanel;
    private ImageButton btnClose;
    private ImageButton btnColor;
    private ImageButton btnEffect;
    private SeekBar brightnessSeekBar;
    private TextView colorPreview;
    private com.example.flashlightai.customviews.ColorSliderView colorSlider;

    // Trạng thái
    private boolean controlsVisible = true;
    private boolean isBatteryLow = false;
    private int currentColor = Color.WHITE;
    private LightEffectsManager.EffectType currentEffect = LightEffectsManager.EffectType.SOLID;
    
    // BroadcastReceiver để theo dõi tình trạng pin
    private BroadcastReceiver batteryReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Giữ màn hình luôn sáng
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        // Sử dụng full screen
        setFullScreenMode();
        
        setContentView(R.layout.activity_screen_light);
        
        // Khởi tạo controller
        screenLightController = new ScreenLightController(this);
        screenLightController.setScreenLightListener(new ScreenLightListener());
        
        // Khởi tạo views
        initViews();
        setupListeners();
        setupBottomNavigation();
        
        // Đăng ký BroadcastReceiver để theo dõi pin
        registerBatteryReceiver();
        
        // Bắt đầu màn hình phát sáng
        screenLightController.startScreenLight();
        
        Log.d(TAG, "ScreenLightActivity created");
    }
    
    /**
     * Thiết lập chế độ full screen
     */
    private void setFullScreenMode() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }
    
    /**
     * Khởi tạo các views
     */
    private void initViews() {
        screenLightView = findViewById(R.id.screen_light_view);
        controlPanel = findViewById(R.id.control_panel);
        btnClose = findViewById(R.id.btn_close);
        btnColor = findViewById(R.id.btn_color);
        btnEffect = findViewById(R.id.btn_effect);
        brightnessSeekBar = findViewById(R.id.brightness_seekbar);
        colorPreview = findViewById(R.id.color_preview);
        colorSlider = findViewById(R.id.color_slider);
        
        // Cài đặt giá trị ban đầu cho brightness seekbar
        brightnessSeekBar.setProgress(100); // 100%
        
        // Cài đặt màu ban đầu cho color slider
        updateColorPreview(currentColor);
    }
    
    /**
     * Thiết lập các sự kiện
     */
    private void setupListeners() {
        // Nút đóng - kết thúc activity
        btnClose.setOnClickListener(v -> finish());
        
        // Nút chọn màu (giờ đã có color slider nên nút này thay đổi vai trò)
        btnColor.setOnClickListener(v -> {
            // Hiện/ẩn phần ColorSlider
            View colorSelection = findViewById(R.id.color_selection);
            if (colorSelection != null) {
                boolean isVisible = colorSelection.getVisibility() == View.VISIBLE;
                colorSelection.setVisibility(isVisible ? View.GONE : View.VISIBLE);
                
                // Cập nhật icon của nút
                btnColor.setImageResource(isVisible ? 
                    android.R.drawable.ic_menu_edit : 
                    android.R.drawable.ic_menu_close_clear_cancel);
            }
        });
        
        // Sự kiện cho ColorSlider - thay thế cho ColorPickerDialog
        colorSlider.setOnColorChangingListener(color -> {
            // Cập nhật màu khi người dùng đang kéo
            currentColor = color;
            updateColorPreview(color);
            
            // Cập nhật màu cho ScreenLightView
            if (screenLightView != null) {
                float brightness = brightnessSeekBar.getProgress() / 100f;
                int displayColor = adjustColorBrightness(color, brightness);
                screenLightView.setColor(displayColor);
            }
        });
        
        colorSlider.setOnColorSelectedListener(color -> {
            // Cập nhật màu khi người dùng chọn xong
            currentColor = color;
            updateColorPreview(color);
            
            // Cập nhật màu cho ScreenLightView
            if (screenLightView != null) {
                float brightness = brightnessSeekBar.getProgress() / 100f;
                int displayColor = adjustColorBrightness(color, brightness);
                screenLightView.setColor(displayColor);
                
                // Thông báo cho controller
                if (screenLightController != null) {
                    screenLightController.setScreenColor(displayColor);
                }
            }
        });
        
        // Nút chọn hiệu ứng
        btnEffect.setOnClickListener(v -> showEffectPicker());
        
        // Thanh điều chỉnh độ sáng
        brightnessSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    try {
                        // Giới hạn progress trong khoảng 1-100 để tránh lỗi
                        int safeBrightnessValue = Math.max(1, Math.min(progress, 100));
                        
                        // Cập nhật brightness
                        if (screenLightController != null) {
                            screenLightController.setBrightness(safeBrightnessValue);
                        }
                        
                        // Trực tiếp điều chỉnh độ sáng của màn hình
                        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
                        layoutParams.screenBrightness = safeBrightnessValue / 100f;
                        getWindow().setAttributes(layoutParams);
                        
                        // Cập nhật màu sắc của ScreenLightView dựa trên độ sáng 
                        // nhưng chỉ khi đang ở chế độ SOLID
                        if (screenLightView != null) {
                            float brightness = safeBrightnessValue / 100f;
                            
                            // Kiểm tra xem có đang chạy hiệu ứng nào không
                            LightEffectsManager.EffectType currentEffectType = screenLightView.getCurrentEffectType();
                            
                            if (currentEffectType == LightEffectsManager.EffectType.SOLID) {
                                // Nếu đang ở chế độ đơn sắc, cập nhật màu trực tiếp
                                int color = adjustColorBrightness(currentColor, brightness);
                                screenLightView.setColor(color);
                            } else if (currentEffectType != null) {
                                // Nếu đang ở chế độ hiệu ứng khác, cập nhật brightness cho hiệu ứng
                                LightEffectsManager.EffectConfig config = new LightEffectsManager.EffectConfig();
                                config.put("brightness", brightness);
                                config.put("color", adjustColorBrightness(currentColor, brightness));
                                screenLightView.updateEffectConfig(config);
                            }
                        }
                    } catch (Exception e) {
                        // Ghi log lỗi nhưng không làm crash app
                        Log.e(TAG, "Error setting brightness: " + e.getMessage());
                        Toast.makeText(ScreenLightActivity.this, 
                                "Không thể thay đổi độ sáng", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Do nothing
            }
            
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Đảm bảo thanh trượt không về giá trị 0
                if (seekBar.getProgress() < 1) {
                    seekBar.setProgress(1);
                }
            }
        });
        
        // Tap để ẩn/hiện controls
        screenLightView.setOnClickListener(v -> toggleControlsVisibility());
    }
    
    /**
     * Điều chỉnh độ sáng của màu sắc
     */
    private int adjustColorBrightness(int color, float brightness) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        
        // Điều chỉnh độ sáng (V trong HSV)
        hsv[2] = Math.max(0.1f, brightness); // Đảm bảo không quá tối
        
        return Color.HSVToColor(hsv);
    }
    
    /**
     * Đăng ký BroadcastReceiver để theo dõi pin
     */
    private void registerBatteryReceiver() {
        batteryReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                
                float batteryPct = level * 100 / (float) scale;
                
                // Nếu pin dưới 15%, đánh dấu pin yếu
                boolean wasLow = isBatteryLow;
                isBatteryLow = batteryPct < 15;
                
                // Chỉ hiển thị cảnh báo nếu trạng thái thay đổi
                if (isBatteryLow && !wasLow) {
                    showLowBatteryWarning();
                }
            }
        };
        
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryReceiver, filter);
    }
    
    /**
     * Ẩn/hiện panel điều khiển
     */
    private void toggleControlsVisibility() {
        controlsVisible = !controlsVisible;
        controlPanel.setVisibility(controlsVisible ? View.VISIBLE : View.GONE);
        
        // Nếu panel bị ẩn, bật lại full screen
        if (!controlsVisible) {
            setFullScreenMode();
        }
    }
    
    /**
     * Hiển thị color picker
     */
    private void showColorPicker() {
        // Ẩn/hiện phần chọn màu
        View colorSelection = findViewById(R.id.color_selection);
        if (colorSelection != null) {
            boolean isVisible = colorSelection.getVisibility() == View.VISIBLE;
            colorSelection.setVisibility(isVisible ? View.GONE : View.VISIBLE);
            
            // Cập nhật icon của nút
            if (btnColor != null) {
                btnColor.setImageResource(isVisible ? 
                    android.R.drawable.ic_menu_edit : 
                    android.R.drawable.ic_menu_close_clear_cancel);
            }
        }
    }
    
    /**
     * Cập nhật hiển thị màu
     */
    private void updateColorPreview(int color) {
        // Cập nhật màu nền cho color preview
        if (colorPreview != null) {
            colorPreview.setBackgroundColor(color);
            
            // Đảm bảo chữ "A" luôn hiển thị với màu tương phản
            boolean isDark = isColorDark(color);
            int textColor = isDark ? Color.WHITE : Color.BLACK;
            colorPreview.setTextColor(textColor);
        }
        
        // Cập nhật màu cho color slider
        if (colorSlider != null) {
            colorSlider.setSelectedColor(color);
        }
    }
    
    /**
     * Kiểm tra xem màu có tối không
     */
    private boolean isColorDark(int color) {
        double darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
        return darkness >= 0.5;
    }
    
    /**
     * Hiển thị dialog chọn hiệu ứng
     */
    private void showEffectPicker() {
        // Tạo danh sách các hiệu ứng
        List<LightEffectsManager.EffectType> effects = Arrays.asList(
                LightEffectsManager.EffectType.SOLID,
                LightEffectsManager.EffectType.PULSE,
                LightEffectsManager.EffectType.STROBE,
                LightEffectsManager.EffectType.WAVE,
                LightEffectsManager.EffectType.RAINBOW,
                LightEffectsManager.EffectType.DISCO
        );
        
        // Tạo danh sách tên hiệu ứng
        String[] effectNames = new String[effects.size()];
        for (int i = 0; i < effects.size(); i++) {
            effectNames[i] = effects.get(i).name().charAt(0) + effects.get(i).name().substring(1).toLowerCase();
        }
        
        // Tạo dialog chọn hiệu ứng
        new AlertDialog.Builder(this)
                .setTitle("Chọn hiệu ứng")
                .setItems(effectNames, (dialog, which) -> {
                    LightEffectsManager.EffectType selectedEffect = effects.get(which);
                    currentEffect = selectedEffect;
                    
                    // Cập nhật hiển thị nút hiệu ứng
                    btnEffect.setContentDescription(effectNames[which]);
                    
                    // Tạo config
                    LightEffectsManager.EffectConfig config = new LightEffectsManager.EffectConfig();
                    
                    // Đặt màu hiện tại vào config
                    config.put("color", currentColor);
                    
                    // Bắt đầu hiệu ứng
                    screenLightView.startLightEffect(selectedEffect, config);
                    
                    // Cập nhật trạng thái hiệu ứng
                    screenLightController.setLightEffect(selectedEffect);
                })
                .show();
    }
    
    /**
     * Hiển thị cảnh báo pin yếu
     */
    private void showLowBatteryWarning() {
        Toast.makeText(this, "Pin yếu! Độ sáng màn hình sẽ được giảm để tiết kiệm pin", Toast.LENGTH_LONG).show();
        
        // Tự động giảm độ sáng
        float reducedBrightness = Math.min(screenLightController.getCurrentBrightness(), 0.5f);
        screenLightController.setBrightness(reducedBrightness);
        
        // Cập nhật thanh trượt
        brightnessSeekBar.setProgress((int) (reducedBrightness * 100));
    }
    
    /**
     * Hiển thị cảnh báo nhiệt độ cao
     */
    private void showOverheatWarning() {
        Toast.makeText(this, "Nhiệt độ thiết bị cao! Độ sáng màn hình sẽ được giảm", Toast.LENGTH_LONG).show();
        
        // Tự động giảm độ sáng
        float reducedBrightness = Math.min(screenLightController.getCurrentBrightness(), 0.3f);
        screenLightController.setBrightness(reducedBrightness);
        
        // Cập nhật thanh trượt
        brightnessSeekBar.setProgress((int) (reducedBrightness * 100));
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        
        // Bật lại màn hình phát sáng
        if (screenLightController != null) {
            screenLightController.startScreenLight();
        }
    }
    
    @Override
    protected void onPause() {
        super.onPause();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        
        // Dừng màn hình phát sáng
        screenLightController.stopScreenLight();
        
        // Hủy đăng ký BroadcastReceiver
        if (batteryReceiver != null) {
            unregisterReceiver(batteryReceiver);
            batteryReceiver = null;
        }
        
        Log.d(TAG, "ScreenLightActivity destroyed");
    }
    
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        // Khi cửa sổ có focus, đảm bảo trạng thái full screen được duy trì
        if (hasFocus) {
            setFullScreenMode();
        }
    }
    
    /**
     * Listener cho ScreenLightController
     */
    private class ScreenLightListener implements ScreenLightController.ScreenLightListener {
        @Override
        public void onScreenLightStarted() {
            Log.d(TAG, "Screen light started");
        }
        
        @Override
        public void onScreenLightStopped() {
            Log.d(TAG, "Screen light stopped");
        }
        
        @Override
        public void onBrightnessChanged(int brightness) {
            // Cập nhật UI nếu cần
            brightnessSeekBar.setProgress(brightness);
        }
        
        @Override
        public void onColorChanged(int color) {
            // Cập nhật màu hiện tại
            currentColor = color;
        }
        
        @Override
        public void onOverheating() {
            runOnUiThread(() -> showOverheatWarning());
        }
        
        @Override
        public void onLowBattery() {
            runOnUiThread(() -> showLowBatteryWarning());
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Thêm animation tương tự như TextLightActivity
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public void finish() {
        super.finish();
        // Thêm animation khi thoát màn hình
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    /**
     * Thiết lập BottomNavigationView
     */
    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        if (bottomNav != null) {
            // Đặt mục hiện tại là Screen Light
            bottomNav.setSelectedItemId(R.id.navigation_screen);
            
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
                    // Đã ở trang Screen Light
                    return true;
                } else if (itemId == R.id.navigation_text_light) {
                    // Chuyển đến trang Text Light
                    Intent intent = new Intent(this, TextLightActivity.class);
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