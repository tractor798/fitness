package com.example.fitness.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.fitness.domain.model.WorkoutSession;
import com.example.fitness.domain.repository.WorkoutRepository;

import java.util.ArrayList;
import java.util.List;

public class SQLiteWorkoutRepository implements WorkoutRepository {

    private final DatabaseHelper helper;

    public SQLiteWorkoutRepository(Context context) {
        helper = new DatabaseHelper(context.getApplicationContext());
    }

    @Override
    public synchronized void saveSession(WorkoutSession session) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", session.getId());
        values.put("planName", session.getPlanName());
        values.put("startTime", session.getStartTime());
        values.put("endTime", session.getEndTime());
        values.put("duration", session.getDuration());
        values.put("calories", session.getCalories());
        values.put("completed", session.isCompleted() ? 1 : 0);

        db.beginTransaction();
        try {
            db.insert("workout_sessions", null, values);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public synchronized List<WorkoutSession> getAllSessions() {
        SQLiteDatabase db = helper.getReadableDatabase();
        List<WorkoutSession> result = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = db.query("workout_sessions", null, null, null, null, null, "startTime DESC");
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndexOrThrow("id"));
                WorkoutSession session = new WorkoutSession(cursor.getString(cursor.getColumnIndexOrThrow("planName")));
                session.setId(id);
                session.setStartTime(cursor.getLong(cursor.getColumnIndexOrThrow("startTime")));
                session.setEndTime(cursor.getLong(cursor.getColumnIndexOrThrow("endTime")));
                session.setDuration(cursor.getLong(cursor.getColumnIndexOrThrow("duration")));
                session.setCalories(cursor.getInt(cursor.getColumnIndexOrThrow("calories")));
                session.setCompleted(cursor.getInt(cursor.getColumnIndexOrThrow("completed")) == 1);
                result.add(session);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;
    }

    @Override
    public synchronized List<WorkoutSession> getRecentSessions(int limit) {
        List<WorkoutSession> all = getAllSessions();
        if (all.size() <= limit) {
            return all;
        }
        return new ArrayList<>(all.subList(0, limit));
    }

    @Override
    public synchronized void clearAll() {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete("workout_sessions", null, null);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }
}
