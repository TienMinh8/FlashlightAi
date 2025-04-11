package com.example.flashlightai.customviews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Custom view hiển thị thanh trượt màu gradient
 */
public class ColorSliderView extends View {
    
    private Paint gradientPaint;
    private Paint thumbPaint;
    private Paint thumbStrokePaint;
    private Paint borderPaint;
    private Paint shadowPaint;
    
    private float thumbX = 0;
    private int selectedColor = Color.WHITE;
    private OnColorSelectedListener colorSelectedListener;
    private OnColorChangingListener colorChangingListener;
    
    // Mảng màu cho thanh gradient với nhiều màu hơn để gradient mượt hơn
    private static final int[] COLORS = new int[]{
            Color.RED, Color.parseColor("#FF7F00"), Color.YELLOW, 
            Color.parseColor("#00FF00"), Color.CYAN, 
            Color.BLUE, Color.parseColor("#8B00FF"), Color.parseColor("#FF00FF"), Color.RED
    };
    
    // Kích thước thumb và các thông số UI
    private static final float THUMB_RADIUS = 20;
    private static final float BORDER_RADIUS = 10f;
    private static final float BORDER_WIDTH = 2f;
    private static final float SHADOW_RADIUS = 5f;
    private RectF sliderRect = new RectF();
    
    public interface OnColorSelectedListener {
        void onColorSelected(int color);
    }
    
    public interface OnColorChangingListener {
        void onColorChanging(int color);
    }
    
    public ColorSliderView(Context context) {
        super(context);
        init();
    }
    
    public ColorSliderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    
    public ColorSliderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    
    private void init() {
        // Paint cho gradient
        gradientPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        
        // Paint cho thumb
        thumbPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        thumbPaint.setColor(selectedColor);
        
        // Paint cho viền thumb - thay đổi sang màu accent
        thumbStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        thumbStrokePaint.setColor(Color.parseColor("#4FC3F7"));
        thumbStrokePaint.setStyle(Paint.Style.STROKE);
        thumbStrokePaint.setStrokeWidth(4);
        
        // Paint cho viền của thanh màu - thay đổi sang màu primary
        borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        borderPaint.setColor(Color.parseColor("#263859"));
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(BORDER_WIDTH);
        
        // Paint cho bóng đổ
        shadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        shadowPaint.setColor(Color.BLACK);
        shadowPaint.setStyle(Paint.Style.FILL);
        shadowPaint.setShadowLayer(SHADOW_RADIUS, 0, 2, Color.parseColor("#80000000"));
        
        // Enable hardware acceleration
        setLayerType(LAYER_TYPE_SOFTWARE, null);
    }
    
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        
        // Tạo gradient shader khi kích thước thay đổi
        gradientPaint.setShader(new LinearGradient(
                BORDER_RADIUS, 0, w - BORDER_RADIUS, 0, 
                COLORS, 
                null, 
                Shader.TileMode.CLAMP
        ));
        
        // Thiết lập vị trí thumb ban đầu (màu trắng ở khoảng 5/6 thanh)
        thumbX = w * 5.0f / 6.0f;
        updateSelectedColor();
        
        // Cập nhật rect cho thanh màu
        sliderRect.set(BORDER_RADIUS, BORDER_RADIUS, w - BORDER_RADIUS, h - BORDER_RADIUS);
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        // Vẽ bóng đổ cho thanh màu
        canvas.drawRoundRect(sliderRect, BORDER_RADIUS, BORDER_RADIUS, shadowPaint);
        
        // Vẽ thanh gradient
        canvas.drawRoundRect(sliderRect, BORDER_RADIUS, BORDER_RADIUS, gradientPaint);
        
        // Vẽ viền cho thanh màu
        canvas.drawRoundRect(sliderRect, BORDER_RADIUS, BORDER_RADIUS, borderPaint);
        
        // Vẽ bóng đổ cho thumb
        shadowPaint.setShadowLayer(SHADOW_RADIUS, 0, 2, Color.parseColor("#80000000"));
        canvas.drawCircle(thumbX, getHeight() / 2.0f, THUMB_RADIUS + 2, shadowPaint);
        
