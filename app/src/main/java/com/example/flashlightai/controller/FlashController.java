package com.example.flashlightai.controller;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Controller class for managing the flashlight functionality
 */
public class FlashController {
    private static final String TAG = "FlashController";
    
    private final Context context;
    private CameraManager cameraManager;
    private String cameraId;
    private final AtomicBoolean isFlashOn = new AtomicBoolean(false);
    private final Handler handler;
    private Runnable flashRunnable;
    
    // Flash modes
    public enum FlashMode {
        NORMAL,
        BLINK,
        SOS,
        STROBE,
        DISCO
    }
    
    private FlashMode currentMode = FlashMode.NORMAL;
    private int blinkFrequency = 500; // in milliseconds
    private int discoMinDelay = 100; // Thời gian delay tối thiểu
    private int discoMaxDelay = 500; // Thời gian delay tối đa
    private final Random random = new Random();
    
    public FlashController(Context context) {
        this.context = context.getApplicationContext(); // Sử dụng application context để tránh rò rỉ bộ nhớ
        this.handler = new Handler(Looper.getMainLooper());
        
        // Initialize camera
        initializeCamera();
    }
    
    private void initializeCamera() {
        // Check if device has a flash
        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            Log.e(TAG, "Device does not have a flash");
            return;
        }
        
