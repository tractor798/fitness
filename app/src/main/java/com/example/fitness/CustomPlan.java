package com.example.fitness;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CustomPlan implements Serializable {
    private String id; // 唯一标识符
    private String name;
    private String description;
    private String category; // 分类：减脂、增肌、塑形、耐力
    private List<Exercise> exercises;
    private int totalDuration; // 总时长（分钟）
    private int totalCalories; // 总消耗卡路里

    public CustomPlan(String name, String description) {
        this.id = UUID.randomUUID().toString(); // 自动生成唯一ID
        this.name = name;
        this.description = description;
        this.category = "";
        this.exercises = new ArrayList<>();
        this.totalDuration = 0;
        this.totalCalories = 0;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

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