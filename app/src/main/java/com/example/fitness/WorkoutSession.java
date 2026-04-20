package com.example.fitness;

import java.io.Serializable;

public class WorkoutSession implements Serializable {
    private String planName;
    private long startTime;
    private long endTime;
    private long duration; // 训练时长（毫秒）
    private int calories; // 消耗卡路里
    private boolean completed; // 是否完成

    public WorkoutSession(String planName) {
        this.planName = planName;
        this.startTime = System.currentTimeMillis();
        this.completed = false;
    }

    // Getters and Setters
    public String getPlanName() { return planName; }
    public void setPlanName(String planName) { this.planName = planName; }

    public long getStartTime() { return startTime; }
    public void setStartTime(long startTime) { this.startTime = startTime; }

    public long getEndTime() { return endTime; }
    public void setEndTime(long endTime) { this.endTime = endTime; }

    public long getDuration() { return duration; }
    public void setDuration(long duration) { this.duration = duration; }

    public int getCalories() { return calories; }
    public void setCalories(int calories) { this.calories = calories; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    // 完成训练会话
    public void complete(int caloriesBurned) {
        this.endTime = System.currentTimeMillis();
        this.duration = endTime - startTime;
        this.calories = caloriesBurned;
        this.completed = true;
    }

    // 获取格式化的时长字符串
    public String getFormattedDuration() {
        long seconds = duration / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;

        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes % 60, seconds % 60);
        } else {
            return String.format("%d:%02d", minutes, seconds % 60);
        }
    }
}