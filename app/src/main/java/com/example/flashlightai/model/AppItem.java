package com.example.flashlightai.model;

import android.graphics.drawable.Drawable;

/**
 * Model class lưu thông tin ứng dụng cho danh sách chọn
 */
public class AppItem {
    private String appName;
    private String packageName;
    private Drawable icon;
    private boolean selected;
    
    public AppItem(String appName, String packageName, Drawable icon, boolean selected) {
        this.appName = appName;
        this.packageName = packageName;
        this.icon = icon;
        this.selected = selected;
    }
    
    public String getAppName() {
        return appName;
    }
    
    public void setAppName(String appName) {
        this.appName = appName;
    }
    
    public String getPackageName() {
        return packageName;
    }
    
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
    
    public Drawable getIcon() {
        return icon;
    }
    
    public void setIcon(Drawable icon) {
        this.icon = icon;
    }
    
    public boolean isSelected() {
        return selected;
    }
    
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        AppItem appItem = (AppItem) obj;
        return packageName.equals(appItem.packageName);
    }
    
    @Override
    public int hashCode() {
        return packageName.hashCode();
    }
} 