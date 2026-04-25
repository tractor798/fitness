package com.example.fitness.domain.usecase;

import com.example.fitness.domain.repository.UserPreferencesRepository;
import com.example.fitness.domain.model.UserPreferences;

/**
 * 加载用户偏好设置的UseCase
 */
public class LoadUserPreferencesUseCase {
    
    private final UserPreferencesRepository repository;
    
    public LoadUserPreferencesUseCase(UserPreferencesRepository repository) {
        this.repository = repository;
    }
    
    /**
     * 执行用例，获取当前用户偏好设置
     */
    public UserPreferences execute() {
        return repository.getPreferences();
    }
}