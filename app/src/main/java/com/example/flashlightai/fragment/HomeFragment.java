package com.example.flashlightai.fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.flashlightai.R;
import com.example.flashlightai.controller.FlashController;
import com.example.flashlightai.service.FlashlightService;

public class HomeFragment extends Fragment {
    
    private ImageButton powerButton;
    private ImageView glowEffect;
    private SeekBar speedSlider;
    private TextView tvSpeedValue;
    private TextView btnTestFlash;
    
    // Service
    private FlashlightService flashlightService;
    private boolean serviceBound = false;
    private boolean isFlashOn = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Sử dụng layout riêng cho fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Khởi tạo view
        initViews(view);
        
        // Bind đến service
        bindFlashlightService();
    }
    
    private void initViews(View view) {
        powerButton = view.findViewById(R.id.power_button);
        glowEffect = view.findViewById(R.id.glow_effect);
        speedSlider = view.findViewById(R.id.speed_slider);
        tvSpeedValue = view.findViewById(R.id.tv_speed_value);
        btnTestFlash = view.findViewById(R.id.btn_test_flash);
        
        // Set up click listeners
        powerButton.setOnClickListener(v -> toggleFlash());
        
        btnTestFlash.setOnClickListener(v -> {
            if (serviceBound && flashlightService != null) {
                Toast.makeText(getContext(), "Đang kiểm tra đèn flash...", Toast.LENGTH_SHORT).show();
                testFlashLight();
            }
        });
        
        // Set up seekbar listener
        speedSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int safeProgress = Math.max(1, progress);
                int frequency = 50 + (safeProgress * 15);
                
                if (tvSpeedValue != null) {
                    tvSpeedValue.setText(frequency + "ms");
                }
                
                if (fromUser && serviceBound && flashlightService != null) {
                    flashlightService.setBlinkFrequency(frequency);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Không cần thực hiện gì
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (seekBar.getProgress() < 1) {
                    seekBar.setProgress(1);
                }
            }
        });
    }
    
    private void bindFlashlightService() {
        Intent serviceIntent = new Intent(getContext(), FlashlightService.class);
        getContext().bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }
    
    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            FlashlightService.LocalBinder binder = (FlashlightService.LocalBinder) service;
            flashlightService = binder.getService();
            serviceBound = true;
            
            // Đồng bộ hóa UI với trạng thái hiện tại
            isFlashOn = flashlightService.isFlashOn();
            updateFlashUI();
            
            // Cập nhật giá trị tốc độ nhấp nháy
            updateSpeedValueDisplay();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };
    
    private void toggleFlash() {
        if (serviceBound && flashlightService != null) {
            isFlashOn = flashlightService.toggleFlash();
            updateFlashUI();
        } else {
            Toast.makeText(getContext(), "Đèn pin không khả dụng", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void updateFlashUI() {
        if (isFlashOn) {
            // Update UI for flash on
            powerButton.setImageResource(R.drawable.power_on_icon);
            powerButton.setBackgroundResource(R.drawable.power_button_bg_active);
            glowEffect.setVisibility(View.VISIBLE);
        } else {
            // Update UI for flash off
            powerButton.setImageResource(R.drawable.power_button_icon);
            powerButton.setBackgroundResource(R.drawable.power_button_bg);
            glowEffect.setVisibility(View.INVISIBLE);
        }
    }
    
    private void updateSpeedValueDisplay() {
        if (speedSlider != null && tvSpeedValue != null) {
            int progress = speedSlider.getProgress();
            int frequency = 50 + (progress * 15);
            tvSpeedValue.setText(frequency + "ms");
        }
    }
    
    private void testFlashLight() {
        if (serviceBound && flashlightService != null) {
            // Lưu trạng thái và chế độ hiện tại
            final boolean wasFlashOn = flashlightService.isFlashOn();
            final FlashController.FlashMode previousMode = flashlightService.getCurrentMode();
            
            // Đặt chế độ nhấp nháy và tần số từ seekbar
            int progress = speedSlider.getProgress();
            int frequency = 50 + (progress * 15);
            flashlightService.setBlinkFrequency(frequency);
            flashlightService.setFlashMode(FlashController.FlashMode.BLINK);
            
            // Bật đèn nếu chưa bật
            if (!flashlightService.isFlashOn()) {
                flashlightService.turnOnFlash();
                isFlashOn = true;
                updateFlashUI();
            }
            
            // Sau 3 giây, khôi phục trạng thái trước đó
            new android.os.Handler().postDelayed(() -> {
                flashlightService.setFlashMode(previousMode);
                
                if (!wasFlashOn) {
                    flashlightService.turnOffFlash();
                    isFlashOn = false;
                    updateFlashUI();
                }
                
                Toast.makeText(getContext(), 
                        "Hoàn thành kiểm tra với tốc độ: " + frequency + "ms", 
                        Toast.LENGTH_SHORT).show();
            }, 3000);
        }
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (serviceBound) {
            getContext().unbindService(serviceConnection);
            serviceBound = false;
        }
    }
} 