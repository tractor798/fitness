package com.example.fitness.domain.usecase;

import android.os.Build;

import com.example.fitness.domain.repository.WorkoutRepository;
import com.example.fitness.domain.model.WorkoutSession;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 获取每周训练日的UseCase
 */
public class GetWeeklyWorkoutDaysUseCase {
    
    private final WorkoutRepository workoutRepository;
    
    public GetWeeklyWorkoutDaysUseCase(WorkoutRepository workoutRepository) {
        this.workoutRepository = workoutRepository;
    }
    
    /**
     * 执行用例，返回本周有训练记录的日期集合
     */
    public Set<LocalDate> execute() {
        List<WorkoutSession> sessions = workoutRepository.getAllSessions();
        Set<LocalDate> days = new HashSet<>();
        
        if (sessions == null) {
            return days;
        }
        
        for (WorkoutSession session : sessions) {
            long timestamp = session.getEndTime() > 0 ? session.getEndTime() : session.getStartTime();
            LocalDate date = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                date = Instant.ofEpochMilli(timestamp)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
            }
            days.add(date);
        }
        
        return days;
    }
}