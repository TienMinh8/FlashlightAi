package com.example.flashlightai.fragment;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flashlightai.R;
import com.example.flashlightai.adapter.LanguageAdapter;
import com.example.flashlightai.model.Language;
import com.example.flashlightai.service.NotificationMonitorService;
import com.example.flashlightai.utils.LanguageManager;
import com.example.flashlightai.utils.PreferenceManager;

import java.util.List;
import java.util.Locale;

/**
 * Fragment hiển thị các thiết lập ứng dụng
 */
public class SettingsFragment extends Fragment {
    
    private PreferenceManager preferenceManager;
    private LanguageManager languageManager;
    private static final String KEY_RATED = "app_rated";
    private static final String KEY_RATING = "app_rating";
    private static final String KEY_FEEDBACK = "app_feedback";
    
    // Service connection
    private NotificationMonitorService notificationService;
    private boolean serviceBound = false;
    
    // Switch controls
    private SwitchCompat switchCallNotification;
    private SwitchCompat switchSmsNotification;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Khởi tạo PreferenceManager
        preferenceManager = new PreferenceManager(requireContext());
        languageManager = new LanguageManager(requireContext());
        
        // Thiết lập ngôn ngữ mặc định nếu chưa có
        String currentLanguage = preferenceManager.getString("app_language", null);
        if (currentLanguage == null) {
            // Sử dụng tiếng Anh làm ngôn ngữ mặc định, bất kể ngôn ngữ hệ thống là gì
            languageManager.saveLanguageCode("en");
        }
        
        // Kiểm tra xem đây có phải là lần đầu khởi động không
        boolean isFirstRun = preferenceManager.getBoolean("is_first_run", true);
        if (isFirstRun) {
            // Đặt mặc định tất cả thông báo là tắt khi lần đầu sử dụng
            preferenceManager.setBoolean("call_flash_enabled", false);
            preferenceManager.setBoolean("sms_flash_enabled", false);
            preferenceManager.setBoolean("is_first_run", false);
        }
        
        // Kết nối với service
        bindNotificationService();
        
