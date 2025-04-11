package com.example.flashlightai.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

/**
 * Receiver xử lý tin nhắn SMS đến
 */
public class SmsReceiver extends BroadcastReceiver {
    private static final String TAG = "SmsReceiver";
    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    private static final String PDU_TYPE = "pdus";
    
    @Override
    public void onReceive(Context context, Intent intent) {
        // Kiểm tra action
        if (intent.getAction() == null || !intent.getAction().equals(SMS_RECEIVED)) {
            return;
        }
        
        // Lấy dữ liệu tin nhắn
        final Bundle bundle = intent.getExtras();
        if (bundle == null) {
            return;
        }
        
        try {
            // Lấy mảng PDU
            Object[] pdus = (Object[]) bundle.get(PDU_TYPE);
            if (pdus == null) {
                return;
            }
            
            // Xử lý từng PDU
            for (Object pdu : pdus) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
                
                // Lấy thông tin tin nhắn
                String sender = smsMessage.getDisplayOriginatingAddress();
                String messageBody = smsMessage.getMessageBody();
                
                // Gửi intent broadcast cho NotificationMonitorService
                Intent smsIntent = new Intent(SMS_RECEIVED);
                smsIntent.putExtra("sender", sender);
                smsIntent.putExtra("message", messageBody);
                context.sendBroadcast(smsIntent);
                
                Log.d(TAG, "SMS từ " + sender + ": " + messageBody);
            }
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi xử lý SMS: " + e.getMessage());
        }
    }
} 