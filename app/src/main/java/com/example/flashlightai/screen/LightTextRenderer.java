package com.example.flashlightai.screen;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.BlurMaskFilter;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Hiển thị và quản lý văn bản phát sáng trên màn hình
 */
public class LightTextRenderer {
    private static final String TAG = "LightTextRenderer";
    
    // Các loại hiệu ứng text
    public enum TextEffect {
        NONE,       // Không hiệu ứng
        GLOW,       // Phát sáng
        WAVE,       // Chuyển động sóng
        RAINBOW,    // Cầu vồng
        SCROLL_DOWN // Chữ chạy từ dưới lên
    }
    
    private final View targetView;
    private final Paint textPaint;
    private final Paint glowPaint;
    private final Handler handler;
    
    private String text = "";
    private int textColor = Color.WHITE;
    private int glowColor = Color.YELLOW;
    private float textSize = 50;
    private boolean autoScroll = false;
    private float scrollPosition = 0;
    private TextEffect currentEffect = TextEffect.NONE;
    private boolean isVisible = false;
    
    private ValueAnimator animator;
    private float animatedValue = 0f;
    
    /**
     * Constructor với view để vẽ text
     */
    public LightTextRenderer(View targetView) {
        this.targetView = targetView;
        this.handler = new Handler(Looper.getMainLooper());
        
        // Khởi tạo paint cho text thường
        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(textSize);
        textPaint.setColor(textColor);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);
        
        // Khởi tạo paint cho hiệu ứng phát sáng
        glowPaint = new Paint(textPaint);
        glowPaint.setColor(glowColor);
        glowPaint.setMaskFilter(new BlurMaskFilter(20, BlurMaskFilter.Blur.NORMAL));
    }

/**
 * Vẽ text lên canvas
 * @param canvas Canvas để vẽ
 */
public void draw(Canvas canvas) {
    if (canvas == null) {
        Log.e(TAG, "Canvas is null in draw method");
        return;
    }
    
    if (!isVisible) {
        Log.d(TAG, "Text is not visible, skipping draw");
        return;
    }
    
    if (text == null || text.isEmpty()) {
        Log.d(TAG, "Text is empty, displaying placeholder");
        text = "Chạm để nhập văn bản...";
    }
    
    Log.d(TAG, "Drawing text: " + text + " with effect: " + currentEffect);
    
    int width = targetView.getWidth();
    int height = targetView.getHeight();
    
    // Vị trí vẽ text
    float x = width / 2f;
    float y = height / 2f;
    
    // Điều chỉnh vị trí y để text nằm giữa màn hình
    Rect textBounds = new Rect();
    textPaint.getTextBounds(text, 0, text.length(), textBounds);
    y = (height + textBounds.height()) / 2f;
    
    // Đảm bảo paint đã được thiết lập đúng
    textPaint.setTextSize(textSize);
    textPaint.setColor(textColor);
    glowPaint.setTextSize(textSize);
    glowPaint.setColor(glowColor);
    
    // Vẽ theo hiệu ứng hiện tại
    try {
        switch (currentEffect) {
            case NONE:
                drawStaticText(canvas, text);
                break;
            case GLOW:
                drawGlowText(canvas, x, y);
                break;
            case WAVE:
                drawWaveText(canvas, x, y);
                break;
            case RAINBOW:
                drawRainbowText(canvas, x, y);
                break;
            case SCROLL_DOWN:
                drawScrollDownText(canvas, x, y);
                break;
            default:
                // Nếu không xác định được hiệu ứng, vẽ text bình thường
                canvas.drawText(text, x, y, textPaint);
                Log.d(TAG, "Drawing with default method at x=" + x + ", y=" + y);
                break;
        }
    } catch (Exception e) {
        Log.e(TAG, "Error drawing text: " + e.getMessage());
        // Vẽ text đơn giản để không bị lỗi
        canvas.drawText(text, x, y, textPaint);
    }
}

/**
 * Vẽ text với hiệu ứng chạy từ dưới lên trên
 */
private void drawScrollDownText(Canvas canvas, float x, float y) {
    // Hiệu ứng scroll được xử lý bởi thay đổi vị trí y
    float actualY = y - scrollPosition;
    canvas.drawText(text, x, actualY, glowPaint);
    canvas.drawText(text, x, actualY, textPaint);
}

