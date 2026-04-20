package com.example.fitness;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

public class ProfileActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setupBottomNavigation(3);

        // 设置点击监听器
        LinearLayout privacySettings = findViewById(R.id.privacySettings);
        if (privacySettings != null) {
            privacySettings.setOnClickListener(v -> {
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
            });
        }
    }
}
