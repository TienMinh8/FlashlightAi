package com.example.flashlightai.screen;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.core.graphics.ColorUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Công cụ chọn và quản lý màu sắc
 */
public class ColorPicker {
    private static final String TAG = "ColorPicker";
    
    // Key lưu lịch sử màu
    private static final String PREF_COLOR_HISTORY = "color_history";
    private static final String COLOR_PREF_NAME = "flashlight_colors";
    private static final int MAX_HISTORY_COLORS = 10;
    
    // Màu mặc định
    private static final int[] DEFAULT_COLORS = {
            Color.WHITE,      // Trắng
            Color.RED,        // Đỏ
            Color.GREEN,      // Xanh lá
            Color.BLUE,       // Xanh dương
            Color.YELLOW,     // Vàng
            Color.CYAN,       // Lục lam
            Color.MAGENTA,    // Hồng sẫm
            Color.parseColor("#FF9800"),  // Cam
            Color.parseColor("#9C27B0"),  // Tím
            Color.parseColor("#795548")   // Nâu
    };
    
    private final Context context;
    private final SharedPreferences preferences;
    private List<Integer> recentColors;
    private ColorSelectedListener listener;
    
    private int currentColor = Color.WHITE;
    private AlertDialog dialog;
    
    /**
     * Constructor với context
     */
    public ColorPicker(Context context) {
        this.context = context;
        this.preferences = context.getSharedPreferences(COLOR_PREF_NAME, Context.MODE_PRIVATE);
        loadRecentColors();
    }
    
    /**
     * Đọc lịch sử màu đã sử dụng gần đây
     */
    private void loadRecentColors() {
        recentColors = new ArrayList<>();
        String colorHistory = preferences.getString(PREF_COLOR_HISTORY, "");
        
        if (!colorHistory.isEmpty()) {
            String[] colorStrings = colorHistory.split(",");
            for (String colorStr : colorStrings) {
                try {
                    recentColors.add(Integer.parseInt(colorStr));
                } catch (NumberFormatException e) {
                    Log.e(TAG, "Error parsing color: " + colorStr, e);
                }
            }
        }
        
        // Thêm các màu mặc định nếu lịch sử trống
        if (recentColors.isEmpty()) {
            recentColors.addAll(Arrays.asList(boxArray(DEFAULT_COLORS)));
        }
    }
    
    /**
     * Lưu lịch sử màu đã sử dụng
     */
    private void saveRecentColors() {
        // Giới hạn số lượng màu lưu trữ
        while (recentColors.size() > MAX_HISTORY_COLORS) {
            recentColors.remove(recentColors.size() - 1);
        }
        
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < recentColors.size(); i++) {
            builder.append(recentColors.get(i));
            if (i < recentColors.size() - 1) {
                builder.append(",");
            }
        }
        
