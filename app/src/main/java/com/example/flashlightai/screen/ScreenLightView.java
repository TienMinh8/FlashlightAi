package com.example.flashlightai.screen;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Custom view cho màn hình phát sáng
 */
public class ScreenLightView extends View {
    private static final String TAG = "ScreenLightView";
    
    // Key lưu trạng thái
    private static final String STATE_PARENT = "state_parent";
    private static final String STATE_BG_COLOR = "state_bg_color";
    private static final String STATE_EFFECT_TYPE = "state_effect_type";
    private static final String STATE_TEXT_ENABLED = "state_text_enabled";
    private static final String STATE_TEXT_CONTENT = "state_text_content";
    
    private int backgroundColor = Color.WHITE;
    private LightEffectsManager effectsManager;
    
    // Thuộc tính cho text
    private Paint textPaint;
    private String currentText = "";
    private int textSize = 50;
    private int textColor = Color.WHITE;
    private boolean showText = false;
    
    /**
     * Constructor
     */
    public ScreenLightView(Context context) {
        super(context);
        init();
    }
    
    /**
     * Constructor với attrs
     */
    public ScreenLightView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    
    /**
     * Constructor với attrs và defStyleAttr
     */
    public ScreenLightView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    
    /**
     * Khởi tạo
     */
    private void init() {
        // Khởi tạo manager hiệu ứng ánh sáng
        effectsManager = new LightEffectsManager(this);
        
        // Khởi tạo paint cho text
        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(textSize);
        textPaint.setColor(textColor);
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);
        
        // Đặt màu nền mặc định
        setBackgroundColor(backgroundColor);
        
