package com.example.fitness.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.fitness.CustomPlan;
import com.example.fitness.Exercise;
import com.example.fitness.data.repository.PlanRepository;

import java.util.ArrayList;
import java.util.List;

public class SQLitePlanRepository implements PlanRepository {

    private final DatabaseHelper helper;

    public SQLitePlanRepository(Context context) {
        helper = new DatabaseHelper(context.getApplicationContext());
    }

    @Override
    public synchronized void savePlan(CustomPlan plan) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransaction();
        try {
            // 检查是否已存在
            Cursor cursor = db.query("plans", new String[]{"id"}, "name = ?", new String[]{plan.getName()}, null, null, null);
            boolean exists = cursor.moveToFirst();
            cursor.close();

            ContentValues values = new ContentValues();
            values.put("name", plan.getName());
            values.put("description", plan.getDescription());
            values.put("category", plan.getCategory());
            values.put("totalDuration", plan.getTotalDuration());
            values.put("totalCalories", plan.getTotalCalories());

            if (!exists) {
                db.insert("plans", null, values);
            } else {
                db.update("plans", values, "name = ?", new String[]{plan.getName()});
            }

            db.delete("exercises", "planName = ?", new String[]{plan.getName()});
            List<Exercise> exercises = plan.getExercises();
            if (exercises != null) {
                for (Exercise ex : exercises) {
                    ContentValues ev = new ContentValues();
                    ev.put("planName", plan.getName());
                    ev.put("name", ex.getName());
                    ev.put("description", ex.getDescription());
                    ev.put("sets", ex.getSets());
                    ev.put("reps", ex.getReps());
                    ev.put("duration", ex.getDuration());
                    ev.put("calories", ex.getCalories());
                    ev.put("selected", ex.isSelected() ? 1 : 0);
                    ev.put("imageUrl", ex.getImageUrl());
                    ev.put("imageResId", ex.getImageResId());
                    db.insert("exercises", null, ev);
                }
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public synchronized List<CustomPlan> getAllPlans() {
        SQLiteDatabase db = helper.getReadableDatabase();
        List<CustomPlan> result = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = db.query("plans", null, null, null, null, null, "name ASC");
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                String category = cursor.getString(cursor.getColumnIndexOrThrow("category"));
                CustomPlan plan = new CustomPlan(name, description);
                plan.setCategory(category == null ? "" : category);
                plan.setTotalDuration(cursor.getInt(cursor.getColumnIndexOrThrow("totalDuration")));
                plan.setTotalCalories(cursor.getInt(cursor.getColumnIndexOrThrow("totalCalories")));
                plan.setExercises(getExercisesForPlan(name));
                result.add(plan);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;
    }

    @Override
    public synchronized CustomPlan getPlanByName(String planName) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query("plans", null, "name = ?", new String[]{planName}, null, null, null);
            if (!cursor.moveToFirst()) {
                return null;
            }
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
            CustomPlan plan = new CustomPlan(name, description);
            plan.setTotalDuration(cursor.getInt(cursor.getColumnIndexOrThrow("totalDuration")));
            plan.setTotalCalories(cursor.getInt(cursor.getColumnIndexOrThrow("totalCalories")));
            plan.setExercises(getExercisesForPlan(name));
            return plan;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Override
    public synchronized void deletePlan(String planName) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete("exercises", "planName = ?", new String[]{planName});
            db.delete("plans", "name = ?", new String[]{planName});
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    private List<Exercise> getExercisesForPlan(String planName) {
        SQLiteDatabase db = helper.getReadableDatabase();
        List<Exercise> exercises = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = db.query("exercises", null, "planName = ?", new String[]{planName}, null, null, null);
            while (cursor.moveToNext()) {
                Exercise ex = new Exercise(
                        cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        cursor.getString(cursor.getColumnIndexOrThrow("description"))
                );
                ex.setSets(cursor.getInt(cursor.getColumnIndexOrThrow("sets")));
                ex.setReps(cursor.getInt(cursor.getColumnIndexOrThrow("reps")));
                ex.setDuration(cursor.getInt(cursor.getColumnIndexOrThrow("duration")));
                ex.setCalories(cursor.getInt(cursor.getColumnIndexOrThrow("calories")));
                ex.setSelected(cursor.getInt(cursor.getColumnIndexOrThrow("selected")) == 1);
                String imageUrl = cursor.getString(cursor.getColumnIndexOrThrow("imageUrl"));
                ex.setImageUrl(imageUrl == null ? "" : imageUrl);
                int imageResId = cursor.getInt(cursor.getColumnIndexOrThrow("imageResId"));
                ex.setImageResId(imageResId);
                exercises.add(ex);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return exercises;
    }
}
