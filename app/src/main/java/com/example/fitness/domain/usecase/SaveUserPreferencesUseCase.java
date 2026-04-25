package com.example.fitness.domain.usecase;

import com.example.fitness.domain.repository.UserPreferencesRepository;

/**
 * 保存用户偏好设置的UseCase
 */
public class SaveUserPreferencesUseCase {
    
    private final UserPreferencesRepository repository;
    
    public SaveUserPreferencesUseCase(UserPreferencesRepository repository) {
        this.repository = repository;
    }
    
    /**
     * 执行用例，更新用户偏好设置
     * @param height 身高 (cm)
     * @param weight 体重 (kg)
     * @param stepGoal 每日步数目标
     */
    public void execute(double height, double weight, int stepGoal) {
        repository.updatePreferences(height, weight, stepGoal);
    }
}