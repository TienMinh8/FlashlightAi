package com.example.flashlightai.service;

import android.content.Intent;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

/**
 * Service lắng nghe thông báo từ các ứng dụng khác
 * Yêu cầu quyền đặc biệt và người dùng phải bật trong Settings
 */
public class NotificationService extends NotificationListenerService {
    private static final String TAG = "NotificationService";
    
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "NotificationService created");
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }
    
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
        
        // Lấy thông tin thông báo
        String packageName = sbn.getPackageName();
        
        // Bỏ qua thông báo của hệ thống hoặc ứng dụng hiện tại
        if (packageName.equals("android") || 
            packageName.equals("com.android.systemui") ||
            packageName.equals(getPackageName())) {
            return;
        }
        
        // Gửi broadcast đến NotificationMonitorService
        Intent intent = new Intent("com.example.flashlightai.NOTIFICATION_RECEIVED");
        intent.putExtra("package_name", packageName);
        sendBroadcast(intent);
        
        Log.d(TAG, "Thông báo từ " + packageName + " đã được chuyển tiếp");
    }
    
    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
        // Không cần xử lý khi thông báo bị xóa
    }
} 