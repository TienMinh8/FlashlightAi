package com.example.flashlightai.controller;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Handler;
import android.util.Log;

/**
 * Controller class for managing the flashlight functionality
 */
public class FlashController {
    private static final String TAG = "FlashController";
    
    private Context context;
    private CameraManager cameraManager;
    private String cameraId;
    private boolean isFlashOn = false;
    private Handler handler;
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
    
    public FlashController(Context context) {
        this.context = context;
        this.handler = new Handler();
        
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
            Log.e(TAG, "Failed to access camera: " + e.getMessage());
        }
    }
    
    /**
     * Toggle the flashlight on/off
     * @return the new state of the flash
     */
    public boolean toggleFlash() {
        if (isFlashOn) {
            turnOffFlash();
        } else {
            // Khi bật đèn, sử dụng chế độ đã chọn
            switch (currentMode) {
                case NORMAL:
                    turnOnFlash();
                    break;
                case BLINK:
                    turnOnFlash();
                    break;
                case SOS:
                    turnOnFlash();
                    break;
                case STROBE:
                    turnOnFlash();
                    break;
                case DISCO:
                    turnOnFlash();
                    break;
                default:
                    turnOnFlash();
                    break;
            }
        }
        Log.d(TAG, "toggleFlash: Flash is now " + (isFlashOn ? "ON" : "OFF") + " in mode " + currentMode);
        return isFlashOn;
    }
    
    /**
     * Turn on the flashlight
     */
    public void turnOnFlash() {
        // Nếu đèn đã bật, không làm gì cả
        if (isFlashOn) {
            Log.d(TAG, "Flash is already ON, doing nothing");
            return;
        }
        
        try {
            Log.d(TAG, "Turning ON flash in mode: " + currentMode);
            
            // Dừng mọi hiệu ứng đang chạy
            stopBlinking();
            
            // Bật đèn với chế độ thích hợp
            switch (currentMode) {
                case NORMAL:
                    // Chế độ thường, chỉ bật đèn
                    if (cameraManager != null && cameraId != null) {
                        cameraManager.setTorchMode(cameraId, true);
                        isFlashOn = true;
                        Log.d(TAG, "Normal mode: flash turned ON");
                    }
                    break;
                
                case BLINK:
                    // Đảm bảo đèn bật trước khi bắt đầu nhấp nháy
                    if (cameraManager != null && cameraId != null) {
                        // Bật đèn trước
                        cameraManager.setTorchMode(cameraId, true);
                        isFlashOn = true;
                        // Sau đó bắt đầu chế độ nhấp nháy
                        startBlinking(blinkFrequency);
                        Log.d(TAG, "Blink mode started with frequency: " + blinkFrequency);
                    }
                    break;
                    
                case SOS:
                    // Bật chế độ SOS - đảm bảo đèn bật trước
                    if (cameraManager != null && cameraId != null) {
                        cameraManager.setTorchMode(cameraId, true);
                        isFlashOn = true;
                        // Sau đó bắt đầu chế độ SOS
                        startSOS();
                        Log.d(TAG, "SOS mode started");
                    }
                    break;
                    
                case STROBE:
                    // Bật chế độ stroboscope
                    if (cameraManager != null && cameraId != null) {
                        // Bật đèn trước
                        cameraManager.setTorchMode(cameraId, true);
                        isFlashOn = true;
                        // Sau đó bắt đầu chế độ strobe
                        startStrobe();
                        Log.d(TAG, "Strobe mode started");
                    }
                    break;
                    
                case DISCO:
                    // Bật chế độ disco
                    if (cameraManager != null && cameraId != null) {
                        // Bật đèn trước
                        cameraManager.setTorchMode(cameraId, true);
                        isFlashOn = true;
                        // Sau đó bắt đầu chế độ disco
                        startDisco();
                        Log.d(TAG, "Disco mode started");
                    }
                    break;
            }
        } catch (CameraAccessException e) {
            Log.e(TAG, "Failed to turn on flash: " + e.getMessage());
        }
    }
    
    /**
     * Turn off the flashlight
     */
    public void turnOffFlash() {
        // Dừng bất kỳ hiệu ứng nhấp nháy nào đang chạy
        stopBlinking();
        
        try {
            if (cameraManager != null && cameraId != null) {
                cameraManager.setTorchMode(cameraId, false);
                isFlashOn = false;
            }
        } catch (CameraAccessException e) {
            Log.e(TAG, "Failed to turn off flash: " + e.getMessage());
        }
    }
    
    /**
     * Set the flash mode
     * @param mode the mode to set
     */
    public void setFlashMode(FlashMode mode) {
        // Stop any current blinking
        stopBlinking();
        
        // Nếu đèn đang bật, tắt đèn trước khi đổi chế độ
        boolean wasOn = isFlashOn;
        if (wasOn) {
            try {
                if (cameraManager != null && cameraId != null) {
                    cameraManager.setTorchMode(cameraId, false);
                    isFlashOn = false;
                }
            } catch (CameraAccessException e) {
                Log.e(TAG, "Failed to turn off flash when changing mode: " + e.getMessage());
            }
        }
        
        // Chỉ cập nhật chế độ, không tự bật đèn
        this.currentMode = mode;
        Log.d(TAG, "Flash mode set to: " + mode + " (Flash is " + (isFlashOn ? "ON" : "OFF") + ")");
        
        // Không tự động bật lại đèn khi thay đổi chế độ
        // if (wasOn) {
        //     turnOnFlash();
        // }
    }
    
    /**
     * Set the blink frequency in milliseconds
     * @param frequency time in milliseconds
     */
    public void setBlinkFrequency(int frequency) {
        this.blinkFrequency = frequency;
        if (currentMode == FlashMode.BLINK) {
            stopBlinking();
            startBlinking(frequency);
        }
    }
    
    /**
     * Start blinking at the given frequency
     * @param frequency time in milliseconds
     */
    private void startBlinking(int frequency) {
        // Nếu đã có runnable, dừng nó
        if (flashRunnable != null) {
            handler.removeCallbacks(flashRunnable);
        }
        
        // Đèn flash đã được bật từ phương thức turnOnFlash(), nên không cần bật lại
        // Log để gỡ lỗi
        Log.d(TAG, "Blinking starting with light state: " + (isFlashOn ? "ON" : "OFF") + ", frequency: " + frequency);
        
        flashRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    if (cameraManager != null && cameraId != null) {
                        isFlashOn = !isFlashOn;
                        cameraManager.setTorchMode(cameraId, isFlashOn);
                        handler.postDelayed(this, frequency);
                    }
                } catch (CameraAccessException e) {
                    Log.e(TAG, "Failed to blink: " + e.getMessage());
                }
            }
        };
        
        handler.postDelayed(flashRunnable, frequency);
    }
    
    /**
     * Start SOS mode (... --- ...)
     */
    private void startSOS() {
        // Nếu đã có runnable, dừng nó
        if (flashRunnable != null) {
            handler.removeCallbacks(flashRunnable);
        }
        
        final int dotDuration = 200;  // 200ms for a dot
        final int dashDuration = dotDuration * 3;  // 600ms for a dash
        final int symbolGap = dotDuration;  // 200ms between symbols
        final int letterGap = dotDuration * 3;  // 600ms between letters
        final int wordGap = dotDuration * 7;  // 1400ms between words
        
        // SOS pattern: ... --- ...
        int[] pattern = {
            1, dotDuration, 0, symbolGap,  // S (dot)
            1, dotDuration, 0, symbolGap,  // S (dot)
            1, dotDuration, 0, letterGap,  // S (dot)
            
            1, dashDuration, 0, symbolGap, // O (dash)
            1, dashDuration, 0, symbolGap, // O (dash)
            1, dashDuration, 0, letterGap, // O (dash)
            
            1, dotDuration, 0, symbolGap,  // S (dot)
            1, dotDuration, 0, symbolGap,  // S (dot)
            1, dotDuration, 0, wordGap     // S (dot)
        };
        
        final int[] patternIndex = {0};
        
        // Đèn flash đã được bật từ phương thức turnOnFlash(), không cần bật lại ở đây
        // Lưu ý: Bây giờ isFlashOn đã được đặt thành true trong turnOnFlash()
        Log.d(TAG, "SOS mode starting with light state: " + (isFlashOn ? "ON" : "OFF"));
        
        flashRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    if (cameraManager != null && cameraId != null) {
                        boolean on = pattern[patternIndex[0]] == 1;
                        int duration = pattern[patternIndex[0] + 1];
                        
                        cameraManager.setTorchMode(cameraId, on);
                        isFlashOn = on;
                        
                        patternIndex[0] += 2;
                        if (patternIndex[0] >= pattern.length) {
                            patternIndex[0] = 0; // Reset to beginning of pattern
                        }
                        
                        handler.postDelayed(this, duration);
                    }
                } catch (CameraAccessException e) {
                    Log.e(TAG, "Failed during SOS: " + e.getMessage());
                }
            }
        };
        
        // Bắt đầu mẫu SOS sau một khoảng thời gian ngắn
        handler.postDelayed(flashRunnable, 100);
    }
    
    /**
     * Start strobe mode (very fast blinking)
     */
    private void startStrobe() {
        // Log để gỡ lỗi
        Log.d(TAG, "Strobe mode starting with light state: " + (isFlashOn ? "ON" : "OFF"));
        
        // Strobe is just very fast blinking
        startBlinking(50); // 50ms for strobe effect
    }
    
    /**
     * Start disco mode (random pattern and speed)
     */
    private void startDisco() {
        if (flashRunnable != null) {
            handler.removeCallbacks(flashRunnable);
        }
        
        // Kiểm tra rằng đèn đã được bật từ phương thức turnOnFlash
        Log.d(TAG, "Disco mode starting with light state: " + (isFlashOn ? "ON" : "OFF"));
        
        flashRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    if (cameraManager != null && cameraId != null) {
                        isFlashOn = !isFlashOn;
                        cameraManager.setTorchMode(cameraId, isFlashOn);
                        
                        // Random delay between discoMinDelay-discoMaxDelay ms for disco effect
                        int randomDelay = discoMinDelay + (int)(Math.random() * (discoMaxDelay - discoMinDelay));
                        handler.postDelayed(this, randomDelay);
                    }
                } catch (CameraAccessException e) {
                    Log.e(TAG, "Failed during disco mode: " + e.getMessage());
                }
            }
        };
        
        handler.post(flashRunnable);
    }
    
    /**
     * Set the disco frequency (speed) in milliseconds
     * @param minDelay minimum delay time in milliseconds
     * @param maxDelay maximum delay time in milliseconds
     */
    public void setDiscoFrequency(int minDelay, int maxDelay) {
        boolean isDiscoModeRunning = currentMode == FlashMode.DISCO && isFlashOn;
        
        // Đảm bảo minDelay luôn nhỏ hơn maxDelay và trong ngưỡng hợp lý
        this.discoMinDelay = Math.max(50, minDelay);
        this.discoMaxDelay = Math.max(this.discoMinDelay + 50, maxDelay);
        
        Log.d(TAG, "Disco frequency set to: " + this.discoMinDelay + "-" + this.discoMaxDelay + "ms");
        
        // Nếu đang ở chế độ disco và đèn flash đang bật, áp dụng ngay tốc độ mới
        if (isDiscoModeRunning) {
            stopBlinking();
            startDisco();
        }
    }
    
    /**
     * Stop any blinking mode
     */
    public void stopBlinking() {
        if (flashRunnable != null) {
            handler.removeCallbacks(flashRunnable);
            flashRunnable = null;
        }
    }
    
    /**
     * Clean up resources
     */
    public void cleanup() {
        stopBlinking();
        turnOffFlash();
    }
    
    /**
     * Get the current mode
     * @return the current flash mode
     */
    public FlashMode getCurrentMode() {
        return currentMode;
    }
    
    /**
     * Lấy chế độ đèn flash hiện tại
     * @return chế độ hiện tại của đèn flash
     */
    public FlashMode getFlashMode() {
        return currentMode;
    }
    
    /**
     * Kiểm tra xem đèn flash có đang bật không
     * @return true nếu đèn flash đang bật
     */
    public boolean isFlashOn() {
        return isFlashOn;
    }
} 