package com.example.fitness.data.local;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.fitness.domain.model.WorkoutSession;
import com.example.fitness.config.AppConfig;
import com.example.fitness.domain.repository.WorkoutRepository;
import com.example.fitness.db.SQLiteWorkoutRepository;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 本地训练记录仓库实现。优先使用 SQLite，失败时降级到 SharedPreferences。
 */
public class LocalWorkoutRepository implements WorkoutRepository {

    private static final String TAG = "LocalWorkoutRepo";
    private static final String WORKOUT_HISTORY_KEY = "workout_history";

    private final SQLiteWorkoutRepository sqliteRepository;
    private final SharedPreferences prefs;
    private final Gson gson;

    public LocalWorkoutRepository(Context context) {
        this.gson = new Gson();
        this.prefs = context.getSharedPreferences(AppConfig.PREFS_NAME, Context.MODE_PRIVATE);

        SQLiteWorkoutRepository repo = null;
        try {
            repo = new SQLiteWorkoutRepository(context);
        } catch (Exception e) {
            Log.e(TAG, "SQLite workout repository init failed", e);
        }
        this.sqliteRepository = repo;
    }

    @Override
    public synchronized void saveSession(WorkoutSession session) {
        if (sqliteRepository != null) {
            try {
                sqliteRepository.saveSession(session);
                return;
            } catch (Exception e) {
                Log.e(TAG, "SQLite save failed for session: " + session.getId() + ", plan: " + session.getPlanName(), e);
                Log.d(TAG, "Falling back to SharedPreferences");
            }
        }

        try {
            List<WorkoutSession> history = getAllSessions();
            history.add(0, session);
            if (history.size() > AppConfig.MAX_WORKOUT_HISTORY) {
                history = new ArrayList<>(history.subList(0, AppConfig.MAX_WORKOUT_HISTORY));
            }
            saveToPrefs(history);
        } catch (Exception e) {
            Log.e(TAG, "Failed to save session to SharedPreferences", e);
        }
    }

    @Override
    public synchronized List<WorkoutSession> getAllSessions() {
        if (sqliteRepository != null) {
            try {
                return sqliteRepository.getAllSessions();
            } catch (Exception e) {
                Log.e(TAG, "SQLite read failed, fallback to SharedPreferences", e);
            }
        }

        String json = prefs.getString(WORKOUT_HISTORY_KEY, "[]");
        Type type = new TypeToken<List<WorkoutSession>>() {}.getType();
        List<WorkoutSession> history = gson.fromJson(json, type);
        return history != null ? history : new ArrayList<>();
    }

    @Override
    public List<WorkoutSession> getRecentSessions(int limit) {
        List<WorkoutSession> all = getAllSessions();
        if (all.size() <= limit) {
            return all;
        }
        return new ArrayList<>(all.subList(0, limit));
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
        prefs.edit().remove(WORKOUT_HISTORY_KEY).apply();
    }

    private synchronized void saveToPrefs(List<WorkoutSession> history) {
        String json = gson.toJson(history);
        prefs.edit().putString(WORKOUT_HISTORY_KEY, json).apply();
    }
}
