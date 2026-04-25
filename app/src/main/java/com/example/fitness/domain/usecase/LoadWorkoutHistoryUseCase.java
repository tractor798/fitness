package com.example.fitness.domain.usecase;

import com.example.fitness.domain.repository.WorkoutRepository;
import com.example.fitness.domain.model.WorkoutSession;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

/**
 * 加载指定日期训练记录的UseCase
 */
public class LoadWorkoutHistoryUseCase {
    
    private final WorkoutRepository repository;
    
    public LoadWorkoutHistoryUseCase(WorkoutRepository repository) {
        this.repository = repository;
    }
    
    /**
     * 执行用例，获取指定日期的所有训练记录
     * @param date 目标日期
     * @return 该日期的训练记录列表
     */
    public List<WorkoutSession> execute(LocalDate date) {
        List<WorkoutSession> allSessions = repository.getAllSessions();
        List<WorkoutSession> dailySessions = new ArrayList<>();
        
        for (WorkoutSession session : allSessions) {
            // WorkoutSession使用startTime毫秒时间戳，需要转换为LocalDate进行比较
            LocalDate sessionDate = Instant.ofEpochMilli(session.getStartTime())
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            
            if (sessionDate.equals(date)) {
                dailySessions.add(session);
            }
        }
        
        return dailySessions;
    }
}