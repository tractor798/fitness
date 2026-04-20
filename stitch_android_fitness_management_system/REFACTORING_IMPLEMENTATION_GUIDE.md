# Zen Kinetic 项目重构实施指南

**版本**: 1.0  
**日期**: 2026年4月21日  
**目标**: 指导逐步实施项目模块化重构

---

## 快速目录

1. [包结构迁移步骤](#包结构迁移步骤)
2. [代码示例](#代码示例)
3. [迁移检查清单](#迁移检查清单)
4. [常见陷阱](#常见陷阱)
5. [版本控制策略](#版本控制策略)

---

## 包结构迁移步骤

### Step 1: 创建新的包结构

```bash
# 在Android Studio中执行以下操作
com.example.fitness
├── core/
│   ├── base/
│   ├── utils/
│   ├── constants/
│   └── application/
├── models/
├── data/
│   ├── repositories/
│   ├── dao/
│   └── manager/
├── ui/
│   ├── home/
│   ├── plan/
│   ├── workout/
│   ├── stats/
│   └── profile/
└── viewmodels/ (可选)
```

**操作步骤**:
1. 在com.example.fitness上右键
2. New → Package
3. 输入包名（如 `core`）
4. 创建子包
5. 重复直到完成所有包结构

### Step 2: 迁移基础类

#### 2.1 保留BaseActivity

**位置**: `com.example.fitness.core.base.BaseActivity`

```java
// BaseActivity保留原有逻辑，不需要改动
// 只是改变包路径
package com.example.fitness.core.base;

import androidx.appcompat.app.AppCompatActivity;
// ... 其他import

public abstract class BaseActivity extends AppCompatActivity {
    // 保持原有实现
    protected void setupBottomNavigation(int selectedIndex) { ... }
    // ...
}
```

**操作**:
1. 将BaseActivity从根包移到 `core.base`
2. 更新其他所有Activity的import语句
3. 编译确保无错误

#### 2.2 创建新的工具类

**位置**: `com.example.fitness.core.utils`

```java
// DateUtils.java
package com.example.fitness.core.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    private static final SimpleDateFormat dateFormat = 
        new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    
    public static String formatDate(long timestamp) {
        return dateFormat.format(new Date(timestamp));
    }
    
    public static long parseDate(String dateStr) throws Exception {
        return dateFormat.parse(dateStr).getTime();
    }
    
    public static String getFormattedTime(long milliseconds) {
        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        
        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes % 60, seconds % 60);
        } else {
            return String.format("%d:%02d", minutes, seconds % 60);
        }
    }
}
```

```java
// Constants.java
package com.example.fitness.core.constants;

public class Constants {
    // SharedPreferences Keys
    public static final String PREFS_NAME = "fitness_plans";
    public static final String KEY_CUSTOM_PLANS = "custom_plans";
    public static final String KEY_WORKOUT_HISTORY = "workout_history";
    public static final String KEY_BODY_METRICS = "body_metrics";
    public static final String KEY_USER_HEIGHT = "user_height";
    
    // Limits
    public static final int MAX_PLAN_HISTORY = 365;
    public static final int MAX_METRIC_RECORDS = 365;
    
    // Request Codes
    public static final int REQUEST_EXERCISE_CONFIG = 100;
    public static final int REQUEST_CUSTOM_PLAN_EDIT = 101;
    
    // Result Codes
    public static final int RESULT_PLAN_SAVED = 200;
    public static final int RESULT_CANCELLED = 201;
}
```

### Step 3: 迁移模型类

**位置**: `com.example.fitness.models`

```bash
models/
├── BodyMetric.java
├── CustomPlan.java
├── Exercise.java
├── PlanItem.java
├── WorkoutSession.java
├── WorkoutStats.java
└── UserProfile.java (新增)
```

**操作步骤**:
1. 将所有Model类从根包移到 `models` 包
2. 更新Model类中的import语句
3. 确保Model类间无循环依赖
4. 编译检查无错误

### Step 4: 创建Repository层

**位置**: `com.example.fitness.data.repositories`

#### 4.1 创建Repository基类

```java
package com.example.fitness.data.repositories;

import android.content.SharedPreferences;
import com.google.gson.Gson;

public abstract class BaseRepository {
    protected SharedPreferences prefs;
    protected Gson gson;
    
    public BaseRepository(SharedPreferences prefs, Gson gson) {
        this.prefs = prefs;
        this.gson = gson;
    }
    
    protected String getValue(String key, String defaultValue) {
        return prefs.getString(key, defaultValue);
    }
    
    protected void setValue(String key, String value) {
        prefs.edit().putString(key, value).apply();
    }
    
    protected void removeValue(String key) {
        prefs.edit().remove(key).apply();
    }
}
```

#### 4.2 创建PlanRepository

```java
package com.example.fitness.data.repositories;

import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.example.fitness.core.constants.Constants;
import com.example.fitness.models.CustomPlan;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PlanRepository extends BaseRepository {
    
    public PlanRepository(SharedPreferences prefs, Gson gson) {
        super(prefs, gson);
    }
    
    // 保存自定义计划
    public void savePlan(CustomPlan plan) {
        List<CustomPlan> plans = getCustomPlans();
        
        // 检查是否存在，存在则更新
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
        
        savePlans(plans);
    }
    
    // 获取所有自定义计划
    public List<CustomPlan> getCustomPlans() {
        String json = getValue(Constants.KEY_CUSTOM_PLANS, "[]");
        Type type = new TypeToken<List<CustomPlan>>(){}.getType();
        List<CustomPlan> plans = gson.fromJson(json, type);
        return plans != null ? plans : new ArrayList<>();
    }
    
    // 获取预设计划
    public List<CustomPlan> getPresetPlans() {
        // TODO: 返回预设计划列表
        // 可以从资源或配置文件读取
        return new ArrayList<>();
    }
    
    // 删除计划
    public void deletePlan(String planName) {
        List<CustomPlan> plans = getCustomPlans();
        plans.removeIf(plan -> plan.getName().equals(planName));
        savePlans(plans);
    }
    
    // 获取指定计划
    public CustomPlan getPlan(String planName) {
        List<CustomPlan> plans = getCustomPlans();
        for (CustomPlan plan : plans) {
            if (plan.getName().equals(planName)) {
                return plan;
            }
        }
        return null;
    }
    
    private void savePlans(List<CustomPlan> plans) {
        String json = gson.toJson(plans);
        setValue(Constants.KEY_CUSTOM_PLANS, json);
    }
}
```

#### 4.3 创建WorkoutRepository

```java
package com.example.fitness.data.repositories;

import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.example.fitness.core.constants.Constants;
import com.example.fitness.models.WorkoutSession;
import com.example.fitness.models.WorkoutStats;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class WorkoutRepository extends BaseRepository {
    
    public WorkoutRepository(SharedPreferences prefs, Gson gson) {
        super(prefs, gson);
    }
    
    // 保存训练会话
    public void saveSession(WorkoutSession session) {
        List<WorkoutSession> history = getWorkoutHistory();
        history.add(0, session); // 添加到开头
        
        if (history.size() > Constants.MAX_PLAN_HISTORY) {
            history = new ArrayList<>(history.subList(0, Constants.MAX_PLAN_HISTORY));
        }
        
        saveHistory(history);
    }
    
    // 获取训练历史
    public List<WorkoutSession> getWorkoutHistory() {
        String json = getValue(Constants.KEY_WORKOUT_HISTORY, "[]");
        Type type = new TypeToken<List<WorkoutSession>>(){}.getType();
        List<WorkoutSession> history = gson.fromJson(json, type);
        return history != null ? history : new ArrayList<>();
    }
    
    // 获取今日统计
    public WorkoutStats getTodayStats() {
        List<WorkoutSession> history = getWorkoutHistory();
        WorkoutStats stats = new WorkoutStats();
        
        long todayStart = System.currentTimeMillis() - (24 * 60 * 60 * 1000);
        
        for (WorkoutSession session : history) {
            if (session.getEndTime() >= todayStart) {
                stats.addDuration(session.getDuration());
                stats.addCalories(session.getCalories());
                stats.incrementWorkouts();
            }
        }
        
        return stats;
    }
    
    // 获取本周统计
    public WorkoutStats getWeekStats() {
        List<WorkoutSession> history = getWorkoutHistory();
        WorkoutStats stats = new WorkoutStats();
        
        long weekStart = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000);
        
        for (WorkoutSession session : history) {
            if (session.getEndTime() >= weekStart) {
                stats.addDuration(session.getDuration());
                stats.addCalories(session.getCalories());
                stats.incrementWorkouts();
            }
        }
        
        return stats;
    }
    
    private void saveHistory(List<WorkoutSession> history) {
        String json = gson.toJson(history);
        setValue(Constants.KEY_WORKOUT_HISTORY, json);
    }
}
```

#### 4.4 创建BodyMetricRepository

```java
package com.example.fitness.data.repositories;

import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.example.fitness.core.constants.Constants;
import com.example.fitness.models.BodyMetric;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class BodyMetricRepository extends BaseRepository {
    
    public BodyMetricRepository(SharedPreferences prefs, Gson gson) {
        super(prefs, gson);
    }
    
    // 保存身体指标
    public void saveMetric(BodyMetric metric) {
        List<BodyMetric> metrics = getMetrics();
        
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
        
        if (metrics.size() > Constants.MAX_METRIC_RECORDS) {
            metrics = new ArrayList<>(metrics.subList(0, Constants.MAX_METRIC_RECORDS));
        }
        
        saveMetrics(metrics);
    }
    
    // 获取所有指标
    public List<BodyMetric> getMetrics() {
        String json = getValue(Constants.KEY_BODY_METRICS, "[]");
        Type type = new TypeToken<List<BodyMetric>>(){}.getType();
        List<BodyMetric> metrics = gson.fromJson(json, type);
        return metrics != null ? metrics : new ArrayList<>();
    }
    
    // 获取最新指标
    public BodyMetric getLatestMetric() {
        List<BodyMetric> metrics = getMetrics();
        if (!metrics.isEmpty()) {
            return metrics.get(0);
        }
        return null;
    }
    
    // 获取N天内的指标
    public List<BodyMetric> getMetricsForDays(int days) {
        List<BodyMetric> allMetrics = getMetrics();
        List<BodyMetric> result = new ArrayList<>();
        
        long startTime = System.currentTimeMillis() - (days * 24L * 60 * 60 * 1000);
        
        for (BodyMetric metric : allMetrics) {
            if (metric.getTimestamp() >= startTime) {
                result.add(metric);
            }
        }
        
        return result;
    }
    
    // 删除指标
    public void deleteMetric(String date) {
        List<BodyMetric> metrics = getMetrics();
        metrics.removeIf(metric -> metric.getDate().equals(date));
        saveMetrics(metrics);
    }
    
    private void saveMetrics(List<BodyMetric> metrics) {
        String json = gson.toJson(metrics);
        setValue(Constants.KEY_BODY_METRICS, json);
    }
}
```

### Step 5: 创建Repository工厂

```java
package com.example.fitness.data;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.example.fitness.core.constants.Constants;
import com.example.fitness.data.repositories.PlanRepository;
import com.example.fitness.data.repositories.WorkoutRepository;
import com.example.fitness.data.repositories.BodyMetricRepository;

public class RepositoryFactory {
    private static RepositoryFactory instance;
    private SharedPreferences prefs;
    private Gson gson;
    
    private PlanRepository planRepository;
    private WorkoutRepository workoutRepository;
    private BodyMetricRepository bodyMetricRepository;
    
    private RepositoryFactory(Context context) {
        this.prefs = context.getSharedPreferences(
            Constants.PREFS_NAME, Context.MODE_PRIVATE);
        this.gson = new Gson();
    }
    
    public static synchronized RepositoryFactory getInstance(Context context) {
        if (instance == null) {
            instance = new RepositoryFactory(context.getApplicationContext());
        }
        return instance;
    }
    
    public PlanRepository getPlanRepository() {
        if (planRepository == null) {
            planRepository = new PlanRepository(prefs, gson);
        }
        return planRepository;
    }
    
    public WorkoutRepository getWorkoutRepository() {
        if (workoutRepository == null) {
            workoutRepository = new WorkoutRepository(prefs, gson);
        }
        return workoutRepository;
    }
    
    public BodyMetricRepository getBodyMetricRepository() {
        if (bodyMetricRepository == null) {
            bodyMetricRepository = new BodyMetricRepository(prefs, gson);
        }
        return bodyMetricRepository;
    }
}
```

### Step 6: 迁移UI层

**位置**: `com.example.fitness.ui.{module}`

#### 6.1 创建BaseAdapter

```java
package com.example.fitness.core.base;

import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public abstract class BaseAdapter<T, VH extends RecyclerView.ViewHolder> 
    extends RecyclerView.Adapter<VH> {
    
    protected List<T> data;
    protected OnItemClickListener<T> itemClickListener;
    
    public interface OnItemClickListener<T> {
        void onItemClick(int position, T item);
    }
    
    public BaseAdapter(List<T> data) {
        this.data = data != null ? data : new ArrayList<>();
    }
    
    public void setOnItemClickListener(OnItemClickListener<T> listener) {
        this.itemClickListener = listener;
    }
    
    public void updateData(List<T> newData) {
        this.data = newData;
        notifyDataSetChanged();
    }
    
    @Override
    public int getItemCount() {
        return data.size();
    }
}
```

#### 6.2 迁移Activity - 示例（PlanActivity）

```java
// 迁移前
package com.example.fitness;

public class PlanActivity extends BaseActivity {
    private PlanDataManager dataManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);
        
        dataManager = PlanDataManager.getInstance(this);
        List<CustomPlan> plans = dataManager.getCustomPlans();
        // ...
    }
}

// 迁移后
package com.example.fitness.ui.plan;

import com.example.fitness.core.base.BaseActivity;
import com.example.fitness.data.RepositoryFactory;
import com.example.fitness.data.repositories.PlanRepository;
import com.example.fitness.models.CustomPlan;

public class PlanActivity extends BaseActivity {
    private PlanRepository planRepository;
    private PlanAdapter adapter;
    private RecyclerView recyclerView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);
        setupBottomNavigation(1);
        
        // 获取Repository
        RepositoryFactory factory = RepositoryFactory.getInstance(this);
        planRepository = factory.getPlanRepository();
        
        initializeViews();
        loadPlans();
    }
    
    private void initializeViews() {
        recyclerView = findViewById(R.id.plansRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        adapter = new PlanAdapter(new ArrayList<>());
        adapter.setOnItemClickListener((position, plan) -> {
            Intent intent = new Intent(this, WorkoutActivity.class);
            intent.putExtra("plan_name", plan.getName());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        loadPlans();
    }
    
    private void loadPlans() {
        // 自定义计划
        List<CustomPlan> customPlans = planRepository.getCustomPlans();
        
        // 预设计划
        List<CustomPlan> presetPlans = planRepository.getPresetPlans();
        
        // 合并
        List<CustomPlan> allPlans = new ArrayList<>(presetPlans);
        allPlans.addAll(customPlans);
        
        adapter.updateData(allPlans);
    }
}
```

#### 6.3 迁移Adapter - 示例（PlanAdapter）

```java
// 迁移前
package com.example.fitness;

public class PlanAdapter extends RecyclerView.Adapter<PlanAdapter.ViewHolder> {
    private List<PlanItem> plans;
    private OnPlanClickListener listener;
    
    // ...
}

// 迁移后
package com.example.fitness.ui.plan.adapters;

import com.example.fitness.core.base.BaseAdapter;
import com.example.fitness.models.CustomPlan;

public class PlanAdapter extends BaseAdapter<CustomPlan, PlanAdapter.ViewHolder> {
    
    public PlanAdapter(List<CustomPlan> plans) {
        super(plans);
    }
    
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_plan, parent, false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CustomPlan plan = data.get(position);
        holder.bind(plan);
        
        holder.itemView.setOnClickListener(v -> {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(position, plan);
            }
        });
    }
    
    class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, descriptionText, durationText;
        
        ViewHolder(View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.planNameText);
            descriptionText = itemView.findViewById(R.id.planDescText);
            durationText = itemView.findViewById(R.id.planDurationText);
        }
        
        void bind(CustomPlan plan) {
            nameText.setText(plan.getName());
            descriptionText.setText(plan.getDescription());
            durationText.setText(plan.getTotalDuration() + " 分钟");
        }
    }
}
```

---

## 迁移检查清单

### Phase 1: 包结构
- [ ] 创建了所有新包
- [ ] BaseActivity移到core.base
- [ ] 所有Model类移到models
- [ ] 编译通过无错误

### Phase 2: 工具类
- [ ] Constants.java创建完成
- [ ] DateUtils.java创建完成
- [ ] 其他工具类迁移完成
- [ ] 导入语句都已更新

### Phase 3: Repository层
- [ ] BaseRepository创建
- [ ] PlanRepository创建并测试
- [ ] WorkoutRepository创建并测试
- [ ] BodyMetricRepository创建并测试
- [ ] RepositoryFactory创建
- [ ] 所有Repository的CRUD操作可用

### Phase 4: UI层迁移
- [ ] MainActivity迁移到ui.home
- [ ] PlanActivity迁移到ui.plan
- [ ] CustomPlanActivity迁移到ui.plan
- [ ] 其他Activity迁移完成
- [ ] 所有Adapter迁移完成
- [ ] 所有Activity都使用Repository而不是PlanDataManager

### Phase 5: 测试
- [ ] 单元测试编写完成
- [ ] 集成测试通过
- [ ] 功能测试通过
- [ ] 性能测试通过

### Phase 6: 清理
- [ ] 旧的PlanDataManager备份
- [ ] 删除重复代码
- [ ] 移除废弃的导入
- [ ] 代码审查完成

---

## 常见陷阱

### 1. 循环依赖
**问题**: Model类之间相互引用导致编译错误
**解决**: 
- 使用接口而不是具体类
- 或使用后期初始化（Lazy Initialization）

### 2. 单例问题
**问题**: PlanDataManager和RepositoryFactory都是单例，冲突
**解决**:
- 逐步迁移，保留PlanDataManager用于旧Activity
- 新Activity只使用RepositoryFactory
- 最后统一删除PlanDataManager

### 3. SharedPreferences并发
**问题**: 多个线程访问SharedPreferences导致数据混乱
**解决**:
- 确保所有写操作使用`.apply()`而非`.commit()`
- Repository类添加synchronized方法
- 或使用Room数据库（更安全）

### 4. 内存泄漏
**问题**: Activity持有Repository导致内存泄漏
**解决**:
```java
// 错误做法
public class MyActivity extends BaseActivity {
    private Repository repo; // 长期持有
}

// 正确做法
public class MyActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Repository repo = RepositoryFactory.getInstance(this)
            .getPlanRepository();
        // 方法执行完后自动释放
    }
}
```

### 5. 测试困难
**问题**: 无法在单元测试中Mock Repository
**解决**:
```java
// 提供接口而不是具体类
public interface IPlanRepository {
    List<Plan> getPlans();
}

// Activity使用接口
public class PlanActivity {
    private IPlanRepository repository;
    
    public PlanActivity(IPlanRepository repo) {
        this.repository = repo; // 便于注入
    }
}
```

---

## 版本控制策略

### Git分支策略

```
main (生产分支)
  └── develop (开发分支)
       ├── feature/refactor-structure (重构特性分支)
       ├── feature/repository-layer (Repository层)
       ├── feature/ui-migration (UI迁移)
       └── bugfix/... (修复分支)
```

### 提交消息规范

```
[模块] 类型: 描述

示例:
[core] feat: 创建BaseRepository基类
[data] refactor: 迁移PlanDataManager到Repository模式
[ui.plan] refactor: PlanActivity使用PlanRepository
[test] test: 添加PlanRepository单元测试
```

### Milestone规划

```
Milestone 1: 基础架构 (Week 1-2)
  - 包结构创建
  - 工具类迁移
  - BaseActivity保留

Milestone 2: 数据层 (Week 3-4)
  - Repository创建
  - DAO层设计
  - 单元测试

Milestone 3: UI迁移 (Week 5-6)
  - Activity逐步迁移
  - Adapter统一化
  - 集成测试

Milestone 4: 优化完善 (Week 7-8)
  - 性能优化
  - 文档完善
  - Release准备
```

---

## 快速参考

### 常用命令

```bash
# 创建新包
File → New → Package → 输入包名

# 移动类
选择类 → Refactor → Move → 选择目标包

# 更新import
Ctrl+Shift+O (Windows/Linux) 或 Cmd+Shift+O (Mac)

# 查找使用
Ctrl+F7 (Windows/Linux) 或 Cmd+F7 (Mac)

# 重命名
Shift+F6
```

### 代码模板

**Repository Template**
```java
public class XxxRepository extends BaseRepository {
    public XxxRepository(SharedPreferences prefs, Gson gson) {
        super(prefs, gson);
    }
    
    public void save(Xxx item) { }
    public Xxx get(String key) { }
    public List<Xxx> getAll() { }
    public void delete(String key) { }
}
```

**Activity Template**
```java
public class XxxActivity extends BaseActivity {
    private XxxRepository repository;
    private XxxAdapter adapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xxx);
        setupBottomNavigation(index);
        
        RepositoryFactory factory = RepositoryFactory.getInstance(this);
        repository = factory.getXxxRepository();
        
        initializeViews();
        loadData();
    }
    
    private void initializeViews() { }
    
    private void loadData() { }
}
```

---

**最后更新**: 2026年4月21日  
**维护者**: 项目团队
