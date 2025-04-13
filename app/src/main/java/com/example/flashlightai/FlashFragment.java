package com.example.flashlightai;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * Lớp chuyển tiếp (forward) đến FlashFragment thực sự trong package fragment.
 * Điều này giúp giải quyết vấn đề "cannot find symbol loadFragment(new FlashFragment());"
 */
public class FlashFragment extends Fragment {
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Tạo instance của FlashFragment chính thức
        com.example.flashlightai.fragment.FlashFragment realFragment = new com.example.flashlightai.fragment.FlashFragment();
        
        // Thay thế fragment hiện tại bằng fragment thực
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, realFragment)
                    .commit();
        }
        
        // Trả về một view trống vì fragment này sẽ bị thay thế ngay lập tức
        return new View(getContext());
    }
} 