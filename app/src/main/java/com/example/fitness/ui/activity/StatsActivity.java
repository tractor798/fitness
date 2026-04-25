package com.example.fitness.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.example.fitness.config.AppConfig;
import com.example.fitness.domain.model.BodyMetric;
import com.example.fitness.domain.model.WorkoutSession;
import com.example.fitness.ui.view.LineChartView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 进度统计页
 */
public class StatsActivity extends BaseActivity {

    private TextView currentWeightText;
    private TextView weightChangeText;
    private LineChartView weightTrendChart;
    private LinearLayout weekDotsContainer;
    private TextView streakText;
    private ProgressBar streakProgress;
    private FloatingActionButton addMetricFab;
    private MaterialCardView workoutHistoryCard;

    // 个人纪录荣誉殿堂
    private TextView maxWeightText;
    private TextView maxWeightDetailText;
    private TextView longestStreakText;
    private TextView longestStreakDetailText;
    private TextView longestWorkoutText;
    private TextView longestWorkoutDetailText;

    private ServiceLocator serviceLocator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        setupBottomNavigation(2);

        serviceLocator = ServiceLocator.getInstance(this);
        initializeViews();
        loadData();
        setupListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void initializeViews() {
        currentWeightText = findViewById(R.id.currentWeightText);
        weightChangeText = findViewById(R.id.weightChangeText);
        weightTrendChart = findViewById(R.id.weightTrendChart);
        weekDotsContainer = findViewById(R.id.weekDotsContainer);
        streakText = findViewById(R.id.streakText);
        streakProgress = findViewById(R.id.streakProgress);
        addMetricFab = findViewById(R.id.addMetricFab);
        workoutHistoryCard = findViewById(R.id.workoutHistoryCard);

        // 个人纪录荣誉殿堂
        maxWeightText = findViewById(R.id.maxWeightText);
        maxWeightDetailText = findViewById(R.id.maxWeightDetailText);
        longestStreakText = findViewById(R.id.longestStreakText);
        longestStreakDetailText = findViewById(R.id.longestStreakDetailText);
        longestWorkoutText = findViewById(R.id.longestWorkoutText);
        longestWorkoutDetailText = findViewById(R.id.longestWorkoutDetailText);
    }

