package com.example.flashlightai.screen;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.WindowManager;
import android.app.Activity;
import com.example.flashlightai.R;
import android.os.Handler;
import android.os.Looper;

/**
 * Điều khiển chức năng màn hình phát sáng
 */
public class ScreenLightController {
    private static final String TAG = "ScreenLightController";
    
    private Context context;
    private ScreenLightView screenLightView;
    private WindowManager.LayoutParams layoutParams;
    private boolean isActive = false;
    private float brightness = 1.0f;
    private int screenColor = Color.WHITE;
    private LightEffectsManager.EffectType currentEffect = LightEffectsManager.EffectType.SOLID;
    private ScreenLightListener listener;
    private Handler mainHandler;
    private int currentBrightness = 100;
    
    /**
     * Khởi tạo controller
     * @param context Context của activity
     */
    public ScreenLightController(Context context) {
        this.context = context;
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            layoutParams = activity.getWindow().getAttributes();
            screenLightView = activity.findViewById(android.R.id.content).getRootView().findViewById(R.id.screen_light_view);
        }
        mainHandler = new Handler(Looper.getMainLooper());
        Log.d(TAG, "ScreenLightController initialized");
    }
    
    /**
     * Bắt đầu màn hình phát sáng
     */
    public void startScreenLight() {
        if (!isActive) {
            isActive = true;
            // Đặt độ sáng và màu
            setBrightness(currentBrightness);
            setScreenColor(screenColor);
            
            if (listener != null) {
                mainHandler.post(() -> listener.onScreenLightStarted());
            }
            
            Log.d(TAG, "Screen light started");
        }
    }
    
    /**
     * Dừng màn hình phát sáng
     */
    public void stopScreenLight() {
        if (isActive) {
            isActive = false;
            
            if (listener != null) {
                mainHandler.post(() -> listener.onScreenLightStopped());
            }
            
            Log.d(TAG, "Screen light stopped");
        }
    }
    
    /**
     * Đặt độ sáng màn hình
     * @param brightness Độ sáng (0.0f - 1.0f)
     */
    public void setBrightness(float brightness) {
        try {
            // Đảm bảo brightness nằm trong khoảng hợp lệ
            this.brightness = Math.max(0.01f, Math.min(brightness, 1.0f));
            
            if (context instanceof Activity && layoutParams != null) {
                layoutParams.screenBrightness = this.brightness;
                ((Activity) context).getWindow().setAttributes(layoutParams);
                
                if (listener != null) {
                    final int brightnessValue = currentBrightness;
                    mainHandler.post(() -> listener.onBrightnessChanged(brightnessValue));
                }
                
                Log.d(TAG, "Screen brightness set to: " + this.brightness);
            }
        } catch (Exception e) {
            // Xử lý lỗi khi thay đổi độ sáng
            Log.e(TAG, "Error setting brightness: " + e.getMessage());
        }
    }
    
    /**
     * Đặt chế độ giữ màn hình luôn sáng
     * @param keepScreenOn true để giữ màn hình luôn sáng
     */
    public void setKeepScreenOn(boolean keepScreenOn) {
        if (context instanceof Activity) {
            if (keepScreenOn) {
                ((Activity) context).getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            } else {
                ((Activity) context).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
            Log.d(TAG, "Keep screen on set to: " + keepScreenOn);
        }
    }
    
    /**
     * Đặt màu màn hình
     * @param color Mã màu RGB
     */
    public void setScreenColor(int color) {
        this.screenColor = color;
        
        if (screenLightView != null) {
            screenLightView.setBackgroundColor(color);
            
            if (listener != null) {
                listener.onColorChanged(color);
            }
            
            Log.d(TAG, "Screen color set to: " + color);
        }
    }
    
    /**
     * Đặt hiệu ứng ánh sáng
     * @param effectType Loại hiệu ứng
     */
    public void setLightEffect(LightEffectsManager.EffectType effectType) {
        this.currentEffect = effectType;
        
        if (screenLightView != null) {
            screenLightView.startLightEffect(effectType);
            Log.d(TAG, "Light effect set to: " + effectType);
        }
    }
    
    /**
     * Kiểm tra xem màn hình phát sáng có đang hoạt động không
     * @return true nếu đang hoạt động
     */
    public boolean isActive() {
        return isActive;
    }
    
    /**
     * Đặt listener cho các sự kiện màn hình phát sáng
     * @param listener ScreenLightListener
     */
    public void setScreenLightListener(ScreenLightListener listener) {
        this.listener = listener;
    }
    
    /**
     * Interface cho các sự kiện màn hình phát sáng
     */
    public interface ScreenLightListener {
        void onScreenLightStarted();
        void onScreenLightStopped();
        void onBrightnessChanged(int brightness);
        void onColorChanged(int color);
        void onOverheating();
        void onLowBattery();
    }

    /**
     * Đặt độ sáng cho đèn màn hình (0-100)
     */
    public void setBrightness(int brightness) {
        if (brightness < 0) brightness = 0;
        if (brightness > 100) brightness = 100;
        
        currentBrightness = brightness;
        
        if (listener != null) {
            final int finalBrightness = brightness;
            mainHandler.post(() -> listener.onBrightnessChanged(finalBrightness));
        }
    }

    /**
     * Kiểm tra trạng thái đèn
     */
    public boolean isRunning() {
        return isActive;
    }

    /**
     * Lấy hiệu ứng hiện tại
     */
    public LightEffectsManager.EffectType getCurrentEffect() {
        return currentEffect;
    }

    /**
     * Lấy độ sáng hiện tại
     */
    public int getCurrentBrightness() {
        return currentBrightness;
    }

    /**
     * Hiển thị văn bản trên đèn màn hình
     */
    public void showText(String text, int textSize) {
        if (screenLightView != null) {
            screenLightView.showText(text, textSize);
        }
    }

    /**
     * Ẩn văn bản trên đèn màn hình
     */
    public void hideText() {
        if (screenLightView != null) {
            screenLightView.hideText();
        }
    }

    /**
     * Lấy văn bản hiện tại
     */
    public String getCurrentText() {
        if (screenLightView != null) {
            return screenLightView.getCurrentText();
        }
        return "";
    }

    /**
     * Lấy kích thước văn bản hiện tại
     */
    public int getTextSize() {
        if (screenLightView != null) {
            return screenLightView.getTextSize();
        }
        return 50;
    }
} 