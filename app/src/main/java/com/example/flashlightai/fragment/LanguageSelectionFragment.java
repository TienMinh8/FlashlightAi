package com.example.flashlightai.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flashlightai.FlashLightApp;
import com.example.flashlightai.R;
import com.example.flashlightai.adapter.LanguageAdapter;
import com.example.flashlightai.model.Language;
import com.example.flashlightai.utils.AdManager;
import com.example.flashlightai.utils.LanguageManager;

import java.util.List;

/**
 * Fragment hiển thị giao diện chọn ngôn ngữ
 * Có thể được sử dụng ở bất kỳ nơi nào trong ứng dụng
 */
public class LanguageSelectionFragment extends Fragment {

    private static final String TAG = "LanguageFragment";
    private LanguageAdapter adapter;
    private Language selectedLanguage = null;
    private LanguageManager languageManager;
    private AdManager adManager;
    private OnLanguageSelectedListener listener;

    /**
     * Interface để giao tiếp với Activity/Fragment cha
     */
    public interface OnLanguageSelectedListener {
        void onLanguageSelected(String languageCode);
        void onLanguageSelectionCancelled();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Kiểm tra xem Activity/Fragment cha có implement OnLanguageSelectedListener không
        try {
            listener = (OnLanguageSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " phải implement OnLanguageSelectedListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_language_selection, container, false);
        
        // Khởi tạo LanguageManager và AdManager
        languageManager = new LanguageManager(requireContext());
        adManager = AdManager.getInstance(requireContext());
        
        Log.d(TAG, "Creating language selection fragment");
        
        // Lấy danh sách ngôn ngữ
        List<Language> languages = languageManager.getLanguages();
        
        // Tìm ngôn ngữ hiện tại
        String currentLanguageCode = languageManager.getCurrentLanguageCode();
        Log.d(TAG, "Current language code: " + currentLanguageCode);
        
        for (Language language : languages) {
            if (language.getCode().equals(currentLanguageCode)) {
                selectedLanguage = language;
                language.setSelected(true);
                break;
            }
        }
        
        // Thiết lập RecyclerView
        RecyclerView recyclerLanguages = view.findViewById(R.id.recycler_languages);
        adapter = new LanguageAdapter(languages);
        recyclerLanguages.setAdapter(adapter);
        
        // Thiết lập callback khi chọn ngôn ngữ
        adapter.setOnLanguageSelectedListener((language, position) -> {
            // Đánh dấu ngôn ngữ đã chọn
            selectedLanguage = language;
            Log.d(TAG, "Selected language: " + language.getCode());
        });
        
        // Lấy nút Áp dụng
        Button btnApply = view.findViewById(R.id.btn_continue);
        
        // Nút Hủy (nếu có)
        Button btnCancel = view.findViewById(R.id.btn_cancel);
        if (btnCancel != null) {
            btnCancel.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onLanguageSelectionCancelled();
                }
            });
        }
        
        // Xử lý khi nhấn nút Áp dụng
        btnApply.setOnClickListener(v -> {
            if (selectedLanguage == null) {
                // Chưa chọn ngôn ngữ
                Toast.makeText(requireContext(), R.string.select_your_language, Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Lấy mã ngôn ngữ đã chọn
            String languageCode = selectedLanguage.getCode();
            Log.d(TAG, "Applying language: " + languageCode);
            
            // Thông báo cho Activity xử lý
            if (listener != null) {
                listener.onLanguageSelected(languageCode);
            }
        });
        
        // Tải quảng cáo banner lớn
        loadLargeBannerAd(view);
        
        return view;
    }
    
    /**
     * Tải và hiển thị quảng cáo banner lớn
     */
    private void loadLargeBannerAd(View view) {
        if (adManager != null && isAdded()) {
            FrameLayout adContainer = view.findViewById(R.id.language_ad_container);
            if (adContainer != null) {
                adManager.showLargeBannerAd(adContainer);
            }
        }
    }
    
    @Override
    public void onDetach() {
        super.onDetach();
        // Xóa listener khi Fragment bị tách ra
        listener = null;
    }
    
    /**
     * Tạo instance mới của Fragment
     */
    public static LanguageSelectionFragment newInstance() {
        return new LanguageSelectionFragment();
    }
} 