package com.example.flashlightai.textlight;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * View hiển thị chữ với các hiệu ứng như chạy, nhấp nháy, đổi màu
 */
public class TextLightView extends View {
    private static final String TAG = "TextLightView";

    // Text properties
    private String text = "";
    private Paint textPaint;
    private float textSize = 60f;
    private int textColor = Color.RED;
    private Typeface typeface = Typeface.DEFAULT_BOLD;

    // Scroll properties
    private float scrollX = 0f;
    private float scrollY = 0f;
    private float scrollSpeed = 5f;
    private ScrollDirection scrollDirection = ScrollDirection.LEFT_TO_RIGHT;
    private boolean isScrolling = false;

    // Blink properties
    private boolean isBlinking = false;
    private float blinkFrequency = 500f; // milliseconds
    private float blinkAlpha = 1f;

    // Handler for animations
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable scrollRunnable;
    private Runnable blinkRunnable;

    // Enum for scroll directions
    public enum ScrollDirection {
        LEFT_TO_RIGHT,
        RIGHT_TO_LEFT,
        TOP_TO_BOTTOM,
        BOTTOM_TO_TOP,
        NONE
    }

    /**
     * Constructor
     */
    public TextLightView(Context context) {
        super(context);
        init();
    }

    /**
     * Constructor with attrs
     */
    public TextLightView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * Constructor with attrs and defStyleAttr
     */
    public TextLightView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * Initialize view
     */
    private void init() {
        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(textSize);
        textPaint.setColor(textColor);
        textPaint.setTypeface(typeface);
        textPaint.setTextAlign(Paint.Align.CENTER);

        createScrollRunnable();
        createBlinkRunnable();
    }

    /**
     * Create runnable for scrolling text
     */
    private void createScrollRunnable() {
        scrollRunnable = new Runnable() {
            @Override
            public void run() {
                if (!isScrolling) return;

                // Update scroll position based on direction
                switch (scrollDirection) {
                    case LEFT_TO_RIGHT:
                        scrollX += scrollSpeed;
                        if (scrollX > getWidth() + getTextWidth()) {
                            scrollX = -getTextWidth();
                        }
                        break;
                    case RIGHT_TO_LEFT:
                        scrollX -= scrollSpeed;
                        if (scrollX < -getTextWidth()) {
                            scrollX = getWidth();
                        }
                        break;
                    case TOP_TO_BOTTOM:
                        scrollY += scrollSpeed;
                        if (scrollY > getHeight() + textSize) {
                            scrollY = -textSize;
                        }
                        break;
                    case BOTTOM_TO_TOP:
                        scrollY -= scrollSpeed;
                        if (scrollY < -textSize) {
                            scrollY = getHeight();
                        }
                        break;
                    case NONE:
                        // No scrolling
                        break;
                }

                // Invalidate the view to redraw
                invalidate();

                // Continue animation
                handler.postDelayed(this, 16); // ~60fps
            }
        };
    }

    /**
     * Create runnable for blinking text
     */
    private void createBlinkRunnable() {
        blinkRunnable = new Runnable() {
            boolean visible = true;

            @Override
            public void run() {
                if (!isBlinking) return;

                visible = !visible;
                blinkAlpha = visible ? 1f : 0f;

                // Invalidate the view to redraw
                invalidate();

                // Continue animation
                handler.postDelayed(this, (long) blinkFrequency);
            }
        };
    }

