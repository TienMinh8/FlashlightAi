package com.example.flashlightai;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flashlightai.controller.FlashController;
import com.example.flashlightai.fragment.FlashFragment;
import com.example.flashlightai.fragment.HomeFragment;
import com.example.flashlightai.fragment.SettingsFragment;
import com.example.flashlightai.screen.ScreenLightActivity;
import com.example.flashlightai.service.FlashlightService;
import com.example.flashlightai.service.NotificationMonitorService;
import com.example.flashlightai.textlight.TextLightActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.content.SharedPreferences;

public class MainActivity extends AppCompatActivity {
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private static final int READ_PHONE_STATE_PERMISSION_REQUEST_CODE = 101;
    private static final int RECEIVE_SMS_PERMISSION_REQUEST_CODE = 102;
    
    private static final String PREF_NAME = "FlashlightPrefs";
    private static final String KEY_FIRST_RUN = "firstRun";
    
    // UI Elements
    private ImageButton powerButton;
    private ImageView glowEffect;
    private SeekBar speedSlider;
    private SwitchCompat switchCalls, switchSms; 
    private TextView btnSelectApps;
    private TextView statusText;
    private TextView tvSpeedValue;
    private TextView flashModeText;
    
    // Service
    private FlashlightService flashlightService;
    private NotificationMonitorService notificationService;
    private boolean serviceBound = false;
    private boolean notificationServiceBound = false;
    
    // Current mode
    private FlashController.FlashMode currentMode = FlashController.FlashMode.NORMAL;
    private boolean isFlashOn = false;