        Log.d(TAG, "ScreenLightView initialized");
    }
    
    /**
     * Bắt đầu hiệu ứng ánh sáng
     * @param effectType Loại hiệu ứng
     * @param config Config của hiệu ứng
     */
    public void startLightEffect(LightEffectsManager.EffectType effectType, LightEffectsManager.EffectConfig config) {
        // Dừng hiệu ứng hiện tại trước khi bắt đầu hiệu ứng mới
        stopLightEffect();
        
        // Bắt đầu hiệu ứng
        effectsManager.startEffect(effectType, config);
        
        // Yêu cầu vẽ lại
        invalidate();
    }
    
    /**
     * Bắt đầu hiệu ứng ánh sáng với cấu hình mặc định
     * @param effectType Loại hiệu ứng
     */
    public void startLightEffect(LightEffectsManager.EffectType effectType) {
        startLightEffect(effectType, null);
    }
    
    /**
     * Dừng hiệu ứng ánh sáng
     */
    public void stopLightEffect() {
        if (effectsManager != null) {
            effectsManager.stopEffect();
        }
    }
    
    /**
     * Cập nhật cấu hình hiệu ứng ánh sáng
     * @param config Config mới
     */
    public void updateEffectConfig(LightEffectsManager.EffectConfig config) {
        if (effectsManager != null) {
            effectsManager.updateEffectConfig(config);
        }
    }
    
    /**
     * Lấy về LightEffectsManager
     * @return LightEffectsManager
     */
    public LightEffectsManager getEffectsManager() {
        return effectsManager;
    }
    
    /**
     * Kiểm tra trạng thái hiệu ứng ánh sáng
     * @return true nếu đang chạy, false nếu không
     */
    public boolean isLightEffectRunning() {
        return effectsManager != null && effectsManager.isEffectRunning();
    }
    
    /**
     * Lấy loại hiệu ứng ánh sáng hiện tại
     * @return Loại hiệu ứng
     */
    public LightEffectsManager.EffectType getCurrentEffectType() {
        return effectsManager != null ? effectsManager.getCurrentEffectType() : null;
    }
    
    /**
     * Draw các hiệu ứng và text
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        // Đảm bảo canvas không null
        if (canvas == null) {
            Log.e("ScreenLightView", "Canvas is null in onDraw");
            return;
        }
        
        try {
            // Vẽ hiệu ứng nếu có
            if (effectsManager != null) {
                effectsManager.drawEffect(canvas);
            }
            
            // Vẽ text nếu được yêu cầu
            if (showText && currentText != null && !currentText.isEmpty()) {
                drawText(canvas);
            }
        } catch (Exception e) {
            Log.e("ScreenLightView", "Error in onDraw: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Vẽ text lên canvas
     */
    private void drawText(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();
        
        // Vẽ text ở giữa màn hình
        canvas.drawText(currentText, width / 2f, height / 2f, textPaint);
    }
    
    /**
     * Hiển thị text với kích thước được chỉ định
     * 
     * @param text Nội dung text
     * @param textSize Kích thước (0-100, sẽ được chuyển đổi thành 20-100)
     */
    public void showText(String text, int textSize) {
        this.currentText = text;
        this.showText = true;
        
        // Chuyển đổi từ giá trị 0-100 sang kích thước thực 20-100
        this.textSize = 20 + (textSize * 80 / 100);
        textPaint.setTextSize(this.textSize);
        
        // Yêu cầu vẽ lại
        invalidate();
    }
    
    /**
     * Ẩn text
     */
    public void hideText() {
        this.showText = false;
        invalidate();
    }
    
    /**
     * Lấy text hiện tại
     * 
     * @return Nội dung text hiện tại
     */
    public String getCurrentText() {
        return currentText;
    }
    
    /**
     * Lấy kích thước text
     * 
     * @return Kích thước text (0-100)
     */
    public int getTextSize() {
        // Chuyển đổi từ kích thước thực về giá trị 0-100
        return textSize > 20 ? (textSize - 20) * 100 / 80 : 0;
    }
    
    /**
     * Thay đổi màu nền của view
     * @param color Mã màu
     */
    public void setColor(int color) {
        this.backgroundColor = color;
        setBackgroundColor(color);
        invalidate();
    }
    
    /**
     * Lấy màu nền hiện tại
     * @return Mã màu
     */
    public int getBackgroundColor() {
        return backgroundColor;
    }
    
    @Override
    protected Parcelable onSaveInstanceState() {
        // Lưu trạng thái của view
        Bundle bundle = new Bundle();
        bundle.putParcelable(STATE_PARENT, super.onSaveInstanceState());
        bundle.putInt(STATE_BG_COLOR, backgroundColor);
        bundle.putString(STATE_EFFECT_TYPE, getCurrentEffectType() != null ? getCurrentEffectType().name() : null);
        
        // Lưu trạng thái text
        bundle.putBoolean(STATE_TEXT_ENABLED, showText);
        bundle.putString(STATE_TEXT_CONTENT, currentText);
        
        return bundle;
    }
    
    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        // Khôi phục trạng thái
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            
            backgroundColor = bundle.getInt(STATE_BG_COLOR, Color.WHITE);
            String effectTypeName = bundle.getString(STATE_EFFECT_TYPE, null);
            
            // Khôi phục hiệu ứng
            if (effectTypeName != null) {
                try {
                    LightEffectsManager.EffectType effectType = LightEffectsManager.EffectType.valueOf(effectTypeName);
                    startLightEffect(effectType, null);
                } catch (IllegalArgumentException e) {
                    Log.e(TAG, "Invalid effect type: " + effectTypeName, e);
                    startLightEffect(LightEffectsManager.EffectType.SOLID, null);
                }
            }
            
            // Khôi phục trạng thái text
            showText = bundle.getBoolean(STATE_TEXT_ENABLED, false);
            currentText = bundle.getString(STATE_TEXT_CONTENT, "");
            
            state = bundle.getParcelable(STATE_PARENT);
        }
        
        super.onRestoreInstanceState(state);
    }
    
    /**
     * Xử lý khi kích thước thay đổi
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }
    
    /**
     * Xử lý khi view bị detach khỏi window
     */
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        
        // Dừng hiệu ứng ánh sáng
        stopLightEffect();
    }
} 