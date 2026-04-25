package com.example.fitness.domain.usecase;

import com.example.fitness.domain.repository.WorkoutRepository;
import com.example.fitness.domain.model.WorkoutSession;

/**
 * 保存训练会话记录的UseCase
 */
public class SaveWorkoutSessionUseCase {
    
    private final WorkoutRepository repository;
    
    public SaveWorkoutSessionUseCase(WorkoutRepository repository) {
        this.repository = repository;
    }
    
    /**
     * 执行用例，保存已完成的训练会话
     * @param planName 计划名称
     * @param durationMs 训练时长（毫秒）
     * @param calories 消耗卡路里
     */
    public void execute(String planName, long durationMs, int calories) {
        WorkoutSession session = new WorkoutSession(planName);
        session.setDuration(durationMs);
        session.setCalories(calories);
        session.setCompleted(true);
        session.setEndTime(System.currentTimeMillis());
        
        repository.saveSession(session);
    }
}