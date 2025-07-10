package com.example.harmonyapp;

import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.*;
import java.io.*;
import com.example.harmonyapp.model.ReadingRecord;
import ohos.data.preferences.Preferences;
import ohos.data.preferences.PreferencesFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class ReaderAbilitySlice extends AbilitySlice {
    private long startTime;
    private String username;
    private String bookTitle;
    private String filePath;

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        filePath = intent.getStringParam("filePath");
        bookTitle = filePath != null ? new File(filePath).getName() : "阅读";
        username = intent.getStringParam("user");
        if (username == null || username.isEmpty()) {
            Preferences userPrefs = PreferencesFactory.getInstance().getPreferences(this, "user_prefs");
            username = userPrefs.getString("last_login", "");
        }
        startTime = System.currentTimeMillis();
        DirectionalLayout layout = new DirectionalLayout(this);
        layout.setOrientation(DirectionalLayout.VERTICAL);
        layout.setWidth(ComponentContainer.LayoutConfig.MATCH_PARENT);
        layout.setHeight(ComponentContainer.LayoutConfig.MATCH_PARENT);
        layout.setPadding(20, 20, 20, 20);

        Text titleText = new Text(this);
        titleText.setText(bookTitle);
        titleText.setTextSize(50);
        layout.addComponent(titleText);

        ScrollView scrollView = new ScrollView(this);
        scrollView.setWidth(ComponentContainer.LayoutConfig.MATCH_PARENT);
        scrollView.setHeight(ComponentContainer.LayoutConfig.MATCH_PARENT);
        Text contentText = new Text(this);
        contentText.setTextSize(40);
        contentText.setMultipleLine(true);
        contentText.setText(loadTxtContent(filePath));
        scrollView.addComponent(contentText);
        layout.addComponent(scrollView);

        setUIContent(layout);
    }

    @Override
    public void onStop() {
        super.onStop();
        long endTime = System.currentTimeMillis();
        float progress = 1.0f; // TODO: 可根据实际阅读进度实现
        saveReadingRecord(username, bookTitle, filePath, startTime, endTime, progress);
    }

    private String loadTxtContent(String filePath) {
        if (filePath == null) return "未指定文件";
        File file = new File(filePath);
        if (!file.exists()) return "文件不存在";
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            return "读取文件失败: " + e.getMessage();
        }
        return sb.toString();
    }

    private void saveReadingRecord(String username, String bookTitle, String filePath, long startTime, long endTime, float progress) {
        Preferences prefs = PreferencesFactory.getInstance().getPreferences(this, "reading_records");
        String json = prefs.getString(username, "");
        List<ReadingRecord> list = new ArrayList<>();
        try {
            if (!json.isEmpty()) {
                JSONArray arr = new JSONArray(json);
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject o = arr.getJSONObject(i);
                    list.add(new ReadingRecord(
                        o.optString("username"),
                        o.optString("bookTitle"),
                        o.optString("filePath"),
                        o.optLong("startTime"),
                        o.optLong("endTime"),
                        (float) o.optDouble("progress")
                    ));
                }
            }
            list.add(new ReadingRecord(username, bookTitle, filePath, startTime, endTime, progress));
            JSONArray arr = new JSONArray();
            for (ReadingRecord r : list) {
                JSONObject o = new JSONObject();
                o.put("username", r.getUsername());
                o.put("bookTitle", r.getBookTitle());
                o.put("filePath", r.getFilePath());
                o.put("startTime", r.getStartTime());
                o.put("endTime", r.getEndTime());
                o.put("progress", r.getProgress());
                arr.put(o);
            }
            prefs.putString(username, arr.toString());
            prefs.flushSync();
        } catch (Exception ignore) {}
    }
}