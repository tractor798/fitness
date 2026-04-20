package com.example.fitness;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class PlanActivity extends BaseActivity {

    private RecyclerView plansRecyclerView;
    private PlanAdapter planAdapter;
    private List<PlanItem> planItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);
        setupBottomNavigation(1);

        initializeViews();
        loadPlans();
        setupListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 每次回到页面时刷新计划列表
        loadPlans();
    }

    private void initializeViews() {
        plansRecyclerView = findViewById(R.id.plansRecyclerView);
        if (plansRecyclerView != null) {
            plansRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        }

        // 设置创建自定义计划按钮的点击监听器
        Button createCustomPlanButton = findViewById(R.id.createCustomPlanButton);
        if (createCustomPlanButton != null) {
            createCustomPlanButton.setOnClickListener(v -> {
                Intent intent = new Intent(this, CustomPlanActivity.class);
                startActivity(intent);
            });
        }
    }

    private void loadPlans() {
        planItems = new ArrayList<>();

        // 添加预设计划
        PlanItem defaultPlan = new PlanItem();
        defaultPlan.setName("全身燃脂");
        defaultPlan.setDescription("极致燃脂体验，结合HIIT与抗阻训练，在45分钟内重塑体能巅峰。");
        defaultPlan.setDuration(45);
        defaultPlan.setCalories(420);
        defaultPlan.setType(PlanItem.TYPE_PRESET);
        planItems.add(defaultPlan);

        // 添加自定义计划
        List<CustomPlan> customPlans = PlanDataManager.getInstance(this).getCustomPlans();
        for (CustomPlan customPlan : customPlans) {
            PlanItem customPlanItem = new PlanItem();
            customPlanItem.setName(customPlan.getName());
            customPlanItem.setDescription(customPlan.getDescription());
            customPlanItem.setDuration(customPlan.getTotalDuration());
            customPlanItem.setCalories(customPlan.getTotalCalories());
            customPlanItem.setType(PlanItem.TYPE_CUSTOM);
            customPlanItem.setCustomPlan(customPlan);
            planItems.add(customPlanItem);
        }

        if (planAdapter == null) {
            planAdapter = new PlanAdapter(planItems, this::onPlanClick);
            if (plansRecyclerView != null) {
                plansRecyclerView.setAdapter(planAdapter);
            }
        } else {
            planAdapter.updatePlans(planItems);
        }
    }

    private void setupListeners() {
        // 其他监听器设置
    }

    private void onPlanClick(PlanItem planItem) {
        Intent intent = new Intent(this, WorkoutActivity.class);
        intent.putExtra("workout_name", planItem.getName());
        intent.putExtra("plan_type", planItem.getType());

        if (planItem.getType() == PlanItem.TYPE_CUSTOM) {
            intent.putExtra("custom_plan", planItem.getCustomPlan());
        }

        startActivity(intent);
    }
}
