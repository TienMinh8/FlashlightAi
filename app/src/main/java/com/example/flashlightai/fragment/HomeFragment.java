package com.example.flashlightai.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.flashlightai.R;
import com.example.flashlightai.screen.ScreenLightActivity;
import com.example.flashlightai.textlight.TextLightActivity;

public class HomeFragment extends Fragment {
    
    private CardView cardFlash;
    private CardView cardScreen;
    private CardView cardText;
    private CardView cardNotifications;

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
        cardFlash = view.findViewById(R.id.card_flash);
        cardScreen = view.findViewById(R.id.card_screen);
        cardText = view.findViewById(R.id.card_text);
        cardNotifications = view.findViewById(R.id.card_notifications);
    }
    
    private void setupListeners() {
        // Flash card click
        cardFlash.setOnClickListener(v -> {
            // Chuyển đến FlashFragment
            if (getActivity() != null) {
                FlashFragment flashFragment = new FlashFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, flashFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .addToBackStack(null)
                        .commit();
                // Chọn tab Flash nếu có bottom navigation
                if (getActivity().findViewById(R.id.bottom_navigation) != null) {
                    getActivity().findViewById(R.id.navigation_flash).performClick();
                }
            }
        });
        
        // Screen light card click
        cardScreen.setOnClickListener(v -> {
            // Mở ScreenLightActivity
            Intent intent = new Intent(getContext(), ScreenLightActivity.class);
            startActivity(intent);
        });
        
        // Text light card click
        cardText.setOnClickListener(v -> {
            // Mở TextLightActivity
            Intent intent = new Intent(getContext(), TextLightActivity.class);
            startActivity(intent);
        });
        
        // Notifications card click
        cardNotifications.setOnClickListener(v -> {
            // Chuyển đến SettingsFragment để cấu hình thông báo
            if (getActivity() != null) {
                SettingsFragment settingsFragment = new SettingsFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, settingsFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .addToBackStack(null)
                        .commit();
                // Chọn tab Settings nếu có bottom navigation
                if (getActivity().findViewById(R.id.bottom_navigation) != null) {
                    getActivity().findViewById(R.id.navigation_settings).performClick();
                }
            }
        });
    }
} 