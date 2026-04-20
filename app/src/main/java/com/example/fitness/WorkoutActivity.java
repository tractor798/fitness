package com.example.fitness;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class WorkoutActivity extends BaseActivity {

    private TextView titleText;
    private TextView descriptionText;
    private RecyclerView exercisesRecyclerView;
    private Button startButton;
    private WorkoutExerciseAdapter exerciseAdapter;
    private CustomPlan currentPlan;
    private WorkoutSession currentSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);
        setupBottomNavigation(1); // Plans tab

        initializeViews();
        loadWorkoutData();
        setupListeners();
    }

    private void initializeViews() {
        titleText = findViewById(R.id.titleText);
        descriptionText = findViewById(R.id.descriptionText);
        exercisesRecyclerView = findViewById(R.id.exercisesRecyclerView);
        startButton = findViewById(R.id.startButton);

        if (exercisesRecyclerView != null) {
            exercisesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        }
    }

    private void loadWorkoutData() {
        // 从Intent获取训练信息
        String workoutName = getIntent().getStringExtra("workout_name");
        int planType = getIntent().getIntExtra("plan_type", PlanItem.TYPE_PRESET);

        if (workoutName != null) {
            titleText.setText(workoutName);
        }

        if (planType == PlanItem.TYPE_CUSTOM) {
            // 自定义计划
            currentPlan = (CustomPlan) getIntent().getSerializableExtra("custom_plan");
            if (currentPlan != null) {
                descriptionText.setText(currentPlan.getDescription());
                List<Exercise> exercises = currentPlan.getExercises();
                exerciseAdapter = new WorkoutExerciseAdapter(exercises);
                exercisesRecyclerView.setAdapter(exerciseAdapter);
            }
        } else {
            // 预设计划 - 显示默认信息
            descriptionText.setText("极致燃脂体验，结合HIIT与抗阻训练，在45分钟内重塑体能巅峰。");
            // 对于预设计划，可以显示固定的运动列表或隐藏RecyclerView
            exercisesRecyclerView.setVisibility(View.GONE);
        }
    }

    private void setupListeners() {
        startButton.setOnClickListener(v -> {
            // 创建训练会话
            String workoutName = getIntent().getStringExtra("workout_name");
            currentSession = new WorkoutSession(workoutName != null ? workoutName : "未命名训练");

            // 跳转到计时器页面
            Intent intent = new Intent(this, TimerActivity.class);
            intent.putExtra("workout_session", currentSession);
            if (currentPlan != null) {
                intent.putExtra("custom_plan", currentPlan);
            }
            startActivity(intent);
        });
    }
}