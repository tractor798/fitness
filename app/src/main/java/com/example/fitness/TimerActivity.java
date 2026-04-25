package com.example.fitness;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.example.fitness.config.AppConfig;
import com.example.fitness.domain.model.CustomPlan;
import com.example.fitness.domain.model.WorkoutSession;

import java.lang.ref.WeakReference;

public class TimerActivity extends BaseActivity {

    private TextView timerText;
    private TextView exerciseNameText;
    private TextView bpmText;
    private TextView caloriesText;
    private TextView currentSetText;
    private TextView volumeText;
    private TextView bestRecordText;
    private TextView restTimeText;
    private EditText weightInput;
    private EditText repsInput;
    private ImageView weightIncrease;
    private ImageView weightDecrease;
    private ImageView repsIncrease;
    private ImageView repsDecrease;
    private Button completeSetButton;

    private Handler handler;
    private long startTime;
    private long elapsedTime = 0L;
    private boolean isRunning = false;
    private Runnable timerRunnable;
    private WorkoutSession currentSession;
    private CustomPlan currentPlan;
    private int currentSet = 1;
    private int totalSets = 5;
    private int currentCalories = 124;

    private ServiceLocator serviceLocator;

    private static class TimerRunnable implements Runnable {
        private final WeakReference<TimerActivity> activityRef;

        TimerRunnable(TimerActivity activity) {
            this.activityRef = new WeakReference<>(activity);
        }

        @Override
        public void run() {
            TimerActivity activity = activityRef.get();
            if (activity != null && activity.isRunning) {
                long now = System.currentTimeMillis();
                activity.elapsedTime = now - activity.startTime;
                activity.updateTimerDisplay();
                activity.handler.postDelayed(this, 1000);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        handler = new Handler(Looper.getMainLooper());
        serviceLocator = ServiceLocator.getInstance(this);

        initializeViews();
        loadSessionData();
        setupListeners();
        startTimer();
    }

    private void initializeViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        timerText = findViewById(R.id.timerText);
        exerciseNameText = findViewById(R.id.exerciseNameText);
        bpmText = findViewById(R.id.bpmText);
        caloriesText = findViewById(R.id.caloriesText);
        currentSetText = findViewById(R.id.currentSetText);
        volumeText = findViewById(R.id.volumeText);
        bestRecordText = findViewById(R.id.bestRecordText);
        restTimeText = findViewById(R.id.restTimeText);
        weightInput = findViewById(R.id.weightInput);
        repsInput = findViewById(R.id.repsInput);
        weightIncrease = findViewById(R.id.weightIncrease);
        weightDecrease = findViewById(R.id.weightDecrease);
        repsIncrease = findViewById(R.id.repsIncrease);
        repsDecrease = findViewById(R.id.repsDecrease);
        completeSetButton = findViewById(R.id.completeSetButton);
    }

    private void loadSessionData() {
        currentSession = (WorkoutSession) getIntent().getSerializableExtra("workout_session");
        currentPlan = (CustomPlan) getIntent().getSerializableExtra("custom_plan");

        if (currentSession == null) {
            String workoutName = getIntent().getStringExtra("workout_name");
            currentSession = new WorkoutSession(workoutName != null ? workoutName : "杠铃深蹲");
        }

        // 以进入计时页为训练起点，避免在详情页停留时间被算入训练时长。
        currentSession.setStartTime(System.currentTimeMillis());
        currentSession.setEndTime(0L);
        currentSession.setDuration(0L);
        currentSession.setCompleted(false);

        if (currentPlan != null && currentPlan.getExercises() != null && !currentPlan.getExercises().isEmpty()) {
            Exercise first = currentPlan.getExercises().get(0);
            if (exerciseNameText != null) {
                exerciseNameText.setText(first.getName());
            }
            totalSets = Math.max(1, first.getSets());
            if (restTimeText != null) {
                restTimeText.setText("01:30");
            }
            if (bestRecordText != null) {
                bestRecordText.setText("110 kg");
            }
        } else if (exerciseNameText != null) {
            exerciseNameText.setText(currentSession.getPlanName());
        }

        if (currentSetText != null) {
            currentSetText.setText(String.valueOf(currentSet));
        }
    }