    private boolean isTestingFlash = false;
    private Handler testHandler = new Handler();
    private Runnable testRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        // Thiết lập chế độ ẩn thanh điều hướng, giữ màn hình luôn sáng
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setFullScreenMode();
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0); // Xóa padding bottom
            return insets;
        });
        
        // Initialize UI elements
        initUI();
        
        // Check and request camera permission only
        checkCameraPermission();
        
        // Start and bind to the service
        startAndBindService();
        
        // Thiết lập bottom navigation
        setupBottomNavigation();
        
        // Mặc định hiển thị FlashFragment làm trang chủ
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new FlashFragment())
                .commit();
        }
        
        // Xử lý intent khi được mở từ các Activity khác
        handleNavigationIntent(getIntent());
        
        // Show tooltip for first-time users
        showFlashModeTooltipIfNeeded();
    }
    
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        // Xử lý intent khi Activity đã tồn tại
        handleNavigationIntent(intent);
    }
    
    /**
     * Thiết lập điều hướng cho Bottom Navigation
     */
    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        if (bottomNav != null) {
            // Đặt mục Flash được chọn mặc định
            bottomNav.setSelectedItemId(R.id.navigation_flash);
            
            // Thiết lập listener cho bottom navigation
            bottomNav.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.navigation_flash) {
                    getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new FlashFragment())
                        .commit();
                    return true;
                } else if (itemId == R.id.navigation_screen) {
                    Intent intent = new Intent(this, ScreenLightActivity.class);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    return false; // Không thay đổi item được chọn trong bottom nav
                } else if (itemId == R.id.navigation_text_light) {
                    Intent intent = new Intent(this, TextLightActivity.class);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    return false; // Không thay đổi item được chọn trong bottom nav
                }
                return false;
            });
        }
    }
    
    /**
     * Xử lý intent được gửi đến khi đã ở trong ứng dụng
     */
    private void handleNavigationIntent(Intent intent) {
        // Kiểm tra intent
        if (intent == null || !intent.hasExtra("navigate_to")) {
            return;
        }
        
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        
        if (bottomNavigationView != null) {
            String navigateTo = intent.getStringExtra("navigate_to");
            if ("flash".equals(navigateTo)) {
                bottomNavigationView.setSelectedItemId(R.id.navigation_flash);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new FlashFragment())
                        .commit();
            } else if ("settings".equals(navigateTo)) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new SettingsFragment())
                        .commit();
            }
        }
    }
    
    private void initUI() {
        // Get references to UI elements
        powerButton = findViewById(R.id.power_button);
        glowEffect = findViewById(R.id.glow_effect);
        
        // Update references to new UI elements
        btnSelectApps = findViewById(R.id.btn_select_apps);
        speedSlider = findViewById(R.id.speed_slider);
        flashModeText = findViewById(R.id.flash_mode_text);
        
        // Tạo text view tạm thời để hiển thị trạng thái
        statusText = new TextView(this);
        
        // Set up click listeners
        powerButton.setOnClickListener(v -> toggleFlash());
        
        // Set up long click listener cho power button
        powerButton.setOnLongClickListener(v -> {
            showFlashModePopup();
            return true; // Consume the long click event
        });
        
        // Settings button (previously help button)
        ImageView settingsButton = findViewById(R.id.settings_button);
        settingsButton.setOnClickListener(v -> {
            // Open settings fragment
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new SettingsFragment())
                .commit();
        });
        
        // Test Flash Button
        TextView btnTestFlash = findViewById(R.id.btn_test_flash);
        btnTestFlash.setOnClickListener(v -> {
            testFlashBlink();
        });
        
        // Set up mode selection buttons
        btnSelectApps.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AppSelectActivity.class);
            startActivity(intent);
        });
        
        // Set up switches
        switchCalls = findViewById(R.id.switch_calls);
        switchSms = findViewById(R.id.switch_sms);
        
        switchCalls.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Kiểm tra quyền khi người dùng bật tính năng
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) 
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            this,
                            new String[]{Manifest.permission.READ_PHONE_STATE},
                            READ_PHONE_STATE_PERMISSION_REQUEST_CODE);
                    buttonView.setChecked(false);
                    return;
                }
            }
            
            if (notificationServiceBound) {
                // Bật/tắt thông báo đèn flash cho cuộc gọi
                notificationService.setCallFlashEnabled(isChecked);
                
                // Hiển thị thông báo
                Toast.makeText(this, "Thông báo đèn flash cho cuộc gọi " + 
                        (isChecked ? "đã bật" : "đã tắt"), Toast.LENGTH_SHORT).show();
            }
        });
        
        switchSms.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Kiểm tra quyền khi người dùng bật tính năng
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) 
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            this,
                            new String[]{Manifest.permission.RECEIVE_SMS},
                            RECEIVE_SMS_PERMISSION_REQUEST_CODE);
                    buttonView.setChecked(false);
                    return;
                }
            }
            
            if (notificationServiceBound) {
                // Bật/tắt thông báo đèn flash cho SMS
                notificationService.setSmsFlashEnabled(isChecked);
                
                // Hiển thị thông báo
                Toast.makeText(this, "Thông báo đèn flash cho SMS " + 
                        (isChecked ? "đã bật" : "đã tắt"), Toast.LENGTH_SHORT).show();
            }
        });
        
        // Set up seekbar listener
        speedSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                try {
                    // Đảm bảo giá trị không thấp hơn 1
                    int safeProgress = Math.max(1, progress);
                    
                    // Convert progress (1-30) to frequency (65ms-500ms)
                    // 65ms là đủ nhanh và tránh lỗi với các thiết bị cũ
                    int frequency = 50 + (safeProgress * 15);
                    
                    // Cập nhật TextView hiển thị giá trị
                    if (tvSpeedValue != null) {
                        tvSpeedValue.setText(frequency + "ms");
                    }
                    
                    // Thông báo dịch vụ về thay đổi
                    if (fromUser && serviceBound) {
                        flashlightService.setBlinkFrequency(frequency);
                    }
                } catch (Exception e) {
                    // Bắt lỗi để tránh crash
                    Log.e("MainActivity", "Error updating speed value: " + e.getMessage());
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Không cần thực hiện gì
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Đảm bảo progress không thấp hơn 1
                if (seekBar.getProgress() < 1) {
                    seekBar.setProgress(1);
                }
            }
        });
        
        // Thiết lập BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new HomeFragment())
                        .commit();
                return true;
            } else if (itemId == R.id.navigation_flash) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new FlashFragment())
                        .commit();
                return true;
            } else if (itemId == R.id.navigation_settings) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new SettingsFragment())
                        .commit();
                return true;
            }
            return false;
        });
        
        // Mặc định load HomeFragment
        if (getSupportFragmentManager().findFragmentById(R.id.fragment_container) == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
            bottomNavigationView.setSelectedItemId(R.id.navigation_home);
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        
        // Đảm bảo giá trị tốc độ hiển thị sau khi ứng dụng trở lại foreground
        updateSpeedValueDisplay();
    }
    
    /**
     * Cập nhật giá trị hiển thị cho tốc độ nhấp nháy
     */
    private void updateSpeedValueDisplay() {
        try {
            if (speedSlider != null && tvSpeedValue != null) {
                int progress = speedSlider.getProgress();
                int frequency = 50 + (progress * 15);
                tvSpeedValue.setText(frequency + "ms");
                
                // Đảm bảo TextView hiển thị
                tvSpeedValue.setVisibility(View.VISIBLE);
            } else if (tvSpeedValue == null) {
                // Trường hợp chưa tìm thấy TextView, thử tìm lại
                tvSpeedValue = findViewById(R.id.tv_speed_value);
            }
        } catch (Exception e) {
            // Bắt lỗi để tránh crash app
            e.printStackTrace();
        }
    }
    
    @Override
    protected void onStart() {
        super.onStart();
        
        // Cập nhật giá trị hiển thị ban đầu cho tốc độ nhấp nháy
        updateSpeedValueDisplay();
    }
    
    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) 
                != PackageManager.PERMISSION_GRANTED) {
            // Permission not granted, request it
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_REQUEST_CODE
            );
        }
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Quyền Camera được cấp
                startAndBindService();
            } else {
                // Quyền bị từ chối
                Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_LONG).show();
                statusText.setText(R.string.permission_denied);
                
                // Disable đèn flash
                powerButton.setEnabled(false);
            }
        } else if (requestCode == READ_PHONE_STATE_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Quyền đọc trạng thái điện thoại được cấp
                Toast.makeText(this, "Đã cấp quyền theo dõi cuộc gọi", Toast.LENGTH_SHORT).show();
                
                // Cập nhật lại trạng thái switch
                switchCalls.setChecked(true);
                if (notificationServiceBound) {
                    notificationService.setCallFlashEnabled(true);
                }
            } else {
                // Quyền bị từ chối
                Toast.makeText(this, 
                        "Tính năng thông báo đèn flash cho cuộc gọi cần quyền này để hoạt động", 
                        Toast.LENGTH_LONG).show();
                switchCalls.setChecked(false);
            }
        } else if (requestCode == RECEIVE_SMS_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Quyền nhận SMS được cấp
                Toast.makeText(this, "Đã cấp quyền đọc tin nhắn SMS", Toast.LENGTH_SHORT).show();
                
                // Cập nhật lại trạng thái switch
                switchSms.setChecked(true);
                if (notificationServiceBound) {
                    notificationService.setSmsFlashEnabled(true);
                }
            } else {
                // Quyền bị từ chối
                Toast.makeText(this, 
                        "Tính năng thông báo đèn flash cho SMS cần quyền này để hoạt động", 
                        Toast.LENGTH_LONG).show();
                switchSms.setChecked(false);
            }
        }
    }
    
    private void startAndBindService() {
        // Start the service
        Intent serviceIntent = new Intent(this, FlashlightService.class);
        serviceIntent.setAction("ACTION_START_FOREGROUND_SERVICE");
        startService(serviceIntent);
        
        // Bind to the service
        Intent bindIntent = new Intent(this, FlashlightService.class);
        bindService(bindIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        
        // Khởi động và kết nối đến NotificationMonitorService
        startNotificationService();
    }
    
    // Service connection
    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            FlashlightService.LocalBinder binder = (FlashlightService.LocalBinder) service;
            flashlightService = binder.getService();
            serviceBound = true;
            
            // Sync UI with service state if needed
            updateUIForCurrentMode();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };
    
    // ServiceConnection cho NotificationMonitorService
    private final ServiceConnection notificationServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            NotificationMonitorService.LocalBinder binder = 
                    (NotificationMonitorService.LocalBinder) service;
            notificationService = binder.getService();
            notificationServiceBound = true;
            
            // Cập nhật trạng thái switch
            updateNotificationSwitches();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            notificationServiceBound = false;
        }
    };
    
    private void toggleFlash() {
        if (serviceBound) {
            // Tạo hiệu ứng nhấn nút
            animatePowerButtonClick();
            
            // Toggle flash và lấy trạng thái mới
            boolean newState = flashlightService.toggleFlash();
            
            // Hiển thị phản hồi về trạng thái mới
            String statusMsg;
            if (newState) {
                statusMsg = getString(R.string.flash_on);
                if (currentMode != FlashController.FlashMode.NORMAL) {
                    statusMsg += " - " + getFlashModeText(currentMode);
                }
            } else {
                statusMsg = getString(R.string.flash_off);
            }
            
            // Hiển thị thông báo ngắn về trạng thái mới
            Toast.makeText(this, statusMsg, Toast.LENGTH_SHORT).show();
            
            // Cập nhật trạng thái và UI
            isFlashOn = newState;
            updateFlashUI();
        }
    }
    
    /**
     * Tạo hiệu ứng nhấn cho nút power
     */
    private void animatePowerButtonClick() {
        // Animation co nhỏ nút khi nhấn
        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(powerButton, "scaleX", 1f, 0.9f);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(powerButton, "scaleY", 1f, 0.9f);
        scaleDownX.setDuration(100);
        scaleDownY.setDuration(100);
        
        // Animation phóng to lại khi thả
        ObjectAnimator scaleUpX = ObjectAnimator.ofFloat(powerButton, "scaleX", 0.9f, 1f);
        ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(powerButton, "scaleY", 0.9f, 1f);
        scaleUpX.setDuration(100);
        scaleUpY.setDuration(100);
        
        // Chạy animation co nhỏ trước, sau đó phóng to
        scaleDownX.start();
        scaleDownY.start();
        
        // Listener để bắt đầu animation phóng to sau khi co nhỏ hoàn tất
        scaleDownX.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                scaleUpX.start();
                scaleUpY.start();
            }
        });
    }
    
    private void setFlashMode(FlashController.FlashMode mode) {
        if (serviceBound) {
            // Ghi nhớ chế độ mới
            currentMode = mode;
            
            // Thiết lập chế độ mới nhưng không tự bật đèn
            flashlightService.setFlashMode(mode);
            
            // Lấy trạng thái đèn hiện tại từ service sau khi đặt chế độ
            isFlashOn = flashlightService.isFlashOn();
            
            // Hiển thị thông báo chế độ mới đã được thiết lập
            Toast.makeText(this, getString(R.string.mode_set_to) + " " + getFlashModeText(mode), 
                          Toast.LENGTH_SHORT).show();
            
            // Cập nhật UI
            updateUIForCurrentMode();
            updateFlashUI();
        }
    }
    
    /**
     * Hiển thị popup menu để chọn chế độ đèn flash
     */
    private void showFlashModePopup() {
        // Inflate the popup layout
        View popupView = getLayoutInflater().inflate(R.layout.flash_mode_popup, null);
        
        // Create popup window
        PopupWindow popupWindow = new PopupWindow(
                popupView,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                true // focusable
        );
        
        // Set elevation for popup (for Android 5.0+)
        popupWindow.setElevation(10);
        
        // Set click listeners for mode buttons
        popupView.findViewById(R.id.btn_mode_normal).setOnClickListener(v -> {
            setFlashMode(FlashController.FlashMode.NORMAL);
            popupWindow.dismiss();
        });
        
        popupView.findViewById(R.id.btn_mode_blink).setOnClickListener(v -> {
            setFlashMode(FlashController.FlashMode.BLINK);
            popupWindow.dismiss();
        });
        
        popupView.findViewById(R.id.btn_mode_sos).setOnClickListener(v -> {
            setFlashMode(FlashController.FlashMode.SOS);
            popupWindow.dismiss();
        });
        
        popupView.findViewById(R.id.btn_mode_strobe).setOnClickListener(v -> {
            setFlashMode(FlashController.FlashMode.STROBE);
            popupWindow.dismiss();
        });
        
        popupView.findViewById(R.id.btn_mode_disco).setOnClickListener(v -> {
            setFlashMode(FlashController.FlashMode.DISCO);
            popupWindow.dismiss();
        });
        
        // Show at center of power button
        popupWindow.showAtLocation(powerButton, Gravity.CENTER, 0, 0);
        
        // Show a brief toast with instructions for future use
        Toast.makeText(this, R.string.flash_mode_hold_tip, Toast.LENGTH_SHORT).show();
    }
    
    /**
     * Update UI for current mode
     */
    private void updateUIForCurrentMode() {
        // Select the appropriate text based on current mode
        String modeText;
        switch (currentMode) {
            case NORMAL:
                modeText = getString(R.string.normal);
                break;
            case BLINK:
                modeText = getString(R.string.blink);
                break;
            case SOS:
                modeText = getString(R.string.sos);
                break;
            case STROBE:
                modeText = getString(R.string.strobe);
                break;
            case DISCO:
                modeText = getString(R.string.disco);
                break;
            default:
                modeText = getString(R.string.normal);
                break;
        }
        
        // Update the flash mode text view
        if (flashModeText != null) {
            flashModeText.setText(modeText);
        }
        
        // Show the current mode in a toast
        Toast.makeText(this, modeText, Toast.LENGTH_SHORT).show();
    }
    
    private void updateFlashUI() {
        if (isFlashOn) {
            // Update UI for flash on
            powerButton.setBackgroundResource(R.drawable.power_button_bg_active);
            statusText.setText(R.string.flash_on);
            
            // Show and animate glow effect
            glowEffect.setVisibility(View.VISIBLE);
            animateGlowEffect();
            
            // Animation hiệu ứng khi bật đèn
            ObjectAnimator rotateAnimator = ObjectAnimator.ofFloat(powerButton, "rotation", 0f, 10f, -10f, 0f);
            rotateAnimator.setDuration(400);
            rotateAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            rotateAnimator.start();
            
            // Show the flash mode text
            if (flashModeText != null) {
                flashModeText.setText(getFlashModeText(currentMode));
                flashModeText.setVisibility(View.VISIBLE);
            }
        } else {
            // Update UI for flash off
            powerButton.setBackgroundResource(R.drawable.power_button_bg);
            statusText.setText(R.string.flash_off);
            
            // Hide glow effect with fade out animation
            ObjectAnimator fadeOut = ObjectAnimator.ofFloat(glowEffect, "alpha", glowEffect.getAlpha(), 0f);
            fadeOut.setDuration(300);
            fadeOut.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    glowEffect.setVisibility(View.INVISIBLE);
                }
            });
            fadeOut.start();
            
            // Hide the flash mode text
            if (flashModeText != null) {
                flashModeText.setVisibility(View.INVISIBLE);
            }
        }
    }
    
    private void animateGlowEffect() {
        // Reset alpha
        glowEffect.setAlpha(0.7f);
        
        // Create pulse animation
        ObjectAnimator pulseAnimator = ObjectAnimator.ofFloat(glowEffect, "alpha", 0.7f, 0.3f);
        pulseAnimator.setDuration(1000);
        pulseAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        pulseAnimator.setRepeatMode(ObjectAnimator.REVERSE);
        pulseAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        
        // Add listener to stop animation when flash is turned off
        pulseAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                glowEffect.setVisibility(View.INVISIBLE);
            }
        });
        
        pulseAnimator.start();
    }
    
    /**
     * Khởi động và kết nối đến NotificationMonitorService
     */
    private void startNotificationService() {
        // Start dịch vụ
        Intent serviceIntent = new Intent(this, NotificationMonitorService.class);
        startService(serviceIntent);
        
        // Kết nối đến dịch vụ
        Intent bindIntent = new Intent(this, NotificationMonitorService.class);
        bindService(bindIntent, notificationServiceConnection, Context.BIND_AUTO_CREATE);
    }
    
    /**
     * Cập nhật trạng thái của các switch thông báo
     */
    private void updateNotificationSwitches() {
        if (notificationServiceBound) {
            // TODO: Lấy trạng thái từ SharedPreferences và cập nhật các switch
        }
    }
    
    /**
     * Test nhấp nháy đèn flash với tần số đã thiết lập
     */
    private void testFlashBlink() {
        if (serviceBound) {
            // Nếu đang test, hủy bỏ test
            if (isTestingFlash) {
                // Hủy bỏ callback
                if (testRunnable != null) {
                    testHandler.removeCallbacks(testRunnable);
                }
                
                // Khôi phục trạng thái trước đó
                flashlightService.setFlashMode(currentMode);
                
                // Tắt đèn nếu ban đầu đèn không sáng
                if (!isFlashOn) {
                    flashlightService.turnOffFlash();
                }
                
                Toast.makeText(this, "Đã dừng test đèn nhấp nháy", Toast.LENGTH_SHORT).show();
                isTestingFlash = false;
                return;
            }
            
            // Bắt đầu test mới
            isTestingFlash = true;
            
            // Lấy tần số hiện tại từ slider
            int progress = speedSlider.getProgress();
            int frequency = 50 + (progress * 15);
            
            // Thông báo tới người dùng
            Toast.makeText(this, 
                    "Đang test với tốc độ: " + frequency + "ms", 
                    Toast.LENGTH_SHORT).show();
            
            // Lưu trạng thái hiện tại của đèn
            final boolean wasFlashOn = flashlightService.isFlashOn();
            // Lưu chế độ hiện tại
            final FlashController.FlashMode previousMode = flashlightService.getCurrentMode();
            
            // Đặt chế độ nhấp nháy và tần số
            flashlightService.setBlinkFrequency(frequency);
            flashlightService.setFlashMode(FlashController.FlashMode.BLINK);
            
            // Bắt đầu nhấp nháy
            if (!flashlightService.isFlashOn()) {
                flashlightService.toggleFlash();
            }
            
            // Tạo hiệu ứng nhấp nháy trong 3 giây
            testRunnable = () -> {
                // Dừng nhấp nháy và khôi phục trạng thái trước đó
                flashlightService.setFlashMode(previousMode);
                
                if (!wasFlashOn) {
                    flashlightService.turnOffFlash();
                }
                
                Toast.makeText(MainActivity.this, 
                        "Hoàn thành test với tốc độ: " + frequency + "ms", 
                        Toast.LENGTH_SHORT).show();
                        
                isTestingFlash = false;
            };
            
            testHandler.postDelayed(testRunnable, 3000); // Test trong 3 giây
        } else {
            Toast.makeText(this, "Service chưa khởi tạo, vui lòng thử lại", Toast.LENGTH_SHORT).show();
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serviceBound) {
            unbindService(serviceConnection);
            serviceBound = false;
        }
        
        if (notificationServiceBound) {
            unbindService(notificationServiceConnection);
            notificationServiceBound = false;
        }
    }
    
    /**
     * Thiết lập chế độ full screen giống với các activity khác
     */
    private void setFullScreenMode() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
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
     * Hiển thị tooltip cho người dùng mới về tính năng đèn flash nhiều chế độ
     */
    private void showFlashModeTooltipIfNeeded() {
        // Check if this is the first run
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        boolean isFirstRun = prefs.getBoolean(KEY_FIRST_RUN, true);
        
        if (isFirstRun) {
            // Show tooltip
            new Handler().postDelayed(() -> {
                Toast.makeText(this, R.string.flash_mode_tooltip, Toast.LENGTH_LONG).show();
                
                // Mark as not first run
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean(KEY_FIRST_RUN, false);
                editor.apply();
            }, 1500); // Delay slightly to let the UI settle
        }
    }
    
    /**
     * Lấy văn bản hiển thị cho từng chế độ đèn flash
     */
    private String getFlashModeText(FlashController.FlashMode mode) {
        switch (mode) {
            case NORMAL:
                return getString(R.string.normal);
            case BLINK:
                return getString(R.string.blink);
            case SOS:
                return getString(R.string.sos);
            case STROBE:
                return getString(R.string.strobe);
            case DISCO:
                return getString(R.string.disco);
            default:
                return getString(R.string.normal);
        }
    }
}