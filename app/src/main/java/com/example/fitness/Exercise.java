package com.example.fitness;

import java.io.Serializable;

public class Exercise implements Serializable {
    private String name;
    private String description;
    private int sets; // 组数
    private int reps; // 次数
    private int duration; // 预计时长（分钟）
    private int calories; // 预计消耗卡路里
    private boolean selected;

    public Exercise(String name, String description) {
        this.name = name;
        this.description = description;
        this.sets = 3;
        this.reps = 10;
        this.duration = 5;
        this.calories = 50;
        this.selected = false;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getSets() { return sets; }
    public void setSets(int sets) { this.sets = sets; }

    public int getReps() { return reps; }
    public void setReps(int reps) { this.reps = reps; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    public int getCalories() { return calories; }
    public void setCalories(int calories) { this.calories = calories; }

    public boolean isSelected() { return selected; }
    public void setSelected(boolean selected) { this.selected = selected; }
}