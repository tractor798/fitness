package com.example.fitness;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PlanDataManager {
    private static final String PREFS_NAME = "fitness_plans";
    private static final String CUSTOM_PLANS_KEY = "custom_plans";
    private static final String WORKOUT_HISTORY_KEY = "workout_history";
    private static final String BODY_METRICS_KEY = "body_metrics";
    private static final String USER_HEIGHT_KEY = "user_height";

    private static PlanDataManager instance;
    private SharedPreferences prefs;
    private Gson gson;

    private PlanDataManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public static synchronized PlanDataManager getInstance(Context context) {
        if (instance == null) {
            instance = new PlanDataManager(context.getApplicationContext());
        }
        return instance;
    }

    // 保存自定义计划
    public void saveCustomPlan(CustomPlan plan) {
        List<CustomPlan> plans = getCustomPlans();
        // 检查是否已存在同名计划，如果存在则更新
        boolean exists = false;
        for (int i = 0; i < plans.size(); i++) {
            if (plans.get(i).getName().equals(plan.getName())) {
                plans.set(i, plan);
                exists = true;
                break;
            }
        }
        if (!exists) {
            plans.add(plan);
        }
        saveCustomPlans(plans);
    }

    // 获取所有自定义计划
    public List<CustomPlan> getCustomPlans() {
        String json = prefs.getString(CUSTOM_PLANS_KEY, "[]");
        Type type = new TypeToken<List<CustomPlan>>(){}.getType();
        List<CustomPlan> plans = gson.fromJson(json, type);
        return plans != null ? plans : new ArrayList<>();
    }

    // 删除自定义计划
    public void deleteCustomPlan(String planName) {
        List<CustomPlan> plans = getCustomPlans();
        plans.removeIf(plan -> plan.getName().equals(planName));
        saveCustomPlans(plans);
    }

    // 获取指定名称的自定义计划
    public CustomPlan getCustomPlan(String planName) {
        List<CustomPlan> plans = getCustomPlans();
        for (CustomPlan plan : plans) {
            if (plan.getName().equals(planName)) {
                return plan;
            }
        }
        return null;
    }

    // 保存训练历史
    public void saveWorkoutSession(WorkoutSession session) {
        List<WorkoutSession> history = getWorkoutHistory();
        history.add(0, session); // 添加到开头
        // 只保留最近50条记录
        if (history.size() > 50) {
            history = history.subList(0, 50);
        }
        saveWorkoutHistory(history);
    }

    // 获取训练历史
    public List<WorkoutSession> getWorkoutHistory() {
        String json = prefs.getString(WORKOUT_HISTORY_KEY, "[]");
        Type type = new TypeToken<List<WorkoutSession>>(){}.getType();
        List<WorkoutSession> history = gson.fromJson(json, type);
        return history != null ? history : new ArrayList<>();
    }

    // 获取今日训练统计
    public WorkoutStats getTodayStats() {
        List<WorkoutSession> history = getWorkoutHistory();
        WorkoutStats stats = new WorkoutStats();

        long todayStart = System.currentTimeMillis() - (24 * 60 * 60 * 1000); // 24小时前

        for (WorkoutSession session : history) {
            if (session.getEndTime() >= todayStart) {
                stats.addDuration(session.getDuration());
                stats.addCalories(session.getCalories());
                stats.incrementWorkouts();
            }
        }

        return stats;
    }

    // 获取本周训练统计
    public WorkoutStats getWeekStats() {
        List<WorkoutSession> history = getWorkoutHistory();
        WorkoutStats stats = new WorkoutStats();

        long weekStart = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000); // 7天前

        for (WorkoutSession session : history) {
            if (session.getEndTime() >= weekStart) {
                stats.addDuration(session.getDuration());
                stats.addCalories(session.getCalories());
                stats.incrementWorkouts();
            }
        }

        return stats;
    }

    private void saveCustomPlans(List<CustomPlan> plans) {
        String json = gson.toJson(plans);
        prefs.edit().putString(CUSTOM_PLANS_KEY, json).apply();
    }

    private void saveWorkoutHistory(List<WorkoutSession> history) {
        String json = gson.toJson(history);
        prefs.edit().putString(WORKOUT_HISTORY_KEY, json).apply();
    }

    // 清空所有数据（用于重置）
    public void clearAllData() {
        prefs.edit().clear().apply();
    }

    // ==================== 身体指标管理方法 ====================

    // 保存用户身高（用于BMI计算）
    public void saveUserHeight(double heightInM) {
        prefs.edit().putString(USER_HEIGHT_KEY, String.valueOf(heightInM)).apply();
    }

    // 获取用户身高
    public double getUserHeight() {
        String height = prefs.getString(USER_HEIGHT_KEY, "1.75");
        try {
            return Double.parseDouble(height);
        } catch (NumberFormatException e) {
            return 1.75;
        }
    }

    // 保存身体指标
    public void saveBodyMetric(BodyMetric metric) {
        List<BodyMetric> metrics = getBodyMetrics();
        // 检查今日是否已有记录
        String todayDate = metric.getDate();
        boolean exists = false;
        for (int i = 0; i < metrics.size(); i++) {
            if (metrics.get(i).getDate().equals(todayDate)) {
                metrics.set(i, metric);
                exists = true;
                break;
            }
        }
        if (!exists) {
            metrics.add(0, metric); // 添加到开头（最新的）
        }
        // 只保留最近365条记录（1年）
        if (metrics.size() > 365) {
            metrics = new ArrayList<>(metrics.subList(0, 365));
        }
        saveBodyMetrics(metrics);
    }

    // 获取所有身体指标
    public List<BodyMetric> getBodyMetrics() {
        String json = prefs.getString(BODY_METRICS_KEY, "[]");
        Type type = new TypeToken<List<BodyMetric>>(){}.getType();
        List<BodyMetric> metrics = gson.fromJson(json, type);
        return metrics != null ? metrics : new ArrayList<>();
    }

    // 获取最新的身体指标
    public BodyMetric getLatestBodyMetric() {
        List<BodyMetric> metrics = getBodyMetrics();
        if (!metrics.isEmpty()) {
            return metrics.get(0);
        }
        return null;
    }

    // 获取指定日期的身体指标
    public BodyMetric getBodyMetricByDate(String date) {
        List<BodyMetric> metrics = getBodyMetrics();
        for (BodyMetric metric : metrics) {
            if (metric.getDate().equals(date)) {
                return metric;
            }
        }
        return null;
    }

    // 删除身体指标
    public void deleteBodyMetric(String date) {
        List<BodyMetric> metrics = getBodyMetrics();
        metrics.removeIf(metric -> metric.getDate().equals(date));
        saveBodyMetrics(metrics);
    }

    // 获取指定天数内的身体指标
    public List<BodyMetric> getBodyMetricsForDays(int days) {
        List<BodyMetric> allMetrics = getBodyMetrics();
        List<BodyMetric> result = new ArrayList<>();
        long startTime = System.currentTimeMillis() - (days * 24L * 60 * 60 * 1000);

        for (BodyMetric metric : allMetrics) {
            if (metric.getTimestamp() >= startTime) {
                result.add(metric);
            }
        }
        return result;
    }

    // 获取体重变化（最新 - 最旧）
    public double getWeightChange() {
        List<BodyMetric> metrics = getBodyMetrics();
        if (metrics.size() >= 2) {
            BodyMetric latest = metrics.get(0);
            BodyMetric oldest = metrics.get(metrics.size() - 1);
            return latest.getWeight() - oldest.getWeight();
        } else if (!metrics.isEmpty()) {
            return 0;
        }
        return 0;
    }

    // 获取体重变化（最新 - 指定天数前）
    public double getWeightChangeInDays(int days) {
        List<BodyMetric> metrics = getBodyMetricsForDays(days);
        if (!metrics.isEmpty()) {
            BodyMetric latest = metrics.get(0);
            BodyMetric oldest = metrics.get(metrics.size() - 1);
            return latest.getWeight() - oldest.getWeight();
        }
        return 0;
    }

    // 获取平均体重（指定天数）
    public double getAverageWeight(int days) {
        List<BodyMetric> metrics = getBodyMetricsForDays(days);
        if (metrics.isEmpty()) {
            return 0;
        }
        double sum = 0;
        for (BodyMetric metric : metrics) {
            sum += metric.getWeight();
        }
        return sum / metrics.size();
    }

    private void saveBodyMetrics(List<BodyMetric> metrics) {
        String json = gson.toJson(metrics);
        prefs.edit().putString(BODY_METRICS_KEY, json).apply();
    }