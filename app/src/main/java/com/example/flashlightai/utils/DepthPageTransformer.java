package com.example.flashlightai.utils;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

/**
 * Hiệu ứng chuyển trang với độ sâu, trang hiện tại lớn hơn và trang tiếp theo như nằm phía sau
 */
public class DepthPageTransformer implements ViewPager2.PageTransformer {
    private static final float MIN_SCALE = 0.75f;

    @Override
    public void transformPage(@NonNull View page, float position) {
        int pageWidth = page.getWidth();

        if (position < -1) { // [-Infinity,-1)
            // Trang ở ngoài màn hình bên trái
            page.setAlpha(0f);

        } else if (position <= 0) { // [-1,0]
            // Trang đang di chuyển ra ngoài màn hình bên trái
            page.setAlpha(1f);
            page.setTranslationX(0f);
            page.setTranslationZ(0f);
            page.setScaleX(1f);
            page.setScaleY(1f);

        } else if (position <= 1) { // (0,1]
            // Trang đang di chuyển vào từ bên phải
            
            // Làm mờ trang khi di chuyển
            page.setAlpha(1f - position);

            // Phản ứng với việc cuộn
            page.setTranslationX(pageWidth * -position);
            // Di chuyển trang về phía sau (Z) khi di chuyển
            page.setTranslationZ(-1f * position);

            // Thu nhỏ trang
            float scaleFactor = MIN_SCALE + (1 - MIN_SCALE) * (1 - Math.abs(position));
            page.setScaleX(scaleFactor);
            page.setScaleY(scaleFactor);

        } else { // (1,+Infinity]
            // Trang ở ngoài màn hình bên phải
            page.setAlpha(0f);
        }
    }
} 