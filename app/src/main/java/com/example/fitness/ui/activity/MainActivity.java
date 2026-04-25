package com.example.fitness.ui.activity;

import com.example.fitness.R;
import com.example.fitness.BaseActivity;
import com.example.fitness.ServiceLocator;
import com.example.fitness.ui.activity.PlanActivity;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.fitness.config.AppConfig;
import com.example.fitness.domain.model.WorkoutSession;
import com.example.fitness.domain.model.WorkoutStats;
import com.example.fitness.ui.view.CircularProgressView;
import com.example.fitness.ui.view.WeeklyTrendChartView;
import com.example.fitness.util.ImageLoader;
import com.google.android.material.card.MaterialCardView;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

/**
 * 首页仪表盘，展示步数、热量、训练时长等核心数据。
 */
public class MainActivity extends BaseActivity {

    private static final int PERMISSION_REQUEST_CODE = 1001;

    private CircularProgressView stepsProgressView;
    private TextView goalText;
    private TextView remainingText;
    private TextView caloriesText;
    private TextView workoutTimeText;
    private WeeklyTrendChartView weeklyTrendChart;
    private MaterialCardView startWorkoutCard;
    private ImageView profileAvatar;
    private ImageView startWorkoutBackground;

    private ServiceLocator serviceLocator;
    private SensorManager sensorManager;
    private Sensor stepSensor;
    private SensorEventListener stepListener;
    private int initialStepCount = -1;
    private int currentSteps = 0;
    private boolean isSensorRegistered = false;
    private static final String PREFS_NAME = "step_counter_prefs";
    private static final String KEY_INITIAL_STEPS = "initial_steps";
    private static final String KEY_LAST_DATE = "last_date";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupBottomNavigation(0);