    private void setupListeners() {
        if (weightIncrease != null) {
            weightIncrease.setOnClickListener(v -> adjustNumericInput(weightInput, 5, 5, 300));
        }
        if (weightDecrease != null) {
            weightDecrease.setOnClickListener(v -> adjustNumericInput(weightInput, -5, 5, 300));
        }
        if (repsIncrease != null) {
            repsIncrease.setOnClickListener(v -> adjustNumericInput(repsInput, 1, 1, 100));
        }
        if (repsDecrease != null) {
            repsDecrease.setOnClickListener(v -> adjustNumericInput(repsInput, -1, 1, 100));
        }
        if (completeSetButton != null) {
            completeSetButton.setOnClickListener(v -> completeCurrentSet());
        }
    }

    private void adjustNumericInput(EditText editText, int delta, int min, int max) {
        if (editText == null) {
            return;
        }
        int value;
        try {
            value = Integer.parseInt(editText.getText().toString().trim());
        } catch (NumberFormatException e) {
            value = min;
        }
        value += delta;
        if (value < min) {
            value = min;
        }
        if (value > max) {
            value = max;
        }
        editText.setText(String.valueOf(value));
    }

    private void completeCurrentSet() {
        updateVolumeAndCalories(currentSet);

        if (currentSet < totalSets) {
            int completedSet = currentSet;
            currentSet++;
            if (currentSetText != null) {
                currentSetText.setText(String.valueOf(currentSet));
            }
            Toast.makeText(this, "第 " + completedSet + " 组已完成", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "所有组已完成", Toast.LENGTH_SHORT).show();
        completeWorkout();
    }

    private void updateVolumeAndCalories(int completedSets) {
        try {
            int weight = Integer.parseInt(weightInput.getText().toString().trim());
            int reps = Integer.parseInt(repsInput.getText().toString().trim());
            int volume = weight * reps * completedSets;
            if (volumeText != null) {
                volumeText.setText(String.format("%,d kg", volume));
            }
            currentCalories = 124 + completedSets * 12;
            if (caloriesText != null) {
                caloriesText.setText(currentCalories + " KCAL");
            }
            if (bpmText != null) {
                bpmText.setText((130 + completedSets * 4) + " BPM");
            }
        } catch (NumberFormatException ignored) {
        }
    }

    private void startTimer() {
        if (isRunning) {
            return;
        }
        startTime = System.currentTimeMillis() - elapsedTime;
        isRunning = true;
        timerRunnable = new TimerRunnable(this);
        handler.post(timerRunnable);
    }

    private void stopTimer() {
        isRunning = false;
        if (handler != null && timerRunnable != null) {
            handler.removeCallbacks(timerRunnable);
        }
    }

    private void completeWorkout() {
        int caloriesBurned = calculateCaloriesBurned();
        currentSession.complete(caloriesBurned);
        serviceLocator.getWorkoutRepository().saveSession(currentSession);

        Toast.makeText(this, "训练完成，消耗 " + caloriesBurned + " kcal", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, StatsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private int calculateCaloriesBurned() {
        long durationMinutes = elapsedTime / (1000 * 60);
        if (durationMinutes <= 0) {
            durationMinutes = 1;
        }

        if (currentPlan != null && currentPlan.getTotalDuration() > 0) {
            return Math.max(1, (int) (currentPlan.getTotalCalories() * durationMinutes / (double) currentPlan.getTotalDuration()));
        }
        return Math.max(1, (int) (durationMinutes * AppConfig.CALORIES_PER_MINUTE));
    }

    private void updateTimerDisplay() {
        int totalSeconds = (int) (elapsedTime / 1000);
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        String timeString = String.format("%02d:%02d", minutes, seconds);
        if (timerText != null) {
            timerText.setText(timeString);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTimer();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }
}
