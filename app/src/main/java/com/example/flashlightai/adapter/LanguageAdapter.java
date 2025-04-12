package com.example.flashlightai.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flashlightai.R;
import com.example.flashlightai.model.Language;

import java.util.List;

/**
 * Adapter cho RecyclerView hiển thị danh sách ngôn ngữ
 */
public class LanguageAdapter extends RecyclerView.Adapter<LanguageAdapter.LanguageViewHolder> {

    private List<Language> languages;
    private OnLanguageSelectedListener onLanguageSelectedListener;

    public LanguageAdapter(List<Language> languages) {
        this.languages = languages;
    }

    public interface OnLanguageSelectedListener {
        void onLanguageSelected(Language language, int position);
    }

    public void setOnLanguageSelectedListener(OnLanguageSelectedListener listener) {
        this.onLanguageSelectedListener = listener;
    }

    @NonNull
    @Override
    public LanguageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_language, parent, false);
        return new LanguageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LanguageViewHolder holder, int position) {
        Language language = languages.get(position);
        holder.textLanguageName.setText(language.getName());
        holder.imageLanguageFlag.setImageResource(language.getFlagResourceId());
        
        // Hoàn toàn ẩn RadioButton
        holder.radioLanguage.setVisibility(View.GONE);
        
        // Hiển thị checkmark dựa trên trạng thái đã chọn
        if (language.isSelected()) {
            holder.imageCheckmark.setVisibility(View.VISIBLE);
        } else {
            holder.imageCheckmark.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            // Bỏ chọn tất cả các ngôn ngữ khác
            for (Language lang : languages) {
                lang.setSelected(false);
            }
            
            // Chọn ngôn ngữ hiện tại
            language.setSelected(true);
            
            // Thông báo cho adapter cập nhật tất cả các view
            notifyDataSetChanged();
            
            // Gọi callback khi ngôn ngữ được chọn
            if (onLanguageSelectedListener != null) {
                onLanguageSelectedListener.onLanguageSelected(language, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return languages.size();
    }

    public int getSelectedPosition() {
        for (int i = 0; i < languages.size(); i++) {
            if (languages.get(i).isSelected()) {
                return i;
            }
        }
        return -1;
    }

    public Language getSelectedLanguage() {
        for (Language language : languages) {
            if (language.isSelected()) {
                return language;
            }
        }
        return null;
    }

    static class LanguageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageLanguageFlag;
        TextView textLanguageName;
        RadioButton radioLanguage;
        ImageView imageCheckmark;

        LanguageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageLanguageFlag = itemView.findViewById(R.id.img_flag);
            textLanguageName = itemView.findViewById(R.id.txt_language_name);
            radioLanguage = itemView.findViewById(R.id.radio_select);
            imageCheckmark = itemView.findViewById(R.id.img_check);
        }
    }
} 