    @SuppressLint("NewApi")
    private void loadData() {
        loadWeightTrend();
        loadWeeklyActivity();
        loadPersonalRecords();
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    private void loadWeightTrend() {
        List<BodyMetric> metrics = new ArrayList<>(
                serviceLocator.getBodyMetricRepository().getMetricsForDays(AppConfig.WEIGHT_TREND_DAYS)
        );
        metrics.sort(Comparator.comparingLong(BodyMetric::getTimestamp));

        if (metrics.isEmpty()) {
            if (currentWeightText != null) {
                currentWeightText.setText("-- kg");
            }
            if (weightChangeText != null) {
                weightChangeText.setText("暂无身体数据");
                weightChangeText.setTextColor(getColor(R.color.onSurfaceVariant));
            }
            if (weightTrendChart != null) {
                // 如果没有数据，显示一条平线或者不显示
                weightTrendChart.setDataWithAnimation(new float[]{0f}, 600);
            }
            return;
        }

        float[] weightData = new float[metrics.size()];
        for (int i = 0; i < metrics.size(); i++) {
            weightData[i] = (float) metrics.get(i).getWeight();
        }

        float currentWeight = weightData[weightData.length - 1];
        float change = weightData[weightData.length - 1] - weightData[0];

        if (currentWeightText != null) {
            currentWeightText.setText(String.format("%.1f kg", currentWeight));
        }
        
        if (weightChangeText != null) {
            @SuppressLint("DefaultLocale") String changeText = String.format("%+.1f kg", change);
            weightChangeText.setText(changeText);
            int colorRes = change <= 0 ? R.color.tertiary : R.color.secondary;
            weightChangeText.setTextColor(getColor(colorRes));
        }

        if (weightTrendChart != null) {
            // 设置标签（例如日期）
            String[] labels = new String[metrics.size()];
            for (int i = 0; i < metrics.size(); i++) {
                // 简单起见，这里只显示最后几条数据的标签，或者根据需求自定义
                labels[i] = "第" + (i + 1) + "次"; 
            }
            weightTrendChart.setLabels(labels);
            weightTrendChart.setDataWithAnimation(weightData, 1200);
        }
    }

    @SuppressLint("DefaultLocale")
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void loadWeeklyActivity() {
        if (weekDotsContainer == null) {
            return;
        }

        weekDotsContainer.removeAllViews();

        Set<LocalDate> workoutDays = extractWorkoutDays();
        LocalDate today = LocalDate.now();
        LocalDate monday = today.minusDays(today.getDayOfWeek().getValue() - 1L);
        boolean[] activeDays = new boolean[7];
        for (int i = 0; i < 7; i++) {
            LocalDate day = monday.plusDays(i);
            activeDays[i] = workoutDays.contains(day);
        }

        String[] dayLabels = {"一", "二", "三", "四", "五", "六", "日"};
        for (int i = 0; i < 7; i++) {
            View dayView = LayoutInflater.from(this).inflate(R.layout.item_week_day, weekDotsContainer, false);
            TextView dayLabel = dayView.findViewById(R.id.dayLabel);
            View dayDot = dayView.findViewById(R.id.dayDot);

            dayLabel.setText(dayLabels[i]);
            dayDot.setBackgroundResource(activeDays[i] ? R.drawable.bg_day_dot_active : R.drawable.bg_day_dot_inactive);
            weekDotsContainer.addView(dayView);
        }

        int streakDays = calculateStreakDays(workoutDays, today);
        if (streakText != null) {
            streakText.setText(String.format("连续达标 %d 天", streakDays));
        }
        if (streakProgress != null) {
            int progress = (streakDays * 100) / AppConfig.STREAK_GOAL_DAYS;
            streakProgress.setProgress(Math.min(100, progress));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private Set<LocalDate> extractWorkoutDays() {
        List<WorkoutSession> sessions = serviceLocator.getWorkoutRepository().getAllSessions();
        Set<LocalDate> days = new HashSet<>();
        if (sessions == null) {
            return days;
        }
        for (WorkoutSession session : sessions) {
            long timestamp = session.getEndTime() > 0 ? session.getEndTime() : session.getStartTime();
            LocalDate date = java.time.Instant.ofEpochMilli(timestamp)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            days.add(date);
        }
        return days;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private int calculateStreakDays(Set<LocalDate> workoutDays, LocalDate today) {
        if (workoutDays.isEmpty()) {
            return 0;
        }
        int streak = 0;
        LocalDate cursor = today;
        while (workoutDays.contains(cursor)) {
            streak++;
            cursor = cursor.minusDays(1);
        }
        return streak;
    }

    /**
     * 加载个人纪录荣誉殿堂数据
     */
    @SuppressLint("DefaultLocale")
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void loadPersonalRecords() {
        // 1. 最高消耗卡路里（替代最高负重，因为当前数据结构不支持重量）
        loadMaxCaloriesRecord();

        // 2. 最长连续训练天数
        loadLongestStreakRecord();

        // 3. 单次最长训练时长
        loadLongestWorkoutRecord();
    }

    private void loadMaxCaloriesRecord() {
        List<WorkoutSession> sessions = serviceLocator.getWorkoutRepository().getAllSessions();
        int maxCalories = 0;
        long recordDate = 0;

        for (WorkoutSession session : sessions) {
            int calories = session.getCalories();
            if (calories > maxCalories) {
                maxCalories = calories;
                recordDate = session.getStartTime();
            }
        }

        if (maxWeightText != null) {
            if (maxCalories > 0) {
                maxWeightText.setText(maxCalories + " kcal");
            } else {
                maxWeightText.setText("-- kcal");
            }
        }

        if (maxWeightDetailText != null) {
            if (recordDate > 0) {
                String dateStr = new java.text.SimpleDateFormat("yyyy.MM.dd", java.util.Locale.getDefault())
                        .format(new java.util.Date(recordDate));
                maxWeightDetailText.setText("单次最高消耗 · " + dateStr);
            } else {
                maxWeightDetailText.setText("暂无记录");
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void loadLongestStreakRecord() {
        Set<LocalDate> workoutDays = extractWorkoutDays();
        if (workoutDays.isEmpty()) {
            if (longestStreakText != null) {
                longestStreakText.setText("-- 天");
            }
            if (longestStreakDetailText != null) {
                longestStreakDetailText.setText("暂无记录");
            }
            return;
        }

        // 计算最长连续天数
        List<LocalDate> sortedDays = new ArrayList<>(workoutDays);
        sortedDays.sort(Comparator.naturalOrder());

        int maxStreak = 1;
        int currentStreak = 1;
        for (int i = 1; i < sortedDays.size(); i++) {
            if (sortedDays.get(i).minusDays(1).equals(sortedDays.get(i - 1))) {
                currentStreak++;
                maxStreak = Math.max(maxStreak, currentStreak);
            } else {
                currentStreak = 1;
            }
        }

        if (longestStreakText != null) {
            longestStreakText.setText(maxStreak + " 天");
        }

        if (longestStreakDetailText != null) {
            longestStreakDetailText.setText("历史最佳连续训练");
        }
    }

    private void loadLongestWorkoutRecord() {
        List<WorkoutSession> sessions = serviceLocator.getWorkoutRepository().getAllSessions();
        long maxDuration = 0;
        long recordDate = 0;

        for (WorkoutSession session : sessions) {
            long duration = session.getDuration();
            if (duration > maxDuration) {
                maxDuration = duration;
                recordDate = session.getStartTime();
            }
        }

        if (longestWorkoutText != null) {
            if (maxDuration > 0) {
                long minutes = maxDuration / (1000 * 60);
                longestWorkoutText.setText(minutes + " min");
            } else {
                longestWorkoutText.setText("-- min");
            }
        }

        if (longestWorkoutDetailText != null) {
            if (recordDate > 0) {
                String dateStr = new java.text.SimpleDateFormat("yyyy.MM.dd", java.util.Locale.getDefault())
                        .format(new java.util.Date(recordDate));
                longestWorkoutDetailText.setText("高效燃脂 · " + dateStr);
            } else {
                longestWorkoutDetailText.setText("暂无记录");
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupListeners() {
        if (addMetricFab != null) {
            addMetricFab.setOnClickListener(v -> startActivity(new Intent(this, BodyMetricActivity.class)));
            
            // 添加FAB拖动功能
            addMetricFab.setOnTouchListener(new View.OnTouchListener() {
                private float startX, startY;
                private boolean isDragging = false;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            startX = event.getRawX();
                            startY = event.getRawY();
                            isDragging = false;
                            return true;
                        case MotionEvent.ACTION_MOVE:
                            float deltaX = event.getRawX() - startX;
                            float deltaY = event.getRawY() - startY;
                            
                            if (Math.abs(deltaX) > 10 || Math.abs(deltaY) > 10) {
                                isDragging = true;
                                
                                // 更新FAB位置
                                float newX = v.getX() + deltaX;
                                float newY = v.getY() + deltaY;
                                
                                // 限制在屏幕范围内
                                newX = Math.max(0, Math.min(newX, getWindowManager().getDefaultDisplay().getWidth() - v.getWidth()));
                                newY = Math.max(0, Math.min(newY, getWindowManager().getDefaultDisplay().getHeight() - v.getHeight() - 200)); // 避开底部导航栏区域
                                
                                v.setX(newX);
                                v.setY(newY);
                                
                                startX = event.getRawX();
                                startY = event.getRawY();
                            }
                            return true;
                        case MotionEvent.ACTION_UP:
                            if (!isDragging) {
                                // 如果没有拖动，就是点击事件
                                addMetricFab.performClick();
                            }
                            return true;
                    }
                    return false;
                }
            });
        }
        if (workoutHistoryCard != null) {
            workoutHistoryCard.setOnClickListener(v -> startActivity(new Intent(this, WorkoutHistoryActivity.class)));
        }
    }
}
