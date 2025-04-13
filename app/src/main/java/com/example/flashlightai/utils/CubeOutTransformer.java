package com.example.flashlightai.utils;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

/**
 * Hiệu ứng chuyển trang 3D kiểu hình khối
 */
public class CubeOutTransformer implements ViewPager2.PageTransformer {
    
    private static final float ROTATION_THRESHOLD = 90.0f;

    @Override
    public void transformPage(@NonNull View page, float position) {
        // Ẩn trang khi ngoài màn hình
        if (position < -1 || position > 1) {
            page.setAlpha(0);
            return;
        }
        
        // Tăng độ trong suốt khi trang chuyển
        page.setAlpha(1 - Math.abs(position));
        
        // Thiết lập điểm xoay ở trung tâm trang và phía bên phải/trái
        page.setPivotX(position < 0 ? page.getWidth() : 0);
        page.setPivotY(page.getHeight() / 2f);
        
        // Hiệu ứng xoay quanh trục Y
        float rotationY = Math.max(-ROTATION_THRESHOLD, Math.min(ROTATION_THRESHOLD, position * -ROTATION_THRESHOLD));
        page.setRotationY(rotationY);
        
        // Tạo hiệu ứng độ sâu (Z)
        float absPosition = Math.abs(position);
        page.setTranslationZ(-absPosition * 5);
        
        // Hiệu ứng thu nhỏ nhẹ
        float scale = Math.max(0.8f, 1 - absPosition * 0.2f);
        page.setScaleX(scale);
        page.setScaleY(scale);
    }
} 