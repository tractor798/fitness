package com.example.fitness.domain.usecase;

import com.example.fitness.domain.repository.WorkoutRepository;
import com.example.fitness.domain.model.WorkoutSession;
import java.util.List;

/**
 * 加载个人纪录的UseCase
 */
public class LoadPersonalRecordsUseCase {
    
    private final WorkoutRepository workoutRepository;
    
    public LoadPersonalRecordsUseCase(WorkoutRepository workoutRepository) {
        this.workoutRepository = workoutRepository;
    }
    
    /**
     * 最高消耗卡路里纪录
     */
    public RecordResult getMaxCaloriesRecord() {
        List<WorkoutSession> sessions = workoutRepository.getAllSessions();
        int maxCalories = 0;
        long recordDate = 0;
        
        for (WorkoutSession session : sessions) {
            int calories = session.getCalories();
            if (calories > maxCalories) {
                maxCalories = calories;
                recordDate = session.getStartTime();
            }
        }
        
        return new RecordResult(maxCalories, recordDate);
    }
    
    /**
     * 单次最长训练时长（分钟）
     */
    public RecordResult getLongestWorkoutRecord() {
        List<WorkoutSession> sessions = workoutRepository.getAllSessions();
        long maxDuration = 0;
        long recordDate = 0;
        
        for (WorkoutSession session : sessions) {
            long duration = session.getDuration();
            if (duration > maxDuration) {
                maxDuration = duration;
                recordDate = session.getStartTime();
            }
        }
        
        return new RecordResult((int)maxDuration, recordDate);
    }
    
    /**
     * 纪录结果数据类
     */
    public static class RecordResult {
        public final int value;
        public final long date;
        
        public RecordResult(int value, long date) {
            this.value = value;
            this.date = date;
        }
    }
}