/**
 * Vẽ text bình thường
 */
private void drawStaticText(Canvas canvas, String text) {
    canvas.drawText(text, targetView.getWidth() / 2f, targetView.getHeight() / 2f, textPaint);
}

/**
 * Vẽ text với hiệu ứng phát sáng
 */
private void drawGlowText(Canvas canvas, float x, float y) {
    canvas.drawText(text, x, y, glowPaint);
    canvas.drawText(text, x, y, textPaint);
}

/**
 * Vẽ text với hiệu ứng sóng
 */
private void drawWaveText(Canvas canvas, float x, float y) {
    float phase = 0;
    if (animator != null) {
        phase = (float) animator.getAnimatedValue();
    }
    
    // Lưu trạng thái canvas
    canvas.save();
    
    for (int i = 0; i < text.length(); i++) {
        char c = text.charAt(i);
        
        // Tính vị trí y dựa trên sin wave
        float offset = (float) Math.sin((i * 0.5f + phase * 10) * Math.PI) * 15;
        
        // Vẽ từng ký tự với vị trí y dao động
        String charStr = String.valueOf(c);
        float charWidth = textPaint.measureText(charStr);
        
        // Tính vị trí x của ký tự
        float posX = x - textPaint.measureText(text) / 2 + getTextWidthUntil(i);
        
        // Vẽ ký tự với hiệu ứng glow
        canvas.drawText(charStr, posX, y + offset, glowPaint);
        canvas.drawText(charStr, posX, y + offset, textPaint);
    }
    
    // Khôi phục trạng thái canvas
    canvas.restore();
}

/**
 * Helper: Lấy độ rộng text đến vị trí index
 */
private float getTextWidthUntil(int index) {
    if (index <= 0) return 0;
    return textPaint.measureText(text.substring(0, index));
}

/**
 * Vẽ text với hiệu ứng cầu vồng
 */
private void drawRainbowText(Canvas canvas, float x, float y) {
    float phase = 0;
    if (animator != null) {
        phase = (float) animator.getAnimatedValue();
    }
    
    // Lưu trạng thái canvas
    canvas.save();
    
    int[] rainbow = {
        Color.RED, Color.rgb(255, 165, 0), Color.YELLOW, 
        Color.GREEN, Color.BLUE, Color.rgb(75, 0, 130), Color.rgb(238, 130, 238)
    };
    
    for (int i = 0; i < text.length(); i++) {
        char c = text.charAt(i);
        
        // Chọn màu từ cầu vồng, dịch chuyển theo thời gian
        int colorIndex = (i + (int)(phase * 10)) % rainbow.length;
        int color = rainbow[colorIndex];
        
        // Cài đặt màu cho text và glow
        textPaint.setColor(color);
        glowPaint.setColor(color);
        
        // Vẽ từng ký tự với màu riêng
        String charStr = String.valueOf(c);
        float charWidth = textPaint.measureText(charStr);
        
        // Tính vị trí x của ký tự
        float posX = x - textPaint.measureText(text) / 2 + getTextWidthUntil(i);
        
        // Vẽ ký tự với hiệu ứng glow
        canvas.drawText(charStr, posX, y, glowPaint);
        canvas.drawText(charStr, posX, y, textPaint);
    }
    
    // Khôi phục màu mặc định
    textPaint.setColor(textColor);
    glowPaint.setColor(glowColor);
    
    // Khôi phục trạng thái canvas
    canvas.restore();
}

/**
 * Bắt đầu hiệu ứng animation
 */
