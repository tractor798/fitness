package com.example.fitness.data.local;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.example.fitness.domain.model.CustomPlan;
import com.example.fitness.domain.repository.PlanRepository;
import com.example.fitness.db.SQLitePlanRepository;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 计划数据仓库的本地实现
 * 使用SQLite优先，SharedPreferences作为降级方案
 */
public class LocalPlanRepository implements PlanRepository {
    
    private static final String TAG = "LocalPlanRepository";
    private static final String PREFS_NAME = "fitness_plans";
    private static final String CUSTOM_PLANS_KEY = "custom_plans";
    
    private final SQLitePlanRepository sqliteRepository;
    private final SharedPreferences prefs;
    private final Gson gson;
    
    public LocalPlanRepository(Context context) {
        this.gson = new Gson();
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        
        // 尝试初始化SQLite仓库
        SQLitePlanRepository repo = null;
        try {
            repo = new SQLitePlanRepository(context);
        } catch (Exception e) {
            Log.e(TAG, "SQLite repository init failed, using SharedPreferences only", e);
        }
        this.sqliteRepository = repo;
    }
    
    @Override
    public void savePlan(CustomPlan plan) {
        if (sqliteRepository != null) {
            try {
                sqliteRepository.savePlan(plan);
                return;
            } catch (Exception e) {
                Log.e(TAG, "SQLite save failed, fallback to SharedPreferences", e);
            }
        }
        
        // SharedPreferences降级方案
        List<CustomPlan> plans = getAllPlans();
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
        saveToPrefs(plans);
    }
    
    @Override
    public List<CustomPlan> getAllPlans() {
        if (sqliteRepository != null) {
            try {
                return sqliteRepository.getAllPlans();
            } catch (Exception e) {
                Log.e(TAG, "SQLite read failed, fallback to SharedPreferences", e);
            }
        }
        
        // SharedPreferences降级方案
        String json = prefs.getString(CUSTOM_PLANS_KEY, "[]");
        Type type = new TypeToken<List<CustomPlan>>(){}.getType();
        List<CustomPlan> plans = gson.fromJson(json, type);
        return plans != null ? plans : new ArrayList<>();
    }
    
    @Override
    public CustomPlan getPlanByName(String planName) {
        if (sqliteRepository != null) {
            try {
                CustomPlan plan = sqliteRepository.getPlanByName(planName);
                if (plan != null) return plan;
            } catch (Exception e) {
                Log.e(TAG, "SQLite query failed", e);
            }
        }
        
        // SharedPreferences降级方案
        List<CustomPlan> plans = getAllPlans();
        for (CustomPlan plan : plans) {
            if (plan.getName().equals(planName)) {
                return plan;
            }
        }
        return null;
    }
    
    @Override
    public void deletePlan(String planName) {
        if (sqliteRepository != null) {
            try {
                sqliteRepository.deletePlan(planName);
                return;
            } catch (Exception e) {
                Log.e(TAG, "SQLite delete failed", e);
            }
        }
        
        // SharedPreferences降级方案
        List<CustomPlan> plans = getAllPlans();
        plans.removeIf(plan -> plan.getName().equals(planName));
        saveToPrefs(plans);
    }
    
    private void saveToPrefs(List<CustomPlan> plans) {
        String json = gson.toJson(plans);
        prefs.edit().putString(CUSTOM_PLANS_KEY, json).apply();
    }
}
