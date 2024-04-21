package com.example.sky;

import android.graphics.Bitmap;

import java.util.Date;

public class LogItem {
    private String url;
    private String event;

    private String date;

    public LogItem(String url, String event, String date) {
        this.url = url;
        this.event = event;
        this.date = date;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
