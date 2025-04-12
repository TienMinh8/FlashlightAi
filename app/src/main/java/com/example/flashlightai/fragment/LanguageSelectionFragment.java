package com.example.flashlightai.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flashlightai.R;
import com.example.flashlightai.adapter.LanguageAdapter;
import com.example.flashlightai.model.Language;
import com.example.flashlightai.utils.LanguageManager;
import com.example.flashlightai.utils.PreferenceManager;
import com.example.flashlightai.FlashLightApp;

import java.util.List;
import java.util.Locale;

/**
 * Fragment hiển thị giao diện chọn ngôn ngữ
 * Có thể được sử dụng ở bất kỳ nơi nào trong ứng dụng
 */
public class LanguageSelectionFragment extends Fragment {

    private LanguageAdapter adapter;
    private Language selectedLanguage = null;
    private LanguageManager languageManager;
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
        
        // Khởi tạo LanguageManager
        languageManager = new LanguageManager(requireContext());
        
        // Lấy danh sách ngôn ngữ
        List<Language> languages = languageManager.getLanguages();
        
        // Tìm ngôn ngữ mặc định (cùng với ngôn ngữ hệ thống hoặc ngôn ngữ đã được chọn trước đó)
        String currentLanguageCode = languageManager.getCurrentLanguageCode();
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
        
        // Thiết lập callback khi chọn ngôn ngữ để hiển thị dấu tích
        adapter.setOnLanguageSelectedListener((language, position) -> {
            // Language đã được chọn, UI đã được cập nhật trong adapter
            // Lưu language đã chọn
            selectedLanguage = language;
            
            // Không hiển thị Toast thông báo khi chọn ngôn ngữ nữa
            // Chỉ hiển thị thông báo khi nhấn nút Confirm
        });
        
        // Lấy nút Apply
        Button btnApply = view.findViewById(R.id.btn_continue);
        
        // Xử lý sự kiện khi nhấn nút Apply
        btnApply.setOnClickListener(v -> {
            if (selectedLanguage == null) {
                // Chưa chọn ngôn ngữ, hiển thị thông báo
                Toast.makeText(requireContext(), R.string.select_your_language, Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Áp dụng ngôn ngữ mới
            languageManager.setLanguage(selectedLanguage.getCode());
            
            // Thông báo cho người dùng
            Toast.makeText(requireContext(), R.string.language_changed, Toast.LENGTH_SHORT).show();
            
            // Thông báo cho Activity/Fragment cha
            if (listener != null) {
                listener.onLanguageSelected(selectedLanguage.getCode());
            }
            
            // Đảm bảo áp dụng ngôn ngữ trên toàn ứng dụng
            try {
                FlashLightApp app = (FlashLightApp) requireActivity().getApplication();
                app.restartApp();
            } catch (Exception e) {
                // Fallback nếu không thể khởi động lại qua FlashLightApp
                requireActivity().recreate();
            }
        });
        
        return view;
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