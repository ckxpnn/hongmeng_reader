package com.example.harmonyapp;

import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;

public class ReaderAbility extends Ability {
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setMainRoute(ReaderAbilitySlice.class.getName());
    }
}