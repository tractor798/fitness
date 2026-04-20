# 图片资源放置说明文档

## 概述
本文档说明了项目中所有图片资源的放置位置及对应的代码引用关系。所有网络图片已替换为本地资源，提升离线可用性和加载速度。

---

## 已完成的图片替换

### 1. 首页 (MainActivity.java)
| 图片位置 | 本地资源文件 | 说明 |
|---------|------------|------|
| 用户头像 | `R.drawable.img_user_profile` | 高清用户头像 |
| 开始训练背景 | `R.drawable.bg_start_workout` | 今日推荐训练背景 |

**代码位置**：`MainActivity.java` 第 80-88 行 `loadImages()` 方法

---

### 2. 训练计划页 (PlanActivity.java)
| 图片位置 | 本地资源文件 | 说明 |
|---------|------------|------|
| 推荐计划封面 | `R.drawable.cover_fat_burn` | 全身燃脂挑战封面 |

**代码位置**：`PlanActivity.java` 第 77-81 行 `loadImages()` 方法

---

### 3. 训练详情页 (WorkoutActivity.java)
| 图片位置 | 本地资源文件 | 说明 |
|---------|------------|------|
| 训练主视觉图 | `R.drawable.bg_recommend_workout` | 今日推荐训练背景 |

**代码位置**：`WorkoutActivity.java` 第 64-70 行 `loadWorkoutData()` 方法

---

### 4. 自定义计划页 (CustomPlanActivity.java)
| 动作名称 | 本地资源文件 | 原始说明 |
|---------|------------|---------|
| 深蹲 Squats | `R.drawable.img_ex_squats` | 杠铃深蹲 |
| 俯卧撑 Push-ups | `R.drawable.img_ex_pushups` | 俯卧撑 |
| 平板支撑 Plank | `R.drawable.img_ex_squats` | 暂时使用深蹲图片（可替换） |
| 哑铃弯举 Bicep Curls | `R.drawable.img_ex_biceps` | 哑铃弯举 |
| 弓步蹲 Lunges | `R.drawable.img_ex_pullups` | 暂时使用引体向上图片（可替换） |
| 登山跑 Mountain Climbers | `R.drawable.img_ex_seated_row` | 暂时使用坐姿划船图片（可替换） |

**代码位置**：`CustomPlanActivity.java` 第 49-55 行 `setupExercises()` 方法

---

### 5. 个人中心页 (activity_profile.xml)
| 图片位置 | 本地资源文件 | 说明 |
|---------|------------|------|
| 用户头像 | `R.drawable.img_user_profile` | 高清用户头像（圆形裁剪） |

**布局位置**：`activity_profile.xml` 第 73-77 行

---

### 6. 训练历史列表 (WorkoutHistoryAdapter.java)
根据训练计划名称智能匹配图片：

| 计划关键词 | 匹配图片资源 | 说明 |
|-----------|------------|------|
| 腿、深蹲、leg | `R.drawable.img_ex_squats` | 深蹲示意图 |
| HIIT、燃脂、cardio | `R.drawable.cover_fat_burn` | 全身燃脂封面 |
| 拉伸、恢复、recovery | `R.drawable.cover_pilates` | 普拉提封面 |
| 其他 | `R.drawable.bg_start_workout` | 默认训练背景 |

**代码位置**：`WorkoutHistoryAdapter.java` 第 114-128 行 `getImageResId()` 方法

---

## 图片资源清单

所有图片已下载至 `app/src/main/res/drawable/` 目录：

### 背景与封面类 (5 张)
- `bg_recommend_workout.jpg` - 今日推荐训练背景
- `cover_strength.jpg` - 力量训练计划封面
- `cover_fat_burn.jpg` - 全身燃脂挑战封面
- `cover_pilates.jpg` - 普拉提曲线雕琢封面
- `banner_nutrition.jpg` - 营养建议长条图

### 运动动作类 (6 张)
- `img_ex_squats.jpg` - 杠铃深蹲
- `img_ex_pushups.jpg` - 俯卧撑
- `img_ex_biceps.jpg` - 哑铃弯举
- `img_ex_bench_press.jpg` - 杠铃卧推
- `img_ex_pullups.jpg` - 引体向上
- `img_ex_seated_row.jpg` - 坐姿划船

### 用户与教练类 (3 张)
- `img_user_profile.jpg` - 高清用户头像
- `ic_avatar_male.jpg` - 默认头像（男）
- `img_coach_marcus.jpg` - 教练头像

### 辅助图类 (2 张)
- `img_weight_chart.jpg` - 体重趋势分析图
- `ic_hall_of_fame.jpg` - 荣誉殿堂图标占位

---

## 修改建议

### 1. 自定义计划动作图片优化
当前自定义计划中有 3 个动作暂时使用了替代图片，建议后续补充：
- 平板支撑：可拍摄或寻找专门的平板支撑示意图
- 弓步蹲：可拍摄或寻找专门的弓步蹲示意图  
- 登山跑：可拍摄或寻找专门的登山跑示意图

### 2. 图片压缩优化
部分图片文件较大（如 `bg_start_workout.jpg` 达到 3.9MB），建议使用 Android Studio 的 Image Asset 工具或在线压缩工具进行优化，建议控制在 500KB 以内。

### 3. 深色模式适配
如需支持深色模式，可以在 `drawable-night` 目录中放置深色版本的图片资源。

---

## 验证方法

1. 重新构建项目：`Build > Rebuild Project`
2. 运行应用到设备或模拟器
3. 检查各个页面的图片是否正确显示
4. 测试断网状态下图片是否仍然可用

---

## 更新日期
2026-04-24

## 备注
所有图片资源均遵循 HTML 原型设计稿的视觉风格，配色统一为红色主题（primary: #b51a1a）。
