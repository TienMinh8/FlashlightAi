package com.example.flashlightai.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.example.flashlightai.MainActivity;
import com.example.flashlightai.R;
import com.example.flashlightai.controller.FlashController;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Service theo dõi cuộc gọi và SMS mà không cần quyền đặc biệt NotificationListenerService
 */
public class NotificationMonitorService extends Service {
    
    private static final String TAG = "NotificationMonitor";
    private static final int NOTIFICATION_ID = 2;
    private static final String CHANNEL_ID = "notification_monitor_channel";
    
    // Các intent action phổ biến cho SMS
    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    private static final String WAP_PUSH_RECEIVED = "android.provider.Telephony.WAP_PUSH_RECEIVED";
    
    // Key cho SharedPreferences
    private static final String PREFS_NAME = "FlashlightPrefs";
    private static final String SELECTED_APPS_KEY = "selectedApps";
    private static final String APP_NOTIFICATIONS_ENABLED = "appNotificationsEnabled";
    
    private FlashController flashController;
    private boolean callFlashEnabled = false;
    private boolean smsFlashEnabled = false;
    private boolean appNotificationsEnabled = false;
    
    // Listener và receiver
    private TelephonyManager telephonyManager;
    private PhoneStateListener phoneStateListener;
    private BroadcastReceiver smsReceiver;
    private NotificationReceiver notificationReceiver;
    
    // Để tránh lỗi khi có nhiều cuộc gọi liên tiếp
    private Handler handler = new Handler();
    private boolean isProcessingCall = false;
    
    // Danh sách các ứng dụng đã chọn để nhận thông báo
    private Set<String> selectedApps = new HashSet<>();
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        // Khởi tạo FlashController
        flashController = new FlashController(this);
        
        // Tạo notification channel
        createNotificationChannel();
        
        // Chạy service ở chế độ foreground để tránh bị kill
        startForeground(NOTIFICATION_ID, createNotification());
        
        // Đăng ký PhoneStateListener để theo dõi cuộc gọi
        setupCallMonitoring();
        
        // Đăng ký SMS BroadcastReceiver
        setupSmsMonitoring();
        
        // Đăng ký NotificationReceiver để theo dõi thông báo từ các ứng dụng
        setupNotificationMonitoring();
        
        // Đọc danh sách các ứng dụng đã chọn từ SharedPreferences
        loadSelectedApps();
        