        // Vẽ thumb
        canvas.drawCircle(thumbX, getHeight() / 2.0f, THUMB_RADIUS, thumbPaint);
        canvas.drawCircle(thumbX, getHeight() / 2.0f, THUMB_RADIUS, thumbStrokePaint);
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                // Cập nhật vị trí thumb dựa trên vị trí chạm
                float x = event.getX();
                thumbX = Math.max(BORDER_RADIUS + THUMB_RADIUS, 
                        Math.min(getWidth() - BORDER_RADIUS - THUMB_RADIUS, x));
                updateSelectedColor();
                invalidate();
                
                // Thông báo màu đang thay đổi trong quá trình di chuyển
                if (colorChangingListener != null) {
                    colorChangingListener.onColorChanging(selectedColor);
                }
                return true;
            case MotionEvent.ACTION_UP:
                // Thông báo màu được chọn khi người dùng thả tay
                if (colorSelectedListener != null) {
                    colorSelectedListener.onColorSelected(selectedColor);
                }
                return true;
        }
        return super.onTouchEvent(event);
    }
    
    /**
     * Cập nhật màu được chọn dựa trên vị trí thumb
     */
    private void updateSelectedColor() {
        // Lấy màu từ pixel tại vị trí thumb
        float effectiveWidth = getWidth() - 2 * (BORDER_RADIUS + THUMB_RADIUS);
        float position = (thumbX - (BORDER_RADIUS + THUMB_RADIUS)) / effectiveWidth;
        position = Math.max(0, Math.min(1, position));
        
        // Tính toán màu dựa trên vị trí trong spectrum
        if (getWidth() > 0) {
            // Sử dụng interpolation để tính toán màu
            selectedColor = interpolateColor(position);
            thumbPaint.setColor(selectedColor);
        }
    }
    
    /**
     * Tính toán màu dựa trên vị trí giữa các màu trong mảng COLORS
     */
    private int interpolateColor(float position) {
        if (position <= 0) return COLORS[0];
        if (position >= 1) return COLORS[COLORS.length - 1];
        
        // Tính vị trí của màu trong mảng
        float colorPosition = position * (COLORS.length - 1);
        int index = (int) colorPosition;
        float remainder = colorPosition - index;
        
        // Nếu đúng vị trí của một màu cụ thể, trả về màu đó
        if (remainder < 0.001f) {
            return COLORS[index];
        }
        
        // Nếu nằm giữa hai màu, tính toán màu trung gian
        int startColor = COLORS[index];
        int endColor = COLORS[Math.min(index + 1, COLORS.length - 1)];
        
        // Interpolate giữa hai màu
        int startA = Color.alpha(startColor);
        int startR = Color.red(startColor);
        int startG = Color.green(startColor);
        int startB = Color.blue(startColor);
        
        int endA = Color.alpha(endColor);
        int endR = Color.red(endColor);
        int endG = Color.green(endColor);
        int endB = Color.blue(endColor);
        
        // Tính giá trị màu mới dựa trên vị trí
        int a = (int) (startA + remainder * (endA - startA));
        int r = (int) (startR + remainder * (endR - startR));
        int g = (int) (startG + remainder * (endG - startG));
        int b = (int) (startB + remainder * (endB - startB));
        
        return Color.argb(a, r, g, b);
    }
    
    /**
     * Thiết lập màu được chọn
     */
    public void setSelectedColor(int color) {
        selectedColor = color;
        thumbPaint.setColor(selectedColor);
        invalidate();
    }
    
    /**
     * Lấy màu đang được chọn
     */
    public int getSelectedColor() {
        return selectedColor;
    }
    
    /**
     * Thiết lập listener cho sự kiện chọn màu
     */
    public void setOnColorSelectedListener(OnColorSelectedListener listener) {
        this.colorSelectedListener = listener;
    }
    
    /**
     * Thiết lập listener cho sự kiện thay đổi màu trong quá trình di chuyển
     */
    public void setOnColorChangingListener(OnColorChangingListener listener) {
        this.colorChangingListener = listener;
    }
} 