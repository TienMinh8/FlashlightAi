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
            turnOnFlash();
        }
        return isFlashOn;
    }
    
    /**
     * Turn on the flashlight
     */
    public void turnOnFlash() {
        try {
            if (cameraManager != null && cameraId != null) {
                cameraManager.setTorchMode(cameraId, true);
                isFlashOn = true;
            }
        } catch (CameraAccessException e) {
            Log.e(TAG, "Failed to turn on flash: " + e.getMessage());
        }
    }
    
    /**
     * Turn off the flashlight
     */
    public void turnOffFlash() {
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
        
        this.currentMode = mode;
        
        // Start the appropriate mode
        switch (mode) {
            case NORMAL:
                // Just a regular on/off state
                break;
            case BLINK:
                startBlinking(blinkFrequency);
                break;
            case SOS:
                startSOS();
                break;
            case STROBE:
                startStrobe();
                break;
            case DISCO:
                startDisco();
                break;
        }
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
        
        handler.post(flashRunnable);
    }
    
    /**
     * Start SOS mode (... --- ...)
     */
    private void startSOS() {
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
        
        handler.post(flashRunnable);
    }
    
    /**
     * Start strobe mode (very fast blinking)
     */
    private void startStrobe() {
        // Strobe is just very fast blinking
        startBlinking(50); // 50ms for strobe effect
    }
    
    /**
     * Start disco mode (random pattern and speed)
     */
    private void startDisco() {
        flashRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    if (cameraManager != null && cameraId != null) {
                        isFlashOn = !isFlashOn;
                        cameraManager.setTorchMode(cameraId, isFlashOn);
                        
                        // Random delay between 100-500ms for disco effect
                        int randomDelay = 100 + (int)(Math.random() * 400);
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