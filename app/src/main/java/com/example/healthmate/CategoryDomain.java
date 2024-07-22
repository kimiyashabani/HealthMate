package com.example.healthmate;

public class CategoryDomain {
    private String category;
    private int picPath;
    private int backgroundColor;

    public CategoryDomain(String category, int picPath, int backgroundColor) {
        this.category = category;
        this.picPath = picPath;
        this.backgroundColor = backgroundColor;

    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getPicPath() {
        return picPath;
    }

    public void setPicPath(int picPath) {
        this.picPath = picPath;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }
}
