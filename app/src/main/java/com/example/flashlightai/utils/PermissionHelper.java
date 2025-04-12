package com.example.flashlightai.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.flashlightai.R;

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
                showPermissionExplanationDialog(activity, android.Manifest.permission.CAMERA, CAMERA_PERMISSION_REQUEST_CODE, null);
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
                showPermissionExplanationDialog(activity, android.Manifest.permission.READ_PHONE_STATE, READ_PHONE_STATE_PERMISSION_REQUEST_CODE, null);
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
                showPermissionExplanationDialog(activity, android.Manifest.permission.RECEIVE_SMS, RECEIVE_SMS_PERMISSION_REQUEST_CODE, null);
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

    // Helper method to show a dialog explaining why a permission is needed
    private static void showPermissionExplanationDialog(Activity activity, String permission, int requestCode, Runnable deniedCallback) {
        // Tạo dialog giải thích
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(activity.getString(R.string.permission_required))
                .setMessage(activity.getString(getPermissionExplanationStringId(permission)))
                .setPositiveButton(activity.getString(R.string.ok), (dialog, which) -> {
                    // Yêu cầu quyền sau khi người dùng đã đọc lời giải thích
                    ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
                })
                .setNegativeButton(activity.getString(R.string.cancel), (dialog, which) -> {
                    // Người dùng từ chối cấp quyền
                    if (deniedCallback != null) {
                        deniedCallback.run();
                    }
                })
                .create()
                .show();
    }

    // Helper method to get the appropriate explanation string resource ID
    private static int getPermissionExplanationStringId(String permission) {
        switch (permission) {
            case android.Manifest.permission.CAMERA:
                return R.string.camera_permission_message;
            case android.Manifest.permission.READ_PHONE_STATE:
                return R.string.phone_permission_required;
            case android.Manifest.permission.RECEIVE_SMS:
                return R.string.sms_permission_message;
            default:
                return R.string.permission_description;
        }
    }
} 