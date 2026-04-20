# Zen Kinetic 健身管理系统 - 需求设计文档 v2.0

**项目名称**: Zen Kinetic (健身管理系统)  
**版本**: 2.0 (重构版)  
**更新日期**: 2026年4月21日  
**状态**: 设计阶段

---

## 目录

1. [项目概述](#项目概述)
2. [功能模块设计](#功能模块设计)
3. [系统架构](#系统架构)
4. [数据流设计](#数据流设计)
5. [模块间通信](#模块间通信)
6. [非功能需求](#非功能需求)
7. [重构路线图](#重构路线图)

---

## 项目概述

### 背景
Zen Kinetic是一款原生Android健身管理应用，采用纯本地存储方案，帮助用户管理训练计划、记录运动数据和追踪身体指标。

### 目标用户
- 健身爱好者
- 健身初学者
- 专业运动员

### 核心价值主张
- 📋 **灵活的计划管理** - 预设计划 + 自定义计划
- 🏋️ **详细的训练记录** - 实时计时、数据统计
- 📊 **身体指标追踪** - 体重、体脂率、BMI等多维度
- 🎯 **目标管理** - 设置和追踪健身目标

---

## 功能模块设计

### 1️⃣ 首页模块 (Home Module)

#### 1.1 功能需求
| 功能 | 描述 | 优先级 |
|------|------|--------|
| 今日统计展示 | 显示今日步数、活跃热量、训练时长 | P0 |
| 快速行动 | 快速启动训练、查看计划 | P0 |
| 当前进度 | 展示周目标完成进度 | P1 |
| 激励信息 | 显示励志文案 | P2 |

#### 1.2 数据需求
```
Input: 无
Output: 
  - today_workouts: int (今日训练次数)
  - today_duration: long (今日训练总时长)
  - today_calories: int (今日消耗卡路里)
  - weekly_progress: float (本周进度%)
```

#### 1.3 交互流程
```
首页加载
  ├→ 从WorkoutRepository查询今日统计
  ├→ 计算当前进度
  ├→ 显示卡片信息
  └→ onResume时刷新数据
```

---

### 2️⃣ 计划模块 (Plan Module)

#### 2.1 功能需求

**2.1.1 计划浏览**
| 功能 | 描述 | 优先级 |
|------|------|--------|
| 显示预设计划 | 内置训练计划列表 | P0 |
| 显示自定义计划 | 用户创建的计划列表 | P0 |
| 计划搜索 | 按名称搜索计划 | P1 |
| 计划收藏 | 收藏/取消收藏计划 | P2 |
| 删除计划 | 删除自定义计划 | P0 |

**2.1.2 自定义计划创建**
| 功能 | 描述 | 优先级 |
|------|------|--------|
| 计划基本信息 | 名称、描述、目标设置 | P0 |
| 添加运动 | 从动作库选择运动 | P0 |
| 设置运动参数 | 组数、次数、重量、休息时间 | P0 |
| 运动排序 | 拖动调整运动顺序 | P1 |
| 运动删除 | 移除不需要的运动 | P0 |
| 计划保存 | 保存计划到本地 | P0 |
| 计划编辑 | 编辑已有计划 | P1 |

#### 2.2 数据需求
```
模型:
  Plan
    - id: String (UUID)
    - name: String
    - description: String
    - category: String (目标)
    - exercises: List<Exercise>
    - totalDuration: long (预计总时长)
    - totalCalories: int (预计消耗)
    - createdAt: long
    - updatedAt: long
    - type: String (预设/自定义)
  
  Exercise
    - id: String
    - name: String
    - category: String (胸部、腿部等)
    - sets: int (组数)
    - reps: int (次数)
    - weight: double (重量，可选)
    - duration: int (时长，秒)
    - calories: int (消耗卡路里)
    - restTime: int (休息时间，秒)
    - tips: String (技巧提示)
```

#### 2.3 交互流程
```
计划浏览
  ├→ PlanRepository.getPresettedPlans()
  ├→ PlanRepository.getCustomPlans()
  ├→ PlanAdapter绑定数据
  └→ 用户点击计划 → WorkoutActivity

创建自定义计划
  ├→ CustomPlanActivity.onCreate()
  ├→ ExerciseAdapter展示动作库
  ├→ 用户选择运动
  ├→ ExerciseConfigActivity配置参数
  ├→ CustomPlanActivity.addExercise()
  ├→ 用户点击保存
  ├→ PlanRepository.savePlan()
  └→ PlanActivity刷新列表
```

---

### 3️⃣ 训练模块 (Workout Module)

#### 3.1 功能需求

**3.1.1 训练准备**
| 功能 | 描述 | 优先级 |
|------|------|--------|
| 显示计划详情 | 展示选中计划的所有运动 | P0 |
| 预设参数 | 显示每个运动的默认参数 | P0 |
| 参数调整 | 用户可调整组数、次数、重量 | P1 |
| 开始训练 | 进入计时器 | P0 |

**3.1.2 实时训练**
| 功能 | 描述 | 优先级 |
|------|------|--------|
| 运动计时 | 显示当前运动的时长 | P0 |
| 组数记录 | 记录当前完成的组数 | P0 |
| 暂停/继续 | 训练暂停和继续 | P0 |
| 跳过运动 | 跳过当前运动 | P1 |
| 提前结束 | 提前完成训练 | P1 |
| 实时统计 | 显示累计时长、卡路里 | P0 |

**3.1.3 训练完成**
| 功能 | 描述 | 优先级 |
|------|------|--------|
| 总结数据 | 显示训练统计 | P0 |
| 数据保存 | 保存训练记录 | P0 |
| 反馈 | 显示完成提示 | P0 |

#### 3.2 数据需求
```
WorkoutSession
  - id: String (UUID)
  - planName: String
  - startTime: long
  - endTime: long
  - duration: long (毫秒)
  - calories: int (消耗卡路里)
  - completed: boolean
  - exercises: List<ExerciseRecord>

ExerciseRecord
  - exerciseName: String
  - plannedSets: int
  - completedSets: int
  - plannedReps: int
  - actualReps: List<Integer> (每组的实际次数)
  - weight: double
  - duration: long (毫秒)
  - calories: int
```

#### 3.3 交互流程
```
启动训练
  ├→ WorkoutActivity显示计划
  ├→ 用户调整参数（可选）
  ├→ 点击"开始训练"
  └→ TimerActivity启动计时

训练进行中
  ├→ TimerActivity显示计时
  ├→ WorkoutExerciseAdapter展示所有运动
  ├→ 用户完成每组后记录
  ├→ 计算累计时长和卡路里
  └→ 用户切换到下一个运动

训练完成
  ├→ TimerActivity显示总结
  ├→ WorkoutRepository.saveSession()
  ├→ 更新WorkoutStats
  └→ 返回首页
```

---

### 4️⃣ 统计模块 (Stats Module)

#### 4.1 功能需求

**4.1.1 训练统计**
| 功能 | 描述 | 优先级 |
|------|------|--------|
| 今日统计 | 今日训练次数、时长、卡路里 | P0 |
| 本周统计 | 周数据汇总 | P0 |
| 月度统计 | 月度数据汇总 | P1 |
| 历史记录 | 列出所有训练记录 | P0 |
| 导出数据 | 导出为CSV/PDF | P2 |

**4.1.2 身体指标追踪**
| 功能 | 描述 | 优先级 |
|------|------|--------|
| 记录指标 | 新增身体指标记录 | P0 |
| 查看历史 | 显示历史指标记录 | P0 |
| 指标分析 | 显示体重变化、趋势 | P0 |
| 删除记录 | 删除指标记录 | P0 |
| 图表展示 | 图表显示数据趋势 | P1 |

#### 4.2 数据需求
```
BodyMetric
  - id: String
  - date: String (yyyy-MM-dd)
  - timestamp: long
  - weight: double (kg)
  - bodyFat: double (%)
  - muscleMass: double (kg)
  - boneMass: double (kg)
  - water: double (%)
  - visceralFat: double
  - bmi: double
  - notes: String
```

#### 4.3 交互流程
```
查看统计
  ├→ StatsActivity.onCreate()
  ├→ WorkoutRepository.getTodayStats()
  ├→ WorkoutRepository.getWeekStats()
  ├→ WorkoutHistoryAdapter绑定历史
  └→ 用户可点击查看详情

记录身体指标
  ├→ BodyMetricActivity.onCreate()
  ├→ 点击"新增记录"按钮
  ├→ 弹出对话框输入数据
  ├→ BodyMetricRepository.saveMetric()
  ├→ BodyMetricAdapter刷新列表
  └→ 显示最新数据和趋势
```

---

### 5️⃣ 个人资料模块 (Profile Module)

#### 5.1 功能需求
| 功能 | 描述 | 优先级 |
|------|------|--------|
| 用户信息 | 展示用户基本信息（昵称、性别、身高等） | P0 |
| 个人目标 | 显示和编辑个人健身目标 | P1 |
| 隐私设置 | 隐私和权限管理 | P1 |
| 应用设置 | 通知、单位制、主题等设置 | P1 |
| 关于应用 | 版本信息、反馈等 | P2 |
| 数据管理 | 导出、备份、清空数据 | P2 |

#### 5.2 数据需求
```
UserProfile
  - userId: String
  - nickname: String
  - gender: String (M/F)
  - height: double (m)
  - birthDate: long
  - goals: List<Goal>
  
Goal
  - name: String
  - targetWeight: double
  - deadline: long
  - status: String (进行中/已完成/已放弃)
```

---

### 6️⃣ 通知模块 (Notification Module) - 可选

#### 6.1 功能需求
| 功能 | 描述 | 优先级 |
|------|------|--------|
| 训练提醒 | 定时提醒用户进行训练 | P2 |
| 指标提醒 | 提醒用户记录身体指标 | P2 |
| 目标提醒 | 接近目标时的提醒 | P2 |

---

## 系统架构

### 整体架构图

```
┌─────────────────────────────────────────────────────────┐
│                    UI Layer (Presentation)              │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │
│  │ HomeActivity │  │ PlanActivity │  │WorkoutActivity│  │
│  └──────────────┘  └──────────────┘  └──────────────┘  │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │
│  │StatsActivity │  │BodyMetricA.  │  │ProfileActivity│  │
│  └──────────────┘  └──────────────┘  └──────────────┘  │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│              ViewModel Layer (Optional)                 │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │
│  │PlanViewModel │  │WorkoutVM     │  │StatsViewModel│  │
│  └──────────────┘  └──────────────┘  └──────────────┘  │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│           Business Logic Layer (Repository)            │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │
│  │PlanRepo      │  │WorkoutRepo   │  │BodyMetricRepo│  │
│  └──────────────┘  └──────────────┘  └──────────────┘  │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│              Data Access Layer (DAO/Manager)           │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │
│  │PlanDao       │  │WorkoutDao    │  │BodyMetricDAO │  │
│  └──────────────┘  └──────────────┘  └──────────────┘  │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│           Storage Layer (LocalDatabase)               │
│  ┌────────────────────────────────────────────────┐   │
│  │     SharedPreferences / Room Database          │   │
│  └────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────┘
```

### 推荐包结构

```
com.example.fitness/
│
├── core/                              # 核心基础
│   ├── base/
│   │   ├── BaseActivity.java          # 基础Activity
│   │   └── BaseAdapter.java           # 基础适配器 (新增)
│   ├── utils/
│   │   ├── DateUtils.java             # 日期工具 (新增)
│   │   ├── CalorieCalculator.java     # 卡路里计算 (新增)
│   │   └── Constants.java             # 常量定义 (新增)
│   └── application/
│       └── FitnessApp.java            # 应用入口 (新增)
│
├── models/                            # 数据模型
│   ├── Exercise.java
│   ├── CustomPlan.java
│   ├── BodyMetric.java
│   ├── WorkoutSession.java
│   ├── WorkoutStats.java
│   ├── PlanItem.java
│   └── UserProfile.java               # 新增
│
├── data/                              # 数据层
│   ├── repositories/
│   │   ├── PlanRepository.java        # 新增
│   │   ├── WorkoutRepository.java     # 新增
│   │   └── BodyMetricRepository.java  # 新增
│   ├── dao/                           # 数据访问对象
│   │   ├── PlanDao.java               # 新增
│   │   ├── WorkoutDao.java            # 新增
│   │   └── BodyMetricDao.java         # 新增
│   └── manager/
│       └── PlanDataManager.java       # 保留（逐步迁移）
│
├── ui/                                # UI层
│   ├── home/
│   │   └── MainActivity.java
│   ├── plan/
│   │   ├── PlanActivity.java
│   │   ├── CustomPlanActivity.java
│   │   ├── ExerciseConfigActivity.java
│   │   └── adapters/
│   │       ├── PlanAdapter.java
│   │       └── ExerciseAdapter.java
│   ├── workout/
│   │   ├── WorkoutActivity.java
│   │   ├── TimerActivity.java
│   │   └── adapters/
│   │       └── WorkoutExerciseAdapter.java
│   ├── stats/
│   │   ├── StatsActivity.java
│   │   ├── BodyMetricActivity.java
│   │   └── adapters/
│   │       ├── WorkoutHistoryAdapter.java
│   │       └── BodyMetricAdapter.java
│   └── profile/
│       ├── ProfileActivity.java
│       └── SettingsActivity.java
│
├── viewmodels/                        # ViewModel层 (可选)
│   ├── PlanViewModel.java
│   ├── WorkoutViewModel.java
│   ├── StatsViewModel.java
│   └── ProfileViewModel.java
│
└── resources/                         # 资源层
    ├── drawable/
    ├── layout/
    ├── values/
    └── xml/
```

---

## 数据流设计

### 1. 计划创建数据流

```
用户输入
  ├─ Plan信息 (名称、描述)
  └─ Exercises[] (运动列表)
        ↓
  CustomPlanActivity
        ↓
  数据验证
        ↓
  CustomPlan 对象创建
        ↓
  PlanRepository.savePlan()
        ↓
  PlanDao.insertPlan()
        ↓
  SharedPreferences.putString() / Room.insert()
        ↓
  事件通知 (LiveData/Flow)
        ↓
  PlanActivity 刷新列表
        ↓
  UI 展示新计划
```

### 2. 训练执行数据流

```
选择计划
  ├─ WorkoutActivity 显示计划详情
  ├─ 用户调整参数（可选）
  └─ 点击开始训练
        ↓
  TimerActivity 启动计时
        ↓
  实时记录
  ├─ 当前组数
  ├─ 当前时长
  └─ 实时卡路里
        ↓
  用户完成训练或点击完成
        ↓
  生成 WorkoutSession
  ├─ 总时长
  ├─ 总卡路里
  ├─ 每个运动的完成情况
  └─ 时间戳
        ↓
  WorkoutRepository.saveSession()
        ↓
  WorkoutDao.insertSession()
        ↓
  数据持久化
        ↓
  更新 WorkoutStats
        ↓
  通知首页和统计页面更新
```

### 3. 数据查询流

```
使用场景: 首页显示今日统计
        ↓
  MainActivity.onCreate()
        ↓
  WorkoutRepository.getTodayStats()
        ↓
  WorkoutDao.queryTodayWorkouts()
        ↓
  从 SharedPreferences/Room 读取
        ↓
  计算 WorkoutStats
  ├─ totalWorkouts
  ├─ totalDuration
  └─ totalCalories
        ↓
  返回 WorkoutStats
        ↓
  更新 UI 组件
```

---

## 模块间通信

### 通信机制

| 场景 | 机制 | 例子 |
|------|------|------|
| Activity → Activity | Intent | PlanActivity → WorkoutActivity |
| Activity ← Activity | 回调/Result | ExerciseConfigActivity 返回参数 |
| Activity ← Data变化 | LiveData/Flow (推荐) | Repository通知Activity更新 |
| 广播事件 | LocalBroadcastManager | 训练完成事件 |

### 推荐的通信模式

```java
// 模式1: Repository + LiveData (推荐)
WorkoutRepository {
  LiveData<List<WorkoutSession>> getWorkoutHistory()
}

MainActivity {
  workoutRepo.getWorkoutHistory().observe(this, sessions -> {
    // 更新UI
  })
}

// 模式2: 回调接口
interface OnPlanSelectedListener {
  void onSelected(CustomPlan plan)
}

// 模式3: 事件总线 (可选)
EventBus.getDefault().post(new TrainingCompleteEvent(...))
```

---

## 非功能需求

### 性能需求

| 指标 | 目标 | 说明 |
|------|------|------|
| 首页加载时间 | < 500ms | 包括数据查询和UI渲染 |
| 列表滚动帧率 | 60 FPS | 流畅的列表滚动 |
| 内存占用 | < 100MB | 稳定状态下的内存使用 |
| 数据查询时间 | < 200ms | 数据库查询响应时间 |
| 应用启动时间 | < 2s | 从启动到首页显示 |

### 可用性需求

| 指标 | 要求 |
|------|------|
| 兼容Android版本 | API 24+ (Android 7.0+) |
| 屏幕尺寸 | 4.5" ~ 6.7" 手机 |
| 离线功能 | 所有功能本地可用 |
| 数据安全 | 加密存储敏感信息 |

### 可维护性需求

| 要求 | 说明 |
|------|------|
| 代码规范 | 遵循Google Java Style |
| 注释覆盖 | 关键业务逻辑必须有注释 |
| 单元测试 | 核心业务逻辑覆盖 > 80% |
| 文档完整性 | 维护README和API文档 |
| 异常处理 | 所有网络/数据操作必须异常处理 |

---

## 重构路线图

### Phase 1: 基础架构 (第1-2周)

**目标**: 建立分层架构和包结构

- [ ] 创建新的包结构
- [ ] 创建BaseActivity
- [ ] 创建Repository基类
- [ ] 创建DAO基类
- [ ] 迁移现有代码到新包结构
- [ ] 编写单元测试框架

**交付物**: 
- 新的项目结构
- BaseActivity/BaseRepository/BaseDao
- 数据迁移完成

### Phase 2: 数据层重构 (第3-4周)

**目标**: 实现Repository + DAO模式

- [ ] 创建PlanRepository + PlanDao
- [ ] 创建WorkoutRepository + WorkoutDao
- [ ] 创建BodyMetricRepository + BodyMetricDao
- [ ] 实现缓存机制
- [ ] 编写数据访问单元测试
- [ ] 集成Room数据库（可选）

**交付物**:
- PlanRepository 完整实现
- WorkoutRepository 完整实现
- BodyMetricRepository 完整实现
- 单元测试覆盖 > 80%

### Phase 3: UI层优化 (第5-6周)

**目标**: 优化UI代码，分离关注点

- [ ] 创建各模块ViewModel（可选）
- [ ] 更新Activity使用Repository
- [ ] 实现LiveData观察者模式
- [ ] 创建BaseAdapter
- [ ] 统一Adapter回调接口
- [ ] 编写UI测试

**交付物**:
- 所有Activity使用Repository
- 统一的Adapter基类
- UI测试用例

### Phase 4: 功能完善 (第7-8周)

**目标**: 添加新功能和优化

- [ ] 实现图表展示功能
- [ ] 添加数据导出功能
- [ ] 性能优化
- [ ] 增加错误处理和日志
- [ ] 完成集成测试

**交付物**:
- 新增功能完整实现
- 性能基准报告
- 集成测试通过

### Phase 5: 文档和发布 (第9-10周)

**目标**: 完成文档和发布准备

- [ ] 编写API文档
- [ ] 编写开发指南
- [ ] 编写用户手册
- [ ] 代码审查
- [ ] Beta测试
- [ ] 发布Release版本

**交付物**:
- 完整的API文档
- 开发者指南
- Release v2.0

---

## 关键设计决策

### 1. 为什么采用Repository模式?
- ✅ 降低UI层对数据源的依赖
- ✅ 便于单元测试（Mock Repository）
- ✅ 易于替换数据源（SharedPreferences → Room → Server）
- ✅ 集中管理业务逻辑

### 2. 为什么使用ViewModel + LiveData?
- ✅ 自动处理配置变更（如旋转屏幕）
- ✅ 非破坏性的View更新
- ✅ 生命周期感知
- ✅ 官方推荐架构

### 3. 为什么分模块化UI?
- ✅ 高内聚、低耦合
- ✅ 便于团队协作
- ✅ 易于维护和测试
- ✅ 支持功能独立演进

### 4. 为什么使用Adapter基类?
- ✅ DRY原则，减少代码重复
- ✅ 统一的列表项点击处理
- ✅ 统一的视图绑定方式
- ✅ 便于维护

### 5. 数据库选择: SharedPreferences → Room
- 现阶段: 保留SharedPreferences + Gson (稳定性优先)
- 中期: 引入Room数据库
  - 支持更复杂的查询
  - 性能更好
  - 类型安全
- 长期: 考虑网络同步

---

## 附录

### A. 常见问题

**Q: 重构会不会破坏现有功能?**  
A: 不会。重构保持功能不变，仅改变代码组织方式。通过单元测试确保功能正确性。

**Q: 旧的PlanDataManager怎么处理?**  
A: 逐步迁移。新代码使用Repository，旧Activity保留。等所有Activity都迁移后再删除。

**Q: 需要立即引入Room数据库吗?**  
A: 不需要。优先完成包结构和Repository模式。Room是可选的，后续优化。

**Q: ViewModel是强制的吗?**  
A: 不是。优先级较低。可先实现Repository模式，后续视情况引入ViewModel。

### B. 参考资源

- [Android Architecture Components](https://developer.android.com/topic/libraries/architecture)
- [Repository Pattern](https://developer.android.com/jetpack/guide)
- [LiveData](https://developer.android.com/topic/libraries/architecture/livedata)
- [Room Database](https://developer.android.com/training/data-storage/room)

---

**文档版本**: 2.0  
**最后更新**: 2026年4月21日  
**维护者**: 项目团队
