package com.example.fitness;

import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends BaseActivity {

    private TextView todayWorkoutsText;
    private TextView todayCaloriesText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupBottomNavigation(0);

        initializeViews();
        loadTodayStats();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 每次回到首页时刷新统计数据
        loadTodayStats();
    }

    private void initializeViews() {
        todayWorkoutsText = findViewById(R.id.todayWorkoutsText);
        todayCaloriesText = findViewById(R.id.todayCaloriesText);
    }

    private void loadTodayStats() {
        PlanDataManager dataManager = PlanDataManager.getInstance(this);
        WorkoutStats todayStats = dataManager.getTodayStats();

        if (todayWorkoutsText != null) {
            todayWorkoutsText.setText(String.valueOf(todayStats.getTotalWorkouts()));
        }
        if (todayCaloriesText != null) {
            todayCaloriesText.setText(todayStats.getTotalCalories() + " kcal");
        }
    }
}