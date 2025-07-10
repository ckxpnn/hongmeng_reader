package com.example.harmonyapp.model;

public class ReadingRecord {
    private String username;
    private String bookTitle;
    private String filePath;
    private long startTime;
    private long endTime;
    private float progress;

    public ReadingRecord(String username, String bookTitle, String filePath, long startTime, long endTime, float progress) {
        this.username = username;
        this.bookTitle = bookTitle;
        this.filePath = filePath;
        this.startTime = startTime;
        this.endTime = endTime;
        this.progress = progress;
    }

    public String getUsername() { return username; }
    public String getBookTitle() { return bookTitle; }
    public String getFilePath() { return filePath; }
    public long getStartTime() { return startTime; }
    public long getEndTime() { return endTime; }
    public float getProgress() { return progress; }
}