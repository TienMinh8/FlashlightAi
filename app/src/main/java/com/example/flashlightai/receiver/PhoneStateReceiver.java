package com.example.flashlightai.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * Receiver xử lý sự kiện trạng thái điện thoại (cuộc gọi đến)
 */
public class PhoneStateReceiver extends BroadcastReceiver {
    private static final String TAG = "PhoneStateReceiver";
    private static final String ACTION_PHONE_STATE_CHANGED = "android.intent.action.PHONE_STATE";
    private static final String ACTION_CALL_STATE_CHANGED = "com.example.flashlightai.CALL_STATE_CHANGED";
    
    @Override
    public void onReceive(Context context, Intent intent) {
        // Kiểm tra action
        if (intent.getAction() == null || !intent.getAction().equals(ACTION_PHONE_STATE_CHANGED)) {
            return;
        }
        
        try {
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            String phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            
            if (phoneNumber == null) {
                phoneNumber = "Số không xác định";
            }
            
            // Tạo intent broadcast để thông báo cho service
            Intent callIntent = new Intent(ACTION_CALL_STATE_CHANGED);
            callIntent.putExtra("state", state);
            callIntent.putExtra("phoneNumber", phoneNumber);
            
            if (TelephonyManager.EXTRA_STATE_RINGING.equals(state)) {
                // Cuộc gọi đến
                Log.d(TAG, "Cuộc gọi đến từ: " + phoneNumber);
                callIntent.putExtra("callState", "RINGING");
                context.sendBroadcast(callIntent);
            } else if (TelephonyManager.EXTRA_STATE_IDLE.equals(state)) {
                // Kết thúc cuộc gọi
                Log.d(TAG, "Cuộc gọi kết thúc");
                callIntent.putExtra("callState", "IDLE");
                context.sendBroadcast(callIntent);
            } else if (TelephonyManager.EXTRA_STATE_OFFHOOK.equals(state)) {
                // Đã trả lời cuộc gọi
                Log.d(TAG, "Đã trả lời cuộc gọi");
                callIntent.putExtra("callState", "OFFHOOK");
                context.sendBroadcast(callIntent);
            }
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi xử lý trạng thái điện thoại: " + e.getMessage());
        }
    }
} 