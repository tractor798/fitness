package com.example.fitness.domain.service;

import android.os.Build;

import com.example.fitness.WorkoutSession;
import com.example.fitness.WorkoutStats;
import com.example.fitness.data.repository.WorkoutRepository;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

/**
 * 训练统计服务，负责聚合训练记录。
 */
public class WorkoutStatsService {

    private final WorkoutRepository workoutRepository;

    public WorkoutStatsService(WorkoutRepository workoutRepository) {
        this.workoutRepository = workoutRepository;
    }

    public WorkoutStats getTodayStats() {
        long start = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            start = LocalDate.now()
                    .atStartOfDay(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli();
        }
        return getStatsForPeriod(start);
    }

    public WorkoutStats getWeekStats() {
        LocalDate today = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            today = LocalDate.now();
        }
        LocalDate monday = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            monday = today.minusDays(today.getDayOfWeek().getValue() - 1L);
        }
        long start = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            start = monday.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
        }
        return getStatsForPeriod(start);
    }

    public WorkoutStats getStatsForPeriod(long startTimeMillis) {
        List<WorkoutSession> history = workoutRepository.getAllSessions();
        WorkoutStats stats = new WorkoutStats();
        for (WorkoutSession session : history) {
            long endTime = session.getEndTime() > 0 ? session.getEndTime() : session.getStartTime();
            if (endTime >= startTimeMillis) {
                stats.addDuration(session.getDuration());
                stats.addCalories(session.getCalories());
                stats.incrementWorkouts();
            }
        }
        return stats;
    }
}
