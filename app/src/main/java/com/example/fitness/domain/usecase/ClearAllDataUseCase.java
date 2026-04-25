package com.example.fitness.domain.usecase;

import com.example.fitness.ServiceLocator;

/**
 * 清除所有用户数据的UseCase
 */
public class ClearAllDataUseCase {
    
    private final ServiceLocator serviceLocator;
    
    public ClearAllDataUseCase(ServiceLocator serviceLocator) {
        this.serviceLocator = serviceLocator;
    }
    
    /**
     * 执行用例，清除所有本地存储的训练、指标和计划数据
     */
    public void execute() {
        serviceLocator.clearAllData();
    }
}