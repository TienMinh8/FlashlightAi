package com.example.flashlightai.screen;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Quản lý các hiệu ứng ánh sáng trên màn hình
 */
public class LightEffectsManager {
    private static final String TAG = "LightEffectsManager";
    
    // Các loại hiệu ứng có sẵn
    public enum EffectType {
        SOLID,      // Màu đơn sắc
        PULSE,      // Nhịp đập
        STROBE,     // Nhấp nháy nhanh
        WAVE,       // Sóng chuyển động
        RAINBOW,    // Cầu vồng
        DISCO       // Disco ngẫu nhiên
    }
    
    private final View targetView;
    private final Handler handler;
    private final Random random;
    
    private EffectType currentEffect = EffectType.SOLID;
    private EffectConfig currentConfig;
    private ValueAnimator currentAnimator;
    private boolean isEffectRunning = false;
    
    // Cấu hình mặc định cho mỗi loại hiệu ứng
    private final Map<EffectType, EffectConfig> defaultConfigs = new HashMap<>();
    
    /**
     * Constructor với view sẽ áp dụng hiệu ứng
     */
    public LightEffectsManager(View targetView) {
        this.targetView = targetView;
        this.handler = new Handler(Looper.getMainLooper());
        this.random = new Random();
        
        // Khởi tạo cấu hình mặc định
        initDefaultConfigs();
    }
    
    /**
     * Khởi tạo cấu hình mặc định cho mỗi loại hiệu ứng
     */
    private void initDefaultConfigs() {
        // Cấu hình mặc định cho SOLID
        EffectConfig solidConfig = new EffectConfig();
        solidConfig.put("color", Color.WHITE);
        defaultConfigs.put(EffectType.SOLID, solidConfig);
        
        // Cấu hình mặc định cho PULSE
        EffectConfig pulseConfig = new EffectConfig();
        pulseConfig.put("color", Color.WHITE);
        pulseConfig.put("minAlpha", 0.4f);
        pulseConfig.put("maxAlpha", 1.0f);
        pulseConfig.put("duration", 1000);
        defaultConfigs.put(EffectType.PULSE, pulseConfig);
        
        // Cấu hình mặc định cho STROBE
        EffectConfig strobeConfig = new EffectConfig();
        strobeConfig.put("color", Color.WHITE);
        strobeConfig.put("onDuration", 50);
        strobeConfig.put("offDuration", 50);
        defaultConfigs.put(EffectType.STROBE, strobeConfig);
        
        // Cấu hình mặc định cho WAVE
        EffectConfig waveConfig = new EffectConfig();
        waveConfig.put("startColor", Color.BLUE);
        waveConfig.put("endColor", Color.CYAN);
        waveConfig.put("duration", 2000);
        defaultConfigs.put(EffectType.WAVE, waveConfig);
        
        // Cấu hình mặc định cho RAINBOW
        EffectConfig rainbowConfig = new EffectConfig();
        rainbowConfig.put("duration", 5000);
        rainbowConfig.put("saturation", 1.0f);
        rainbowConfig.put("brightness", 1.0f);
        defaultConfigs.put(EffectType.RAINBOW, rainbowConfig);
        
        // Cấu hình mặc định cho DISCO
        EffectConfig discoConfig = new EffectConfig();
        discoConfig.put("interval", 300);
        discoConfig.put("brightness", 1.0f);
        defaultConfigs.put(EffectType.DISCO, discoConfig);
    }
    
    /**
     * Bắt đầu hiệu ứng với loại và cấu hình được chỉ định
     * @param effectType Loại hiệu ứng
     * @param config Cấu hình (null để sử dụng mặc định)
     */
    public void startEffect(EffectType effectType, EffectConfig config) {
        // Dừng hiệu ứng đang chạy (nếu có)
        stopEffect();
        
        // Lưu trạng thái hiện tại
        currentEffect = effectType;
        currentConfig = config != null ? config : defaultConfigs.get(effectType);
        
        // Bắt đầu hiệu ứng mới
        switch (effectType) {
            case SOLID:
                applySolidEffect();
                break;
            case PULSE:
                startPulseEffect();
                break;
            case STROBE:
                startStrobeEffect();
                break;
            case WAVE:
                startWaveEffect();
                break;
            case RAINBOW:
                startRainbowEffect();
                break;
            case DISCO:
                startDiscoEffect();
                break;
        }
        
        isEffectRunning = true;
        Log.d(TAG, "Started effect: " + effectType.name());
    }
    