        Log.d(TAG, "NotificationMonitorService created");
    }
    
    /**
     * Đọc danh sách các ứng dụng đã chọn từ SharedPreferences
     */
    private void loadSelectedApps() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        selectedApps = prefs.getStringSet(SELECTED_APPS_KEY, new HashSet<>());
        appNotificationsEnabled = prefs.getBoolean(APP_NOTIFICATIONS_ENABLED, false);
        Log.d(TAG, "Đã tải " + selectedApps.size() + " ứng dụng được chọn, trạng thái: " + (appNotificationsEnabled ? "Bật" : "Tắt"));
    }
    
    /**
     * Lưu danh sách các ứng dụng đã chọn vào SharedPreferences
     */
    private void saveSelectedApps() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putStringSet(SELECTED_APPS_KEY, selectedApps);
        editor.putBoolean(APP_NOTIFICATIONS_ENABLED, appNotificationsEnabled);
        editor.apply();
        Log.d(TAG, "Đã lưu " + selectedApps.size() + " ứng dụng được chọn, trạng thái: " + (appNotificationsEnabled ? "Bật" : "Tắt"));
    }
    
    /**
     * Tạo notification cho foreground service
     */
    private Notification createNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        
        String statusText = "Đang theo dõi: ";
        if (callFlashEnabled) statusText += "Cuộc gọi ";
        if (smsFlashEnabled) statusText += "SMS ";
        if (appNotificationsEnabled) statusText += "Thông báo ứng dụng";
        
        if (!callFlashEnabled && !smsFlashEnabled && !appNotificationsEnabled) {
            statusText = "Không có tính năng nào được bật";
        }
        
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Thông báo đèn flash")
                .setContentText(statusText)
                .setSmallIcon(R.drawable.icon_flash)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setContentIntent(pendingIntent);
        
        return builder.build();
    }
    
    /**
     * Tạo notification channel
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Kênh thông báo đèn flash",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Theo dõi cuộc gọi và tin nhắn");
            
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
    
    /**
     * Thiết lập theo dõi thông báo từ các ứng dụng
     */
    private void setupNotificationMonitoring() {
        notificationReceiver = new NotificationReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.example.flashlightai.NOTIFICATION_RECEIVED");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(notificationReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(notificationReceiver, filter);
        }
    }
    
    /**
     * Thiết lập theo dõi cuộc gọi
     */
    private void setupCallMonitoring() {
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        
        if (telephonyManager != null) {
            phoneStateListener = new PhoneStateListener() {
                @Override
                public void onCallStateChanged(int state, String phoneNumber) {
                    if (callFlashEnabled) {
                        switch (state) {
                            case TelephonyManager.CALL_STATE_RINGING:
                                // Tránh xử lý nhiều cuộc gọi cùng lúc
                                if (!isProcessingCall) {
                                    isProcessingCall = true;
                                    // Có cuộc gọi đến
                                    startCallFlash();
                                }
                                break;
                            case TelephonyManager.CALL_STATE_IDLE:
                                // Kết thúc cuộc gọi
                                stopFlash();
                                // Đặt lại cờ sau 1 giây
                                handler.postDelayed(() -> isProcessingCall = false, 1000);
                                break;
                        }
                    }
                }
            };
            
            // Kiểm tra quyền trước khi đăng ký listener
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                // Đăng ký listener
                telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
            } else {
                Log.w(TAG, "Không có quyền đọc trạng thái điện thoại, tính năng này sẽ không hoạt động");
                // Không làm gì, tính năng sẽ không hoạt động nhưng service vẫn chạy
            }
        }
    }
    
    /**
     * Thiết lập theo dõi SMS
     */
    private void setupSmsMonitoring() {
        smsReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (smsFlashEnabled) {
                    startSmsFlash();
                }
            }
        };
        
        // Kiểm tra quyền trước khi đăng ký receiver
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED) {
            // Đăng ký receiver cho các intent liên quan đến SMS
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(SMS_RECEIVED);
            intentFilter.addAction(WAP_PUSH_RECEIVED);
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                registerReceiver(smsReceiver, intentFilter, Context.RECEIVER_NOT_EXPORTED);
            } else {
                registerReceiver(smsReceiver, intentFilter);
            }
        } else {
            Log.w(TAG, "Không có quyền nhận SMS, tính năng này sẽ không hoạt động");
            // Không làm gì, tính năng sẽ không hoạt động nhưng service vẫn chạy
        }
    }
    
    /**
     * Class để nhận thông báo từ NotificationListenerService
     */
    private class NotificationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("com.example.flashlightai.NOTIFICATION_RECEIVED".equals(intent.getAction())) {
                if (appNotificationsEnabled) {
                    String packageName = intent.getStringExtra("package_name");
                    // Kiểm tra xem ứng dụng có trong danh sách đã chọn không
                    if (packageName != null && selectedApps.contains(packageName)) {
                        startAppNotificationFlash(packageName);
                    }
                }
            }
        }
    }
    
    /**
     * Bắt đầu nhấp nháy đèn flash cho thông báo từ ứng dụng
     */
    private void startAppNotificationFlash(String packageName) {
        if (flashController != null) {
            try {
                // Lưu trạng thái đèn flash hiện tại
                boolean wasFlashOn = flashController.isFlashOn();
                
                // Tạo thread mới để không chặn main thread
                new Thread(() -> {
                    try {
                        // Nhấp nháy theo kiểu riêng cho thông báo ứng dụng
                        // 3 lần nháy nhanh
                        for (int i = 0; i < 3; i++) {
                            flashController.turnOnFlash();
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                return;
                            }
                            
                            flashController.turnOffFlash();
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                return;
                            }
                        }
                        
                        // Nghỉ một chút
                        try {
                            Thread.sleep(300);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                        
                        // 2 lần nháy chậm hơn
                        for (int i = 0; i < 2; i++) {
                            flashController.turnOnFlash();
                            try {
                                Thread.sleep(300);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                return;
                            }
                            
                            flashController.turnOffFlash();
                            try {
                                Thread.sleep(300);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                return;
                            }
                        }
                        
                        // Khôi phục trạng thái trước đó
                        if (wasFlashOn) {
                            flashController.turnOnFlash();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Lỗi khi nhấp nháy đèn cho thông báo ứng dụng: " + e.getMessage());
                    }
                }).start();
            } catch (Exception e) {
                Log.e(TAG, "Lỗi khi bật đèn flash cho thông báo ứng dụng: " + e.getMessage());
            }
        }
    }
    
    /**
     * Bắt đầu nhấp nháy đèn flash cho cuộc gọi
     */
    private void startCallFlash() {
        try {
            if (flashController != null) {
                flashController.setFlashMode(FlashController.FlashMode.BLINK);
                flashController.setBlinkFrequency(500); // 0.5 giây
            }
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi bật đèn flash cho cuộc gọi: " + e.getMessage());
            isProcessingCall = false;
        }
    }
    
    /**
     * Bắt đầu nhấp nháy đèn flash cho SMS
     */
    private void startSmsFlash() {
        if (flashController != null) {
            try {
                // Lưu trạng thái đèn flash hiện tại
                boolean wasFlashOn = flashController.isFlashOn();
                
                // Tạo thread mới để không chặn main thread
                new Thread(() -> {
                    try {
                        // Nhấp nháy nhanh 5 lần
                        for (int i = 0; i < 5; i++) {
                            flashController.turnOnFlash();
                            try {
                                Thread.sleep(200);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                return;
                            }
                            
                            flashController.turnOffFlash();
                            try {
                                Thread.sleep(200);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                return;
                            }
                        }
                        
                        // Khôi phục trạng thái trước đó
                        if (wasFlashOn) {
                            flashController.turnOnFlash();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Lỗi khi nhấp nháy đèn cho SMS: " + e.getMessage());
                    }
                }).start();
            } catch (Exception e) {
                Log.e(TAG, "Lỗi khi bật đèn flash cho SMS: " + e.getMessage());
            }
        }
    }
    
    /**
     * Dừng đèn flash
     */
    private void stopFlash() {
        try {
            if (flashController != null) {
                flashController.setFlashMode(FlashController.FlashMode.NORMAL);
                flashController.turnOffFlash();
            }
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi tắt đèn flash: " + e.getMessage());
        }
    }
    
    /**
     * Bật/tắt tính năng thông báo đèn flash cho cuộc gọi
     * @param enabled true để bật, false để tắt
     */
    public void setCallFlashEnabled(boolean enabled) {
        callFlashEnabled = enabled;
        updateNotification(callFlashEnabled, smsFlashEnabled, appNotificationsEnabled);
        
        if (enabled && telephonyManager != null && phoneStateListener != null) {
            // Kiểm tra quyền trước khi đăng ký listener
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                // Đăng ký listener
                telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
            } else {
                Toast.makeText(this, "Không có quyền đọc trạng thái điện thoại", Toast.LENGTH_SHORT).show();
                // Disable tính năng vì không có quyền
                callFlashEnabled = false;
                updateNotification(callFlashEnabled, smsFlashEnabled, appNotificationsEnabled);
            }
        } else if (!enabled && telephonyManager != null && phoneStateListener != null) {
            // Hủy đăng ký listener
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
        }
    }
    
    /**
     * Bật/tắt tính năng thông báo đèn flash cho SMS
     * @param enabled true để bật, false để tắt
     */
    public void setSmsFlashEnabled(boolean enabled) {
        smsFlashEnabled = enabled;
        updateNotification(callFlashEnabled, smsFlashEnabled, appNotificationsEnabled);
        
        if (enabled) {
            // Kiểm tra quyền trước khi đăng ký receiver
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Không có quyền nhận SMS", Toast.LENGTH_SHORT).show();
                // Disable tính năng vì không có quyền
                smsFlashEnabled = false;
                updateNotification(callFlashEnabled, smsFlashEnabled, appNotificationsEnabled);
            }
        }
    }
    
    /**
     * Bật/tắt tính năng thông báo đèn flash cho ứng dụng
     * @param enabled true để bật, false để tắt
     */
    public void setAppNotificationsEnabled(boolean enabled) {
        appNotificationsEnabled = enabled;
        updateNotification(callFlashEnabled, smsFlashEnabled, appNotificationsEnabled);
        saveSelectedApps();
    }
    
    /**
     * Cập nhật danh sách ứng dụng đã chọn
     * @param apps danh sách ứng dụng đã chọn
     */
    public void setSelectedApps(Set<String> apps) {
        selectedApps = new HashSet<>(apps);
        saveSelectedApps();
        updateNotification(callFlashEnabled, smsFlashEnabled, appNotificationsEnabled);
    }
    
    /**
     * Lấy danh sách ứng dụng đã chọn
     * @return danh sách ứng dụng đã chọn
     */
    public Set<String> getSelectedApps() {
        return new HashSet<>(selectedApps);
    }
    
    /**
     * Kiểm tra xem tính năng thông báo đèn flash cho ứng dụng có được bật không
     * @return true nếu được bật, false nếu không
     */
    public boolean isAppNotificationsEnabled() {
        return appNotificationsEnabled;
    }
    
    /**
     * Cập nhật nội dung thông báo của service
     */
    private void updateNotification(boolean callEnabled, boolean smsEnabled, boolean appEnabled) {
        NotificationManager notificationManager = 
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        
        if (notificationManager == null) return;
        
        String statusText = "Đang theo dõi: ";
        if (callEnabled) statusText += "Cuộc gọi ";
        if (smsEnabled) statusText += "SMS ";
        if (appEnabled) statusText += "Thông báo ứng dụng";
        
        if (!callEnabled && !smsEnabled && !appEnabled) {
            statusText = "Không có tính năng nào được bật";
        }
        
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Thông báo đèn flash")
                .setContentText(statusText)
                .setSmallIcon(R.drawable.icon_flash)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setContentIntent(pendingIntent)
                .build();
        
        notificationManager.notify(NOTIFICATION_ID, notification);
    }
    
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new LocalBinder();
    }
    
    /**
     * Binder for Client
     */
    public class LocalBinder extends android.os.Binder {
        public NotificationMonitorService getService() {
            return NotificationMonitorService.this;
        }
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Bắt đầu hoặc dừng service khi nhận intent
        if (intent != null && intent.getAction() != null) {
            switch (intent.getAction()) {
                case "START_SERVICE":
                    // Service đã được tạo trong onCreate()
                    break;
                case "STOP_SERVICE":
                    stopSelf();
                    break;
                case "UPDATE_CALL_FLASH":
                    boolean callEnabled = intent.getBooleanExtra("enabled", false);
                    setCallFlashEnabled(callEnabled);
                    break;
                case "UPDATE_SMS_FLASH":
                    boolean smsEnabled = intent.getBooleanExtra("enabled", false);
                    setSmsFlashEnabled(smsEnabled);
                    break;
                case "UPDATE_APP_NOTIFICATIONS":
                    boolean appEnabled = intent.getBooleanExtra("enabled", false);
                    setAppNotificationsEnabled(appEnabled);
                    break;
            }
        }
        
        // Nếu service bị kill, hệ thống sẽ tạo lại service và truyền intent cuối cùng vào onStartCommand
        return START_STICKY;
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        
        // Dừng mọi tác vụ đèn flash đang chạy
        if (flashController != null) {
            flashController.setFlashMode(FlashController.FlashMode.NORMAL);
            flashController.turnOffFlash();
            flashController.cleanup();
        }
        
        // Hủy đăng ký receiver và listener
        if (telephonyManager != null && phoneStateListener != null) {
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
        }
        
        try {
            if (smsReceiver != null) {
                unregisterReceiver(smsReceiver);
            }
            
            if (notificationReceiver != null) {
                unregisterReceiver(notificationReceiver);
            }
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi hủy đăng ký receiver: " + e.getMessage());
        }
        
        Log.d(TAG, "NotificationMonitorService destroyed");
    }
} 