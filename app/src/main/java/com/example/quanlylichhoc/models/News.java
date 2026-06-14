package com.example.quanlylichhoc.models;

import java.io.Serializable;

public class News implements Serializable {
    private String id;
    private String title;
    private String content;
    private String date;
    private String imageUrl; // For now, we can use a placeholder or resource ID string

    public News(String id, String title, String content, String date) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.date = date;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getDate() { return date; }
}
