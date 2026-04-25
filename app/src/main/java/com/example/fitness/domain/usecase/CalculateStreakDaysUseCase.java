package com.example.fitness.domain.usecase;

import java.time.LocalDate;
import java.util.Set;

/**
 * 计算连续训练天数的UseCase
 */
public class CalculateStreakDaysUseCase {
    
    /**
     * 执行用例，计算从today往前推的连续训练天数
     * @param workoutDays 有训练记录的日期集合
     * @param today 当前日期
     * @return 连续训练天数
     */
    public int execute(Set<LocalDate> workoutDays, LocalDate today) {
        if (workoutDays == null || workoutDays.isEmpty()) {
            return 0;
        }
        
        int streak = 0;
        LocalDate cursor = today;
        
        while (workoutDays.contains(cursor)) {
            streak++;
            cursor = cursor.minusDays(1);
        }
        
        return streak;
    }
}