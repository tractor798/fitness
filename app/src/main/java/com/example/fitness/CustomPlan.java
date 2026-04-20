package com.example.fitness;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CustomPlan implements Serializable {
    private String name;
    private String description;
    private List<Exercise> exercises;
    private int totalDuration; // 总时长（分钟）
    private int totalCalories; // 总消耗卡路里

    public CustomPlan(String name, String description) {
        this.name = name;
        this.description = description;
        this.exercises = new ArrayList<>();
        this.totalDuration = 0;
        this.totalCalories = 0;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<Exercise> getExercises() { return exercises; }
    public void setExercises(List<Exercise> exercises) { this.exercises = exercises; }

    public int getTotalDuration() { return totalDuration; }
    public void setTotalDuration(int totalDuration) { this.totalDuration = totalDuration; }

    public int getTotalCalories() { return totalCalories; }
    public void setTotalCalories(int totalCalories) { this.totalCalories = totalCalories; }

    // 计算总时长和卡路里
    public void calculateTotals() {
        totalDuration = 0;
        totalCalories = 0;
        for (Exercise exercise : exercises) {
            if (exercise.isSelected()) {
                totalDuration += exercise.getDuration();
                totalCalories += exercise.getCalories();
            }
        }
    }

    // 添加运动
    public void addExercise(Exercise exercise) {
        exercises.add(exercise);
        calculateTotals();
    }

    // 移除运动
    public void removeExercise(Exercise exercise) {
        exercises.remove(exercise);
        calculateTotals();
    }
}