package com.example.fitness.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    private static final String DB_NAME = "fitness_local.db";
    private static final int DB_VERSION = 2;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createAllTables(db);
    }

    /**
     * 创建所有表（首次安装时调用）
     */
    private void createAllTables(SQLiteDatabase db) {
        // plans table
        db.execSQL("CREATE TABLE IF NOT EXISTS plans ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "name TEXT UNIQUE NOT NULL,"
                + "description TEXT,"
                + "category TEXT,"
                + "totalDuration INTEGER,"
                + "totalCalories INTEGER"
                + ")");

        // exercises table
        db.execSQL("CREATE TABLE IF NOT EXISTS exercises ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "planName TEXT NOT NULL,"
                + "name TEXT NOT NULL,"
                + "description TEXT,"
                + "sets INTEGER,"
                + "reps INTEGER,"
                + "duration INTEGER,"
                + "calories INTEGER,"
                + "selected INTEGER,"
                + "imageUrl TEXT,"
                + "imageResId INTEGER DEFAULT 0"
                + ")");

        // workout_sessions table
        db.execSQL("CREATE TABLE IF NOT EXISTS workout_sessions ("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "planName TEXT,"
            + "startTime INTEGER,"
            + "endTime INTEGER,"
            + "duration INTEGER,"
            + "calories INTEGER,"
            + "completed INTEGER"
            + ")");

        // body_metrics table
        db.execSQL("CREATE TABLE IF NOT EXISTS body_metrics ("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "date TEXT,"
            + "timestamp INTEGER,"
            + "weight REAL,"
            + "bodyFat REAL,"
            + "muscleMass REAL,"
            + "boneMass REAL,"
            + "water REAL,"
            + "visceralFat REAL,"
            + "bmi REAL,"
            + "notes TEXT"
            + ")");
        
        Log.d(TAG, "All tables created successfully");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);
        
        // 增量升级策略：逐步执行每个版本的迁移
        for (int version = oldVersion; version < newVersion; version++) {
            switch (version) {
                case 1:
                    upgradeFromV1ToV2(db);
                    break;
                // 未来版本在此添加case
                // case 2: upgradeFromV2ToV3(db); break;
                default:
                    Log.w(TAG, "Unknown version upgrade path: " + version);
                    break;
            }
        }
    }
    
    /**
     * 从版本1升级到版本2
     * 添加workout_sessions和body_metrics表
     */
    private void upgradeFromV1ToV2(SQLiteDatabase db) {
        Log.d(TAG, "Executing upgrade from V1 to V2");
        
        // 添加workout_sessions表（如果不存在）
        db.execSQL("CREATE TABLE IF NOT EXISTS workout_sessions ("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "planName TEXT,"
            + "startTime INTEGER,"
            + "endTime INTEGER,"
            + "duration INTEGER,"
            + "calories INTEGER,"
            + "completed INTEGER"
            + ")");
        
        // 添加body_metrics表（如果不存在）
        db.execSQL("CREATE TABLE IF NOT EXISTS body_metrics ("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "date TEXT,"
            + "timestamp INTEGER,"
            + "weight REAL,"
            + "bodyFat REAL,"
            + "muscleMass REAL,"
            + "boneMass REAL,"
            + "water REAL,"
            + "visceralFat REAL,"
            + "bmi REAL,"
            + "notes TEXT"
            + ")");
        
        Log.d(TAG, "Upgrade from V1 to V2 completed");
    }
    
    /**
     * 未来版本升级方法示例
     */
    /*
    private void upgradeFromV2ToV3(SQLiteDatabase db) {
        Log.d(TAG, "Executing upgrade from V2 to V3");
        // 添加新字段或新表，不要DROP现有表
        // db.execSQL("ALTER TABLE plans ADD COLUMN category TEXT");
        Log.d(TAG, "Upgrade from V2 to V3 completed");
    }
    */
}
