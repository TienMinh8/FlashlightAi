package com.example.flashlightai.model;

/**
 * Model lưu trữ thông tin ngôn ngữ
 */
public class Language {
    private String code;
    private String name;
    private int flagResourceId;
    private boolean selected;

    public Language(String code, String name, int flagResourceId, boolean selected) {
        this.code = code;
        this.name = name;
        this.flagResourceId = flagResourceId;
        this.selected = selected;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFlagResourceId() {
        return flagResourceId;
    }

    public void setFlagResourceId(int flagResourceId) {
        this.flagResourceId = flagResourceId;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
} 