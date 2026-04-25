package com.example.fitness.domain.usecase;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalDate;

/**
 * 获取今日步数并管理基准值的UseCase
 */
public class GetTodayStepsUseCase {
    
    private static final String PREFS_NAME = "step_prefs";
    private static final String KEY_LAST_DATE = "last_date";
    private static final String KEY_INITIAL_STEPS = "initial_steps";
    
    private final Context context;
    
    public GetTodayStepsUseCase(Context context) {
        this.context = context.getApplicationContext();
    }
    
    /**
     * 执行用例，检查并返回今日的基准步数状态
     * @return BaselineResult 包含基准值和是否为新的一天
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public BaselineResult execute() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String today = LocalDate.now().toString();
        String lastDate = prefs.getString(KEY_LAST_DATE, "");
        
        boolean isNewDay = !today.equals(lastDate);
        int baselineSteps = -1;
        
        if (isNewDay) {
            // 新的一天，清除旧的基准值
            prefs.edit()
                .putString(KEY_LAST_DATE, today)
                .remove(KEY_INITIAL_STEPS)
                .apply();
        } else {
            // 同一天，恢复基准值
            baselineSteps = prefs.getInt(KEY_INITIAL_STEPS, -1);
        }
        
        return new BaselineResult(baselineSteps, isNewDay, today);
    }
    
    /**
     * 保存基准步数
     */
    public void saveBaseline(int baselineSteps) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String today = LocalDate.now().toString();
        prefs.edit()
            .putInt(KEY_INITIAL_STEPS, baselineSteps)
            .putString(KEY_LAST_DATE, today)
            .apply();
    }
    
    /**
     * 基准值结果
     */
    public static class BaselineResult {
        public final int baselineSteps;
        public final boolean isNewDay;
        public final String today;
        
        public BaselineResult(int baselineSteps, boolean isNewDay, String today) {
            this.baselineSteps = baselineSteps;
            this.isNewDay = isNewDay;
            this.today = today;
        }
    }
}