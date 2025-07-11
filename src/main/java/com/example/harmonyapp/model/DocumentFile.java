package com.example.harmonyapp.model;

public class DocumentFile {
    private String fileName;
    private String filePath;
    private String fileExtension;
    private long fileSize;
    private long lastModified;

    public DocumentFile(String fileName, String filePath, String fileExtension, long fileSize, long lastModified) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileExtension = fileExtension;
        this.fileSize = fileSize;
        this.lastModified = lastModified;
    }

    public String getFileName() { return fileName; }
    public String getFilePath() { return filePath; }
    public String getFileExtension() { return fileExtension; }
    public long getFileSize() { return fileSize; }
    public long getLastModified() { return lastModified; }

    public boolean isSupportedFormat() {
        String ext = fileExtension.toLowerCase();
        return ext.equals("pdf") || ext.equals("mobi") || ext.equals("epub");
    }
}