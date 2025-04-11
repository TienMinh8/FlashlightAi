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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.flashlightai.R;
import com.example.flashlightai.controller.FlashController;
import com.example.flashlightai.service.FlashlightService;
import com.example.flashlightai.utils.MorseCodeUtil;

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

    // Morse code components
    private LinearLayout morseContainer;
    private EditText morseInput;
    private TextView morseOutput;
    private Button sendMorseButton;
    private Button sosButton;
    private boolean isMorseVisible = false;

    // Service
    private FlashlightService flashlightService;
    private boolean serviceBound = false;

    // State
    private boolean isFlashOn = false;
    private int currentMode = MODE_NORMAL;
    private MorseCodeUtil morseCodeUtil;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_flash, container, false);
        initViews(rootView);
        startAndBindService();
        return rootView;
    }

    private void initViews(View view) {
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
        
        // Thiết lập listeners
        setupListeners();
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
                Toast.makeText(context, "Chế độ thường", Toast.LENGTH_SHORT).show();
            }
        });

        modeSos.setOnClickListener(v -> {
            setFlashMode(MODE_SOS);
            updateModeSelection(modeSos);
            Context context = getContext();
            if (context != null) {
                Toast.makeText(context, "Chế độ SOS", Toast.LENGTH_SHORT).show();
            }
        });

        modeDisco.setOnClickListener(v -> {
            setFlashMode(MODE_DISCO);
            updateModeSelection(modeDisco);
            Context context = getContext();
            if (context != null) {
                Toast.makeText(context, "Chế độ Disco", Toast.LENGTH_SHORT).show();
            }
        });

        // Morse code buttons
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
            Context context = getContext();
            if (!morse.isEmpty()) {
                if (serviceBound && flashlightService != null) {
                    // Gọi phương thức gửi mã Morse nếu service hỗ trợ
                    if (morseCodeUtil != null) {
                        morseCodeUtil.playMorseCode(morseInput.getText().toString(), 1.0f);
                        if (context != null) {
                            Toast.makeText(context, "Đang gửi mã Morse", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            } else {
                if (context != null) {
                    Toast.makeText(context, "Vui lòng nhập văn bản", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(context, "Đang gửi tín hiệu SOS", Toast.LENGTH_SHORT).show();
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
                            // Tính toán min và max delay cho chế độ disco dựa trên giá trị speed
                            int[] discoDelays = calculateDiscoDelays(progress);
                            flashlightService.setDiscoFrequency(discoDelays[0], discoDelays[1]);
                            
                            // Cập nhật hiển thị cho người dùng
                            tvSpeedValue.setText(discoDelays[0] + "-" + discoDelays[1] + "ms");
                            
                            // Cập nhật trạng thái nếu đèn đang bật
                            if (isFlashOn) {
                                updateStatusText();
                            }
                        } else {
                            // Chế độ thường hoặc SOS sử dụng tốc độ đơn
                            int speed = calculateSpeedValue(progress);
                            flashlightService.setBlinkFrequency(speed);
                            
                            // Cập nhật hiển thị cho người dùng
                            tvSpeedValue.setText(speed + "ms");
                            
                            // Cập nhật trạng thái nếu đèn đang bật
                            if (isFlashOn) {
                                updateStatusText();
                            }
                        }
                    } catch (Exception e) {
                        // Xử lý lỗi nếu cần
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }
    
    private void toggleMorseContainer() {
        // Hiển thị container (không cần thiết nữa vì phần Morse luôn hiển thị trong ScrollView)
    }
    
    private void updateSpeedValueDisplay() {
        updateSpeedDisplayForCurrentMode();
    }
    
    private void toggleFlashlight() {
        if (flashlightService != null) {
            try {
                isFlashOn = flashlightService.toggleFlash();
            } catch (Exception e) {
                // Phương thức không tồn tại hoặc có lỗi
                isFlashOn = !isFlashOn;
            }
            updateFlashUI();
        } else {
            Context context = getContext();
            if (context != null) {
                Toast.makeText(context, "Đèn pin không khả dụng", Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    private void setFlashMode(int mode) {
        if (serviceBound && flashlightService != null) {
            // Cập nhật mode
            currentMode = mode;
            try {
                // Chuyển đổi int mode sang FlashMode
                FlashController.FlashMode flashMode;
                if (mode == MODE_NORMAL) {
                    flashMode = FlashController.FlashMode.NORMAL;
                } else if (mode == MODE_SOS) {
                    flashMode = FlashController.FlashMode.SOS;
                } else if (mode == MODE_DISCO) {
                    flashMode = FlashController.FlashMode.DISCO;
                } else {
                    // Default mode
                    flashMode = FlashController.FlashMode.NORMAL;
                }

                // Gọi phương thức đúng trong service
                flashlightService.setFlashMode(flashMode);
                
                // Highlight card được chọn 
                updateModeSelection();
                
                // Cập nhật trạng thái
                updateStatusText();
                
                // Cập nhật hiển thị tốc độ theo chế độ mới
                updateSpeedDisplayForCurrentMode();
            } catch (Exception e) {
                // Phương thức không tồn tại hoặc có lỗi
            }
        } else {
            Context context = getContext();
            if (context != null) {
                Toast.makeText(context, "Dịch vụ đèn pin chưa sẵn sàng", Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    private void updateModeSelection(CardView selectedCard) {
        if (!isAdded()) {
            return; // Không làm gì nếu fragment không còn gắn với activity
        }
        
        // Reset tất cả về màu mặc định
        modeNormal.setCardBackgroundColor(getResources().getColor(R.color.mode_card_background));
        modeSos.setCardBackgroundColor(getResources().getColor(R.color.mode_card_background));
        modeDisco.setCardBackgroundColor(getResources().getColor(R.color.mode_card_background));
        
        // Highlight card được chọn
        selectedCard.setCardBackgroundColor(getResources().getColor(R.color.mode_card_selected));
    }
    
    private void updateModeSelection() {
        if (!isAdded()) {
            return; // Không làm gì nếu fragment không còn gắn với activity
        }
        
        // Reset tất cả về màu mặc định
        modeNormal.setCardBackgroundColor(getResources().getColor(R.color.mode_card_background));
        modeSos.setCardBackgroundColor(getResources().getColor(R.color.mode_card_background));
        modeDisco.setCardBackgroundColor(getResources().getColor(R.color.mode_card_background));
        
        // Highlight card được chọn
        switch (currentMode) {
            case MODE_NORMAL:
                modeNormal.setCardBackgroundColor(getResources().getColor(R.color.mode_card_selected));
                break;
            case MODE_SOS:
                modeSos.setCardBackgroundColor(getResources().getColor(R.color.mode_card_selected));
                break;
            case MODE_DISCO:
                modeDisco.setCardBackgroundColor(getResources().getColor(R.color.mode_card_selected));
                break;
        }
    }
    
    private void updateFlashUI() {
        if (!isAdded()) {
            return; // Không làm gì nếu fragment không còn gắn với activity
        }
        
        if (isFlashOn) {
            // Hiệu ứng khi đèn bật
            glowEffect.setAlpha(1.0f);
            powerButton.setImageResource(R.drawable.power_on_icon);
        } else {
            // Hiệu ứng khi đèn tắt
            glowEffect.setAlpha(0.0f);
            powerButton.setImageResource(R.drawable.power_icon);
        }
        
        // Cập nhật trạng thái
        updateStatusText();
    }
    
    private void updateStatusText() {
        String modeText = "";
        
        switch (currentMode) {
            case MODE_NORMAL:
                modeText = "Chế độ bình thường";
                break;
            case MODE_SOS:
                modeText = "Chế độ SOS (... --- ...)";
                break;
            case MODE_DISCO:
                int[] discoDelays = calculateDiscoDelays(speedSlider.getProgress());
                modeText = "Chế độ Disco (" + discoDelays[0] + "-" + discoDelays[1] + "ms)";
                break;
            default:
                modeText = "Chế độ bình thường";
        }
        
        String statusText = isFlashOn ? "Đèn bật - " + modeText : "Đèn tắt";
        flashStatus.setText(statusText);
    }
    
    private void startAndBindService() {
        if (!isAdded()) {
            return; // Không làm gì nếu fragment không còn gắn với activity
        }
        
        Context context = getContext();
        if (context == null) {
            return; // Không làm gì nếu không có context
        }
        
        // Khởi động service
        Intent intent = new Intent(context, FlashlightService.class);
        context.startService(intent);
        
        // Bind service
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }
    
    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            FlashlightService.LocalBinder binder = (FlashlightService.LocalBinder) service;
            flashlightService = binder.getService();
            serviceBound = true;
            
            // Đồng bộ UI với trạng thái hiện tại của service
            try {
                isFlashOn = flashlightService.isFlashOn();
            } catch (Exception e) {
                isFlashOn = false;
            }
            
            // Lấy mode hiện tại từ service nếu có
            try {
                FlashController.FlashMode serviceMode = flashlightService.getCurrentMode();
                if (serviceMode == FlashController.FlashMode.NORMAL) {
                    currentMode = MODE_NORMAL;
                } else if (serviceMode == FlashController.FlashMode.SOS) {
                    currentMode = MODE_SOS;
                } else if (serviceMode == FlashController.FlashMode.DISCO) {
                    currentMode = MODE_DISCO;
                }
            } catch (Exception e) {
                // Mode không thay đổi
            }
            
            updateFlashUI();
            updateModeSelection();
            
            // Khởi tạo MorseCodeUtil
            if (flashlightService != null) {
                morseCodeUtil = new MorseCodeUtil(flashlightService);
            }
        }
        
        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };
    
    @Override
    public void onResume() {
        super.onResume();
        if (!isAdded()) {
            return; // Không làm gì nếu fragment không còn gắn với activity
        }
        
        if (serviceBound && flashlightService != null) {
            // Cập nhật UI theo trạng thái hiện tại của đèn
            try {
                isFlashOn = flashlightService.isFlashOn();
                
                // Lấy mode hiện tại từ service nếu có
                FlashController.FlashMode serviceMode = flashlightService.getCurrentMode();
                if (serviceMode == FlashController.FlashMode.NORMAL) {
                    currentMode = MODE_NORMAL;
                } else if (serviceMode == FlashController.FlashMode.SOS) {
                    currentMode = MODE_SOS;
                } else if (serviceMode == FlashController.FlashMode.DISCO) {
                    currentMode = MODE_DISCO;
                }
            } catch (Exception e) {
                // Không có phương thức hoặc xảy ra lỗi
            }
            
            updateFlashUI();
            updateModeSelection();
            updateSpeedDisplayForCurrentMode();
        }
        
        if (!serviceBound) {
            startAndBindService();
        }
    }
    
    @Override
    public void onPause() {
        super.onPause();
        
        // Dừng đèn flash nếu đang chạy chế độ đặc biệt
        if (serviceBound && flashlightService != null) {
            // Có thể gọi phương thức stopSpecialMode nếu service có
            if (morseCodeUtil != null && morseCodeUtil.isPlaying()) {
                morseCodeUtil.stopMorseCode();
            }
        }
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Unbind service khi fragment bị destroy
        if (serviceBound && isAdded()) {  // Kiểm tra fragment có được gắn vào context không
            try {
                Context context = getContext();
                if (context != null) {
                    context.unbindService(serviceConnection);
                }
            } catch (IllegalStateException e) {
                // Fragment đã không còn attach vào context
            }
            serviceBound = false;
        }
    }

    /**
     * Tính toán min/max delay cho chế độ disco dựa trên giá trị progress của slider
     * @param progress giá trị progress từ thanh trượt (0-30)
     * @return mảng int[2] với [0]=minDelay, [1]=maxDelay
     */
    private int[] calculateDiscoDelays(int progress) {
        // Đảo ngược progress (30-progress) vì tốc độ cao = delay thấp
        int invertedProgress = 30 - progress;
        
        // Tính các giá trị min và max delay dựa trên progress
        // Min delay: 50ms (nhanh) đến 200ms (chậm)
        int minDelay = 50 + (invertedProgress * 5);
        
        // Max delay: 150ms (nhanh) đến 600ms (chậm)
        int maxDelay = 150 + (invertedProgress * 15);
        
        return new int[] {minDelay, maxDelay};
    }

    /**
     * Tính toán giá trị tốc độ dựa trên progress
     * @param progress giá trị từ thanh trượt (0-30)
     * @return giá trị tốc độ nhấp nháy tính bằng ms
     */
    private int calculateSpeedValue(int progress) {
        // Chuyển đổi giá trị từ thanh seekbar thành milliseconds
        // Giá trị thấp nhất là 50ms, cao nhất là 500ms
        return 50 + (progress * 15);
    }

    private void updateSpeedDisplayForCurrentMode() {
        if (speedSlider != null && tvSpeedValue != null) {
            int progress = speedSlider.getProgress();
            
            if (currentMode == MODE_DISCO) {
                // Hiển thị định dạng tốc độ cho chế độ disco (hiển thị range)
                int[] discoDelays = calculateDiscoDelays(progress);
                tvSpeedValue.setText(discoDelays[0] + "-" + discoDelays[1] + "ms");
            } else {
                // Hiển thị định dạng tốc độ cho các chế độ khác
                int speed = calculateSpeedValue(progress);
                tvSpeedValue.setText(speed + "ms");
            }
        }
    }
} 