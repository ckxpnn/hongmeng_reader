package com.example.harmonyapp;

import ohos.agp.components.Component;
import ohos.agp.components.ComponentContainer;
import ohos.agp.components.DirectionalLayout;
import ohos.agp.components.Text;
import ohos.agp.utils.Color;
import java.util.List;
import java.util.Map;

public class BarChartView extends ComponentContainer {
    public BarChartView(ComponentContainer parent, Map<String, Long> data) {
        super(parent.getContext());
        setWidth(ComponentContainer.LayoutConfig.MATCH_PARENT);
        setHeight(400);
        setPadding(10, 10, 10, 10);
        if (data == null || data.isEmpty()) return;
        long max = 1;
        for (long v : data.values()) max = Math.max(max, v);
        DirectionalLayout bars = new DirectionalLayout(getContext());
        bars.setOrientation(DirectionalLayout.HORIZONTAL);
        bars.setWidth(ComponentContainer.LayoutConfig.MATCH_PARENT);
        bars.setHeight(300);
        for (Map.Entry<String, Long> entry : data.entrySet()) {
            DirectionalLayout barCol = new DirectionalLayout(getContext());
            barCol.setOrientation(DirectionalLayout.VERTICAL);
            barCol.setWidth(40);
            barCol.setHeight(300);
            Component bar = new Component(getContext());
            bar.setWidth(40);
            int barHeight = (int)(entry.getValue() * 250 / max);
            bar.setHeight(barHeight);
            bar.setBackground(new Color(0xFF2196F3));
            Text label = new Text(getContext());
            label.setText(entry.getKey().substring(entry.getKey().length()-2));
            label.setTextSize(20);
            barCol.addComponent(bar);
            barCol.addComponent(label);
            bars.addComponent(barCol);
        }
        addComponent(bars);
    }
}