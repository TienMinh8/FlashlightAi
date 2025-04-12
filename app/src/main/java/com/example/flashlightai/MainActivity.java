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
import android.net.Uri;
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
import androidx.appcompat.app.AlertDialog;
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

import com.example.flashlightai.base.BaseActivity;
import com.example.flashlightai.controller.FlashController;
import com.example.flashlightai.fragment.FlashFragment;
import com.example.flashlightai.fragment.SettingsFragment;
import com.example.flashlightai.screen.ScreenLightActivity;
import com.example.flashlightai.service.FlashlightService;
import com.example.flashlightai.service.NotificationMonitorService;
import com.example.flashlightai.textlight.TextLightActivity;
import com.example.flashlightai.utils.PreferenceManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends BaseActivity {
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
    
    // Preference Manager
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Khởi tạo PreferenceManager
        preferenceManager = new PreferenceManager(this);
        
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
        
        // Chỉ yêu cầu quyền camera khi khởi động
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
            // Mở SettingsActivity thay vì thay thế fragment
            Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(settingsIntent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
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
                    
                    // Log thông tin để debug
                    Log.d("MainActivity", "Đang yêu cầu quyền READ_PHONE_STATE");
                    
                    // Hiển thị lý do yêu cầu quyền nếu cần
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE)) {
                        Toast.makeText(this, getString(R.string.phone_permission_required),
                            Toast.LENGTH_SHORT).show();
                    }
                    
                    // Yêu cầu quyền
                    ActivityCompat.requestPermissions(
                            this,
                            new String[]{Manifest.permission.READ_PHONE_STATE},
                            READ_PHONE_STATE_PERMISSION_REQUEST_CODE);
                    
                    // Trả switch về trạng thái chưa bật
                    buttonView.setChecked(false);
                    return;
                } else {
                    Log.d("MainActivity", "Đã có quyền READ_PHONE_STATE");
                }
            }
            
            // Lưu trạng thái cho lần khởi động sau
            preferenceManager.setBoolean("call_flash_enabled", isChecked);
            
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
                    
                    // Log thông tin để debug
                    Log.d("MainActivity", "Đang yêu cầu quyền RECEIVE_SMS");
                    
                    // Hiển thị lý do yêu cầu quyền nếu cần
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECEIVE_SMS)) {
                        Toast.makeText(this, getString(R.string.sms_permission_message),
                            Toast.LENGTH_LONG).show();
                    }
                    
                    // Yêu cầu quyền
                    ActivityCompat.requestPermissions(
                            this,
                            new String[]{Manifest.permission.RECEIVE_SMS},
                            RECEIVE_SMS_PERMISSION_REQUEST_CODE);
                    
                    // Trả switch về trạng thái chưa bật
                    buttonView.setChecked(false);
                    return;
                } else {
                    Log.d("MainActivity", "Đã có quyền RECEIVE_SMS");
                }
            }
            
            // Lưu trạng thái cho lần khởi động sau
            preferenceManager.setBoolean("sms_flash_enabled", isChecked);
            
            if (notificationServiceBound) {
                // Bật/tắt thông báo đèn flash cho SMS
                notificationService.setSmsFlashEnabled(isChecked);
                
                // Hiển thị thông báo
                Toast.makeText(this, 
                        isChecked ? getString(R.string.sms_flash_notification_enabled) : getString(R.string.sms_flash_notification_disabled), 
                        Toast.LENGTH_SHORT).show();
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
        
        // Thêm nút yêu cầu quyền
        Button btnRequestPermissions = findViewById(R.id.btn_request_permissions);
        if (btnRequestPermissions != null) {
            btnRequestPermissions.setOnClickListener(v -> {
                // Mở trực tiếp cài đặt ứng dụng để người dùng cấp quyền thủ công
                openAppSettings();
            });

        }
        
        // Thiết lập BottomNavigationView
        setupBottomNavigation();
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
    
    /**
     * Chỉ kiểm tra và yêu cầu quyền camera
     */
    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) 
                != PackageManager.PERMISSION_GRANTED) {
            // Hiển thị lý do yêu cầu quyền
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                Toast.makeText(this, getString(R.string.camera_permission_message), Toast.LENGTH_LONG).show();
            }
            
            // Yêu cầu quyền camera
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_REQUEST_CODE);
            
            // Log thông tin cho debugging
            Log.d("MainActivity", "Đang yêu cầu quyền Camera: CAMERA_PERMISSION_REQUEST_CODE = " + CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            // Đã có quyền camera, bắt đầu dịch vụ
            Log.d("MainActivity", "Đã có quyền Camera, bắt đầu dịch vụ");
            startAndBindService();
        }
    }
    
    /**
     * Yêu cầu quyền READ_PHONE_STATE và RECEIVE_SMS riêng lẻ
     */
    private void requestPhoneAndSmsPermissions() {
        // Yêu cầu quyền READ_PHONE_STATE
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) 
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.READ_PHONE_STATE},
                    READ_PHONE_STATE_PERMISSION_REQUEST_CODE);
            
            Toast.makeText(this, getString(R.string.phone_permission_required),
                Toast.LENGTH_LONG).show();
        }
        
        // Yêu cầu quyền RECEIVE_SMS
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) 
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.RECEIVE_SMS},
                    RECEIVE_SMS_PERMISSION_REQUEST_CODE);
            
            Toast.makeText(this, getString(R.string.sms_permission_message),
                Toast.LENGTH_LONG).show();
        }
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        Log.d("MainActivity", "onRequestPermissionsResult called with code: " + requestCode);
        
        if (grantResults.length == 0) {
            Log.d("MainActivity", "Permission request was cancelled");
            return;
        }
        
        switch (requestCode) {
            case CAMERA_PERMISSION_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("MainActivity", "Camera permission granted");
                    Toast.makeText(this, "Quyền camera đã được cấp", Toast.LENGTH_SHORT).show();
                    
                    // Khởi động camera service
                    startAndBindService();
                } else {
                    Log.d("MainActivity", "Camera permission denied");
                    Toast.makeText(this, "Bạn cần cấp quyền để sử dụng đèn flash", Toast.LENGTH_LONG).show();
                    
                    // Hiển thị dialog để giải thích và dẫn người dùng đến cài đặt ứng dụng
                    new AlertDialog.Builder(this)
                            .setTitle("Yêu cầu quyền")
                            .setMessage("Ứng dụng cần quyền truy cập camera để sử dụng đèn flash. Vui lòng cấp quyền trong cài đặt ứng dụng.")
                            .setPositiveButton("Đi đến Cài đặt", (dialog, which) -> openAppSettings())
                            .setNegativeButton("Để sau", null)
                            .create()
                            .show();
                }
                break;
                
            case READ_PHONE_STATE_PERMISSION_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("MainActivity", "Phone state permission granted");
                    Toast.makeText(this, "Quyền theo dõi cuộc gọi đã được cấp", Toast.LENGTH_SHORT).show();
                    
                    // Bật switch và lưu trạng thái
                    switchCalls.setChecked(true);
                    preferenceManager.setBoolean("call_flash_enabled", true);
                    
                    // Báo cho service về thay đổi
                    if (notificationServiceBound) {
                        notificationService.setCallFlashEnabled(true);
                    }
                } else {
                    Log.d("MainActivity", "Phone state permission denied");
                    Toast.makeText(this, "Bạn cần cấp quyền để nhận thông báo cuộc gọi", Toast.LENGTH_LONG).show();
                    
                    // Đảm bảo switch ở trạng thái tắt
                    switchCalls.setChecked(false);
                    preferenceManager.setBoolean("call_flash_enabled", false);
                }
                break;
                
            case RECEIVE_SMS_PERMISSION_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("MainActivity", "SMS permission granted");
                    Toast.makeText(this, "Quyền đọc SMS đã được cấp", Toast.LENGTH_SHORT).show();
                    
                    // Bật switch và lưu trạng thái
                    switchSms.setChecked(true);
                    preferenceManager.setBoolean("sms_flash_enabled", true);
                    
                    // Báo cho service về thay đổi
                    if (notificationServiceBound) {
                        notificationService.setSmsFlashEnabled(true);
                    }
                } else {
                    Log.d("MainActivity", "SMS permission denied");
                    Toast.makeText(this, "Bạn cần cấp quyền để nhận thông báo SMS", Toast.LENGTH_LONG).show();
                    
                    // Đảm bảo switch ở trạng thái tắt
                    switchSms.setChecked(false);
                    preferenceManager.setBoolean("sms_flash_enabled", false);
                }
                break;
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
            // Cập nhật trạng thái của các switch dựa trên trạng thái trong service
            switchCalls.setChecked(preferenceManager.getBoolean("call_flash_enabled", false));
            switchSms.setChecked(preferenceManager.getBoolean("sms_flash_enabled", false));
            
            // Đồng bộ trạng thái với service
            notificationService.setCallFlashEnabled(switchCalls.isChecked());
            notificationService.setSmsFlashEnabled(switchSms.isChecked());
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
     * Set the app to fullscreen mode, hiding navigation bars
     */
    private void setFullScreenMode() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
    }

    /**
     * Show tooltip for flash mode when app is first run
     */
    private void showFlashModeTooltipIfNeeded() {
        // Check if this is the first run
        boolean isFirstRun = preferenceManager.getBoolean(KEY_FIRST_RUN, true);
        if (isFirstRun) {
            Toast.makeText(this, R.string.flash_mode_hold_tip, Toast.LENGTH_LONG).show();
            preferenceManager.setBoolean(KEY_FIRST_RUN, false);
        }
    }

    /**
     * Get text representation of flash mode
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

    /**
     * Mở cài đặt ứng dụng để người dùng cấp quyền thủ công
     */
    private void openAppSettings() {
        Intent intent = new Intent();
        intent.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
        
        Toast.makeText(this, "Hãy cấp quyền truy cập điện thoại và SMS trong cài đặt ứng dụng", 
                Toast.LENGTH_LONG).show();
    }
}