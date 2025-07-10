package com.example.harmonyapp;

import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.*;
import java.io.*;

public class ReaderAbilitySlice extends AbilitySlice {
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        String filePath = intent.getStringParam("filePath");
        String title = filePath != null ? new File(filePath).getName() : "阅读";
        DirectionalLayout layout = new DirectionalLayout(this);
        layout.setOrientation(DirectionalLayout.VERTICAL);
        layout.setWidth(ComponentContainer.LayoutConfig.MATCH_PARENT);
        layout.setHeight(ComponentContainer.LayoutConfig.MATCH_PARENT);
        layout.setPadding(20, 20, 20, 20);

        Text titleText = new Text(this);
        titleText.setText(title);
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
}