public void startAnimation() {
    Log.d(TAG, "startAnimation called with effect: " + currentEffect);
    
    // Dừng animation hiện tại nếu có
    stopAnimation();
    
    // Đảm bảo text hiển thị
    if (!isVisible) {
        Log.w(TAG, "Text was not visible, forcing to visible in startAnimation");
        isVisible = true;
    }
    
    // Khởi tạo animator phù hợp với hiệu ứng
    try {
        switch (currentEffect) {
            case WAVE:
            case RAINBOW:
                Log.d(TAG, "Starting WAVE/RAINBOW animation");
                animator = ValueAnimator.ofFloat(0f, 1f);
                animator.setDuration(1000);
                animator.setRepeatCount(ValueAnimator.INFINITE);
                animator.setRepeatMode(ValueAnimator.RESTART);
                animator.setInterpolator(new LinearInterpolator());
                animator.addUpdateListener(animation -> targetView.invalidate());
                break;
                
            case GLOW:
                Log.d(TAG, "Starting GLOW animation");
                animator = ValueAnimator.ofFloat(0f, 1f);
                animator.setDuration(1000);
                animator.setRepeatCount(ValueAnimator.INFINITE);
                animator.setRepeatMode(ValueAnimator.REVERSE);
                animator.addUpdateListener(animation -> {
                    float value = (float) animation.getAnimatedValue();
                    glowPaint.setMaskFilter(new BlurMaskFilter(10 + 15 * value, BlurMaskFilter.Blur.NORMAL));
                    targetView.invalidate();
                });
                break;
                
            case SCROLL_DOWN:
                Log.d(TAG, "Starting SCROLL_DOWN animation");
                // Bắt đầu hiệu ứng chạy từ dưới lên
                startScrollDownAnimation();
                break;
                
            case NONE:
            default:
                Log.d(TAG, "No special animation, using static text");
                animatedValue = 1f;
                targetView.invalidate();
                break;
        }
        
        // Nếu có animator, bắt đầu
        if (animator != null) {
            animator.start();
            Log.d(TAG, "Animation started successfully");
        }
        
        // Bắt đầu auto-scroll nếu được bật
        if (autoScroll) {
            startAutoScroll();
        }
    } catch (Exception e) {
        Log.e(TAG, "Error starting animation: " + e.getMessage());
        e.printStackTrace();
        
        // Đảm bảo text vẫn hiển thị ngay cả khi animation lỗi
        animatedValue = 1f;
        targetView.invalidate();
    }
}

/**
 * Bắt đầu hiệu ứng chữ chạy từ dưới lên
 */
private void startScrollDownAnimation() {
    // Bắt đầu gần hơn
    scrollPosition = -targetView.getHeight() * 0.5f;
    
    // Runnable để cập nhật vị trí scroll
    Runnable scrollDownRunnable = new Runnable() {
        @Override
        public void run() {
            if (animatedValue == 0f) return;
            
            // Cập nhật vị trí scroll với tốc độ nhanh hơn
            scrollPosition += 5; // Tăng tốc độ
            
            // Khi chữ đã đi qua hết màn hình
            if (scrollPosition > targetView.getHeight() * 2) {
                // Reset về vị trí bắt đầu
                scrollPosition = -targetView.getHeight() * 0.5f;
            }
            
            // Yêu cầu vẽ lại
            targetView.invalidate();
            
            // Tăng tốc độ refresh
            handler.postDelayed(this, 10);
        }
    };
    
    // Bắt đầu hiệu ứng
    handler.post(scrollDownRunnable);
    animatedValue = 1f;
}

/**
 * Lấy nội dung văn bản hiện tại
 * @return Nội dung văn bản
 */
public String getText() {
    return text;
}

/**
 * Kiểm tra xem văn bản có đang được hiển thị không
 * @return true nếu văn bản đang hiển thị, false nếu không
 */
public boolean isTextVisible() {
    return isVisible;
}

/**
 * Dừng animation hiện tại
 */
public void stopAnimation() {
    animatedValue = 0f;
    
    if (animator != null) {
        animator.cancel();
        animator = null;
    }
    
    // Đảm bảo handler không còn callback nào đang chạy
    handler.removeCallbacksAndMessages(null);
}

/**
 * Bắt đầu auto-scroll
 */
private void startAutoScroll() {
    // Runnable để cập nhật vị trí scroll
    Runnable autoScrollRunnable = new Runnable() {
        @Override
        public void run() {
            if (animatedValue == 0f || !autoScroll) return;
            
            // Cập nhật vị trí scroll
            scrollPosition += 3; // Tăng tốc độ từ 1 lên 3
            
            // Yêu cầu vẽ lại
            targetView.invalidate();
            
            // Tăng tốc độ refresh
            handler.postDelayed(this, 10);
        }
    };
    
    // Bắt đầu hiệu ứng
    handler.post(autoScrollRunnable);
}