    /**
     * Calculate the width of text
     */
    private float getTextWidth() {
        if (text == null || text.isEmpty()) return 0;
        return textPaint.measureText(text);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (text == null || text.isEmpty()) return;

        // Set text opacity for blinking
        int alpha = (int) (255 * blinkAlpha);
        textPaint.setAlpha(alpha);

        // Calculate position based on text align and scroll position
        float x = getWidth() / 2f + scrollX;
        float y = getHeight() / 2f + scrollY;

        // Adjust y position to center text vertically
        Rect textBounds = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), textBounds);
        y += textBounds.height() / 2f;

        // Draw text
        canvas.drawText(text, x, y, textPaint);
    }

    /**
     * Start scrolling animation
     */
    public void startScrolling() {
        if (isScrolling) return;

        isScrolling = true;
        handler.post(scrollRunnable);
    }

    /**
     * Stop scrolling animation
     */
    public void stopScrolling() {
        isScrolling = false;
        handler.removeCallbacks(scrollRunnable);
    }

    /**
     * Start blinking animation
     */
    public void startBlinking() {
        if (isBlinking) return;

        isBlinking = true;
        handler.post(blinkRunnable);
    }

    /**
     * Stop blinking animation
     */
    public void stopBlinking() {
        isBlinking = false;
        handler.removeCallbacks(blinkRunnable);
        blinkAlpha = 1f;
        invalidate();
    }

    /**
     * Set text content
     */
    public void setText(String text) {
        this.text = text;
        invalidate();
    }

    /**
     * Get current text
     */
    public String getText() {
        return text;
    }

    /**
     * Set text size
     */
    public void setTextSize(float textSize) {
        this.textSize = textSize;
        textPaint.setTextSize(textSize);
        invalidate();
    }

    /**
     * Get text size
     */
    public float getTextSize() {
        return textSize;
    }

    /**
     * Set text color
     */
    public void setTextColor(int color) {
        this.textColor = color;
        textPaint.setColor(color);
        invalidate();
    }

    /**
     * Get text color
     */
    public int getTextColor() {
        return textColor;
    }

    /**
     * Set typeface
     */
    public void setTypeface(Typeface typeface) {
        this.typeface = typeface;
        textPaint.setTypeface(typeface);
        invalidate();
    }

    /**
     * Set scroll direction
     */
    public void setScrollDirection(ScrollDirection direction) {
        this.scrollDirection = direction;
        
        // Reset scroll position when direction changes
        if (direction == ScrollDirection.LEFT_TO_RIGHT || direction == ScrollDirection.RIGHT_TO_LEFT) {
            scrollY = 0;
            if (direction == ScrollDirection.LEFT_TO_RIGHT) {
                scrollX = -getTextWidth();
            } else {
                scrollX = getWidth();
            }
        } else if (direction == ScrollDirection.TOP_TO_BOTTOM || direction == ScrollDirection.BOTTOM_TO_TOP) {
            scrollX = 0;
            if (direction == ScrollDirection.TOP_TO_BOTTOM) {
                scrollY = -textSize;
            } else {
                scrollY = getHeight();
            }
        } else if (direction == ScrollDirection.NONE) {
            scrollX = 0;
            scrollY = 0;
            stopScrolling();
        }
        
        invalidate();
        
        // If we're supposed to be scrolling but were paused, restart
        if (direction != ScrollDirection.NONE && !isScrolling) {
            startScrolling();
        }
    }

    /**
     * Set scroll speed
     */
    public void setScrollSpeed(float speed) {
        this.scrollSpeed = speed;
    }

    /**
     * Set blink frequency
     */
    public void setBlinkFrequency(float frequency) {
        this.blinkFrequency = frequency;
    }

    /**
     * Set blinking state
     */
    public void setBlinking(boolean blinking) {
        if (blinking && !isBlinking) {
            startBlinking();
        } else if (!blinking && isBlinking) {
            stopBlinking();
        }
    }

    /**
     * Setup with settings
     */
    public void setupTextLight(String text, float textSize, int textColor, ScrollDirection direction, 
                              float scrollSpeed, boolean blinking, float blinkFrequency) {
        // First stop any ongoing animations
        stopScrolling();
        stopBlinking();
        
        // Set text properties
        setText(text);
        setTextSize(textSize);
        setTextColor(textColor);
        
        // Set scrolling properties
        this.scrollSpeed = scrollSpeed;
        this.scrollDirection = direction;
        
        // Set blinking properties
        this.blinkFrequency = blinkFrequency;
        this.isBlinking = blinking;
        
        // Start animations if needed
        if (direction != ScrollDirection.NONE) {
            startScrolling();
        }
        
        if (blinking) {
            startBlinking();
        }
    }

    /**
     * Setup with settings using default blink frequency
     */
    public void setupTextLight(String text, int textSize, int textColor, ScrollDirection direction, 
                             int scrollSpeed, boolean blinking) {
        setupTextLight(text, textSize, textColor, direction, scrollSpeed, blinking, 500f);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        
        // Clean up animations
        stopScrolling();
        stopBlinking();
        handler.removeCallbacksAndMessages(null);
    }

    /**
     * Stop all animations
     */
    public void stop() {
        stopScrolling();
        stopBlinking();
    }
    
    /**
     * Check if any animation is running
     */
    public boolean isRunning() {
        return isScrolling || isBlinking;
    }
} 