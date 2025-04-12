package com.example.flashlightai.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.flashlightai.MainActivity;
import com.example.flashlightai.R;
import com.example.flashlightai.controller.FlashController;

/**
 * Foreground service for keeping the flashlight active even when the app is in background
 */
public class FlashlightService extends Service {
    private static final String TAG = "FlashlightService";
    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "flashlight_channel";
    
    private final IBinder binder = new LocalBinder();
    private FlashController flashController;
    
    public class LocalBinder extends Binder {
        public FlashlightService getService() {
            return FlashlightService.this;
        }
    }
    
    @Override
    public void onCreate() {
        super.onCreate();
        flashController = new FlashController(this);
        createNotificationChannel();
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            String action = intent.getAction();
            Log.d(TAG, "Received action: " + action);
            
            switch (action) {
                case "ACTION_START_FOREGROUND_SERVICE":
                    startForegroundService();
                    break;
                case "ACTION_STOP_FOREGROUND_SERVICE":
                    stopForegroundService();
                    break;
                case "ACTION_TOGGLE_FLASH":
                    // Ghi log trạng thái hiện tại
                    if (flashController != null) {
                        Log.d(TAG, "Before toggle: Flash is " + 
                              (flashController.isFlashOn() ? "ON" : "OFF") + 
                              " in mode " + flashController.getCurrentMode());
                    }
                    
                    // Chuyển đổi trạng thái đèn flash
                    boolean newState = toggleFlash();
                    
                    // Ghi log trạng thái mới
                    Log.d(TAG, "After toggle: Flash is " + (newState ? "ON" : "OFF"));
                    break;
            }
        }
        
        // If the service is killed, it will be restarted with the last intent
        return START_STICKY;
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
    
    /**
     * Start the service as a foreground service
     */
    private void startForegroundService() {
        Log.d(TAG, "Starting foreground service");
        
        try {
            // Create notification for foreground service
            Intent notificationIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(
                    this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
            
            // Create toggle action
            Intent toggleIntent = new Intent(this, FlashlightService.class);
            toggleIntent.setAction("ACTION_TOGGLE_FLASH");
            PendingIntent togglePendingIntent = PendingIntent.getService(
                    this, 1, toggleIntent, PendingIntent.FLAG_IMMUTABLE);
            
            // Build the notification
            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("Flashlight Active")
                    .setContentText("Tap to open the app")
                    .setSmallIcon(R.drawable.ic_launcher_foreground) // Replace with appropriate icon
                    .setContentIntent(pendingIntent)
                    .addAction(R.drawable.ic_launcher_foreground, "Toggle Flash", togglePendingIntent) // Replace with appropriate icon
                    .build();
            
            // Start as foreground service
            startForeground(NOTIFICATION_ID, notification);
        } catch (SecurityException e) {
            Log.e(TAG, "SecurityException when starting foreground service: " + e.getMessage());
            // Thông báo cho người dùng
            stopSelf();
        } catch (Exception e) {
            Log.e(TAG, "Error starting foreground service: " + e.getMessage());
            stopSelf();
        }
    }
    
    /**
     * Stop the foreground service
     */
    private void stopForegroundService() {
        Log.d(TAG, "Stopping foreground service");
        
        // Clean up flash controller
        if (flashController != null) {
            flashController.cleanup();
        }
        
        // Stop the foreground service
        stopForeground(true);
        stopSelf();
    }
    
    /**
     * Toggle the flashlight on/off
     * @return boolean trạng thái của đèn flash sau khi chuyển đổi
     */
    public boolean toggleFlash() {
        if (flashController != null) {
            // Lấy trạng thái đèn từ controller
            boolean isCurrentlyOn = flashController.isFlashOn();
            
            if (isCurrentlyOn) {
                // Nếu đèn đang bật, dừng mọi hiệu ứng và tắt đèn
                Log.d(TAG, "Toggle: Turning flash OFF");
                flashController.stopBlinking();
                flashController.turnOffFlash();
                updateNotification(false);
                return false;
            } else {
                // Nếu đèn đang tắt, bật lên với chế độ hiện tại
                Log.d(TAG, "Toggle: Turning flash ON in mode " + flashController.getCurrentMode());
                // Sử dụng turnOnFlash để bật đèn với chế độ hiện tại
                flashController.turnOnFlash();
                // Cập nhật thông báo
                updateNotification(true);
                Log.d(TAG, "After toggle: Flash is now " + (flashController.isFlashOn() ? "ON" : "OFF"));
                return flashController.isFlashOn(); // Trả về trạng thái thực tế từ controller
            }
        }
        return false;
    }
    
    /**
     * Set the flash mode
     * @param mode the mode to set
     */
    public void setFlashMode(FlashController.FlashMode mode) {
        if (flashController != null) {
            boolean wasFlashOn = flashController.isFlashOn();
            
            // Thiết lập chế độ mới mà không tự bật đèn
            flashController.setFlashMode(mode);
            
            // Ghi log trạng thái mới
            Log.d(TAG, "Flash mode set to: " + mode + 
                " (Flash is " + (flashController.isFlashOn() ? "ON" : "OFF") + ")");
            
            // Cập nhật thông báo với trạng thái hiện tại
            updateNotification(flashController.isFlashOn());
        }
    }
    
    /**
     * Set the blink frequency
     * @param frequency time in milliseconds
     */
    public void setBlinkFrequency(int frequency) {
        if (flashController != null) {
            flashController.setBlinkFrequency(frequency);
        }
    }
    
    /**
     * Set the disco frequency range
     * @param minDelay minimum delay time in milliseconds 
     * @param maxDelay maximum delay time in milliseconds
     */
    public void setDiscoFrequency(int minDelay, int maxDelay) {
        if (flashController != null) {
            flashController.setDiscoFrequency(minDelay, maxDelay);
        }
    }
    
    /**
     * Update the notification with current state
     * @param isOn whether the flash is on
     */
    private void updateNotification(boolean isOn) {
        NotificationManager notificationManager = 
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        
        if (notificationManager == null) return;
        
        // Create notification intent
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        
        // Create toggle action
        Intent toggleIntent = new Intent(this, FlashlightService.class);
        toggleIntent.setAction("ACTION_TOGGLE_FLASH");
        PendingIntent togglePendingIntent = PendingIntent.getService(
                this, 1, toggleIntent, PendingIntent.FLAG_IMMUTABLE);
        
        // Build the notification
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(isOn ? "Flashlight On" : "Flashlight Off")
                .setContentText("Tap to open the app")
                .setSmallIcon(R.drawable.ic_launcher_foreground) // Replace with appropriate icon
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.ic_launcher_foreground, 
                        isOn ? "Turn Off" : "Turn On", togglePendingIntent) // Replace with appropriate icon
                .build();
        
        // Update the notification
        notificationManager.notify(NOTIFICATION_ID, notification);
    }
    
