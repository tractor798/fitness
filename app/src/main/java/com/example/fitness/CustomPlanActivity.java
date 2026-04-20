package com.example.fitness;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class CustomPlanActivity extends BaseActivity {

    private EditText planNameEdit;
    private EditText planDescriptionEdit;
    private RecyclerView exercisesRecyclerView;
    private Button savePlanButton;
    private ExerciseAdapter exerciseAdapter;
    private List<Exercise> availableExercises;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_plan);
        setupBottomNavigation(1);

        initializeViews();
        setupExercises();
        setupListeners();
    }

    private void initializeViews() {
        planNameEdit = findViewById(R.id.planNameEdit);
        planDescriptionEdit = findViewById(R.id.planDescriptionEdit);
        exercisesRecyclerView = findViewById(R.id.exercisesRecyclerView);
        savePlanButton = findViewById(R.id.savePlanButton);

        exercisesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupExercises() {
        availableExercises = new ArrayList<>();
        // 添加预定义的运动
        availableExercises.add(new Exercise("深蹲 Squats", "增强下肢力量，改善臀部线条"));
        availableExercises.add(new Exercise("俯卧撑 Push-ups", "锻炼胸肌、肩肌和三头肌"));
        availableExercises.add(new Exercise("仰卧起坐 Crunches", "锻炼腹部肌肉"));
        availableExercises.add(new Exercise("平板支撑 Plank", "增强核心肌群稳定性"));
        availableExercises.add(new Exercise("哑铃弯举 Bicep Curls", "锻炼二头肌"));
        availableExercises.add(new Exercise("弓步蹲 Lunges", "增强腿部力量和平衡"));
        availableExercises.add(new Exercise("卷腹 Leg Raises", "锻炼下腹部肌肉"));
        availableExercises.add(new Exercise("登山跑 Mountain Climbers", "高强度有氧运动"));
        availableExercises.add(new Exercise("开合跳 Jumping Jacks", "全身有氧热身运动"));
        availableExercises.add(new Exercise("俯身提膝 Burpees", "高强度全身运动"));

        exerciseAdapter = new ExerciseAdapter(availableExercises, this::onExerciseConfigClick);
        exercisesRecyclerView.setAdapter(exerciseAdapter);
    }

    private void setupListeners() {
        savePlanButton.setOnClickListener(v -> saveCustomPlan());
    }

    private void onExerciseConfigClick(Exercise exercise) {
        // 打开运动配置对话框或Activity
        Intent intent = new Intent(this, ExerciseConfigActivity.class);
        intent.putExtra("exercise", exercise);
        startActivityForResult(intent, 100);
    }

    private void saveCustomPlan() {
        String planName = planNameEdit.getText().toString().trim();
        String planDescription = planDescriptionEdit.getText().toString().trim();

        if (planName.isEmpty()) {
            Toast.makeText(this, "请输入计划名称", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Exercise> selectedExercises = new ArrayList<>();
        for (Exercise exercise : availableExercises) {
            if (exercise.isSelected()) {
                selectedExercises.add(exercise);
            }
        }

        if (selectedExercises.isEmpty()) {
            Toast.makeText(this, "请至少选择一个运动", Toast.LENGTH_SHORT).show();
            return;
        }

        CustomPlan customPlan = new CustomPlan(planName, planDescription);
        customPlan.setExercises(selectedExercises);
        customPlan.calculateTotals();

        // 使用PlanDataManager保存计划
        PlanDataManager.getInstance(this).saveCustomPlan(customPlan);

        Toast.makeText(this, "自定义计划保存成功", Toast.LENGTH_SHORT).show();

        // 返回计划页面
        Intent intent = new Intent(this, PlanActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            Exercise updatedExercise = (Exercise) data.getSerializableExtra("exercise");
            if (updatedExercise != null) {
                // 更新运动列表中的对应项目
                for (int i = 0; i < availableExercises.size(); i++) {
                    if (availableExercises.get(i).getName().equals(updatedExercise.getName())) {
                        availableExercises.set(i, updatedExercise);
                        break;
                    }
                }
                exerciseAdapter.notifyDataSetChanged();
            }
        }
    }
}