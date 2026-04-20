package com.example.fitness;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

/**
 * 个人中心页。
 */
public class ProfileActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setupBottomNavigation(3);

        ServiceLocator serviceLocator = ServiceLocator.getInstance(this);

        LinearLayout privacySettings = findViewById(R.id.privacySettings);
        if (privacySettings != null) {
            privacySettings.setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));
        }
    }
}
