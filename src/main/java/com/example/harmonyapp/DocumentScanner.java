package com.example.harmonyapp;

import com.example.harmonyapp.model.DocumentFile;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DocumentScanner {
    
    public static List<DocumentFile> scanDocuments() {
        List<DocumentFile> documents = new ArrayList<>();
        
        // 扫描常见文档目录
        String[] scanPaths = {
            "/storage/emulated/0/Download",
            "/storage/emulated/0/Documents", 
            "/storage/emulated/0/Books",
            "/storage/emulated/0/DCIM"
        };
        
        for (String path : scanPaths) {
            scanDirectory(new File(path), documents);
        }
        
        return documents;
    }
    
    private static void scanDirectory(File directory, List<DocumentFile> documents) {
        if (!directory.exists() || !directory.isDirectory()) {
            return;
        }
        
        File[] files = directory.listFiles();
        if (files == null) return;
        
        for (File file : files) {
            if (file.isFile()) {
                String fileName = file.getName();
                String extension = getFileExtension(fileName);
                
                DocumentFile doc = new DocumentFile(
                    fileName,
                    file.getAbsolutePath(),
                    extension,
                    file.length(),
                    file.lastModified()
                );
                
                if (doc.isSupportedFormat()) {
                    documents.add(doc);
                }
            } else if (file.isDirectory()) {
                // 递归扫描子目录（限制深度避免过深）
                scanDirectory(file, documents);
            }
        }
    }
    
    private static String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            return fileName.substring(lastDotIndex + 1);
        }
        return "";
    }
    
    // 预留扩展接口：支持更多格式
    public static boolean isSupportedFormat(String extension) {
        String ext = extension.toLowerCase();
        return ext.equals("pdf") || ext.equals("mobi") || ext.equals("epub");
    }
    
    // 预留扩展接口：添加新格式支持
    public static void addSupportedFormat(String extension) {
        // TODO: 实现动态添加支持格式的逻辑
    }
}