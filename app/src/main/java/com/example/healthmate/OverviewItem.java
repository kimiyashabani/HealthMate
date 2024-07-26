package com.example.healthmate;

import android.widget.ImageView;

public class OverviewItem {
    private int listImage;
    private String title;
    private String lastUpdated;

    public OverviewItem(int listImage, String title, String lastUpdated) {
        this.listImage = listImage;
        this.title = title;
        this.lastUpdated = lastUpdated;
    }

    public int getListImage() {
        return listImage;
    }

    public void setListImage(int listImage) {
        this.listImage = listImage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
