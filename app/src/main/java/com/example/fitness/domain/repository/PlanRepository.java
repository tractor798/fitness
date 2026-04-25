package com.example.fitness.domain.repository;

import com.example.fitness.domain.model.CustomPlan;
import java.util.List;

/**
 * 计划数据仓库接口
 * 定义自定义计划的CRUD操作
 */
public interface PlanRepository {
    
    /**
     * 保存或更新自定义计划
     * @param plan 要保存的计划
     */
    void savePlan(CustomPlan plan);
    
    /**
     * 获取所有自定义计划
     * @return 计划列表
     */
    List<CustomPlan> getAllPlans();
    
    /**
     * 根据名称获取计划
     * @param planName 计划名称
     * @return 计划对象，不存在返回null
     */
    CustomPlan getPlanByName(String planName);
    
    /**
     * 删除计划
     * @param planName 计划名称
     */
    void deletePlan(String planName);
}
