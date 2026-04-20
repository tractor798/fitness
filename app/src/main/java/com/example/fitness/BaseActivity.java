package com.example.fitness;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {

    protected void setupBottomNavigation(int selectedIndex) {
        setNavClick(R.id.navHome, MainActivity.class, selectedIndex == 0);
        setNavClick(R.id.navPlans, PlanActivity.class, selectedIndex == 1);
        setNavClick(R.id.navStats, StatsActivity.class, selectedIndex == 2);
        setNavClick(R.id.navProfile, ProfileActivity.class, selectedIndex == 3);
    }

    private void setNavClick(int viewId, final Class<?> activityClass, boolean selected) {
        View navView = findViewById(viewId);
        if (navView == null) {
            return;
        }

        ImageView icon = navView.findViewById(R.id.icon);
        TextView label = navView.findViewById(R.id.label);
        int selectedColor = ContextCompat.getColor(this, R.color.primary);
        int unselectedColor = ContextCompat.getColor(this, R.color.onSurfaceVariant);

        if (icon != null) {
            icon.setColorFilter(selected ? selectedColor : unselectedColor);
        }
        if (label != null) {
            label.setTextColor(selected ? selectedColor : unselectedColor);
        }

        navView.setOnClickListener(v -> {
            if (!selected) {
                startActivity(new Intent(this, activityClass));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
    }
}