        preferences.edit().putString(PREF_COLOR_HISTORY, builder.toString()).apply();
    }
    
    /**
     * Chuyển đổi array nguyên thủy sang ArrayList
     */
    private Integer[] boxArray(int[] array) {
        Integer[] boxed = new Integer[array.length];
        for (int i = 0; i < array.length; i++) {
            boxed[i] = array[i];
        }
        return boxed;
    }
    
    /**
     * Thêm màu vào lịch sử
     * @param color Màu cần thêm
     */
    private void addColorToRecent(int color) {
        // Xóa màu này nếu đã tồn tại
        recentColors.remove(Integer.valueOf(color));
        
        // Thêm vào đầu danh sách
        recentColors.add(0, color);
        
        // Lưu lịch sử
        saveRecentColors();
    }
    
    /**
     * Hiển thị hộp thoại chọn màu
     */
    public void showColorPickerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Chọn màu");
        
        View colorPickerView = LayoutInflater.from(context).inflate(getLayoutResourceIdByName("color_picker_dialog"), null);
        builder.setView(colorPickerView);
        
        // Thiết lập các màu có sẵn
        setupColorGrid(colorPickerView);
        
        // Nút đóng
        builder.setNegativeButton("Đóng", (dialog, which) -> dialog.dismiss());
        
        dialog = builder.create();
        dialog.show();
    }
    
    /**
     * Thiết lập lưới các màu
     */
    private void setupColorGrid(View rootView) {
        // TODO: Thay thế bằng tham chiếu trực tiếp đến ID khi tạo layout
        LinearLayout colorGrid = rootView.findViewById(getIdResourceIdByName("color_grid"));
        
        // Thêm màu gần đây
        for (int color : recentColors) {
            addColorView(colorGrid, color);
        }
        
        // Thêm các màu mặc định nếu chưa có trong danh sách gần đây
        for (int color : DEFAULT_COLORS) {
            if (!recentColors.contains(color)) {
                addColorView(colorGrid, color);
            }
        }
        
        // TODO: Thêm nút mở color picker nâng cao (HSV/RGB)
    }
    
    /**
     * Tạo và thêm view màu vào grid
     */
    private void addColorView(LinearLayout parent, int color) {
        // TODO: Thay thế bằng tham chiếu trực tiếp đến layout khi tạo
        View colorView = LayoutInflater.from(context).inflate(getLayoutResourceIdByName("color_item"), parent, false);
        ImageView colorCircle = colorView.findViewById(getIdResourceIdByName("color_circle"));
        
        // Tạo nền tròn với màu
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.OVAL);
        shape.setColor(color);
        colorCircle.setBackground(shape);
        
        // Thêm border nếu là màu hiện tại
        if (color == currentColor) {
            shape.setStroke(4, isDarkColor(color) ? Color.WHITE : Color.BLACK);
        }
        
        // Xử lý sự kiện click
        colorView.setOnClickListener(v -> {
            selectColor(color);
            addColorToRecent(color);
            if (dialog != null) {
                dialog.dismiss();
            }
        });
        
        // Xử lý sự kiện long click để thêm vào favorites
        colorView.setOnLongClickListener(v -> {
            // TODO: Triển khai tính năng thêm vào favorites
            return true;
        });
        
        parent.addView(colorView);
    }
    
    /**
     * Kiểm tra xem màu có tối hay không
     */
    private boolean isDarkColor(int color) {
        return ColorUtils.calculateLuminance(color) < 0.5;
    }
    
    /**
     * Chọn màu và thông báo cho listener
     */
    private void selectColor(int color) {
        currentColor = color;
        
        if (listener != null) {
            listener.onColorSelected(color);
        }
        
        Log.d(TAG, "Color selected: #" + Integer.toHexString(color));
    }
    
    /**
     * Lấy ID resource theo tên
     * Note: Sẽ được thay thế bằng ID trực tiếp sau khi tạo layout
     */
    private int getLayoutResourceIdByName(String name) {
        // TODO: Thay thế bằng R.layout.xxx khi đã tạo layout
        return context.getResources().getIdentifier(name, "layout", context.getPackageName());
    }
    
    /**
     * Lấy ID view theo tên
     * Note: Sẽ được thay thế bằng ID trực tiếp sau khi tạo layout
     */
    private int getIdResourceIdByName(String name) {
        // TODO: Thay thế bằng R.id.xxx khi đã tạo layout
        return context.getResources().getIdentifier(name, "id", context.getPackageName());
    }
    
    /**
     * Lấy màu hiện tại
     * @return Mã màu hiện tại
     */
    public int getSelectedColor() {
        return currentColor;
    }
    
    /**
     * Cài đặt màu hiện tại
     * @param color Mã màu cần đặt
     */
    public void setSelectedColor(int color) {
        selectColor(color);
    }
    
    /**
     * Cài đặt listener khi màu được chọn
     * @param listener ColorSelectedListener
     */
    public void setColorSelectedListener(ColorSelectedListener listener) {
        this.listener = listener;
    }
    
    /**
     * Interface cho sự kiện chọn màu
     */
    public interface ColorSelectedListener {
        void onColorSelected(int color);
    }
} 