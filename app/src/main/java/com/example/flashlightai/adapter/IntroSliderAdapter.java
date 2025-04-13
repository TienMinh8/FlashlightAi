package com.example.flashlightai.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flashlightai.R;
import com.example.flashlightai.model.IntroSlide;
import com.example.flashlightai.utils.AdManager;

import java.util.List;

public class IntroSliderAdapter extends RecyclerView.Adapter<IntroSliderAdapter.IntroSlideViewHolder> {

    private List<IntroSlide> introSlides;
    private OnButtonClickListener buttonClickListener;
    private AdManager adManager;

    public IntroSliderAdapter(List<IntroSlide> introSlides, AdManager adManager) {
        this.introSlides = introSlides;
        this.adManager = adManager;
    }

    @NonNull
    @Override
    public IntroSlideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new IntroSlideViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_intro_slide, parent, false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull IntroSlideViewHolder holder, int position) {
        holder.bind(introSlides.get(position));
        
        // Cập nhật text của nút Next thành "Finish" khi đến trang cuối cùng
        if (position == getItemCount() - 1) {
            holder.nextButton.setText(holder.itemView.getContext().getString(R.string.intro_finish));
        } else {
            holder.nextButton.setText(holder.itemView.getContext().getString(R.string.intro_next));
        }
        
        // Thiết lập sự kiện click cho nút Next/Finish
        holder.nextButton.setOnClickListener(v -> {
            if (buttonClickListener != null) {
                buttonClickListener.onNextClick(position);
            }
        });
        
        // Thiết lập indicators
        setupIndicators(holder, position);
        
        // Hiển thị quảng cáo trong mỗi slide
        showAdInSlide(holder, position);
    }
    
    /**
     * Hiển thị quảng cáo khác nhau trong mỗi slide
     */
    private void showAdInSlide(IntroSlideViewHolder holder, int position) {
        if (adManager == null || holder.adContainer == null) return;
        
        // Xóa quảng cáo cũ nếu có
        holder.adContainer.removeAllViews();
        
        // Hiển thị quảng cáo banner lớn (MEDIUM_RECTANGLE) như trong màn hình chọn ngôn ngữ
        adManager.showLargeBannerAd(holder.adContainer);
    }

    @Override
    public int getItemCount() {
        return introSlides.size();
    }
    
    // Interface lắng nghe sự kiện click nút Next/Finish
    public interface OnButtonClickListener {
        void onNextClick(int position);
    }
    
    // Thiết lập listener
    public void setOnButtonClickListener(OnButtonClickListener listener) {
        this.buttonClickListener = listener;
    }
    
    // Thiết lập indicators
    private void setupIndicators(IntroSlideViewHolder holder, int currentPosition) {
        LinearLayout indicatorContainer = holder.indicatorContainer;
        Context context = holder.itemView.getContext();
        
        // Xóa tất cả indicators cũ
        indicatorContainer.removeAllViews();
        
        // Tạo mới các indicators
        ImageView[] indicators = new ImageView[getItemCount()];
        
        // Thiết lập các thông số layout cho các indicators
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(8, 0, 8, 0);
        
        // Khởi tạo các indicators
        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new ImageView(context);
            indicators[i].setImageDrawable(ContextCompat.getDrawable(
                    context,
                    i == currentPosition ? 
                        R.drawable.intro_indicator_active : 
                        R.drawable.intro_indicator_inactive
            ));
            indicators[i].setLayoutParams(layoutParams);
            indicatorContainer.addView(indicators[i]);
        }
    }

    static class IntroSlideViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView titleTextView;
        private TextView descriptionTextView;
        private TextView nextButton;
        private LinearLayout indicatorContainer;
        private FrameLayout adContainer;

        IntroSlideViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.intro_image);
            titleTextView = itemView.findViewById(R.id.intro_title);
            descriptionTextView = itemView.findViewById(R.id.intro_description);
            nextButton = itemView.findViewById(R.id.intro_btn_next);
            indicatorContainer = itemView.findViewById(R.id.intro_indicator_container);
            adContainer = itemView.findViewById(R.id.slide_ad_container);
        }

        void bind(IntroSlide introSlide) {
            imageView.setImageResource(introSlide.getImageResId());
            titleTextView.setText(introSlide.getTitle());
            descriptionTextView.setText(introSlide.getDescription());
        }
    }
} 