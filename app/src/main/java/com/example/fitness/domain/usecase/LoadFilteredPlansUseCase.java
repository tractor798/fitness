package com.example.fitness.domain.usecase;

import com.example.fitness.domain.repository.PlanRepository;
import com.example.fitness.domain.model.CustomPlan;
import java.util.ArrayList;
import java.util.List;

/**
 * 加载并过滤训练计划的UseCase
 */
public class LoadFilteredPlansUseCase {
    
    private final PlanRepository repository;
    
    public LoadFilteredPlansUseCase(PlanRepository repository) {
        this.repository = repository;
    }
    
    /**
     * 执行用例，根据过滤器获取计划列表
     * @param filter 过滤条件：all, fat_loss, muscle, shaping, recovery
     * @return 过滤后的计划列表
     */
    public List<CustomPlan> execute(String filter) {
        List<CustomPlan> allPlans = repository.getAllPlans();
        
        if ("all".equals(filter)) {
            return allPlans;
        }
        
        List<CustomPlan> filtered = new ArrayList<>();
        for (CustomPlan plan : allPlans) {
            if (matchesFilter(plan, filter)) {
                filtered.add(plan);
            }
        }
        
        return filtered;
    }
    
    private boolean matchesFilter(CustomPlan plan, String filter) {
        String category = plan.getCategory();
        if (category != null && !category.isEmpty()) {
            return switch (filter) {
                case "fat_loss" -> category.contains("减脂");
                case "muscle" -> category.contains("增肌");
                case "shaping" -> category.contains("塑形");
                case "recovery" -> category.contains("耐力");
                default -> true;
            };
        }
        
        // 兼容旧数据
        String text = (plan.getName() + " " + plan.getDescription()).toLowerCase();
        return switch (filter) {
            case "fat_loss" -> text.contains("减脂") || text.contains("燃脂");
            case "muscle" -> text.contains("增肌") || text.contains("力量");
            case "shaping" -> text.contains("塑形") || text.contains("核心");
            case "recovery" -> text.contains("恢复") || text.contains("拉伸");
            default -> true;
        };
    }
}