package com.example.fitness.data.local;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.fitness.domain.model.BodyMetric;
import com.example.fitness.domain.repository.BodyMetricRepository;
import com.example.fitness.db.SQLiteBodyMetricRepository;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 本地身体指标仓库实现。优先使用 SQLite，失败时降级到 SharedPreferences。
 */
public class LocalBodyMetricRepository implements BodyMetricRepository {

    private static final String TAG = "LocalBodyMetricRepo";
    private static final String PREFS_NAME = "fitness_plans";
    private static final String BODY_METRICS_KEY = "body_metrics";
    private static final String USER_HEIGHT_KEY = "user_height";
    private static final int MAX_METRICS_SIZE = 365;

    private final SQLiteBodyMetricRepository sqliteRepository;
    private final SharedPreferences prefs;
    private final Gson gson;

    public LocalBodyMetricRepository(Context context) {
        this.gson = new Gson();
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        SQLiteBodyMetricRepository repo = null;
        try {
            repo = new SQLiteBodyMetricRepository(context);
        } catch (Exception e) {
            Log.e(TAG, "SQLite body metric repository init failed", e);
        }
        this.sqliteRepository = repo;
    }

    @Override
    public void saveMetric(BodyMetric metric) {
        if (sqliteRepository != null) {
            try {
                sqliteRepository.saveMetric(metric);
                return;
            } catch (Exception e) {
                Log.e(TAG, "SQLite save failed", e);
            }
        }

        List<BodyMetric> metrics = getAllMetrics();
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
            metrics.add(0, metric);
        }
        if (metrics.size() > MAX_METRICS_SIZE) {
            metrics = new ArrayList<>(metrics.subList(0, MAX_METRICS_SIZE));
        }
        saveToPrefs(metrics);
    }

    @Override
    public List<BodyMetric> getAllMetrics() {
        if (sqliteRepository != null) {
            try {
                return sqliteRepository.getAllMetrics();
            } catch (Exception e) {
                Log.e(TAG, "SQLite read failed, fallback to SharedPreferences", e);
            }
        }

        String json = prefs.getString(BODY_METRICS_KEY, "[]");
        Type type = new TypeToken<List<BodyMetric>>() {}.getType();
        List<BodyMetric> metrics = gson.fromJson(json, type);
        return metrics != null ? metrics : new ArrayList<>();
    }

    @Override
    public BodyMetric getLatestMetric() {
        if (sqliteRepository != null) {
            try {
                BodyMetric metric = sqliteRepository.getLatestMetric();
                if (metric != null) {
                    return metric;
                }
            } catch (Exception e) {
                Log.e(TAG, "SQLite query failed", e);
            }
        }

        List<BodyMetric> metrics = getAllMetrics();
        return metrics.isEmpty() ? null : metrics.get(0);
    }

    @Override
    public BodyMetric getMetricByDate(String date) {
        List<BodyMetric> metrics = getAllMetrics();
        for (BodyMetric metric : metrics) {
            if (metric.getDate().equals(date)) {
                return metric;
            }
        }
        return null;
    }

    @Override
    public void deleteMetric(String date) {
        if (sqliteRepository != null) {
            try {
                sqliteRepository.deleteMetric(date);
                return;
            } catch (Exception e) {
                Log.e(TAG, "SQLite delete failed", e);
            }
        }

        List<BodyMetric> metrics = getAllMetrics();
        metrics.removeIf(metric -> metric.getDate().equals(date));
        saveToPrefs(metrics);
    }

    @Override
    public List<BodyMetric> getMetricsForDays(int days) {
        List<BodyMetric> allMetrics = getAllMetrics();
        List<BodyMetric> result = new ArrayList<>();
        long startTime = System.currentTimeMillis() - (days * 24L * 60 * 60 * 1000);
        for (BodyMetric metric : allMetrics) {
            if (metric.getTimestamp() >= startTime) {
                result.add(metric);
            }
        }
        return result;
    }

    @Override
    public void clearAll() {
        if (sqliteRepository != null) {
            try {
                sqliteRepository.clearAll();
            } catch (Exception e) {
                Log.e(TAG, "SQLite clear failed", e);
            }
        }
        prefs.edit().remove(BODY_METRICS_KEY).remove(USER_HEIGHT_KEY).apply();
    }

    public void saveUserHeight(double heightInM) {
        prefs.edit().putString(USER_HEIGHT_KEY, String.valueOf(heightInM)).apply();
    }

    public double getUserHeight() {
        String height = prefs.getString(USER_HEIGHT_KEY, "1.75");
        try {
            return Double.parseDouble(height);
        } catch (NumberFormatException e) {
            return 1.75;
        }
    }

    private void saveToPrefs(List<BodyMetric> metrics) {
        String json = gson.toJson(metrics);
        prefs.edit().putString(BODY_METRICS_KEY, json).apply();
    }
}
