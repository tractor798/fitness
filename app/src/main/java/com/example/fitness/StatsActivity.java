package com.example.fitness;

import android.os.Bundle;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class StatsActivity extends BaseActivity {

    private TextView todayWorkoutsText;
    private TextView todayDurationText;
    private TextView todayCaloriesText;
    private TextView weekWorkoutsText;
    private TextView weekDurationText;
    private TextView weekCaloriesText;
    private RecyclerView historyRecyclerView;
    private WorkoutHistoryAdapter historyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        setupBottomNavigation(2);

        initializeViews();
        loadStatsData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 每次回到页面时刷新统计数据
        loadStatsData();
    }

    private void initializeViews() {
        todayWorkoutsText = findViewById(R.id.todayWorkoutsText);
        todayDurationText = findViewById(R.id.todayDurationText);
        todayCaloriesText = findViewById(R.id.todayCaloriesText);
        weekWorkoutsText = findViewById(R.id.weekWorkoutsText);
        weekDurationText = findViewById(R.id.weekDurationText);
        weekCaloriesText = findViewById(R.id.weekCaloriesText);
        historyRecyclerView = findViewById(R.id.historyRecyclerView);

        if (historyRecyclerView != null) {
            historyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        }
    }

    private void loadStatsData() {
        PlanDataManager dataManager = PlanDataManager.getInstance(this);

        // 今日统计
        WorkoutStats todayStats = dataManager.getTodayStats();
        updateStatsDisplay(todayWorkoutsText, todayDurationText, todayCaloriesText, todayStats);

        // 本周统计
        WorkoutStats weekStats = dataManager.getWeekStats();
        updateStatsDisplay(weekWorkoutsText, weekDurationText, weekCaloriesText, weekStats);

        // 训练历史
        List<WorkoutSession> history = dataManager.getWorkoutHistory();
        if (historyAdapter == null) {
            historyAdapter = new WorkoutHistoryAdapter(history);
            historyRecyclerView.setAdapter(historyAdapter);
        } else {
            historyAdapter.updateHistory(history);
        }
    }

    private void updateStatsDisplay(TextView workoutsText, TextView durationText,
                                  TextView caloriesText, WorkoutStats stats) {
        if (workoutsText != null) {
            workoutsText.setText(String.valueOf(stats.getTotalWorkouts()));
        }
        if (durationText != null) {
            durationText.setText(stats.getFormattedDuration());
        }
        if (caloriesText != null) {
            caloriesText.setText(stats.getTotalCalories() + " kcal");
        }
    }
}
