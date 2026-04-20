package com.example.fitness.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.fitness.BodyMetric;
import com.example.fitness.data.repository.BodyMetricRepository;

import java.util.ArrayList;
import java.util.List;

public class SQLiteBodyMetricRepository implements BodyMetricRepository {

    private final DatabaseHelper helper;

    public SQLiteBodyMetricRepository(Context context) {
        helper = new DatabaseHelper(context.getApplicationContext());
    }

    @Override
    public synchronized void saveMetric(BodyMetric metric) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("date", metric.getDate());
        values.put("timestamp", metric.getTimestamp());
        values.put("weight", metric.getWeight());
        values.put("bodyFat", metric.getBodyFat());
        values.put("muscleMass", metric.getMuscleMass());
        values.put("boneMass", metric.getBoneMass());
        values.put("water", metric.getWater());
        values.put("visceralFat", metric.getVisceralFat());
        values.put("bmi", metric.getBmi());
        values.put("notes", metric.getNotes());

        db.beginTransaction();
        try {
            // 使用 timestamp 作为唯一标识进行更新或插入，避免同一天多条记录被覆盖
            int updated = db.update("body_metrics", values, "timestamp = ?", new String[]{String.valueOf(metric.getTimestamp())});
            if (updated == 0) {
                db.insert("body_metrics", null, values);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public synchronized List<BodyMetric> getAllMetrics() {
        SQLiteDatabase db = helper.getReadableDatabase();
        List<BodyMetric> result = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = db.query("body_metrics", null, null, null, null, null, "timestamp DESC");
            while (cursor.moveToNext()) {
                result.add(buildMetric(cursor));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;
    }

    @Override
    public synchronized BodyMetric getLatestMetric() {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query("body_metrics", null, null, null, null, null, "timestamp DESC", "1");
            if (cursor.moveToFirst()) {
                return buildMetric(cursor);
            }
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Override
    public synchronized BodyMetric getMetricByDate(String date) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query("body_metrics", null, "date = ?", new String[]{date}, null, null, null, "1");
            if (cursor.moveToFirst()) {
                return buildMetric(cursor);
            }
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Override
    public synchronized void deleteMetric(String date) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransaction();
        try {
            // 根据日期前缀删除，支持 "yyyy-MM-dd" 格式的查询
            db.delete("body_metrics", "date LIKE ?", new String[]{date + "%"});
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public synchronized List<BodyMetric> getMetricsForDays(int days) {
        long startTime = System.currentTimeMillis() - (days * 24L * 60L * 60L * 1000L);
        SQLiteDatabase db = helper.getReadableDatabase();
        List<BodyMetric> result = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = db.query(
                    "body_metrics",
                    null,
                    "timestamp >= ?",
                    new String[]{String.valueOf(startTime)},
                    null,
                    null,
                    "timestamp DESC"
            );
            while (cursor.moveToNext()) {
                result.add(buildMetric(cursor));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;
    }

    @Override
    public synchronized void clearAll() {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete("body_metrics", null, null);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    private BodyMetric buildMetric(Cursor cursor) {
        BodyMetric metric = new BodyMetric();
        metric.setDate(cursor.getString(cursor.getColumnIndexOrThrow("date")));
        metric.setTimestamp(cursor.getLong(cursor.getColumnIndexOrThrow("timestamp")));
        metric.setWeight(cursor.getDouble(cursor.getColumnIndexOrThrow("weight")));
        metric.setBodyFat(cursor.getDouble(cursor.getColumnIndexOrThrow("bodyFat")));
        metric.setMuscleMass(cursor.getDouble(cursor.getColumnIndexOrThrow("muscleMass")));
        metric.setBoneMass(cursor.getDouble(cursor.getColumnIndexOrThrow("boneMass")));
        metric.setWater(cursor.getDouble(cursor.getColumnIndexOrThrow("water")));
        metric.setVisceralFat(cursor.getDouble(cursor.getColumnIndexOrThrow("visceralFat")));
        metric.setBmi(cursor.getDouble(cursor.getColumnIndexOrThrow("bmi")));
        metric.setNotes(cursor.getString(cursor.getColumnIndexOrThrow("notes")));
        return metric;
    }
}
