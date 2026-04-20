package com.example.fitness;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class TimerActivity extends BaseActivity {

    private TextView timerText;
    private Button pauseButton;
    private Button stopButton;
    private Handler handler = new Handler();
    private long startTime;
    private long elapsedTime = 0;
    private boolean isRunning = false;
    private Runnable timerRunnable;
    private WorkoutSession currentSession;
    private CustomPlan currentPlan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        setupBottomNavigation(1); // Plans tab

        initializeViews();
        loadSessionData();
        setupListeners();
        startTimer();
    }

    private void initializeViews() {
        timerText = findViewById(R.id.timerText);
        pauseButton = findViewById(R.id.pauseButton);
        stopButton = findViewById(R.id.stopButton);
    }

    private void loadSessionData() {
        currentSession = (WorkoutSession) getIntent().getSerializableExtra("workout_session");
        currentPlan = (CustomPlan) getIntent().getSerializableExtra("custom_plan");

        // 如果没有传递WorkoutSession，创建一个默认的
        if (currentSession == null) {
            String workoutName = getIntent().getStringExtra("workout_name");
            currentSession = new WorkoutSession(workoutName != null ? workoutName : "未命名训练");
        }
    }

    private void setupListeners() {
        pauseButton.setOnClickListener(v -> {
            if (isRunning) {
                pauseTimer();
            } else {
                startTimer();
            }
        });

        stopButton.setOnClickListener(v -> {
            stopTimer();
            completeWorkout();
            finish();
        });
    }

    private void startTimer() {
        if (!isRunning) {
            startTime = System.currentTimeMillis() - elapsedTime;
            isRunning = true;
            pauseButton.setText("暂停");
            handler.post(timerRunnable);
        }
    }

    private void pauseTimer() {
        if (isRunning) {
            isRunning = false;
            pauseButton.setText("继续");
            handler.removeCallbacks(timerRunnable);
        }
    }

    private void stopTimer() {
        isRunning = false;
        handler.removeCallbacks(timerRunnable);
    }

    private void completeWorkout() {
        // 计算消耗的卡路里
        int caloriesBurned = calculateCaloriesBurned();

        // 完成训练会话
        currentSession.complete(caloriesBurned);

        // 保存到数据管理器
        PlanDataManager.getInstance(this).saveWorkoutSession(currentSession);

        Toast.makeText(this, "训练完成！消耗 " + caloriesBurned + " kcal", Toast.LENGTH_LONG).show();
    }

    private int calculateCaloriesBurned() {
        long durationMinutes = elapsedTime / (1000 * 60);

        if (currentPlan != null) {
            // 自定义计划：使用计划中设置的卡路里
            return Math.max(1, (int)(currentPlan.getTotalCalories() * durationMinutes / (double)currentPlan.getTotalDuration()));
        } else {
            // 预设计划：使用默认计算
            return Math.max(1, (int)(durationMinutes * 9.3)); // 平均每分钟消耗9.3卡路里
        }
    }

    {
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                if (isRunning) {
                    long currentTime = System.currentTimeMillis();
                    elapsedTime = currentTime - startTime;
                    updateTimerDisplay();
                    handler.postDelayed(this, 1000);
                }
            }
        };
    }

    private void updateTimerDisplay() {
        int seconds = (int) (elapsedTime / 1000) % 60;
        int minutes = (int) ((elapsedTime / (1000 * 60)) % 60);
        int hours = (int) ((elapsedTime / (1000 * 60 * 60)) % 24);

        String timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        timerText.setText(timeString);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTimer();
    }
}