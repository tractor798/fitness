package com.example.fitness;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitness.domain.model.CustomPlan;
import com.example.fitness.domain.model.Exercise;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义训练计划创建页。
 */
public class CustomPlanActivity extends BaseActivity {

    private EditText planNameEdit;
    private RecyclerView exercisesRecyclerView;
    private Button savePlanButton;
    private ExerciseAdapter exerciseAdapter;
    private List<Exercise> availableExercises;
    private ServiceLocator serviceLocator;

    private TextView categoryFatLoss;
    private TextView categoryMuscle;
    private TextView categoryShaping;
    private TextView categoryEndurance;
    private String selectedCategory = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_plan);

        serviceLocator = ServiceLocator.getInstance(this);
        initializeViews();
        setupExercises();
        setupListeners();
    }

    private void initializeViews() {
        planNameEdit = findViewById(R.id.planNameEdit);
        exercisesRecyclerView = findViewById(R.id.exercisesRecyclerView);
        savePlanButton = findViewById(R.id.savePlanButton);
        categoryFatLoss = findViewById(R.id.categoryFatLoss);
        categoryMuscle = findViewById(R.id.categoryMuscle);
        categoryShaping = findViewById(R.id.categoryShaping);
        categoryEndurance = findViewById(R.id.categoryEndurance);

        if (exercisesRecyclerView != null) {
            exercisesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        }
    }

    private void setupExercises() {
        availableExercises = new ArrayList<>();
        availableExercises.add(new Exercise("深蹲 Squats", "腿臀强化 · 基础动作", R.drawable.img_ex_squats));
        availableExercises.add(new Exercise("俯卧撑 Push-ups", "胸肩激活 · 自重训练", R.drawable.img_ex_pushups));
        availableExercises.add(new Exercise("平板支撑 Plank", "核心稳定 · 控制呼吸", R.drawable.img_ex_squats));
        availableExercises.add(new Exercise("哑铃弯举 Bicep Curls", "手臂力量 · 孤立刺激", R.drawable.img_ex_biceps));
        availableExercises.add(new Exercise("弓步蹲 Lunges", "腿部平衡 · 单侧控制", R.drawable.img_ex_pullups));
        availableExercises.add(new Exercise("登山跑 Mountain Climbers", "心肺燃脂 · 节奏冲刺", R.drawable.img_ex_seated_row));

        exerciseAdapter = new ExerciseAdapter(availableExercises, this::showExerciseConfigDialog);
        if (exercisesRecyclerView != null) {
            exercisesRecyclerView.setAdapter(exerciseAdapter);
        }
    }

    private void setupListeners() {
        if (savePlanButton != null) {
            savePlanButton.setOnClickListener(v -> saveCustomPlan());
        }

        // 分类选择
        View.OnClickListener categoryClickListener = v -> {
            resetAllCategories();
            TextView selectedCategoryView = (TextView) v;
            selectedCategoryView.setBackgroundResource(R.drawable.bg_filter_active);
            selectedCategoryView.setTextColor(getColor(R.color.onPrimary));

            if (selectedCategoryView == categoryFatLoss) {
                selectedCategory = "减脂";
            } else if (selectedCategoryView == categoryMuscle) {
                selectedCategory = "增肌";
            } else if (selectedCategoryView == categoryShaping) {
                selectedCategory = "塑形";
            } else if (selectedCategoryView == categoryEndurance) {
                selectedCategory = "耐力";
            }
        };

        if (categoryFatLoss != null) {
            categoryFatLoss.setOnClickListener(categoryClickListener);
        }
        if (categoryMuscle != null) {
            categoryMuscle.setOnClickListener(categoryClickListener);
        }
        if (categoryShaping != null) {
            categoryShaping.setOnClickListener(categoryClickListener);
        }
        if (categoryEndurance != null) {
            categoryEndurance.setOnClickListener(categoryClickListener);
        }
    }

    private void resetAllCategories() {
        TextView[] categories = {categoryFatLoss, categoryMuscle, categoryShaping, categoryEndurance};
        for (TextView category : categories) {
            if (category != null) {
                category.setBackgroundResource(R.drawable.bg_filter_inactive);
                category.setTextColor(getColor(R.color.onSurfaceVariant));
            }
        }
    }

    private void showExerciseConfigDialog(Exercise exercise) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("配置动作：" + exercise.getName());

        EditText setsInput = new EditText(this);
        setsInput.setHint("组数");
        setsInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        setsInput.setText(String.valueOf(exercise.getSets()));

        EditText repsInput = new EditText(this);
        repsInput.setHint("次数");
        repsInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        repsInput.setText(String.valueOf(exercise.getReps()));

        androidx.appcompat.widget.LinearLayoutCompat layout = new androidx.appcompat.widget.LinearLayoutCompat(this);
        layout.setOrientation(androidx.appcompat.widget.LinearLayoutCompat.VERTICAL);
        layout.setPadding(40, 20, 40, 10);
        layout.addView(setsInput);
        layout.addView(repsInput);
        builder.setView(layout);

        builder.setPositiveButton("保存", (dialog, which) -> {
            try {
                int sets = Integer.parseInt(setsInput.getText().toString().trim());
                int reps = Integer.parseInt(repsInput.getText().toString().trim());
                if (sets <= 0 || reps <= 0) {
                    Toast.makeText(this, "组数和次数必须大于 0", Toast.LENGTH_SHORT).show();
                    return;
                }
                exercise.setSets(sets);
                exercise.setReps(reps);
                if (exerciseAdapter != null) {
                    exerciseAdapter.notifyDataSetChanged();
                }
                Toast.makeText(this, "动作配置已更新", Toast.LENGTH_SHORT).show();
            } catch (NumberFormatException e) {
                Toast.makeText(this, "请输入有效数字", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    private void saveCustomPlan() {
        String planName = planNameEdit != null ? planNameEdit.getText().toString().trim() : "";
        if (planName.isEmpty()) {
            Toast.makeText(this, "请输入计划名称", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedCategory.isEmpty()) {
            Toast.makeText(this, "请选择训练目标", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Exercise> selectedExercises = new ArrayList<>();
        for (Exercise exercise : availableExercises) {
            if (exercise.isSelected()) {
                selectedExercises.add(exercise);
            }
        }

        if (selectedExercises.isEmpty()) {
            Toast.makeText(this, "请至少选择一个动作", Toast.LENGTH_SHORT).show();
            return;
        }

        CustomPlan customPlan = new CustomPlan(planName, "自定义训练计划");
        customPlan.setCategory(selectedCategory);
        customPlan.setExercises(selectedExercises);
        // 确保所有添加的动作都被标记为选中
        for (Exercise exercise : selectedExercises) {
            exercise.setSelected(true);
        }
        customPlan.calculateTotals();
        serviceLocator.getPlanRepository().savePlan(customPlan);

        Toast.makeText(this, "计划已保存", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, PlanActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}