    /**
     * Create the notification channel for Android Oreo and above
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Flashlight Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
            }
        }
    }
    
    /**
     * Get the flash controller
     * @return the flash controller
     */
    public FlashController getFlashController() {
        return flashController;
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (flashController != null) {
            flashController.cleanup();
        }
    }
    
    /**
     * Kiểm tra xem đèn flash có đang bật hay không
     * @return true nếu đèn flash đang bật
     */
    public boolean isFlashOn() {
        if (flashController != null) {
            return flashController.isFlashOn();
        }
        return false;
    }
    
    /**
     * Bật đèn flash với chế độ hiện tại
     */
    public void turnOnFlash() {
        if (flashController != null) {
            // Nếu đèn đã bật, không làm gì cả
            if (flashController.isFlashOn()) {
                Log.d(TAG, "Flash is already ON, doing nothing");
                return;
            }
            
            // Bật đèn với chế độ hiện tại bằng phương thức turnOnFlash
            Log.d(TAG, "Turning ON flash in mode: " + flashController.getCurrentMode());
            flashController.turnOnFlash();
            
            // Cập nhật thông báo
            updateNotification(true);
        }
    }
    
    /**
     * Tắt đèn flash
     */
    public void turnOffFlash() {
        if (flashController != null) {
            // Dừng mọi hiệu ứng nhấp nháy trước khi tắt đèn
            flashController.stopBlinking();
            
            // Tắt đèn flash nếu đang bật
            if (flashController.isFlashOn()) {
                flashController.turnOffFlash();
            }
            
            // Cập nhật thông báo
            updateNotification(false);
        }
    }
    
    /**
     * Lấy chế độ đèn flash hiện tại
     * @return chế độ đèn flash hiện tại
     */
    public FlashController.FlashMode getCurrentMode() {
        if (flashController != null) {
            return flashController.getFlashMode();
        }
        return FlashController.FlashMode.NORMAL;
    }
    
    /**
     * Dừng mọi chế độ nhấp nháy đèn flash
     */
    public void stopBlinking() {
        if (flashController != null) {
            flashController.stopBlinking();
        }
    }
} 