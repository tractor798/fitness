package com.example.fitness.ui.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * 设置页，提供基础偏好与数据安全操作。
 */
public class SettingsActivity extends BaseActivity {

    private ServiceLocator serviceLocator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        serviceLocator = ServiceLocator.getInstance(this);
        setupSettingsItems();
    }

    private void setupSettingsItems() {
        LinearLayout notificationSettings = findViewById(R.id.notificationSettings);
        if (notificationSettings != null) {
            notificationSettings.setOnClickListener(v -> showFeatureNotAvailableDialog("通知设置"));
        }

        LinearLayout linkedApps = findViewById(R.id.linkedApps);
        if (linkedApps != null) {
            linkedApps.setOnClickListener(v -> showFeatureNotAvailableDialog("关联应用"));
        }

        LinearLayout privacySecurity = findViewById(R.id.privacySecurity);
        if (privacySecurity != null) {
            privacySecurity.setOnClickListener(v -> showPrivacyDialog());
        }

        LinearLayout clearData = findViewById(R.id.clearData);
        if (clearData != null) {
            clearData.setOnClickListener(v -> showClearDataDialog());
        }

        LinearLayout aboutApp = findViewById(R.id.aboutApp);
        if (aboutApp != null) {
            aboutApp.setOnClickListener(v -> showAboutDialog());
        }
    }

    private void showFeatureNotAvailableDialog(String featureName) {
        new AlertDialog.Builder(this)
                .setTitle(featureName)
                .setMessage(featureName + " 功能将在后续版本开放。")
                .setPositiveButton("确定", null)
                .show();
    }

    private void showPrivacyDialog() {
        new AlertDialog.Builder(this)
                .setTitle("隐私与安全")
                .setMessage("所有数据仅存储在本地，不会上云。你可以随时在设置中清除全部数据。")
                .setPositiveButton("确定", null)
                .show();
    }

    private void showClearDataDialog() {
        new AlertDialog.Builder(this)
                .setTitle("清除数据")
                .setMessage("确认清除所有训练、身体指标和计划数据吗？该操作不可恢复。")
                .setPositiveButton("清除", (dialog, which) -> {
                    serviceLocator.clearAllData();
                    Toast.makeText(this, "所有数据已清除", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void showAboutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("关于 Zen Kinetic")
                .setMessage("版本 1.0.0\n离线个人健康管理系统。")
                .setPositiveButton("确定", null)
                .show();
    }
}
