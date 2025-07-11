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
import ohos.net.http.HttpClient;
import ohos.net.http.HttpRequest;
import ohos.net.http.HttpStringBody;
import ohos.net.http.HttpResponseCallback;
import ohos.net.http.HttpResponse;
import ohos.app.dispatcher.TaskDispatcher;
import ohos.app.dispatcher.task.TaskPriority;

public class ReaderAbilitySlice extends AbilitySlice {
    private long startTime;
    private String username;
    private String bookTitle;
    private String filePath;
    private float progress = 0.0f;
    private String fileExtension;
    private static final String API_URL = "https://your-backend.com/api/reading/progress";

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
        fileExtension = getFileExtension(filePath);
        // 恢复进度
        progress = ReadingProgressHelper.getProgress(this, username, filePath);
        Component contentComponent = createContentComponent(filePath, fileExtension, progress);
        scrollView.addComponent(contentComponent);
        layout.addComponent(scrollView);

        setUIContent(layout);
    }

    @Override
    public void onStop() {
        super.onStop();
        long endTime = System.currentTimeMillis();
        // TODO: 真实进度获取逻辑
        ReadingProgressHelper.saveProgress(this, username, filePath, progress);
        float uploadProgress = progress;
        uploadProgressToServer(username, filePath, bookTitle, uploadProgress, endTime);
        saveReadingRecord(username, bookTitle, filePath, startTime, endTime, uploadProgress);
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

    private Component createContentComponent(String filePath, String ext, float progress) {
        Text contentText = new Text(this);
        contentText.setTextSize(40);
        contentText.setMultipleLine(true);
        if (ext.equalsIgnoreCase("txt")) {
            contentText.setText(loadTxtContent(filePath));
            // TODO: 可根据progress滚动到指定位置
        } else if (ext.equalsIgnoreCase("pdf")) {
            contentText.setText("[PDF阅读功能待实现]");
        } else if (ext.equalsIgnoreCase("epub")) {
            contentText.setText("[EPUB阅读功能待实现]");
        } else if (ext.equalsIgnoreCase("mobi")) {
            contentText.setText("[MOBI阅读功能待实现]");
        } else {
            contentText.setText("暂不支持该格式: " + ext);
        }
        return contentText;
    }

    private String getFileExtension(String filePath) {
        int lastDot = filePath.lastIndexOf('.');
        if (lastDot > 0 && lastDot < filePath.length() - 1) {
            return filePath.substring(lastDot + 1);
        }
        return "";
    }

    private void uploadProgressToServer(String username, String filePath, String bookTitle, float progress, long timestamp) {
        try {
            org.json.JSONObject o = new org.json.JSONObject();
            o.put("username", username);
            o.put("filePath", filePath);
            o.put("bookTitle", bookTitle);
            o.put("progress", progress);
            o.put("timestamp", timestamp);
            String jsonBody = o.toString();
            TaskDispatcher dispatcher = getGlobalTaskDispatcher(TaskPriority.DEFAULT);
            dispatcher.asyncDispatch(() -> {
                HttpClient client = HttpClient.newBuilder().build();
                HttpRequest request = HttpRequest.newBuilder(API_URL)
                    .setBody(new HttpStringBody(jsonBody))
                    .setMethod("POST")
                    .addHeader("Content-Type", "application/json")
                    .build();
                client.sendRequest(request, new HttpResponseCallback() {
                    @Override
                    public void onResponse(HttpResponse response) {}
                    @Override
                    public void onFailed(Exception e) {}
                });
            });
        } catch (Exception ignore) {}
    }
}