/**
 * Đặt nội dung văn bản hiển thị
 * @param text Nội dung văn bản
 */
public void setText(String text) {
    Log.d(TAG, "setText called with: " + text);
    this.text = text;
    this.isVisible = true; // Đảm bảo text visible khi setText được gọi
    resetTextPaint();
}

/**
 * Đặt màu cho văn bản
 * @param textColor Màu văn bản
 */
public void setTextColor(int textColor) {
    this.textColor = textColor;
    textPaint.setColor(textColor);
    targetView.invalidate();
}

/**
 * Đặt màu glow cho văn bản
 * @param glowColor Màu hiệu ứng glow
 */
public void setGlowColor(int glowColor) {
    this.glowColor = glowColor;
    glowPaint.setColor(glowColor);
    targetView.invalidate();
}

/**
 * Đặt màu cho văn bản và hiệu ứng glow
 * @param textColor Màu văn bản
 * @param glowColor Màu hiệu ứng glow (nếu null, sẽ sử dụng màu mặc định)
 */
public void setTextColors(int textColor, Integer glowColor) {
    this.textColor = textColor;
    textPaint.setColor(textColor);
    
    if (glowColor != null) {
        this.glowColor = glowColor;
        this.glowPaint.setColor(glowColor);
    }
    
    targetView.invalidate();
}

/**
 * Đặt kích thước văn bản
 * @param textSize Kích thước văn bản
 */
public void setTextSize(float textSize) {
    this.textSize = textSize;
    textPaint.setTextSize(textSize);
    glowPaint.setTextSize(textSize);
    
    targetView.invalidate();
}

/**
 * Lấy kích thước văn bản hiện tại
 * @return Kích thước văn bản
 */
public float getTextSize() {
    return this.textSize;
}

/**
 * Đặt hiệu ứng cho văn bản
 * @param effect Loại hiệu ứng
 */
public void setTextEffect(TextEffect effect) {
    this.currentEffect = effect;
    startAnimation();
}

/**
 * Bắt/tắt chế độ tự động cuộn văn bản
 * @param autoScroll true để bật tự động cuộn, false để tắt
 */
public void setAutoScroll(boolean autoScroll) {
    this.autoScroll = autoScroll;
    
    if (autoScroll && animatedValue == 0f) {
        startAutoScroll();
    }
    
    targetView.invalidate();
}

/**
 * Hiển thị text gợi ý khi không có nội dung
 * @param placeholderText Text hiển thị mặc định
 */
public void showPlaceholderText(String placeholderText) {
    if (text == null || text.isEmpty()) {
        // Lưu giá trị màu hiện tại
        int originalTextColor = textColor;
        int originalGlowColor = glowColor;
        
        // Đặt màu mờ hơn cho text gợi ý
        int dimmedTextColor = adjustAlpha(originalTextColor, 0.5f);
        int dimmedGlowColor = adjustAlpha(originalGlowColor, 0.3f);
        
        // Cập nhật tạm thời
        setTextColors(dimmedTextColor, dimmedGlowColor);
        text = placeholderText;
        
        // Vẽ lại để hiển thị text gợi ý
        targetView.invalidate();
        
        // Khôi phục lại màu ban đầu
        handler.postDelayed(() -> {
            setTextColors(originalTextColor, originalGlowColor);
            text = "";
        }, 3000); // Hiển thị trong 3 giây
    }
}

/**
 * Điều chỉnh alpha (độ trong suốt) của màu
 * @param color Màu gốc
 * @param factor Hệ số alpha (0-1)
 * @return Màu đã điều chỉnh
 */
private int adjustAlpha(int color, float factor) {
    int alpha = Math.round(Color.alpha(color) * factor);
    int red = Color.red(color);
    int green = Color.green(color);
    int blue = Color.blue(color);
    return Color.argb(alpha, red, green, blue);
}

private void resetTextPaint() {
    textPaint.setTextSize(textSize);
    textPaint.setColor(textColor);
    glowPaint.setColor(glowColor);
    glowPaint.setMaskFilter(new BlurMaskFilter(20, BlurMaskFilter.Blur.NORMAL));
}
} 