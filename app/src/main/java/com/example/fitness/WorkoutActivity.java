package com.example.fitness;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fitness.domain.model.CustomPlan;
import com.example.fitness.domain.model.PlanItem;
import com.example.fitness.domain.model.WorkoutSession;

import java.util.ArrayList;
import java.util.List;

public class WorkoutActivity extends BaseActivity {

    private ImageView heroImage;
    private TextView titleText;
    private TextView descriptionText;
    private TextView durationText;
    private TextView difficultyText;
    private TextView equipmentText;
    private TextView burnText;
    private TextView exerciseCountText;
    private RecyclerView exercisesRecyclerView;
    private Button startButton;
    private WorkoutExerciseAdapter exerciseAdapter;
    private CustomPlan currentPlan;
    private WorkoutSession currentSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);
        initializeViews();
        loadWorkoutData();
        setupListeners();
    }

    private void initializeViews() {
        heroImage = findViewById(R.id.heroImage);
        titleText = findViewById(R.id.titleText);
        descriptionText = findViewById(R.id.descriptionText);
        durationText = findViewById(R.id.durationText);
        difficultyText = findViewById(R.id.difficultyText);
        equipmentText = findViewById(R.id.equipmentText);
        burnText = findViewById(R.id.burnText);
        exerciseCountText = findViewById(R.id.exerciseCountText);
        exercisesRecyclerView = findViewById(R.id.exercisesRecyclerView);
        startButton = findViewById(R.id.startButton);

        if (exercisesRecyclerView != null) {
            exercisesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        }
    }

    private void loadWorkoutData() {
        String workoutName = getIntent().getStringExtra("workout_name");
        int planType = getIntent().getIntExtra("plan_type", PlanItem.TYPE_PRESET);

        if (heroImage != null) {
            Glide.with(this)
                    .load(R.drawable.bg_recommend_workout)
                    .placeholder(R.drawable.bg_exercise_thumbnail)
                    .error(R.drawable.bg_exercise_thumbnail)
                    .into(heroImage);
        }

        if (workoutName != null && titleText != null) {
            titleText.setText(workoutName);
        }

        if (planType == PlanItem.TYPE_CUSTOM) {
            currentPlan = (CustomPlan) getIntent().getSerializableExtra("custom_plan");
            if (currentPlan != null) {
                bindCustomPlan(currentPlan);
                return;
            }
        }

        bindPresetPlan();
    }

    private void bindCustomPlan(CustomPlan plan) {
        if (descriptionText != null) {
            descriptionText.setText(plan.getDescription());
        }
        if (durationText != null) {
            durationText.setText(plan.getTotalDuration() + "分钟");
        }
        if (difficultyText != null) {
            difficultyText.setText("中级");
        }
        if (equipmentText != null) {
            equipmentText.setText("哑铃");
        }
        if (burnText != null) {
            burnText.setText(plan.getTotalCalories() + " kcal");
        }

        List<Exercise> exercises = plan.getExercises();
        if (exerciseCountText != null) {
            exerciseCountText.setText(exercises.size() + "个动作");
        }
        exerciseAdapter = new WorkoutExerciseAdapter(exercises);
        if (exercisesRecyclerView != null) {
            exercisesRecyclerView.setAdapter(exerciseAdapter);
        }
    }

    private void bindPresetPlan() {
        if (descriptionText != null) {
            descriptionText.setText("极致燃脂体验，结合 HIIT 与抗阻训练，在 45 分钟内重塑体能巅峰。");
        }
        if (durationText != null) {
            durationText.setText("45分钟");
        }
        if (difficultyText != null) {
            difficultyText.setText("中级");
        }
        if (equipmentText != null) {
            equipmentText.setText("哑铃");
        }
        if (burnText != null) {
            burnText.setText("420 kcal");
        }

        List<Exercise> presetExercises = createPresetExercises();
        if (exerciseCountText != null) {
            exerciseCountText.setText(presetExercises.size() + "个动作");
        }
        exerciseAdapter = new WorkoutExerciseAdapter(presetExercises);
        if (exercisesRecyclerView != null) {
            exercisesRecyclerView.setAdapter(exerciseAdapter);
            exercisesRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private List<Exercise> createPresetExercises() {
        List<Exercise> exercises = new ArrayList<>();
        exercises.add(new Exercise(
                "杠铃深蹲",
                "4 组 × 12 次 · 休息 60s",
                R.drawable.img_ex_squats
        ));
        exercises.add(new Exercise(
                "俯卧撑",
                "3 组 × 15 次 · 休息 45s",
                R.drawable.img_ex_pushups
        ));
        exercises.add(new Exercise(
                "平板支撑",
                "3 组 × 60s · 休息 30s",
                R.drawable.img_ex_squats
        ));
        return exercises;
    }

    private void setupListeners() {
        if (startButton == null) {
            return;
        }
        startButton.setOnClickListener(v -> {
            String workoutName = getIntent().getStringExtra("workout_name");
            currentSession = new WorkoutSession(workoutName != null ? workoutName : "未命名训练");

            Intent intent = new Intent(this, TimerActivity.class);
            intent.putExtra("workout_session", currentSession);
            if (currentPlan != null) {
                intent.putExtra("custom_plan", currentPlan);
            }
            startActivity(intent);
        });
    }
}
