package com.example.fitness;

import android.content.Context;
import com.example.fitness.data.local.LocalBodyMetricRepository;
import com.example.fitness.data.local.LocalPlanRepository;
import com.example.fitness.data.local.LocalWorkoutRepository;
import com.example.fitness.data.local.UserPreferences;
import com.example.fitness.domain.model.CustomPlan;
import com.example.fitness.domain.repository.BodyMetricRepository;
import com.example.fitness.domain.repository.PlanRepository;
import com.example.fitness.domain.repository.WorkoutRepository;
import com.example.fitness.domain.service.BodyMetricAnalysisService;
import com.example.fitness.domain.service.WorkoutStatsService;
import com.example.fitness.domain.usecase.CalculateStreakDaysUseCase;
import com.example.fitness.domain.usecase.GetWeeklyWorkoutDaysUseCase;
import com.example.fitness.domain.usecase.LoadPersonalRecordsUseCase;
import com.example.fitness.domain.usecase.GetTodayStepsUseCase;
import com.example.fitness.domain.usecase.LoadBodyMetricOverviewUseCase;
import com.example.fitness.domain.usecase.LoadFilteredPlansUseCase;
import com.example.fitness.domain.usecase.CreatePresetExercisesUseCase;
import com.example.fitness.domain.usecase.ClearAllDataUseCase;
import com.example.fitness.domain.usecase.SaveCustomPlanUseCase;
import com.example.fitness.domain.usecase.LoadWorkoutHistoryUseCase;
import java.util.List;

/**
 * 依赖注入容器
 * 统一管理应用中的所有依赖关系
 * 替代原来的PlanDataManager上帝类
 */
public class ServiceLocator {
    
    private static ServiceLocator instance;
    
    private final Context context;
    
    // Repositories
    private PlanRepository planRepository;
    private WorkoutRepository workoutRepository;
    private BodyMetricRepository bodyMetricRepository;
    
    // User Preferences
    private UserPreferences userPreferences;
    
    // Services
    private WorkoutStatsService workoutStatsService;
    private BodyMetricAnalysisService bodyMetricAnalysisService;
    
    // UseCases
    private GetWeeklyWorkoutDaysUseCase getWeeklyWorkoutDaysUseCase;
    private CalculateStreakDaysUseCase calculateStreakDaysUseCase;
    private LoadPersonalRecordsUseCase loadPersonalRecordsUseCase;
    private GetTodayStepsUseCase getTodayStepsUseCase;
    private LoadBodyMetricOverviewUseCase loadBodyMetricOverviewUseCase;
    private LoadFilteredPlansUseCase loadFilteredPlansUseCase;
    private CreatePresetExercisesUseCase createPresetExercisesUseCase;
    private ClearAllDataUseCase clearAllDataUseCase;
    private SaveCustomPlanUseCase saveCustomPlanUseCase;
    private LoadWorkoutHistoryUseCase loadWorkoutHistoryUseCase;
    
    private ServiceLocator(Context context) {
        this.context = context.getApplicationContext();
        initializeRepositories();
        initializeUserPreferences();
        initializeServices();
        initializeUseCases();
    }
    
    public static synchronized ServiceLocator getInstance(Context context) {
        if (instance == null) {
            instance = new ServiceLocator(context);
        }
        return instance;
    }
    
    /**
     * 初始化所有Repository
     */
    private void initializeRepositories() {
        this.planRepository = new LocalPlanRepository(context);
        this.workoutRepository = new LocalWorkoutRepository(context);
        this.bodyMetricRepository = new LocalBodyMetricRepository(context);
    }
    
    /**
     * 初始化用户偏好设置
     */
    private void initializeUserPreferences() {
        this.userPreferences = new UserPreferences(context);
    }
    
    /**
     * 初始化所有Service
     */
    private void initializeServices() {
        this.workoutStatsService = new WorkoutStatsService(workoutRepository);
        this.bodyMetricAnalysisService = new BodyMetricAnalysisService(bodyMetricRepository);
    }
    
