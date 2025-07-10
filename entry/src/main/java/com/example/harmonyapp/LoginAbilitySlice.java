package com.example.harmonyapp;

import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.*;
import ohos.agp.window.service.Window;
import ohos.data.preferences.Preferences;
import ohos.data.preferences.PreferencesFactory;

public class LoginAbilitySlice extends AbilitySlice {
    private TextField usernameField;
    private TextField passwordField;
    private Button loginButton;
    private Button registerButton;
    private Text tipText;
    private Preferences preferences;

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        ComponentContainer layout = new DirectionalLayout(this);
        ((DirectionalLayout) layout).setOrientation(DirectionalLayout.VERTICAL);
        layout.setWidth(ComponentContainer.LayoutConfig.MATCH_PARENT);
        layout.setHeight(ComponentContainer.LayoutConfig.MATCH_PARENT);
        layout.setPadding(50, 100, 50, 50);

        usernameField = new TextField(this);
        usernameField.setHint("用户名");
        passwordField = new TextField(this);
        passwordField.setHint("密码");
        passwordField.setTextInputType(TextField.PATTERN_PASSWORD);

        loginButton = new Button(this);
        loginButton.setText("登录");
        registerButton = new Button(this);
        registerButton.setText("注册");
        tipText = new Text(this);

        layout.addComponent(usernameField);
        layout.addComponent(passwordField);
        layout.addComponent(loginButton);
        layout.addComponent(registerButton);
        layout.addComponent(tipText);

        setUIContent(layout);

        preferences = PreferencesFactory.getInstance().getPreferences(this, "user_prefs");

        loginButton.setClickedListener(component -> login());
        registerButton.setClickedListener(component -> register());
    }

    private void login() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String savedPassword = preferences.getString(username, "");
        if (!savedPassword.isEmpty() && savedPassword.equals(password)) {
            tipText.setText("登录成功");
            // 跳转到主页面
            present(new Intent().setParam("user", username), MainAbility.class.getName());
        } else {
            tipText.setText("用户名或密码错误");
        }
    }

    private void register() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        if (username.isEmpty() || password.isEmpty()) {
            tipText.setText("用户名和密码不能为空");
            return;
        }
        if (!preferences.getString(username, "").isEmpty()) {
            tipText.setText("用户已存在");
            return;
        }
        preferences.putString(username, password);
        preferences.flushSync();
        tipText.setText("注册成功，请登录");
    }
}