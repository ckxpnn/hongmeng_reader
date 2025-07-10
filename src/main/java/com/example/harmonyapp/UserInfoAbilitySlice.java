package com.example.harmonyapp;

import com.example.harmonyapp.model.ReadingRecord;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.*;
import ohos.data.preferences.Preferences;
import ohos.data.preferences.PreferencesFactory;
import java.util.*;

public class UserInfoAbilitySlice extends AbilitySlice {
    private String username;
    private Preferences userPrefs;
    private Preferences recordPrefs;
    private List<ReadingRecord> records = new ArrayList<>();
    private String statsMode = "day";
    private BarChartView barChartView;
    private DirectionalLayout statsLayout;
    private ListContainer recordList;

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        username = intent.getStringParam("user");
        if (username == null || username.isEmpty()) {
            username = userPrefs().getString("last_login", "");
        }
        DirectionalLayout layout = new DirectionalLayout(this);
        layout.setOrientation(DirectionalLayout.VERTICAL);
        layout.setWidth(ComponentContainer.LayoutConfig.MATCH_PARENT);
        layout.setHeight(ComponentContainer.LayoutConfig.MATCH_PARENT);
        layout.setPadding(20, 20, 20, 20);

        // 用户名显示
        Text userText = new Text(this);
        userText.setText("当前用户：" + username);
        userText.setTextSize(40);
        layout.addComponent(userText);

        // 重设密码
        TextField newPwd = new TextField(this);
        newPwd.setHint("新密码");
        Button resetBtn = new Button(this);
        resetBtn.setText("重设密码");
        Text resetTip = new Text(this);
        resetBtn.setClickedListener(c -> {
            if (newPwd.getText().isEmpty()) {
                resetTip.setText("密码不能为空");
                return;
            }
            userPrefs().putString(username, newPwd.getText());
            userPrefs().flushSync();
            resetTip.setText("密码已重设");
        });
        layout.addComponent(newPwd);
        layout.addComponent(resetBtn);
        layout.addComponent(resetTip);

        // 统计切换按钮
        DirectionalLayout btns = new DirectionalLayout(this);
        btns.setOrientation(DirectionalLayout.HORIZONTAL);
        String[] modes = {"年", "月", "周", "天"};
        String[] modeKeys = {"year", "month", "week", "day"};
        for (int i = 0; i < modes.length; i++) {
            Button b = new Button(this);
            b.setText(modes[i]);
            String key = modeKeys[i];
            b.setClickedListener(c -> {
                statsMode = key;
                updateStats();
            });
            btns.addComponent(b);
        }
        layout.addComponent(btns);

        // 柱状图区域
        statsLayout = new DirectionalLayout(this);
        statsLayout.setOrientation(DirectionalLayout.VERTICAL);
        layout.addComponent(statsLayout);

        // 阅读数据列表
        recordList = new ListContainer(this);
        layout.addComponent(recordList);
        recordList.setWidth(ComponentContainer.LayoutConfig.MATCH_PARENT);
        recordList.setHeight(400);

        setUIContent(layout);
        recordPrefs = PreferencesFactory.getInstance().getPreferences(this, "reading_records");
        loadRecords();
        updateStats();
        updateRecordList();
    }

    private Preferences userPrefs() {
        if (userPrefs == null) {
            userPrefs = PreferencesFactory.getInstance().getPreferences(this, "user_prefs");
        }
        return userPrefs;
    }

    private void loadRecords() {
        records.clear();
        String json = recordPrefs.getString(username, "");
        if (!json.isEmpty()) {
            try {
                org.json.JSONArray arr = new org.json.JSONArray(json);
                for (int i = 0; i < arr.length(); i++) {
                    org.json.JSONObject o = arr.getJSONObject(i);
                    records.add(new ReadingRecord(
                        o.optString("username"),
                        o.optString("bookTitle"),
                        o.optString("filePath"),
                        o.optLong("startTime"),
                        o.optLong("endTime"),
                        (float) o.optDouble("progress")
                    ));
                }
            } catch (Exception ignore) {}
        }
    }

    private void updateStats() {
        statsLayout.removeAllComponents();
        Map<String, Long> data;
        switch (statsMode) {
            case "year":
                data = ReadingStatsHelper.aggregateByYear(records); break;
            case "month":
                data = ReadingStatsHelper.aggregateByMonth(records); break;
            case "week":
                data = ReadingStatsHelper.aggregateByWeek(records); break;
            default:
                data = ReadingStatsHelper.aggregateByDay(records); break;
        }
        barChartView = new BarChartView(statsLayout, data);
        statsLayout.addComponent(barChartView);
    }

    private void updateRecordList() {
        recordList.setItemProvider(new BaseItemProvider() {
            @Override
            public int getCount() { return records.size(); }
            @Override
            public Object getItem(int i) { return records.get(i); }
            @Override
            public long getItemId(int i) { return i; }
            @Override
            public Component getComponent(int i, Component c, ComponentContainer p) {
                DirectionalLayout l = new DirectionalLayout(p.getContext());
                l.setOrientation(DirectionalLayout.HORIZONTAL);
                l.setWidth(ComponentContainer.LayoutConfig.MATCH_PARENT);
                l.setHeight(100);
                ReadingRecord r = records.get(i);
                Text t1 = new Text(p.getContext());
                t1.setText(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(r.getStartTime())));
                t1.setWidth(250);
                Text t2 = new Text(p.getContext());
                t2.setText(r.getBookTitle());
                t2.setWidth(200);
                Text t3 = new Text(p.getContext());
                t3.setText("进度:" + (int)(r.getProgress()*100) + "%");
                t3.setWidth(120);
                Text t4 = new Text(p.getContext());
                t4.setText("最后阅读:" + new java.text.SimpleDateFormat("MM-dd HH:mm").format(new Date(r.getEndTime())));
                t4.setWidth(200);
                l.addComponent(t1); l.addComponent(t2); l.addComponent(t3); l.addComponent(t4);
                return l;
            }
        });
    }
}