package com.example.harmonyapp;

import com.example.harmonyapp.model.DocumentFile;
import com.example.harmonyapp.model.Book;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.*;
import ohos.data.preferences.Preferences;
import ohos.data.preferences.PreferencesFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class ScanAbilitySlice extends AbilitySlice {
    private ListContainer documentList;
    private List<DocumentFile> documents = new ArrayList<>();
    private Preferences bookshelfPrefs;
    private static final String BOOKS_KEY = "books";

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        DirectionalLayout layout = new DirectionalLayout(this);
        layout.setOrientation(DirectionalLayout.VERTICAL);
        layout.setWidth(ComponentContainer.LayoutConfig.MATCH_PARENT);
        layout.setHeight(ComponentContainer.LayoutConfig.MATCH_PARENT);
        layout.setPadding(20, 20, 20, 20);

        Text titleText = new Text(this);
        titleText.setText("扫描到的文档");
        titleText.setTextSize(50);
        layout.addComponent(titleText);

        Button scanButton = new Button(this);
        scanButton.setText("重新扫描");
        scanButton.setClickedListener(component -> {
            scanDocuments();
        });
        layout.addComponent(scanButton);

        documentList = new ListContainer(this);
        documentList.setWidth(ComponentContainer.LayoutConfig.MATCH_PARENT);
        documentList.setHeight(ComponentContainer.LayoutConfig.MATCH_PARENT);
        layout.addComponent(documentList);

        setUIContent(layout);
        bookshelfPrefs = PreferencesFactory.getInstance().getPreferences(this, "bookshelf_prefs");
        scanDocuments();
    }

    private void scanDocuments() {
        documents = DocumentScanner.scanDocuments();
        updateDocumentList();
    }

    private void updateDocumentList() {
        documentList.setItemProvider(new DocumentItemProvider(documents));
        documentList.setItemClickedListener((container, component, pos, id) -> {
            DocumentFile doc = documents.get(pos);
            addToBookshelf(doc);
        });
    }

    private void addToBookshelf(DocumentFile doc) {
        try {
            // 读取现有书架数据
            String booksJson = bookshelfPrefs.getString(BOOKS_KEY, "");
            JSONArray booksArray;
            if (booksJson.isEmpty()) {
                booksArray = new JSONArray();
            } else {
                booksArray = new JSONArray(booksJson);
            }

            // 检查是否已存在
            boolean exists = false;
            for (int i = 0; i < booksArray.length(); i++) {
                JSONObject book = booksArray.getJSONObject(i);
                if (book.optString("filePath").equals(doc.getFilePath())) {
                    exists = true;
                    break;
                }
            }

            if (!exists) {
                // 添加新书到书架
                JSONObject newBook = new JSONObject();
                newBook.put("title", doc.getFileName());
                newBook.put("filePath", doc.getFilePath());
                newBook.put("coverPath", ""); // 默认封面路径
                booksArray.put(newBook);
                
                bookshelfPrefs.putString(BOOKS_KEY, booksArray.toString());
                bookshelfPrefs.flushSync();
                
                // 显示成功提示
                showToast("已添加到书架：" + doc.getFileName());
            } else {
                showToast("该书籍已在书架中");
            }
        } catch (Exception e) {
            showToast("添加失败：" + e.getMessage());
        }
    }

    private void showToast(String message) {
        // 简单的提示显示
        Text toastText = new Text(this);
        toastText.setText(message);
        toastText.setTextSize(30);
        // 这里可以添加更美观的Toast实现
    }

    // 文档列表适配器
    private static class DocumentItemProvider extends BaseItemProvider {
        private final List<DocumentFile> docs;
        
        DocumentItemProvider(List<DocumentFile> docs) {
            this.docs = docs;
        }
        
        @Override
        public int getCount() { return docs.size(); }
        @Override
        public Object getItem(int i) { return docs.get(i); }
        @Override
        public long getItemId(int i) { return i; }
        
        @Override
        public Component getComponent(int i, Component component, ComponentContainer parent) {
            DirectionalLayout itemLayout = new DirectionalLayout(parent.getContext());
            itemLayout.setOrientation(DirectionalLayout.HORIZONTAL);
            itemLayout.setWidth(ComponentContainer.LayoutConfig.MATCH_PARENT);
            itemLayout.setHeight(120);
            itemLayout.setPadding(10, 10, 10, 10);

            DocumentFile doc = docs.get(i);
            
            Text titleText = new Text(parent.getContext());
            titleText.setText(doc.getFileName());
            titleText.setTextSize(40);
            titleText.setWidth(300);
            
            Text formatText = new Text(parent.getContext());
            formatText.setText("格式: " + doc.getFileExtension().toUpperCase());
            formatText.setTextSize(30);
            formatText.setWidth(150);
            
            Text sizeText = new Text(parent.getContext());
            sizeText.setText("大小: " + formatFileSize(doc.getFileSize()));
            sizeText.setTextSize(30);
            sizeText.setWidth(200);
            
            Button addButton = new Button(parent.getContext());
            addButton.setText("添加到书架");
            addButton.setWidth(200);
            
            itemLayout.addComponent(titleText);
            itemLayout.addComponent(formatText);
            itemLayout.addComponent(sizeText);
            itemLayout.addComponent(addButton);
            
            return itemLayout;
        }
        
        private String formatFileSize(long size) {
            if (size < 1024) return size + " B";
            if (size < 1024 * 1024) return String.format("%.1f KB", size / 1024.0);
            return String.format("%.1f MB", size / (1024.0 * 1024.0));
        }
    }
}