package com.example.fitness.config;

/**
 * 应用配置常量类
 * 统一管理所有魔法数字和配置项，便于维护和调整
 */
public final class AppConfig {
    
    // ==================== 训练相关配置 ====================
    
    /** 默认每日步数目标 */
    public static final int DEFAULT_STEP_GOAL = 10000;
    
    /** 默认每分钟消耗卡路里（用于预设计划） */
    public static final float CALORIES_PER_MINUTE = 9.3f;
    
    /** 最大训练历史记录数量 */
    public static final int MAX_WORKOUT_HISTORY = 50;
    
    /** 默认训练组数 */
    public static final int DEFAULT_EXERCISE_SETS = 3;
    
    /** 默认每组次数 */
    public static final int DEFAULT_EXERCISE_REPS = 10;
    
    /** 默认单个运动时长（分钟） */
    public static final int DEFAULT_EXERCISE_DURATION = 5;
    
    /** 默认单个运动消耗卡路里 */
    public static final int DEFAULT_EXERCISE_CALORIES = 50;
    
    /** 连续达标目标天数 */
    public static final int STREAK_GOAL_DAYS = 21;
    
    /** 每日训练目标时长（分钟） */
    public static final int DAILY_WORKOUT_GOAL_MINUTES = 60;
    
    // ==================== UI动画配置 ====================
    
    /** 圆形进度条动画时长（毫秒） */
    public static final long PROGRESS_ANIMATION_DURATION = 1500;
    
    /** 图表数据动画时长（毫秒） */
    public static final long CHART_ANIMATION_DURATION = 1200;
    
    /** Activity切换动画时长（毫秒） */
    public static final int ACTIVITY_TRANSITION_DURATION = 300;
    
    // ==================== 身体指标配置 ====================
    
    /** 体重趋势显示天数 */
    public static final int WEIGHT_TREND_DAYS = 30;
    
    /** 身体指标历史显示天数 */
    public static final int BODY_METRIC_HISTORY_DAYS = 90;
    
    /** BMI计算默认身高（米）- 当用户未设置时使用 */
    public static final double DEFAULT_HEIGHT_FOR_BMI = 1.75;
    
    // ==================== 网络配置 ====================
    
    /** 图片加载超时时间（毫秒） */
    public static final int IMAGE_LOAD_TIMEOUT = 10000;
    
    /** 默认占位图资源ID（需要在使用时替换为实际资源） */
    public static final int DEFAULT_PLACEHOLDER_IMAGE = 0; // TODO: 设置为实际drawable资源
    
    // ==================== 数据库配置 ====================
    
    /** SharedPreferences名称 */
    public static final String PREFS_NAME = "fitness_plans";
    
    /** 数据库名称 */
    public static final String DATABASE_NAME = "fitness_local.db";
    
    /** 数据库版本 */
    public static final int DATABASE_VERSION = 2;
    
    // ==================== 私有构造函数，防止实例化 ====================
    
    private AppConfig() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
}
