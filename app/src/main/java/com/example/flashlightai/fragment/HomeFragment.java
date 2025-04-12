package com.example.flashlightai.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.flashlightai.R;
import com.example.flashlightai.screen.ScreenLightActivity;
import com.example.flashlightai.textlight.TextLightActivity;

public class HomeFragment extends Fragment {
    
    private FrameLayout cardFlash;
    private LinearLayout cardScreen;
    private LinearLayout cardText;
    private LinearLayout cardNotifications;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Sử dụng layout riêng cho fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Khởi tạo view
        initViews(view);
        
        // Thiết lập listeners
        setupListeners();
    }
    
    private void initViews(View view) {
        try {
            cardFlash = view.findViewById(R.id.card_flash);
            cardScreen = view.findViewById(R.id.card_screen);
            cardText = view.findViewById(R.id.card_text);
            cardNotifications = view.findViewById(R.id.card_notifications);
        } catch (Exception e) {
            // Xử lý lỗi để tránh crash
            Toast.makeText(getContext(), "Lỗi khởi tạo giao diện: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    private void setupListeners() {
        // Flash card click
        setupFlashCardListener();
        
        // Screen light card click
        setupScreenCardListener();
        
        // Text light card click
        setupTextCardListener();
        
        // Notifications card click
        setupNotificationsCardListener();
    }
    
    private void setupFlashCardListener() {
        if (cardFlash == null) return;
        
        cardFlash.setOnClickListener(v -> {
            if (getActivity() == null) return;
            
            try {
                FlashFragment flashFragment = new FlashFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, flashFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .addToBackStack(null)
                        .commit();
                
                // Chọn tab Flash nếu có bottom navigation
                View navFlash = getActivity().findViewById(R.id.navigation_flash);
                if (navFlash != null) {
                    navFlash.performClick();
                }
            } catch (Exception e) {
                Toast.makeText(getContext(), "Không thể chuyển đến chế độ đèn pin", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void setupScreenCardListener() {
        if (cardScreen == null) return;
        
        cardScreen.setOnClickListener(v -> {
            if (getContext() == null) return;
            
            try {
                Intent intent = new Intent(getContext(), ScreenLightActivity.class);
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(getContext(), "Không thể mở chế độ đèn màn hình", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void setupTextCardListener() {
        if (cardText == null) return;
        
        cardText.setOnClickListener(v -> {
            if (getContext() == null) return;
            
            try {
                Intent intent = new Intent(getContext(), TextLightActivity.class);
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(getContext(), "Không thể mở chế độ đèn văn bản", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void setupNotificationsCardListener() {
        if (cardNotifications == null) return;
        
        cardNotifications.setOnClickListener(v -> {
            if (getActivity() == null) return;
            
            try {
                SettingsFragment settingsFragment = new SettingsFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, settingsFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .addToBackStack(null)
                        .commit();
                
                // Chọn tab Settings nếu có bottom navigation
                View navSettings = getActivity().findViewById(R.id.navigation_settings);
                if (navSettings != null) {
                    navSettings.performClick();
                }
            } catch (Exception e) {
                Toast.makeText(getContext(), "Không thể chuyển đến cài đặt thông báo", Toast.LENGTH_SHORT).show();
            }
        });
    }
} 