package com.example.harmonyapp;

import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Text;
import ohos.agp.components.ComponentContainer;

public class MainAbilitySlice extends AbilitySlice {
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        Text text = new Text(this);
        text.setText("Hello HarmonyOS Java!");
        ComponentContainer.LayoutConfig config = new ComponentContainer.LayoutConfig(
            ComponentContainer.LayoutConfig.MATCH_PARENT,
            ComponentContainer.LayoutConfig.MATCH_PARENT
        );
        text.setLayoutConfig(config);
        super.setUIContent(text);
    }
}