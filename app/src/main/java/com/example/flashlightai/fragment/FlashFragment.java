package com.example.flashlightai.fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.flashlightai.R;
import com.example.flashlightai.controller.FlashController;
import com.example.flashlightai.service.FlashlightService;
import com.example.flashlightai.utils.MorseCodeUtil;
import com.example.flashlightai.utils.AdManager;

public class FlashFragment extends Fragment {

    // Định nghĩa các mode
    public static final int MODE_NORMAL = 0;
    public static final int MODE_SOS = 1;
    public static final int MODE_DISCO = 2;

    // UI elements
    private View rootView;
    private ImageButton powerButton;
    private ImageView glowEffect;
    private SeekBar speedSlider;
    private TextView tvSpeedValue;
    private TextView flashStatus;
    private CardView modeNormal, modeSos, modeDisco;
    private FrameLayout flashAdContainer;

    // Morse code components
    private LinearLayout morseContainer;
    private EditText morseInput;
    private TextView morseOutput;
    private Button sendMorseButton;
    private Button sosButton;

    // Service
    private FlashlightService flashlightService;
    private boolean serviceBound = false;

    // State
    private boolean isFlashOn = false;
    private int currentMode = MODE_NORMAL;
    private MorseCodeUtil morseCodeUtil;

    // Ad Manager
    private AdManager adManager;

    /**
     * Thiết lập chế độ full screen, ẩn thanh điều hướng
     */
    private void setFullScreenMode() {
        if (getActivity() != null) {
            getActivity().getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_flash, container, false);
        
        // Ẩn thanh điều hướng của thiết bị
        setFullScreenMode();
        
        initViews(rootView);
        startAndBindService();
        return rootView;
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Khởi động với trạng thái mặc định
        updateSpeedDisplayForCurrentMode();
        updateModeSelection();
    }