        // Thiết lập controls và listeners cho trang cài đặt
        setupSettingsControls(view);
    }
    
    @Override
    public void onStart() {
        super.onStart();
        if (!serviceBound) {
            bindNotificationService();
        }
    }
    
    @Override
    public void onStop() {
        super.onStop();
        if (serviceBound) {
            requireContext().unbindService(serviceConnection);
            serviceBound = false;
        }
    }
    
    /**
     * Kết nối với NotificationMonitorService
     */
    private void bindNotificationService() {
        Intent intent = new Intent(requireContext(), NotificationMonitorService.class);
        intent.setAction("ACTION_START_FOREGROUND_SERVICE");
        
        // Khởi động service nếu chưa chạy
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requireContext().startForegroundService(intent);
        } else {
            requireContext().startService(intent);
        }
        
        // Bind với service
        requireContext().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }
    
    /**
     * ServiceConnection để kết nối với NotificationMonitorService
     */
    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            NotificationMonitorService.LocalBinder binder = (NotificationMonitorService.LocalBinder) service;
            notificationService = binder.getService();
            serviceBound = true;
            
            // Cập nhật UI dựa trên trạng thái service
            updateUIFromService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };
    
    /**
     * Cập nhật UI dựa trên trạng thái service
     */
    private void updateUIFromService() {
        if (serviceBound && notificationService != null) {
            try {
                Log.d("SettingsFragment", "Đang cập nhật UI từ service");
                
                // Lấy trạng thái đã lưu từ preferences
                boolean isCallFlashEnabled = preferenceManager.getBoolean("call_flash_enabled", false);
                boolean isSmsFlashEnabled = preferenceManager.getBoolean("sms_flash_enabled", false);
                
                Log.d("SettingsFragment", "Trạng thái từ preferences: call=" + isCallFlashEnabled + ", sms=" + isSmsFlashEnabled);
                
                // Lấy trạng thái quyền
                boolean hasPhonePermission = ContextCompat.checkSelfPermission(
                        requireContext(), Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED;
                boolean hasSmsPermission = ContextCompat.checkSelfPermission(
                        requireContext(), Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED;
                
                Log.d("SettingsFragment", "Trạng thái quyền: phone=" + hasPhonePermission + ", sms=" + hasSmsPermission);
                
                // Cập nhật UI
                if (switchCallNotification != null) {
                    // Luôn cho phép bấm, bất kể đã có quyền hay chưa
                    switchCallNotification.setEnabled(true);
                    
                    // Cập nhật trạng thái switch dựa trên giá trị từ preferences
                    switchCallNotification.setChecked(isCallFlashEnabled);
                }
                
                if (switchSmsNotification != null) {
                    // Luôn cho phép bấm, bất kể đã có quyền hay chưa
                    switchSmsNotification.setEnabled(true);
                    
                    // Cập nhật trạng thái switch dựa trên giá trị từ preferences
                    switchSmsNotification.setChecked(isSmsFlashEnabled);
                }
            } catch (Exception e) {
                Log.e("SettingsFragment", "Lỗi khi cập nhật UI", e);
            }
        }
    }
    
    private void setupSettingsControls(View view) {
        // Notification Settings
        switchCallNotification = view.findViewById(R.id.switch_call_notification);
        switchSmsNotification = view.findViewById(R.id.switch_sms_notification);
        
        // Tìm RelativeLayout chứa các switch
        View layoutCallNotification = view.findViewById(R.id.layout_call_notification);
        View layoutSmsNotification = view.findViewById(R.id.layout_sms_notification);
        
        // App Settings
        TextView btnLanguage = view.findViewById(R.id.btn_language);
        
        // About
        TextView btnRate = view.findViewById(R.id.btn_rate);
        TextView btnShare = view.findViewById(R.id.btn_share);
        TextView btnPrivacy = view.findViewById(R.id.btn_privacy);
        TextView btnTerms = view.findViewById(R.id.btn_terms);
        
        // Thiết lập trạng thái ban đầu từ preferences
        boolean callFlashEnabled = preferenceManager.getBoolean("call_flash_enabled", false);
        boolean smsFlashEnabled = preferenceManager.getBoolean("sms_flash_enabled", false);
        
        switchCallNotification.setChecked(callFlashEnabled);
        switchSmsNotification.setChecked(smsFlashEnabled);
        
        // Thiết lập listeners
        
        // Flash Notification Settings
        switchCallNotification.setOnCheckedChangeListener((buttonView, isChecked) -> {
            try {
                Log.d("SettingsFragment", "switchCallNotification changed to: " + isChecked);
                
                if (isChecked) {
                    // Kiểm tra quyền READ_PHONE_STATE khi người dùng BẬT tính năng
                    if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_PHONE_STATE) 
                            != PackageManager.PERMISSION_GRANTED) {
                        
                        // Chưa có quyền, yêu cầu quyền và đặt switch về trạng thái chưa bật
                        Log.d("SettingsFragment", "Yêu cầu quyền READ_PHONE_STATE");
                        buttonView.setChecked(false);
                        requestPermission(Manifest.permission.READ_PHONE_STATE);
                        return;
                    }
                }
                
                // Lưu trạng thái và cập nhật service
                Log.d("SettingsFragment", "Lưu trạng thái call_flash_enabled: " + isChecked);
                preferenceManager.setBoolean("call_flash_enabled", isChecked);
                
                // Gửi lệnh cho service
                if (serviceBound && notificationService != null) {
                    notificationService.setCallFlashEnabled(isChecked);
                    
                    // Hiển thị thông báo
                    Toast.makeText(
                        requireContext(),
                        isChecked ? getString(R.string.call_notification_enabled) : getString(R.string.call_notification_disabled),
                        Toast.LENGTH_SHORT
                    ).show();
                }
            } catch (Exception e) {
                Log.e("SettingsFragment", "Lỗi khi thay đổi trạng thái cuộc gọi", e);
            }
        });
        
        switchSmsNotification.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Kiểm tra quyền RECEIVE_SMS
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECEIVE_SMS) 
                        != PackageManager.PERMISSION_GRANTED) {
                    
                    // Chưa có quyền, yêu cầu quyền và đặt switch về trạng thái chưa bật
                    buttonView.setChecked(false);
                    requestPermission(Manifest.permission.RECEIVE_SMS);
                    return;
                }
            }
            
            // Lưu trạng thái và cập nhật service
            preferenceManager.setBoolean("sms_flash_enabled", isChecked);
            
            // Gửi lệnh cho service
            if (serviceBound && notificationService != null) {
                notificationService.setSmsFlashEnabled(isChecked);
                
                // Hiển thị thông báo
                Toast.makeText(requireContext(), 
                        isChecked ? getString(R.string.sms_flash_notification_enabled) : getString(R.string.sms_flash_notification_disabled), 
                        Toast.LENGTH_SHORT).show();
            }
        });
        
        // Thêm xử lý click cho RelativeLayout call notification
        layoutCallNotification.setOnClickListener(v -> {
            // Đảo ngược trạng thái của switch khi nhấn vào layout
            switchCallNotification.setChecked(!switchCallNotification.isChecked());
        });
        
        // Thêm xử lý click cho RelativeLayout sms notification
        layoutSmsNotification.setOnClickListener(v -> {
            // Đảo ngược trạng thái của switch khi nhấn vào layout
            switchSmsNotification.setChecked(!switchSmsNotification.isChecked());
        });
        
        // Ngôn ngữ
        btnLanguage.setOnClickListener(v -> {
            showLanguageSelectionDialog();
        });
        
        // About
        btnRate.setOnClickListener(v -> {
            showRatingDialog();
        });
        
        btnShare.setOnClickListener(v -> {
            shareApp();
        });
        
        btnPrivacy.setOnClickListener(v -> {
            showPrivacyPolicy(btnPrivacy);
        });
        
        btnTerms.setOnClickListener(v -> {
            showTermsOfUse(btnTerms);
        });
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        Log.d("SettingsFragment", "onRequestPermissionsResult: requestCode=" + requestCode);
        
        // Kiểm tra xem có quyền được cấp không
        boolean isGranted = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
        
        if (requestCode == 100 && permissions.length > 0 && permissions[0].equals(Manifest.permission.CAMERA)) {
            if (isGranted) {
                Toast.makeText(requireContext(), getString(R.string.camera_permission_granted), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), getString(R.string.camera_permission_denied), Toast.LENGTH_SHORT).show();
            }
        } 
        else if (requestCode == 101 && permissions.length > 0 && permissions[0].equals(Manifest.permission.READ_PHONE_STATE)) {
            if (isGranted) {
                Toast.makeText(requireContext(), getString(R.string.phone_permission_granted), Toast.LENGTH_SHORT).show();
                
                try {
                    // TỰ ĐỘNG BẬT TÍNH NĂNG
                    if (switchCallNotification != null) {
                        // Thực hiện các bước sau một cách rõ ràng và độc lập
                        // 1. Lưu trạng thái BẬT vào preferences
                        preferenceManager.setBoolean("call_flash_enabled", true);
                        
                        // 2. Bật switch trong giao diện
                        switchCallNotification.setEnabled(true);
                        switchCallNotification.setChecked(true);
                        
                        // 3. Gửi lệnh cho service
                        if (serviceBound && notificationService != null) {
                            notificationService.setCallFlashEnabled(true);
                            Log.d("SettingsFragment", "Đã bật tính năng đèn flash cho cuộc gọi");
                        } else {
                            Log.w("SettingsFragment", "Service chưa được kết nối");
                        }
                    } else {
                        Log.e("SettingsFragment", "switchCallNotification is null");
                    }
                } catch (Exception e) {
                    Log.e("SettingsFragment", "Lỗi khi bật tính năng đèn flash cho cuộc gọi", e);
                }
            } else {
                Toast.makeText(requireContext(), getString(R.string.phone_permission_denied), Toast.LENGTH_SHORT).show();
                // Đảm bảo switch ở trạng thái TẮT
                preferenceManager.setBoolean("call_flash_enabled", false);
                if (switchCallNotification != null) {
                    switchCallNotification.setChecked(false);
                }
            }
        } 
        else if (requestCode == 102 && permissions.length > 0 && permissions[0].equals(Manifest.permission.RECEIVE_SMS)) {
            if (isGranted) {
                Toast.makeText(requireContext(), getString(R.string.sms_permission_granted), Toast.LENGTH_SHORT).show();
                
                try {
                    // TỰ ĐỘNG BẬT TÍNH NĂNG
                    if (switchSmsNotification != null) {
                        // Thực hiện các bước sau một cách rõ ràng và độc lập
                        // 1. Lưu trạng thái BẬT vào preferences
                        preferenceManager.setBoolean("sms_flash_enabled", true);
                        
                        // 2. Bật switch trong giao diện
                        switchSmsNotification.setEnabled(true);
                        switchSmsNotification.setChecked(true);
                        
                        // 3. Gửi lệnh cho service
                        if (serviceBound && notificationService != null) {
                            notificationService.setSmsFlashEnabled(true);
                            Log.d("SettingsFragment", "Đã bật tính năng đèn flash cho SMS");
                        } else {
                            Log.w("SettingsFragment", "Service chưa được kết nối");
                        }
                    } else {
                        Log.e("SettingsFragment", "switchSmsNotification is null");
                    }
                } catch (Exception e) {
                    Log.e("SettingsFragment", "Lỗi khi bật tính năng đèn flash cho SMS", e);
                }
            } else {
                Toast.makeText(requireContext(), getString(R.string.sms_permission_denied), Toast.LENGTH_SHORT).show();
                // Đảm bảo switch ở trạng thái TẮT
                preferenceManager.setBoolean("sms_flash_enabled", false);
                if (switchSmsNotification != null) {
                    switchSmsNotification.setChecked(false);
                }
            }
        }
        
        // Cập nhật UI dựa trên trạng thái hiện tại
        try {
            // Đợi một chút để đảm bảo thay đổi đã được áp dụng
            getView().postDelayed(() -> {
                updateUIFromService();
            }, 100);
        } catch (Exception e) {
            Log.e("SettingsFragment", "Lỗi khi cập nhật UI", e);
            updateUIFromService();
        }
    }
    
    @Override
    public void onResume() {
        super.onResume();
        
        // Cập nhật UI dựa trên trạng thái service
        updateUIFromService();
    }
    
    private void showLanguageSelectionDialog() {
        // Sử dụng LanguageSelectionFragment thay vì dialog
        if (getActivity() instanceof com.example.flashlightai.SettingsActivity) {
            com.example.flashlightai.SettingsActivity activity = (com.example.flashlightai.SettingsActivity) getActivity();
            activity.showLanguageSelectionFragment();
        } else {
            // Fallback nếu không gọi từ SettingsActivity
            showLanguageSelectionDialogLegacy();
        }
    }
    
    /**
     * Phương thức cũ sử dụng dialog cho trường hợp không thể dùng Fragment
     */
    private void showLanguageSelectionDialogLegacy() {
        // Hiển thị một dialog đơn giản thông báo chức năng đã thay đổi
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(R.string.language);
        builder.setMessage("Tính năng chọn ngôn ngữ đã được cập nhật. Vui lòng khởi động lại ứng dụng.");
        builder.setPositiveButton(R.string.ok, (dialog, which) -> {
            dialog.dismiss();
        });
        builder.create().show();
    }
    
    private void showRatingDialog() {
        // Tạo dialog từ layout
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_rating, null);
        builder.setView(dialogView);
        
        // Lấy các controls từ layout
        RatingBar ratingBar = dialogView.findViewById(R.id.rating_bar);
        EditText editFeedback = dialogView.findViewById(R.id.edit_feedback);
        TextView txtFeedbackPrompt = dialogView.findViewById(R.id.txt_feedback_prompt);
        Button btnSubmit = dialogView.findViewById(R.id.btn_submit);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
        
        // Đặt giá trị ban đầu
        float savedRating = preferenceManager.getFloat(KEY_RATING, 0);
        if (savedRating > 0) {
            ratingBar.setRating(savedRating);
        }
        
        // Tạo và hiển thị dialog
        AlertDialog dialog = builder.create();
        dialog.show();
        
        // Xử lý sự kiện thay đổi rating
        ratingBar.setOnRatingBarChangeListener((ratingBar1, rating, fromUser) -> {
            if (fromUser) {
                if (rating <= 3) {
                    // Hiển thị ô nhập feedback
                    txtFeedbackPrompt.setVisibility(View.VISIBLE);
                    editFeedback.setVisibility(View.VISIBLE);
                } else {
                    // Ẩn ô nhập feedback
                    txtFeedbackPrompt.setVisibility(View.GONE);
                    editFeedback.setVisibility(View.GONE);
                }
            }
        });
        
        // Xử lý sự kiện nhấn nút Submit
        btnSubmit.setOnClickListener(v -> {
            float rating = ratingBar.getRating();
            if (rating == 0) {
                Toast.makeText(requireContext(), R.string.rate_no_rating, Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Lưu rating và feedback vào preferences
            preferenceManager.setFloat(KEY_RATING, rating);
            preferenceManager.setBoolean(KEY_RATED, true);
            
            if (rating <= 3) {
                // Lưu feedback
                String feedback = editFeedback.getText().toString().trim();
                preferenceManager.setString(KEY_FEEDBACK, feedback);
                Toast.makeText(requireContext(), R.string.rate_thanks_low, Toast.LENGTH_SHORT).show();
            } else {
                // Redirect to Google Play
                Toast.makeText(requireContext(), R.string.rate_thanks_high, Toast.LENGTH_SHORT).show();
                redirectToGooglePlay();
            }
            
            dialog.dismiss();
        });
        
        // Xử lý sự kiện nhấn nút Cancel
        btnCancel.setOnClickListener(v -> dialog.dismiss());
    }
    
    private void redirectToGooglePlay() {
        String packageName = requireContext().getPackageName();
        Uri uri = Uri.parse("market://details?id=" + packageName);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // Nếu không có Google Play, mở bằng browser
            Uri webUri = Uri.parse("http://play.google.com/store/apps/details?id=" + packageName);
            Intent webIntent = new Intent(Intent.ACTION_VIEW, webUri);
            startActivity(webIntent);
        }
    }
    
    private void shareApp() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
        shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_message) + "\nhttps://play.google.com/store/apps/details?id=" + requireContext().getPackageName());
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_via)));
    }
    
    private void showPrivacyPolicy(TextView btnPrivacy) {
        // Mở trang chính sách riêng tư trong trình duyệt
        String privacyPolicyUrl = btnPrivacy.getContentDescription().toString();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(privacyPolicyUrl));
        startActivity(intent);
    }
    
    private void showTermsOfUse(TextView btnTerms) {
        // Mở trang điều khoản sử dụng trong trình duyệt
        String termsOfUseUrl = btnTerms.getContentDescription().toString();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(termsOfUseUrl));
        startActivity(intent);
    }
    
    private void requestPermission(String permission) {
        if (ContextCompat.checkSelfPermission(requireContext(), permission) 
                != PackageManager.PERMISSION_GRANTED) {
            
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), permission)) {
                // Người dùng đã từ chối quyền trước đó, hiển thị giải thích
                showPermissionExplanationDialog(permission);
            } else {
                // Yêu cầu quyền
                ActivityCompat.requestPermissions(
                        requireActivity(),
                        new String[]{permission},
                        getPermissionRequestCode(permission)
                );
            }
        } else {
            // Đã có quyền, hiển thị thông báo
            Toast.makeText(requireContext(), "Quyền đã được cấp", Toast.LENGTH_SHORT).show();
        }
    }
    
    private int getPermissionRequestCode(String permission) {
        switch (permission) {
            case Manifest.permission.CAMERA:
                return 100;
            case Manifest.permission.READ_PHONE_STATE:
                return 101;
            case Manifest.permission.READ_SMS:
                return 102;
            default:
                return 0;
        }
    }
    
    private void showPermissionExplanationDialog(String permission) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(R.string.permission_required);
        builder.setMessage(R.string.permission_description);
        
        builder.setPositiveButton(R.string.go_to_settings, (dialog, which) -> {
            // Mở settings để người dùng cấp quyền
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", requireContext().getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);
        });
        
        builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

} 