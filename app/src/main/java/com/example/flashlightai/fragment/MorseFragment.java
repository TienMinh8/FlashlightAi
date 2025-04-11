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
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.flashlightai.R;
import com.example.flashlightai.service.FlashlightService;
import com.example.flashlightai.utils.MorseCodeUtil;

/**
 * Fragment để chuyển đổi văn bản thành mã Morse và phát bằng đèn flash
 */
public class MorseFragment extends Fragment {

    private EditText etInput;
    private TextView tvMorseOutput;
    private Button btnPlay;
    private Button btnSOS;
    private SeekBar speedSlider;
    private TextView tvSpeed;
    private ImageButton btnClear;
    
    private FlashlightService flashlightService;
    private boolean serviceBound = false;
    private MorseCodeUtil morseCodeUtil;
    
    private float playSpeed = 1.0f;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_morse, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Khởi tạo các view
        initViews(view);
        
        // Bind với FlashlightService
        bindFlashlightService();
        
        // Thiết lập người nghe sự kiện
        setupListeners();
    }
    
    private void initViews(View view) {
        etInput = view.findViewById(R.id.et_morse_input);
        tvMorseOutput = view.findViewById(R.id.tv_morse_output);
        btnPlay = view.findViewById(R.id.btn_play_morse);
        btnSOS = view.findViewById(R.id.btn_sos);
        speedSlider = view.findViewById(R.id.speed_slider);
        tvSpeed = view.findViewById(R.id.tv_speed);
        btnClear = view.findViewById(R.id.btn_clear);
        
        // Thiết lập giá trị ban đầu
        updateSpeedText(speedSlider.getProgress());
    }
    
    private void setupListeners() {
        // TextWatcher cho việc nhập text
        etInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            
            @Override
            public void afterTextChanged(Editable s) {
                updateMorseOutput();
            }
        });
        
        // Nút Play
        btnPlay.setOnClickListener(v -> {
            if (serviceBound && morseCodeUtil != null) {
                if (morseCodeUtil.isPlaying()) {
                    morseCodeUtil.stopMorseCode();
                    btnPlay.setText(R.string.play);
                } else {
                    String text = etInput.getText().toString().trim();
                    if (!text.isEmpty()) {
                        morseCodeUtil.playMorseCode(text, playSpeed);
                        btnPlay.setText(R.string.stop);
                    } else {
                        Toast.makeText(getContext(), "Vui lòng nhập văn bản", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        
        // Nút SOS
        btnSOS.setOnClickListener(v -> {
            if (serviceBound && morseCodeUtil != null) {
                if (morseCodeUtil.isPlaying()) {
                    morseCodeUtil.stopMorseCode();
                    btnSOS.setText(R.string.sos);
                } else {
                    morseCodeUtil.playSOS(playSpeed);
                    btnSOS.setText(R.string.stop);
                    
                    // Reset trạng thái nút Play
                    btnPlay.setText(R.string.play);
                }
            }
        });
        
        // Thanh trượt tốc độ
        speedSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateSpeedText(progress);
            }
            
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        
        // Nút Clear
        btnClear.setOnClickListener(v -> {
            etInput.setText("");
            tvMorseOutput.setText("");
            
            // Dừng nếu đang phát
            if (serviceBound && morseCodeUtil != null && morseCodeUtil.isPlaying()) {
                morseCodeUtil.stopMorseCode();
                btnPlay.setText(R.string.play);
                btnSOS.setText(R.string.sos);
            }
        });
    }
    
    private void updateMorseOutput() {
        if (serviceBound && morseCodeUtil != null) {
            String text = etInput.getText().toString().trim();
            if (!text.isEmpty()) {
                String morseCode = morseCodeUtil.convertTextToMorse(text);
                tvMorseOutput.setText(morseCode);
            } else {
                tvMorseOutput.setText("");
            }
        }
    }
    
    private void updateSpeedText(int progress) {
        // Chuyển đổi từ progress (0-100) sang tốc độ (0.5 - 2.0)
        playSpeed = 0.5f + (progress / 100f) * 1.5f;
        
        // Cập nhật UI
        tvSpeed.setText(String.format("%.1fx", playSpeed));
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
            
            // Khởi tạo MorseCodeUtil khi service đã được kết nối
            morseCodeUtil = new MorseCodeUtil(flashlightService);
            
            // Cập nhật UI nếu cần
            updateMorseOutput();
        }
        
        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };
    
    @Override
    public void onResume() {
        super.onResume();
        
        // Nếu đang phát, cập nhật nút thành "Stop"
        if (serviceBound && morseCodeUtil != null && morseCodeUtil.isPlaying()) {
            btnPlay.setText(R.string.stop);
        }
    }
    
    @Override
    public void onPause() {
        super.onPause();
        
        // Dừng phát khi fragment không hiển thị
        if (serviceBound && morseCodeUtil != null && morseCodeUtil.isPlaying()) {
            morseCodeUtil.stopMorseCode();
            btnPlay.setText(R.string.play);
            btnSOS.setText(R.string.sos);
        }
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        
        // Unbind service khi fragment bị hủy
        if (serviceBound) {
            getContext().unbindService(serviceConnection);
            serviceBound = false;
        }
    }
} 