    private void initViews(View view) {
        try {
            // Khởi tạo các thành phần UI
            powerButton = view.findViewById(R.id.power_button);
            glowEffect = view.findViewById(R.id.glow_effect);
            speedSlider = view.findViewById(R.id.speed_slider);
            tvSpeedValue = view.findViewById(R.id.tv_speed_value);
            flashStatus = view.findViewById(R.id.flash_status);
            
            // Card chế độ
            modeNormal = view.findViewById(R.id.mode_normal);
            modeSos = view.findViewById(R.id.mode_sos);
            modeDisco = view.findViewById(R.id.mode_disco);
            
            // Morse code views
            morseContainer = view.findViewById(R.id.morse_container);
            morseInput = view.findViewById(R.id.et_morse_input);
            morseOutput = view.findViewById(R.id.tv_morse_output);
            sendMorseButton = view.findViewById(R.id.btn_send_morse);
            sosButton = view.findViewById(R.id.btn_sos);
            
            // Ad container
            flashAdContainer = view.findViewById(R.id.flash_ad_container);
            
            // Khởi tạo Ad Manager
            adManager = AdManager.getInstance(requireContext());
            
            // Hiển thị quảng cáo banner
            loadBannerAd();
            
            // Thiết lập listeners
            setupListeners();
            
            // Khởi tạo trạng thái
            updateModeSelection(modeNormal);
        } catch (Exception e) {
            Context context = getContext();
            if (context != null) {
                Toast.makeText(context, "Lỗi khởi tạo giao diện: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    private void setupListeners() {
        // Power button
        powerButton.setOnClickListener(v -> {
            toggleFlashlight();
        });

        // Mode buttons
        modeNormal.setOnClickListener(v -> {
            setFlashMode(MODE_NORMAL);
            updateModeSelection(modeNormal);

            Context context = getContext();
            if (context != null) {
                Toast.makeText(context, R.string.normal_mode, Toast.LENGTH_SHORT).show();
            }
        });

        modeSos.setOnClickListener(v -> {
            setFlashMode(MODE_SOS);
            updateModeSelection(modeSos);

            Context context = getContext();
            if (context != null) {
                Toast.makeText(context, R.string.sos_mode, Toast.LENGTH_SHORT).show();
            }
        });

        modeDisco.setOnClickListener(v -> {
            setFlashMode(MODE_DISCO);
            updateModeSelection(modeDisco);

            Context context = getContext();
            if (context != null) {
                Toast.makeText(context, R.string.disco_mode, Toast.LENGTH_SHORT).show();
            }
        });

        // Morse text change listener
        morseInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (morseCodeUtil != null) {
                    String morse = morseCodeUtil.convertTextToMorse(s.toString());
                    morseOutput.setText(morse);
                }
            }
        });

        // Send Morse button
        sendMorseButton.setOnClickListener(v -> {
            String morse = morseOutput.getText().toString();
            if (!morse.isEmpty()) {

                if (serviceBound && flashlightService != null) {
                    // Gọi phương thức gửi mã Morse nếu service hỗ trợ
                    if (morseCodeUtil != null) {
                        morseCodeUtil.playMorseCode(morseInput.getText().toString(), 1.0f);
                        Context context = getContext();
                        if (context != null) {
                            Toast.makeText(context, R.string.sending_morse_code, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            } else {
                Context context = getContext();
                if (context != null) {
                    Toast.makeText(context, R.string.please_enter_text, Toast.LENGTH_SHORT).show();
                }

            }
        });

        // SOS button
        sosButton.setOnClickListener(v -> {

            if (serviceBound && flashlightService != null) {
                // Gọi phương thức gửi SOS nếu service hỗ trợ
                if (morseCodeUtil != null) {
                    morseCodeUtil.playSOS(1.0f);
                    Context context = getContext();
                    if (context != null) {
                        Toast.makeText(context, R.string.sending_sos_signal, Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        // Speed slider
        speedSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (serviceBound && flashlightService != null && fromUser) {
                    try {
                        if (currentMode == MODE_DISCO) {
                            // Tính toán min và max delay cho chế độ disco
                            int[] discoDelays = calculateDiscoDelays(progress);
                            flashlightService.setDiscoFrequency(discoDelays[0], discoDelays[1]);
                            
                            // Cập nhật hiển thị
                            tvSpeedValue.setText(discoDelays[0] + "-" + discoDelays[1] + "ms");
                            
                            // Cập nhật trạng thái
                            if (isFlashOn) {
                                updateStatusText();
                            }
                        } else {
                            // Chế độ thường hoặc SOS
                            int speed = calculateSpeedValue(progress);
                            flashlightService.setBlinkFrequency(speed);
                            
                            // Cập nhật hiển thị
                            tvSpeedValue.setText(speed + "ms");
                            
                            // Cập nhật trạng thái
                            if (isFlashOn) {
                                updateStatusText();
                            }
                        }
                    } catch (Exception e) {
                        showToast("Lỗi cập nhật tốc độ: " + e.getMessage());
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }
    
    private void showToast(String message) {
        Context context = getContext();
        if (context != null) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }
    
    private void updateSpeedValueDisplay() {
        updateSpeedDisplayForCurrentMode();
    }
    
    private void toggleFlashlight() {
        if (flashlightService != null) {
            try {
                isFlashOn = flashlightService.toggleFlash();
                updateFlashUI();
            } catch (Exception e) {
                showToast("Lỗi bật/tắt đèn pin: " + e.getMessage());
            }
        } else {
            showToast("Dịch vụ đèn pin chưa sẵn sàng, vui lòng thử lại");
            startAndBindService();
        }
    }
    
    private void setFlashMode(int mode) {
        if (serviceBound && flashlightService != null) {
            try {
                currentMode = mode;
                
                // Chuyển đổi từ mode của fragment sang mode của FlashController
                FlashController.FlashMode flashMode;
                switch (mode) {
                    case MODE_SOS:
                        flashMode = FlashController.FlashMode.SOS;
                        break;
                    case MODE_DISCO:
                        flashMode = FlashController.FlashMode.DISCO;
                        break;
                    case MODE_NORMAL:
                    default:
                        flashMode = FlashController.FlashMode.NORMAL;
                        break;
                }
                
                // Cập nhật chế độ trong service
                flashlightService.setFlashMode(flashMode);
                
                // Cập nhật hiển thị tốc độ và trạng thái
                updateSpeedDisplayForCurrentMode();
                
                // Cập nhật trạng thái nếu đèn đang bật
                if (isFlashOn) {
                    updateStatusText();
                }
            } catch (Exception e) {
                showToast("Lỗi đổi chế độ: " + e.getMessage());
            }
        } else {
            showToast("Dịch vụ đèn pin chưa sẵn sàng, vui lòng thử lại");
            startAndBindService();
        }
    }
    
    private void updateModeSelection(CardView selectedCard) {
        // Đặt tất cả card về trạng thái không được chọn
        if (modeNormal != null) modeNormal.setCardBackgroundColor(getResources().getColor(R.color.mode_card_normal));
        if (modeSos != null) modeSos.setCardBackgroundColor(getResources().getColor(R.color.mode_card_normal));
        if (modeDisco != null) modeDisco.setCardBackgroundColor(getResources().getColor(R.color.mode_card_normal));
        
        // Đặt card được chọn
        if (selectedCard != null) {
            selectedCard.setCardBackgroundColor(getResources().getColor(R.color.mode_card_selected));
        }
    }
    
    private void updateModeSelection() {
        // Cập nhật hiển thị card được chọn dựa trên chế độ hiện tại
        switch (currentMode) {
            case MODE_SOS:
                updateModeSelection(modeSos);
                break;
            case MODE_DISCO:
                updateModeSelection(modeDisco);
                break;
            case MODE_NORMAL:
            default:
                updateModeSelection(modeNormal);
                break;
        }
    }
    
    private void updateFlashUI() {
        if (isFlashOn) {
            // Đèn đang bật
            if (glowEffect != null) {
                glowEffect.setVisibility(View.VISIBLE);
            }
            
            if (powerButton != null) {
                powerButton.setImageResource(R.drawable.power_on_icon);
            }
            
            // Hiển thị trạng thái
            updateStatusText();
        } else {
            // Đèn đang tắt
            if (glowEffect != null) {
                glowEffect.setVisibility(View.INVISIBLE);
            }
            
            if (powerButton != null) {
                powerButton.setImageResource(R.drawable.power_off_icon);
            }
            
            // Xóa trạng thái
            if (flashStatus != null) {
                flashStatus.setText(R.string.flash_off);
            }
        }
    }
    
    private void updateStatusText() {
        if (flashStatus != null) {
            String statusText;
            switch (currentMode) {
                case MODE_SOS:
                    statusText = getString(R.string.mode_sos_active);
                    break;
                case MODE_DISCO:
                    int[] delays = calculateDiscoDelays(speedSlider.getProgress());
                    statusText = getString(R.string.mode_disco_active, delays[0], delays[1]);
                    break;
                case MODE_NORMAL:
                default:
                    int speed = calculateSpeedValue(speedSlider.getProgress());
                    statusText = getString(R.string.mode_normal_active, speed);
                    break;
            }
            flashStatus.setText(statusText);
        }
    }
    
    private void startAndBindService() {
        try {
            // Khởi tạo và kết nối đến service
            Context context = getContext();
            if (context != null) {
                Intent serviceIntent = new Intent(context, FlashlightService.class);
                context.startService(serviceIntent);
                context.bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
            }
        } catch (Exception e) {
            showToast("Lỗi kết nối đến dịch vụ đèn pin: " + e.getMessage());
        }
    }
    
    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            try {
                FlashlightService.LocalBinder binder = (FlashlightService.LocalBinder) service;
                flashlightService = binder.getService();
                serviceBound = true;
                
                // Đồng bộ trạng thái với service
                if (flashlightService != null) {
                    // Khởi tạo Morse util
                    morseCodeUtil = new MorseCodeUtil(flashlightService);
                    
                    // Lấy trạng thái hiện tại từ service
                    isFlashOn = flashlightService.isFlashOn();
                    
                    // Lấy chế độ hiện tại từ service
                    FlashController.FlashMode serviceMode = flashlightService.getCurrentMode();
                    if (serviceMode != null) {
                        // Chuyển đổi từ FlashController.FlashMode sang mode của fragment
                        switch (serviceMode) {
                            case SOS:
                                currentMode = MODE_SOS;
                                break;
                            case DISCO:
                                currentMode = MODE_DISCO;
                                break;
                            case NORMAL:
                            default:
                                currentMode = MODE_NORMAL;
                                break;
                        }
                    }
                    
                    // Cập nhật UI để phản ánh trạng thái hiện tại
                    updateModeSelection();
                    updateFlashUI();
                    updateSpeedValueDisplay();
                }
            } catch (Exception e) {
                showToast("Lỗi kết nối đến dịch vụ: " + e.getMessage());
            }
        }
        
        @Override
        public void onServiceDisconnected(ComponentName name) {
            flashlightService = null;
            serviceBound = false;
        }
    };
    
    @Override
    public void onResume() {
        super.onResume();
        
        // Set fullscreen mode
        setFullScreenMode();
        
        // Bind to service if not already bound
        if (!serviceBound) {
            startAndBindService();
        }
        
        // Reload ad if needed
        if (adManager != null && flashAdContainer != null) {
            loadBannerAd();
        }
        
        // Update UI with current state
        updateFlashUI();
    }
    
    @Override
    public void onPause() {
        super.onPause();
        
        // Cleanup logic if needed
        if (isFlashOn && serviceBound && flashlightService != null) {
            // Tắt đèn pin khi thoát khỏi app
            flashlightService.turnOffFlash();
            isFlashOn = false;
        }
        
        // Stop any ongoing morse code playback
        if (morseCodeUtil != null) {
            morseCodeUtil.stopMorseCode();
        }
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        
        // Unbind service
        if (serviceBound && getActivity() != null) {
            getActivity().unbindService(serviceConnection);
            serviceBound = false;
        }
        
        // Clear the ad container
        if (flashAdContainer != null) {
            flashAdContainer.removeAllViews();
        }
        
        // Reset UI references
        powerButton = null;
        glowEffect = null;
        speedSlider = null;
        tvSpeedValue = null;
        flashStatus = null;
        modeNormal = null;
        modeSos = null;
        modeDisco = null;
        morseContainer = null;
        morseInput = null;
        morseOutput = null;
        sendMorseButton = null;
        sosButton = null;
        flashAdContainer = null;
        
        // Disconnect service
        flashlightService = null;
        // Stop MorseCodeUtil if needed
        if (morseCodeUtil != null) {
            morseCodeUtil.stopMorseCode();
            morseCodeUtil = null;
        }
    }
    
    private int[] calculateDiscoDelays(int progress) {
        // Giá trị mặc định
        int minDelay = 100;
        int maxDelay = 500;
        
        // Tùy chỉnh dựa trên progress
        if (progress <= 10) {
            // Chậm
            minDelay = 300;
            maxDelay = 700;
        } else if (progress <= 20) {
            // Trung bình
            minDelay = 150;
            maxDelay = 450;
        } else {
            // Nhanh
            minDelay = 50;
            maxDelay = 250;
        }
        
        return new int[]{minDelay, maxDelay};
    }
    
    private int calculateSpeedValue(int progress) {
        // Từ 0-30 thành 500-50ms (sử dụng ánh xạ tuyến tính)
        return 500 - ((progress * 450) / 30);
    }
    
    private void updateSpeedDisplayForCurrentMode() {
        if (speedSlider != null && tvSpeedValue != null) {
            int progress = speedSlider.getProgress();
            if (currentMode == MODE_DISCO) {
                int[] delays = calculateDiscoDelays(progress);
                tvSpeedValue.setText(delays[0] + "-" + delays[1] + "ms");
            } else {
                int speed = calculateSpeedValue(progress);
                tvSpeedValue.setText(speed + "ms");
            }
        }
    }

    /**
     * Tải và hiển thị quảng cáo banner trong Flash Fragment
     * Sử dụng kích thước Medium Rectangle (320x250)
     */
    private void loadBannerAd() {
        if (flashAdContainer != null && adManager != null) {
            adManager.showLargeBannerAd(flashAdContainer);
        }
    }
} 