        try {
            cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
            if (cameraManager != null) {
                cameraId = cameraManager.getCameraIdList()[0]; // Usually the back camera
            }
        } catch (CameraAccessException e) {
            Log.e(TAG, "Failed to access camera: " + e.getMessage(), e);
        } catch (Exception e) {
            Log.e(TAG, "Unexpected error initializing camera: " + e.getMessage(), e);
        }
    }
    
    /**
     * Toggle the flashlight on/off
     * @return the new state of the flash
     */
    public boolean toggleFlash() {
        if (isFlashOn.get()) {
            turnOffFlash();
        } else {
            turnOnFlash();
        }
        Log.d(TAG, "toggleFlash: Flash is now " + (isFlashOn.get() ? "ON" : "OFF") + " in mode " + currentMode);
        return isFlashOn.get();
    }
    
    /**
     * Turn on the flashlight
     */
    public void turnOnFlash() {
        // Nếu đèn đã bật, không làm gì cả
        if (isFlashOn.get()) {
            Log.d(TAG, "Flash is already ON, doing nothing");
            return;
        }
        
        if (cameraManager == null || cameraId == null) {
            Log.e(TAG, "Cannot turn on flash: Camera not initialized");
            return;
        }
        
        try {
            Log.d(TAG, "Turning ON flash in mode: " + currentMode);
            
            // Dừng mọi hiệu ứng đang chạy
            stopBlinking();
            
            // Bật đèn với chế độ thích hợp
            switch (currentMode) {
                case NORMAL:
                    setTorchMode(true);
                    break;
                
                case BLINK:
                    setTorchMode(true);
                    startBlinking(blinkFrequency);
                    break;
                    
                case SOS:
                    setTorchMode(true);
                    startSOS();
                    break;
                    
                case STROBE:
                    setTorchMode(true);
                    startStrobe();
                    break;
                    
                case DISCO:
                    setTorchMode(true);
                    startDisco();
                    break;
                
                default:
                    setTorchMode(true);
                    break;
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to turn on flash: " + e.getMessage(), e);
        }
    }
    
    /**
     * Safely set torch mode with exception handling
     */
    private void setTorchMode(boolean enabled) {
        try {
            if (cameraManager != null && cameraId != null) {
                cameraManager.setTorchMode(cameraId, enabled);
                isFlashOn.set(enabled);
                Log.d(TAG, "Torch mode set to: " + enabled);
            }
        } catch (CameraAccessException e) {
            Log.e(TAG, "Failed to set torch mode: " + e.getMessage(), e);
        } catch (Exception e) {
            Log.e(TAG, "Unexpected error setting torch mode: " + e.getMessage(), e);
        }
    }
    
    /**
     * Turn off the flashlight
     */
    public void turnOffFlash() {
        // Dừng bất kỳ hiệu ứng nhấp nháy nào đang chạy
        stopBlinking();
        setTorchMode(false);
    }
    
    /**
     * Set the flash mode
     * @param mode the mode to set
     */
    public void setFlashMode(FlashMode mode) {
        // Stop any current blinking
        stopBlinking();
        
        // Nếu đèn đang bật, tắt đèn trước khi đổi chế độ
        if (isFlashOn.get()) {
            setTorchMode(false);
        }
        
        this.currentMode = mode;
        Log.d(TAG, "Flash mode set to: " + mode.name());
        
        // Nếu đèn đang bật, bật lại đèn với chế độ mới
        if (isFlashOn.get()) {
            turnOnFlash();
        }
    }
    
    /**
     * Set the blink frequency
     * @param frequency frequency in milliseconds
     */
    public void setBlinkFrequency(int frequency) {
        this.blinkFrequency = Math.max(50, Math.min(frequency, 2000));
        
        // Nếu đang nhấp nháy, cập nhật tần số
        if (isFlashOn.get() && currentMode == FlashMode.BLINK) {
            stopBlinking();
            startBlinking(blinkFrequency);
        }
    }
    
    private void startBlinking(int frequency) {
        // Hủy runnable cũ nếu có
        stopBlinking();
        
        // Tạo runnable mới
        flashRunnable = new Runnable() {
            private boolean flashState = true;
            
            @Override
            public void run() {
                try {
                    flashState = !flashState;
                    setTorchMode(flashState);
                    
                    // Lập lịch lần tiếp theo
                    handler.postDelayed(this, frequency);
                } catch (Exception e) {
                    Log.e(TAG, "Error in blink runnable: " + e.getMessage(), e);
                }
            }
        };
        
        // Bắt đầu nhấp nháy
        handler.postDelayed(flashRunnable, frequency);
    }
    
    private void startSOS() {
        stopBlinking();
        
        final int dotDuration = 250; // 0.25 giây cho dấu chấm
        final int dashDuration = 3 * dotDuration; // 0.75 giây cho dấu gạch
        final int symbolPause = dotDuration; // 0.25 giây nghỉ giữa các ký tự
        final int letterPause = 3 * dotDuration; // 0.75 giây nghỉ giữa các chữ
        final int wordPause = 7 * dotDuration; // 1.75 giây nghỉ sau khi hoàn thành chuỗi SOS
        
        // SOS pattern: ... --- ...
        flashRunnable = new Runnable() {
            private int step = 0;
            private final int totalSteps = 3 + 3 + 3 + 1; // 3 dots + 3 dashes + 3 dots + word pause
            
            @Override
            public void run() {
                try {
                    if (step < 3) {
                        // S: dot (.)
                        toggleForPattern(dotDuration, symbolPause);
                    } else if (step == 3) {
                        // Pause between S and O
                        handler.postDelayed(this, letterPause);
                        step++;
                        return;
                    } else if (step < 7) {
                        // O: dash (-)
                        toggleForPattern(dashDuration, symbolPause);
                    } else if (step == 7) {
                        // Pause between O and S
                        handler.postDelayed(this, letterPause);
                        step++;
                        return;
                    } else if (step < 11) {
                        // S: dot (.)
                        toggleForPattern(dotDuration, symbolPause);
                    } else {
                        // Complete cycle, restart after pause
                        setTorchMode(false);
                        handler.postDelayed(() -> {
                            step = 0;
                            setTorchMode(true);
                            handler.post(this);
                        }, wordPause);
                        return;
                    }
                    
                    step++;
                } catch (Exception e) {
                    Log.e(TAG, "Error in SOS runnable: " + e.getMessage(), e);
                }
            }
            
            private void toggleForPattern(int onDuration, int offDuration) {
                setTorchMode(true);
                handler.postDelayed(() -> {
                    setTorchMode(false);
                    handler.postDelayed(flashRunnable, offDuration);
                }, onDuration);
            }
        };
        
        // Start the sequence
        handler.post(flashRunnable);
    }
    
    private void startStrobe() {
        stopBlinking();
        
        // Strobe uses very fast blinking
        startBlinking(50); // 50ms is very fast
    }
    
    private void startDisco() {
        stopBlinking();
        
        flashRunnable = new Runnable() {
            private boolean flashState = true;
            
            @Override
            public void run() {
                try {
                    flashState = !flashState;
                    setTorchMode(flashState);
                    
                    // Random delay for disco effect
                    int randomDelay = discoMinDelay + random.nextInt(discoMaxDelay - discoMinDelay);
                    handler.postDelayed(this, randomDelay);
                } catch (Exception e) {
                    Log.e(TAG, "Error in disco runnable: " + e.getMessage(), e);
                }
            }
        };
        
        // Bắt đầu hiệu ứng disco
        handler.post(flashRunnable);
    }
    
    public void setDiscoFrequency(int minDelay, int maxDelay) {
        // Đảm bảo minDelay <= maxDelay
        this.discoMinDelay = Math.min(minDelay, maxDelay);
        this.discoMaxDelay = Math.max(minDelay, maxDelay);
        
        // Đảm bảo các giá trị nằm trong khoảng hợp lệ
        this.discoMinDelay = Math.max(50, this.discoMinDelay);
        this.discoMaxDelay = Math.min(2000, this.discoMaxDelay);
        
        // Nếu đang trong chế độ disco, cập nhật tần số
        if (isFlashOn.get() && currentMode == FlashMode.DISCO) {
            stopBlinking();
            startDisco();
        }
    }
    
    public void stopBlinking() {
        if (flashRunnable != null) {
            handler.removeCallbacks(flashRunnable);
            flashRunnable = null;
        }
    }
    
    public void cleanup() {
        // Dừng tất cả hiệu ứng
        stopBlinking();
        
        // Tắt đèn nếu đang bật
        if (isFlashOn.get()) {
            turnOffFlash();
        }
    }
    
    public FlashMode getCurrentMode() {
        return currentMode;
    }
    
    public FlashMode getFlashMode() {
        return currentMode;
    }
    
    public boolean isFlashOn() {
        return isFlashOn.get();
    }
} 