package com.example.harmonyapp;

import com.example.harmonyapp.model.ReadingRecord;
import java.util.*;

public class ReadingStatsHelper {
    public static Map<String, Long> aggregateByDay(List<ReadingRecord> records) {
        Map<String, Long> map = new TreeMap<>();
        for (ReadingRecord r : records) {
            String day = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new Date(r.getStartTime()));
            long duration = r.getEndTime() - r.getStartTime();
            map.put(day, map.getOrDefault(day, 0L) + duration);
        }
        return map;
    }
    public static Map<String, Long> aggregateByWeek(List<ReadingRecord> records) {
        Map<String, Long> map = new TreeMap<>();
        java.text.SimpleDateFormat fmt = new java.text.SimpleDateFormat("yyyy-ww");
        for (ReadingRecord r : records) {
            String week = fmt.format(new Date(r.getStartTime()));
            long duration = r.getEndTime() - r.getStartTime();
            map.put(week, map.getOrDefault(week, 0L) + duration);
        }
        return map;
    }
    public static Map<String, Long> aggregateByMonth(List<ReadingRecord> records) {
        Map<String, Long> map = new TreeMap<>();
        java.text.SimpleDateFormat fmt = new java.text.SimpleDateFormat("yyyy-MM");
        for (ReadingRecord r : records) {
            String month = fmt.format(new Date(r.getStartTime()));
            long duration = r.getEndTime() - r.getStartTime();
            map.put(month, map.getOrDefault(month, 0L) + duration);
        }
        return map;
    }
    public static Map<String, Long> aggregateByYear(List<ReadingRecord> records) {
        Map<String, Long> map = new TreeMap<>();
        java.text.SimpleDateFormat fmt = new java.text.SimpleDateFormat("yyyy");
        for (ReadingRecord r : records) {
            String year = fmt.format(new Date(r.getStartTime()));
            long duration = r.getEndTime() - r.getStartTime();
            map.put(year, map.getOrDefault(year, 0L) + duration);
        }
        return map;
    }
}