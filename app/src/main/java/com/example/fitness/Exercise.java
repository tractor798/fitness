package com.example.fitness;

import com.example.fitness.config.AppConfig;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Exercise implements Serializable {
    private String name;
    private String description;
    private int sets; // 组数
    private int reps; // 次数
    private int duration; // 预计时长（分钟）
    private int calories; // 预计消耗卡路里
    private boolean selected;
    private String imageUrl; // 网络图片URL
    private int imageResId; // 本地资源ID

    public Exercise(String name, String description) {
        this.name = name;
        this.description = description;
        this.sets = AppConfig.DEFAULT_EXERCISE_SETS;
        this.reps = AppConfig.DEFAULT_EXERCISE_REPS;
        this.duration = AppConfig.DEFAULT_EXERCISE_DURATION;
        this.calories = AppConfig.DEFAULT_EXERCISE_CALORIES;
        this.selected = false;
        this.imageUrl = "";
        this.imageResId = 0;
    }

    public Exercise(String name, String description, String imageUrl) {
        this(name, description);
        this.imageUrl = imageUrl != null ? imageUrl : "";
    }

    public Exercise(String name, String description, int imageResId) {
        this(name, description);
        this.imageResId = imageResId;
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

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public int getImageResId() { return imageResId; }
    public void setImageResId(int imageResId) { this.imageResId = imageResId; }
}