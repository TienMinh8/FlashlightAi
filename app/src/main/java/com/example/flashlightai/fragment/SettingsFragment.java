package com.example.flashlightai.fragment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import androidx.fragment.app.Fragment;

import com.example.flashlightai.LanguageActivity;
import com.example.flashlightai.R;
import com.example.flashlightai.utils.PreferenceManager;

/**
 * Fragment hiển thị các thiết lập ứng dụng
 */
public class SettingsFragment extends Fragment {
    
    private PreferenceManager preferenceManager;
    private static final String KEY_RATED = "app_rated";
    private static final String KEY_RATING = "app_rating";
    private static final String KEY_FEEDBACK = "app_feedback";

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
        
        // Thiết lập controls và listeners cho trang cài đặt
        setupSettingsControls(view);
    }
    
    private void setupSettingsControls(View view) {
        // Notification Settings
        SwitchCompat switchCallNotification = view.findViewById(R.id.switch_call_notification);
        SwitchCompat switchSmsNotification = view.findViewById(R.id.switch_sms_notification);
        
        // App Settings
        SwitchCompat switchDarkMode = view.findViewById(R.id.switch_dark_mode);
        TextView btnLanguage = view.findViewById(R.id.btn_language);
        
        // About
        TextView btnRate = view.findViewById(R.id.btn_rate);
        TextView btnShare = view.findViewById(R.id.btn_share);
        TextView btnPrivacy = view.findViewById(R.id.btn_privacy);
        TextView btnTerms = view.findViewById(R.id.btn_terms);
        
        // Thiết lập trạng thái ban đầu từ preferences
        switchCallNotification.setChecked(preferenceManager.getBoolean("call_notification", false));
        switchSmsNotification.setChecked(preferenceManager.getBoolean("sms_notification", false));
        switchDarkMode.setChecked(preferenceManager.getBoolean("dark_mode", false));
        
        // Thiết lập listeners
        
        // Notification Settings
        switchCallNotification.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferenceManager.setBoolean("call_notification", isChecked);
        });
        
        switchSmsNotification.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferenceManager.setBoolean("sms_notification", isChecked);
        });
        
        // App Settings
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferenceManager.setBoolean("dark_mode", isChecked);
            // TODO: Áp dụng chế độ tối nếu cần
        });
        
        // Ngôn ngữ
        btnLanguage.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), LanguageActivity.class);
            startActivity(intent);
        });
        
        // About
        btnRate.setOnClickListener(v -> {
            showRatingDialog();
        });
        
        btnShare.setOnClickListener(v -> {
            shareApp();
        });
        
        btnPrivacy.setOnClickListener(v -> {
            showPrivacyPolicy();
        });
        
        btnTerms.setOnClickListener(v -> {
            showTermsOfUse();
        });
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
    
    private void showPrivacyPolicy() {
        // Mở trang chính sách riêng tư trong trình duyệt
        Uri uri = Uri.parse("https://www.privacypolicygenerator.info/live.php?token=YourPrivacyPolicyToken");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
    
    private void showTermsOfUse() {
        // Mở trang điều khoản sử dụng trong trình duyệt
        Uri uri = Uri.parse("https://www.termsofusegenerator.net/live.php?token=YourTermsOfUseToken");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
} 