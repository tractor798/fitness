package com.example.fitness;

import java.io.Serializable;

public class PlanItem implements Serializable {
    public static final int TYPE_PRESET = 0;
    public static final int TYPE_CUSTOM = 1;

    private String name;
    private String description;
    private int duration; // 分钟
    private int calories;
    private int type; // 0: 预设计划, 1: 自定义计划
    private CustomPlan customPlan; // 仅自定义计划有此字段

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    public int getCalories() { return calories; }
    public void setCalories(int calories) { this.calories = calories; }

    public int getType() { return type; }
    public void setType(int type) { this.type = type; }

    public CustomPlan getCustomPlan() { return customPlan; }
    public void setCustomPlan(CustomPlan customPlan) { this.customPlan = customPlan; }

    // 获取格式化的时长字符串
    public String getFormattedDuration() {
        return duration + "分钟";
    }

    // 获取格式化的卡路里字符串
    public String getFormattedCalories() {
        return calories + " kcal";
    }
}