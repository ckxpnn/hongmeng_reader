package com.example.harmonyapp;

import ohos.data.preferences.Preferences;
import ohos.data.preferences.PreferencesFactory;

public class ReadingProgressHelper {
    public static void saveProgress(ohos.app.Context context, String username, String filePath, float progress) {
        Preferences prefs = PreferencesFactory.getInstance().getPreferences(context, "reading_progress");
        String key = username + "::" + filePath;
        prefs.putFloat(key, progress);
        prefs.flushSync();
    }

    public static float getProgress(ohos.app.Context context, String username, String filePath) {
        Preferences prefs = PreferencesFactory.getInstance().getPreferences(context, "reading_progress");
        String key = username + "::" + filePath;
        return prefs.getFloat(key, 0.0f);
    }
}