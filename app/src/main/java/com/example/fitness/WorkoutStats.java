package com.example.fitness;

import android.annotation.SuppressLint;

public class WorkoutStats {
    private int totalWorkouts;
    private long totalDuration; // 总时长（毫秒）
    private int totalCalories;

    public WorkoutStats() {
        this.totalWorkouts = 0;
        this.totalDuration = 0;
        this.totalCalories = 0;
    }

    // Getters
    public int getTotalWorkouts() { return totalWorkouts; }
    public long getTotalDuration() { return totalDuration; }
    public int getTotalCalories() { return totalCalories; }

    // 累加方法
    public void incrementWorkouts() { totalWorkouts++; }
    public void addDuration(long duration) { totalDuration += duration; }
    public void addCalories(int calories) { totalCalories += calories; }

    // 获取格式化的时长字符串
    @SuppressLint("DefaultLocale")
    public String getFormattedDuration() {
        long seconds = totalDuration / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;

        if (hours > 0) {
            return String.format("%d时%d分", hours, minutes % 60);
        } else if (minutes > 0) {
            return String.format("%d分", minutes);
        } else {
            return String.format("%d秒", seconds);
        }
    }

    // 获取平均每次训练时长
    @SuppressLint("DefaultLocale")
    public String getAverageDuration() {
        if (totalWorkouts == 0) return "0分";

        long avgSeconds = (totalDuration / totalWorkouts) / 1000;
        long avgMinutes = avgSeconds / 60;

        return String.format("%d分", avgMinutes);
    }

    // 获取平均每次消耗卡路里
    public int getAverageCalories() {
        if (totalWorkouts == 0) return 0;
        return totalCalories / totalWorkouts;
    }
}