    /**
     * 初始化所有UseCase
     */
    private void initializeUseCases() {
        this.getWeeklyWorkoutDaysUseCase = new GetWeeklyWorkoutDaysUseCase(workoutRepository);
        this.calculateStreakDaysUseCase = new CalculateStreakDaysUseCase();
        this.loadPersonalRecordsUseCase = new LoadPersonalRecordsUseCase(workoutRepository);
        this.getTodayStepsUseCase = new GetTodayStepsUseCase(context);
        this.loadBodyMetricOverviewUseCase = new LoadBodyMetricOverviewUseCase(bodyMetricRepository, bodyMetricAnalysisService);
        this.loadFilteredPlansUseCase = new LoadFilteredPlansUseCase(planRepository);
        this.createPresetExercisesUseCase = new CreatePresetExercisesUseCase();
        this.clearAllDataUseCase = new ClearAllDataUseCase(this);
        this.saveCustomPlanUseCase = new SaveCustomPlanUseCase(planRepository);
        this.loadWorkoutHistoryUseCase = new LoadWorkoutHistoryUseCase(workoutRepository);
    }
    
    // ==================== Repository访问方法 ====================
    
    public PlanRepository getPlanRepository() {
        return planRepository;
    }
    
    public WorkoutRepository getWorkoutRepository() {
        return workoutRepository;
    }
    
    public BodyMetricRepository getBodyMetricRepository() {
        return bodyMetricRepository;
    }
    
    // ==================== UserPreferences访问方法 ====================
    
    public UserPreferences getUserPreferences() {
        return userPreferences;
    }
    
    // ==================== Service访问方法 ====================
    
    public WorkoutStatsService getWorkoutStatsService() {
        return workoutStatsService;
    }
    
    public BodyMetricAnalysisService getBodyMetricAnalysisService() {
        return bodyMetricAnalysisService;
    }
    
    // ==================== UseCase访问方法 ====================
    
    public GetWeeklyWorkoutDaysUseCase getGetWeeklyWorkoutDaysUseCase() {
        return getWeeklyWorkoutDaysUseCase;
    }
    
    public CalculateStreakDaysUseCase getCalculateStreakDaysUseCase() {
        return calculateStreakDaysUseCase;
    }
    
    public LoadPersonalRecordsUseCase getLoadPersonalRecordsUseCase() {
        return loadPersonalRecordsUseCase;
    }
    
    public GetTodayStepsUseCase getGetTodayStepsUseCase() {
        return getTodayStepsUseCase;
    }
    
    public LoadBodyMetricOverviewUseCase getLoadBodyMetricOverviewUseCase() {
        return loadBodyMetricOverviewUseCase;
    }
    
    public LoadFilteredPlansUseCase getLoadFilteredPlansUseCase() {
        return loadFilteredPlansUseCase;
    }
    
    public CreatePresetExercisesUseCase getCreatePresetExercisesUseCase() {
        return createPresetExercisesUseCase;
    }
    
    public ClearAllDataUseCase getClearAllDataUseCase() {
        return clearAllDataUseCase;
    }
    
    public SaveCustomPlanUseCase getSaveCustomPlanUseCase() {
        return saveCustomPlanUseCase;
    }
    
    public LoadWorkoutHistoryUseCase getLoadWorkoutHistoryUseCase() {
        return loadWorkoutHistoryUseCase;
    }
    
    /**
     * 清空所有数据
     */
    public void clearAllData() {
        // 清空所有自定义计划
        List<CustomPlan> allPlans = planRepository.getAllPlans();
        for (CustomPlan plan : allPlans) {
            planRepository.deletePlan(plan.getName());
        }
        
        // 清空训练记录
        workoutRepository.clearAll();
        
        // 清空身体指标
        bodyMetricRepository.clearAll();
        
        // 清空用户偏好
        userPreferences.clearAll();
    }
    
    /**
     * 重置单例（用于测试）
     */
    public static void resetForTesting() {
        instance = null;
    }
}