    /**
     * Dừng hiệu ứng đang chạy
     */
    public void stopEffect() {
        if (currentAnimator != null && currentAnimator.isRunning()) {
            currentAnimator.cancel();
            currentAnimator = null;
        }
        
        handler.removeCallbacksAndMessages(null);
        isEffectRunning = false;
        
        Log.d(TAG, "Stopped all effects");
    }
    
    /**
     * Áp dụng hiệu ứng màu đơn sắc
     */
    private void applySolidEffect() {
        int color = currentConfig.getInt("color", Color.WHITE);
        targetView.setBackgroundColor(color);
    }
    
    /**
     * Bắt đầu hiệu ứng nhịp đập
     */
    private void startPulseEffect() {
        int color = currentConfig.getInt("color", Color.WHITE);
        float minAlpha = currentConfig.getFloat("minAlpha", 0.4f);
        float maxAlpha = currentConfig.getFloat("maxAlpha", 1.0f);
        int duration = currentConfig.getInt("duration", 1000);
        
        // Tạo animator để thay đổi alpha
        ValueAnimator pulseAnimator = ValueAnimator.ofFloat(minAlpha, maxAlpha);
        pulseAnimator.setDuration(duration / 2);
        pulseAnimator.setRepeatCount(ValueAnimator.INFINITE);
        pulseAnimator.setRepeatMode(ValueAnimator.REVERSE);
        
        pulseAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float alpha = (float) animation.getAnimatedValue();
                int alphaInt = Math.round(255 * alpha);
                int pulseColor = Color.argb(alphaInt, 
                        Color.red(color), 
                        Color.green(color), 
                        Color.blue(color));
                targetView.setBackgroundColor(pulseColor);
            }
        });
        
        pulseAnimator.start();
        currentAnimator = pulseAnimator;
    }
    
    /**
     * Bắt đầu hiệu ứng nhấp nháy nhanh
     */
    private void startStrobeEffect() {
        final int color = currentConfig.getInt("color", Color.WHITE);
        final int onDuration = currentConfig.getInt("onDuration", 50);
        final int offDuration = currentConfig.getInt("offDuration", 50);
        
        // Tạo runnable để luân phiên giữa sáng và tối
        final Runnable strobeRunnable = new Runnable() {
            boolean isOn = false;
            
            @Override
            public void run() {
                if (!isEffectRunning) return;
                
                if (isOn) {
                    targetView.setBackgroundColor(color);
                } else {
                    targetView.setBackgroundColor(Color.BLACK);
                }
                
                isOn = !isOn;
                handler.postDelayed(this, isOn ? onDuration : offDuration);
            }
        };
        
        handler.post(strobeRunnable);
    }
    
    /**
     * Bắt đầu hiệu ứng sóng chuyển động
     */
    private void startWaveEffect() {
        int startColor = currentConfig.getInt("startColor", Color.BLUE);
        int endColor = currentConfig.getInt("endColor", Color.CYAN);
        int duration = currentConfig.getInt("duration", 2000);
        
        // Tạo animator để chuyển đổi màu
        ValueAnimator waveAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), startColor, endColor);
        waveAnimator.setDuration(duration);
        waveAnimator.setRepeatCount(ValueAnimator.INFINITE);
        waveAnimator.setRepeatMode(ValueAnimator.REVERSE);
        
        waveAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int color = (int) animation.getAnimatedValue();
                targetView.setBackgroundColor(color);
            }
        });
        
        waveAnimator.start();
        currentAnimator = waveAnimator;
    }
    
    /**
     * Bắt đầu hiệu ứng cầu vồng
     */
    private void startRainbowEffect() {
        int duration = currentConfig.getInt("duration", 5000);
        float saturation = currentConfig.getFloat("saturation", 1.0f);
        final float brightness = currentConfig.getFloat("brightness", 1.0f);
        
        ValueAnimator rainbowAnimator = ValueAnimator.ofFloat(0f, 360f);
        rainbowAnimator.setDuration(duration);
        rainbowAnimator.setRepeatCount(ValueAnimator.INFINITE);
        rainbowAnimator.setRepeatMode(ValueAnimator.RESTART);
        
        rainbowAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            float[] hsv = new float[]{0f, saturation, brightness};
            
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                hsv[0] = (float) animation.getAnimatedValue(); // Hue
                hsv[2] = currentConfig.getFloat("brightness", brightness); 
                int color = Color.HSVToColor(hsv);
                targetView.setBackgroundColor(color);
            }
        });
        
        rainbowAnimator.start();
        currentAnimator = rainbowAnimator;
    }
    
    /**
     * Bắt đầu hiệu ứng disco với màu ngẫu nhiên
     */
    private void startDiscoEffect() {
        final int interval = currentConfig.getInt("interval", 300);
        final float brightness = currentConfig.getFloat("brightness", 1.0f);
        
        Runnable discoRunnable = new Runnable() {
            @Override
            public void run() {
                float[] hsv = new float[3];
                hsv[0] = random.nextFloat() * 360f; // Hue ngẫu nhiên
                hsv[1] = 0.8f + random.nextFloat() * 0.2f; // Saturation cao
                hsv[2] = currentConfig.getFloat("brightness", brightness);
                
                int randomColor = Color.HSVToColor(hsv);
                targetView.setBackgroundColor(randomColor);
                
                handler.postDelayed(this, interval);
            }
        };
        
        handler.post(discoRunnable);
    }
    
    /**
     * Cập nhật cấu hình cho hiệu ứng đang chạy
     * @param config Cấu hình mới
     */
    public void updateEffectConfig(EffectConfig config) {
        if (!isEffectRunning) return;
        
        // Lưu các tham số cần thiết từ config mới (giữ lại các tham số cũ)
        if (config.getAllParams().containsKey("color")) {
            currentConfig.put("color", config.getInt("color", Color.WHITE));
        }
        if (config.getAllParams().containsKey("brightness")) {
            currentConfig.put("brightness", config.getFloat("brightness", 1.0f));
            
            // Cập nhật độ sáng cho hiệu ứng đang chạy mà không khởi động lại
            applyBrightnessToCurrentEffect(config.getFloat("brightness", 1.0f));
        }
        
        Log.d(TAG, "Updated effect config for: " + currentEffect.name());
    }
    
    /**
     * Áp dụng độ sáng cho hiệu ứng đang chạy mà không khởi động lại
     * @param brightness Độ sáng (0.0f - 1.0f)
     */
    private void applyBrightnessToCurrentEffect(float brightness) {
        switch (currentEffect) {
            case SOLID:
                // Điều chỉnh độ sáng cho màu đơn sắc
                int color = currentConfig.getInt("color", Color.WHITE);
                int adjustedColor = adjustColorBrightness(color, brightness);
                targetView.setBackgroundColor(adjustedColor);
                break;
                
            case PULSE:
            case STROBE:
            case WAVE:
                // Các hiệu ứng này sẽ sử dụng brightness trong animator
                // Animator đang chạy sẽ tiếp tục với brightness mới trong lần lặp tiếp theo
                break;
                
            case RAINBOW:
            case DISCO:
                // Các hiệu ứng này đã cập nhật cấu hình và sẽ sử dụng 
                // brightness mới trong lần lặp tiếp theo
                break;
        }
    }
    
    /**
     * Điều chỉnh độ sáng của màu
     * @param color Màu gốc
     * @param brightness Độ sáng (0.0f - 1.0f)
     * @return Màu sau khi điều chỉnh độ sáng
     */
    private int adjustColorBrightness(int color, float brightness) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        
        // Điều chỉnh độ sáng (V trong HSV)
        hsv[2] = Math.max(0.1f, Math.min(1.0f, brightness));
        
        return Color.HSVToColor(hsv);
    }
    
    /**
     * Lấy cấu hình mặc định cho loại hiệu ứng cụ thể
     * @param effectType Loại hiệu ứng
     * @return Cấu hình mặc định
     */
    public EffectConfig getDefaultConfig(EffectType effectType) {
        return new EffectConfig(defaultConfigs.get(effectType));
    }
    
    /**
     * Kiểm tra xem hiệu ứng có đang chạy hay không
     * @return true nếu hiệu ứng đang chạy
     */
    public boolean isEffectRunning() {
        return isEffectRunning;
    }
    
    /**
     * Lấy loại hiệu ứng hiện tại
     * @return Loại hiệu ứng hiện tại
     */
    public EffectType getCurrentEffectType() {
        return currentEffect;
    }
    
    /**
     * Lấy cấu hình hiệu ứng hiện tại
     * @return Cấu hình hiện tại
     */
    public EffectConfig getCurrentConfig() {
        return currentConfig;
    }
    
    /**
     * Vẽ hiệu ứng lên canvas
     * @param canvas Canvas để vẽ
     */
    public void drawEffect(Canvas canvas) {
        // Hầu hết các hiệu ứng được xử lý thông qua setBackgroundColor
        // Phương thức này để hỗ trợ các hiệu ứng phức tạp hơn trong tương lai
        
        // Không cần vẽ thêm gì vào canvas vì hiệu ứng đã được áp dụng
        // thông qua setBackgroundColor trong các phương thức startXxxEffect
        
        // Log.d(TAG, "Drawing effect: " + currentEffect);
    }
    
    /**
     * Lớp EffectConfig để lưu trữ và quản lý cấu hình hiệu ứng
     */
    public static class EffectConfig {
        private final Map<String, Object> params = new HashMap<>();
        
        public EffectConfig() {
            // Constructor rỗng
        }
        
        /**
         * Tạo bản sao của cấu hình khác
         * @param other Cấu hình cần sao chép
         */
        public EffectConfig(EffectConfig other) {
            if (other != null) {
                params.putAll(other.params);
            }
        }
        
        /**
         * Thêm tham số vào cấu hình
         * @param key Tên tham số
         * @param value Giá trị tham số
         */
        public void put(String key, Object value) {
            params.put(key, value);
        }
        
        /**
         * Lấy tham số kiểu int
         * @param key Tên tham số
         * @param defaultValue Giá trị mặc định nếu không tìm thấy
         * @return Giá trị tham số
         */
        public int getInt(String key, int defaultValue) {
            Object value = params.get(key);
            if (value instanceof Integer) {
                return (Integer) value;
            }
            return defaultValue;
        }
        
        /**
         * Lấy tham số kiểu float
         * @param key Tên tham số
         * @param defaultValue Giá trị mặc định nếu không tìm thấy
         * @return Giá trị tham số
         */
        public float getFloat(String key, float defaultValue) {
            Object value = params.get(key);
            if (value instanceof Float) {
                return (Float) value;
            }
            return defaultValue;
        }
        
        /**
         * Lấy tham số kiểu boolean
         * @param key Tên tham số
         * @param defaultValue Giá trị mặc định nếu không tìm thấy
         * @return Giá trị tham số
         */
        public boolean getBoolean(String key, boolean defaultValue) {
            Object value = params.get(key);
            if (value instanceof Boolean) {
                return (Boolean) value;
            }
            return defaultValue;
        }
        
        /**
         * Lấy tham số kiểu String
         * @param key Tên tham số
         * @param defaultValue Giá trị mặc định nếu không tìm thấy
         * @return Giá trị tham số
         */
        public String getString(String key, String defaultValue) {
            Object value = params.get(key);
            if (value instanceof String) {
                return (String) value;
            }
            return defaultValue;
        }
        
        /**
         * Lấy tất cả tham số
         * @return Map chứa tất cả tham số
         */
        public Map<String, Object> getAllParams() {
            return new HashMap<>(params);
        }
    }
} 