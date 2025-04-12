package com.example.flashlightai.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * Lớp hỗ trợ xử lý quyền truy cập cho ứng dụng
 */
public class PermissionHelper {
    
    // Các mã yêu cầu quyền
    public static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    public static final int READ_PHONE_STATE_PERMISSION_REQUEST_CODE = 101;
    public static final int RECEIVE_SMS_PERMISSION_REQUEST_CODE = 102;
    
    /**
     * Kiểm tra quyền camera và yêu cầu nếu chưa được cấp
     */
    public static boolean checkAndRequestCameraPermission(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.CAMERA) 
                != PackageManager.PERMISSION_GRANTED) {
            
            // Kiểm tra xem có nên hiển thị lý do trước khi yêu cầu không
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, android.Manifest.permission.CAMERA)) {
                Toast.makeText(activity, 
                        "Cần quyền truy cập camera để sử dụng đèn flash", 
                        Toast.LENGTH_LONG).show();
            }
            
            // Yêu cầu quyền
            ActivityCompat.requestPermissions(
                    activity,
                    new String[]{android.Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_REQUEST_CODE);
            return false;
        }
        return true;
    }
    
    /**
     * Kiểm tra quyền đọc trạng thái điện thoại và yêu cầu nếu chưa được cấp
     */
    public static boolean checkAndRequestPhoneStatePermission(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.READ_PHONE_STATE) 
                != PackageManager.PERMISSION_GRANTED) {
            
            // Kiểm tra xem có nên hiển thị lý do trước khi yêu cầu không
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, android.Manifest.permission.READ_PHONE_STATE)) {
                Toast.makeText(activity, 
                        "Cần quyền đọc trạng thái điện thoại để nhận thông báo cuộc gọi", 
                        Toast.LENGTH_LONG).show();
            }
            
            // Yêu cầu quyền
            ActivityCompat.requestPermissions(
                    activity,
                    new String[]{android.Manifest.permission.READ_PHONE_STATE},
                    READ_PHONE_STATE_PERMISSION_REQUEST_CODE);
            return false;
        }
        return true;
    }
    
    /**
     * Kiểm tra quyền nhận SMS và yêu cầu nếu chưa được cấp
     */
    public static boolean checkAndRequestSmsPermission(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.RECEIVE_SMS) 
                != PackageManager.PERMISSION_GRANTED) {
            
            // Kiểm tra xem có nên hiển thị lý do trước khi yêu cầu không
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, android.Manifest.permission.RECEIVE_SMS)) {
                Toast.makeText(activity, 
                        "Cần quyền đọc tin nhắn SMS để nhận thông báo tin nhắn", 
                        Toast.LENGTH_LONG).show();
            }
            
            // Yêu cầu quyền
            ActivityCompat.requestPermissions(
                    activity,
                    new String[]{android.Manifest.permission.RECEIVE_SMS},
                    RECEIVE_SMS_PERMISSION_REQUEST_CODE);
            return false;
        }
        return true;
    }
    
    /**
     * Mở cài đặt ứng dụng để người dùng có thể cấp quyền thủ công
     */
    public static void openAppSettings(Context context) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.setData(uri);
        context.startActivity(intent);
        
        Toast.makeText(context, 
                "Hãy cấp quyền cần thiết trong cài đặt ứng dụng", 
                Toast.LENGTH_LONG).show();
    }
    
    /**
     * Xử lý kết quả của việc yêu cầu quyền
     * @return true nếu quyền đã được cấp, false nếu bị từ chối
     */
    public static boolean handlePermissionResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }
} 