package com.example.fitness.domain.usecase;

import com.example.fitness.domain.model.Exercise;
import java.util.ArrayList;
import java.util.List;

/**
 * 创建预设训练动作列表的UseCase
 */
public class CreatePresetExercisesUseCase {
    
    /**
     * 执行用例，返回默认的预设训练动作列表
     */
    public List<Exercise> execute() {
        List<Exercise> exercises = new ArrayList<>();
        
        // 这里可以添加更多预设动作
        exercises.add(new Exercise("热身运动", "全身激活", 0));
        exercises.add(new Exercise("深蹲 Squats", "腿臀强化 · 基础动作", 0));
        exercises.add(new Exercise("俯卧撑 Push-ups", "胸肩激活 · 自重训练", 0));
        exercises.add(new Exercise("平板支撑 Plank", "核心稳定 · 控制呼吸", 0));
        exercises.add(new Exercise("登山跑 Mountain Climbers", "心肺燃脂 · 节奏冲刺", 0));
        
        return exercises;
    }
}