        serviceLocator = ServiceLocator.getInstance(this);
        initializeViews();
        checkAndRequestPermission();
        // 注意：loadDashboardData 将在权限授予后调用
        setupListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isSensorRegistered) {
            registerStepSensor();
        }
        loadDashboardData();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterStepSensor();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sensorManager != null && stepListener != null) {
            sensorManager.unregisterListener(stepListener);
        }
    }

    private void initializeViews() {
        stepsProgressView = findViewById(R.id.stepsProgressView);
        goalText = findViewById(R.id.goalText);
        remainingText = findViewById(R.id.remainingText);
        caloriesText = findViewById(R.id.caloriesText);
        workoutTimeText = findViewById(R.id.workoutTimeText);
        weeklyTrendChart = findViewById(R.id.weeklyTrendChart);
        startWorkoutCard = findViewById(R.id.startWorkoutCard);
        profileAvatar = findViewById(R.id.profileAvatar);
        startWorkoutBackground = findViewById(R.id.startWorkoutBackground);
    }

    private void initializeStepSensor() {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            if (stepSensor != null) {
                isSensorRegistered = true;
                // 检查是否需要重置基准值（新的一天）
                checkAndResetDailyBaseline();
                registerStepSensor();
                android.util.Log.d("StepCounter", "传感器注册成功，开始监听系统步数");
                Toast.makeText(this, "已连接手机系统计步器", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "设备不支持系统计步器", Toast.LENGTH_LONG).show();
            }
        }

        stepListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
                    // 读取系统累计步数（这是手机系统健康App使用的同一数据源）
                    int systemTotalSteps = (int) event.values[0];
                    
                    // 计算今日步数 = 当前系统累计 - 今日零点基准值
                    if (initialStepCount >= 0) {
                        currentSteps = Math.max(0, systemTotalSteps - initialStepCount);
                        android.util.Log.d("StepCounter", "系统累计: " + systemTotalSteps + " | 今日步数: " + currentSteps);
                    } else {
                        // 首次启动，设置基准值
                        initialStepCount = systemTotalSteps;
                        currentSteps = 0;
                        saveBaseline(systemTotalSteps);
                        android.util.Log.d("StepCounter", "设置基准值: " + initialStepCount + "（应用启动时刻的系统累计值）");
                    }
                    
                    runOnUiThread(() -> updateStepsDisplay());
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                // 不需要处理
            }
        };
    }

    private void registerStepSensor() {
        if (sensorManager != null && stepSensor != null && stepListener != null) {
            // 使用SENSOR_DELAY_NORMAL以平衡精度和功耗，系统传感器在后台也会持续记录
            sensorManager.registerListener(stepListener, stepSensor, SensorManager.SENSOR_DELAY_NORMAL);
            isSensorRegistered = true;
        }
    }

    private void unregisterStepSensor() {
        if (sensorManager != null && stepListener != null) {
            sensorManager.unregisterListener(stepListener);
            isSensorRegistered = false;
        }
    }

    /**
     * 检查并重置每日基准步数
     */
    private void checkAndResetDailyBaseline() {
        android.content.SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String today = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            today = LocalDate.now().toString();
        }
        String lastDate = prefs.getString(KEY_LAST_DATE, "");

        assert today != null;
        if (!today.equals(lastDate)) {
            // 新的一天，重置基准值
            initialStepCount = -1;
            prefs.edit()
                .putString(KEY_LAST_DATE, today)
                .remove(KEY_INITIAL_STEPS)
                .apply();
        } else {
            // 同一天，恢复基准值
            initialStepCount = prefs.getInt(KEY_INITIAL_STEPS, -1);
        }
    }

    /**
     * 保存基准步数
     */
    private void saveBaseline(int baselineSteps) {
        android.content.SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            prefs.edit()
                .putInt(KEY_INITIAL_STEPS, baselineSteps)
                .putString(KEY_LAST_DATE, LocalDate.now().toString())
                .apply();
        }
    }

    private void loadDashboardData() {
        loadStepsData();
        loadStatsData();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            loadWeeklyTrend();
        }
        loadImages();
    }

    private void loadImages() {
        if (profileAvatar != null) {
            profileAvatar.setImageResource(R.drawable.img_user_profile);
        }
        if (startWorkoutBackground != null) {
            startWorkoutBackground.setImageResource(R.drawable.bg_start_workout);
        }
    }

    @SuppressLint("DefaultLocale")
    private void loadStepsData() {
        // 如果传感器已注册且有数据，直接使用传感器数据
        if (isSensorRegistered && currentSteps > 0) {
            updateStepsDisplay();
            return;
        }
        
        // 如果没有传感器数据，使用模拟数据
        if (currentSteps == 0 && !isSensorRegistered) {
            currentSteps = 8432;
        }
        
        int goalSteps = AppConfig.DEFAULT_STEP_GOAL;
        int remainingSteps = goalSteps - currentSteps;
        float progressPercent = (float) currentSteps / goalSteps * 100f;

        if (goalText != null) {
            goalText.setText(String.format("%,d", goalSteps));
        }
        if (remainingText != null) {
            remainingText.setText(String.format("%,d", Math.max(0, remainingSteps)));
        }

        if (stepsProgressView != null) {
            stepsProgressView.setProgressWithAnimation(progressPercent, AppConfig.PROGRESS_ANIMATION_DURATION);
        }
    }

    @SuppressLint("DefaultLocale")
    private void updateStepsDisplay() {
        int goalSteps = AppConfig.DEFAULT_STEP_GOAL;
        int remainingSteps = goalSteps - currentSteps;
        float progressPercent = (float) currentSteps / goalSteps * 100f;

        if (goalText != null) {
            goalText.setText(String.format("%,d", goalSteps));
        }
        if (remainingText != null) {
            remainingText.setText(String.format("%,d", Math.max(0, remainingSteps)));
        }

        if (stepsProgressView != null) {
            // 更新进度环的显示
            stepsProgressView.setMainText(String.format("%,d", currentSteps));
            stepsProgressView.setProgress(progressPercent);
        }
        
        // 首次启动提示用户行走以激活计步器
        if (currentSteps == 0 && initialStepCount >= 0) {
            android.util.Log.d("StepCounter", "等待传感器数据更新，请携带手机行走几步...");
        }
    }

    @SuppressLint("DefaultLocale")
    private void loadStatsData() {
        WorkoutStats todayStats = serviceLocator.getWorkoutStatsService().getTodayStats();

        if (caloriesText != null) {
            caloriesText.setText(String.format("%,d kcal", todayStats.getTotalCalories()));
        }

        if (workoutTimeText != null) {
            int durationMinutes = (int) (todayStats.getTotalDuration() / (1000 * 60));
            workoutTimeText.setText(String.format("%,d 分钟", durationMinutes));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void loadWeeklyTrend() {
        if (weeklyTrendChart == null) {
            return;
        }

        float[] weeklyData = new float[7];
        List<WorkoutSession> history = serviceLocator.getWorkoutRepository().getAllSessions();
        LocalDate today = LocalDate.now();

        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            long dayStart = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
            long dayEnd = date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();

            int dayMinutes = 0;
            if (history != null) {
                for (WorkoutSession session : history) {
                    long timestamp = session.getEndTime() > 0 ? session.getEndTime() : session.getStartTime();
                    if (timestamp >= dayStart && timestamp < dayEnd) {
                        dayMinutes += (int) (session.getDuration() / (1000 * 60));
                    }
                }
            }
            weeklyData[6 - i] = Math.min(100f, (dayMinutes / (float) AppConfig.DAILY_WORKOUT_GOAL_MINUTES) * 100f);
        }

        boolean hasData = false;
        for (float value : weeklyData) {
            if (value > 0) {
                hasData = true;
                break;
            }
        }

        if (!hasData) {
            weeklyData = new float[]{40f, 60f, 30f, 85f, 55f, 70f, 45f};
        }

        weeklyTrendChart.setDataWithAnimation(weeklyData, AppConfig.CHART_ANIMATION_DURATION);
        int currentDayOfWeek = java.time.LocalDate.now().getDayOfWeek().getValue() - 1;
        weeklyTrendChart.setActiveDay(currentDayOfWeek);
    }

    private void setupListeners() {
        if (startWorkoutCard != null) {
            startWorkoutCard.setOnClickListener(v -> {
                startActivity(new Intent(this, PlanActivity.class));
                Toast.makeText(this, "请选择一个训练计划", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void checkAndRequestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACTIVITY_RECOGNITION},
                        PERMISSION_REQUEST_CODE);
            } else {
                initializeStepSensor();
                loadDashboardData(); // 权限已授予，立即加载数据
            }
        } else {
            initializeStepSensor();
            loadDashboardData(); // 低版本无需权限，直接加载
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeStepSensor();
                loadDashboardData(); // 权限授予后加载数据
                Toast.makeText(this, "已开启实时计步功能", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "需要权限才能使用计步功能", Toast.LENGTH_LONG).show();
                // 即使没有权限，也加载模拟数据
                loadDashboardData();
            }
        }
    }
}
