package com.example.fitness.data.local;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 用户偏好设置管理类
 * 专门负责存储用户的个人配置信息
 */
public class UserPreferences {
    
    private static final String PREFS_NAME = "fitness_user_prefs";
    private static final String KEY_USER_HEIGHT = "user_height";
    private static final String KEY_USER_WEIGHT_GOAL = "user_weight_goal";
    private static final String KEY_USER_TRAINING_FREQ = "user_training_freq";
    
    private final SharedPreferences prefs;
    
    public UserPreferences(Context context) {
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
    
    /**
     * 保存用户身高（米）
     */
    public void saveUserHeight(double heightInMeters) {
        prefs.edit().putString(KEY_USER_HEIGHT, String.valueOf(heightInMeters)).apply();
    }
    
    /**
     * 获取用户身高（米），默认1.75米
     */
    public double getUserHeight() {
        String height = prefs.getString(KEY_USER_HEIGHT, "1.75");
        try {
            return Double.parseDouble(height);
        } catch (NumberFormatException e) {
            return 1.75;
        }
    }
    
    /**
     * 保存用户体重目标
     */
    public void saveWeightGoal(double weightGoal) {
        prefs.edit().putString(KEY_USER_WEIGHT_GOAL, String.valueOf(weightGoal)).apply();
    }
    
    /**
     * 获取用户体重目标
     */
    public double getWeightGoal() {
        String goal = prefs.getString(KEY_USER_WEIGHT_GOAL, "0");
        try {
            return Double.parseDouble(goal);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    /**
     * 保存训练频率（每周天数）
     */
    public void saveTrainingFrequency(int daysPerWeek) {
        prefs.edit().putInt(KEY_USER_TRAINING_FREQ, daysPerWeek).apply();
    }
    
    /**
     * 获取训练频率，默认5天
     */
    public int getTrainingFrequency() {
        return prefs.getInt(KEY_USER_TRAINING_FREQ, 5);
    }
    
    /**
     * 清空所有用户偏好
     */
    public void clearAll() {
        prefs.edit().clear().apply();
    }
}
