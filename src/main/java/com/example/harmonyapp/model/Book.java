package com.example.harmonyapp.model;

public class Book {
    private String title;
    private String filePath;
    private String coverPath;

    public Book(String title, String filePath, String coverPath) {
        this.title = title;
        this.filePath = filePath;
        this.coverPath = coverPath;
    }

    public String getTitle() {
        return title;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getCoverPath() {
        return coverPath;
    }
}