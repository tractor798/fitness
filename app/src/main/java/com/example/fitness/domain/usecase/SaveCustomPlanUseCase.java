package com.example.fitness.domain.usecase;

import com.example.fitness.domain.repository.PlanRepository;
import com.example.fitness.domain.model.CustomPlan;
import com.example.fitness.domain.model.Exercise;
import java.util.List;

/**
 * 保存自定义训练计划的UseCase
 */
public class SaveCustomPlanUseCase {
    
    private final PlanRepository repository;
    
    public SaveCustomPlanUseCase(PlanRepository repository) {
        this.repository = repository;
    }
    
    /**
     * 执行用例，保存或更新自定义计划
     * @param planName 计划名称
     * @param description 描述
     * @param category 分类（减脂/增肌/塑形/耐力）
     * @param exercises 动作列表
     */
    public void execute(String planName, String description, String category, List<Exercise> exercises) {
        if (planName == null || planName.isEmpty()) {
            throw new IllegalArgumentException("计划名称不能为空");
        }
        
        CustomPlan plan = new CustomPlan(planName, description != null ? description : "");
        plan.setCategory(category != null ? category : "通用");
        plan.setExercises(exercises);
        
        // 自动计算总时长和卡路里
        int totalDuration = 0;
        int totalCalories = 0;
        for (Exercise exercise : exercises) {
            totalDuration += exercise.getDuration() > 0 ? exercise.getDuration() : 5; // 默认5分钟
            totalCalories += exercise.getCalories() > 0 ? exercise.getCalories() : 50; // 默认50卡
        }
        
        plan.setTotalDuration(totalDuration);
        plan.setTotalCalories(totalCalories);
        
        repository.savePlan(plan);
    }
}