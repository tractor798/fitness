# 图片资源占位符说明

本文档列出了项目中所有需要替换为本地图片资源的网络 URL 地址。请根据分类将下载好的图片放入 `app/src/main/res/drawable` 目录，并更新代码中的引用。

## 1. 首页 (MainActivity)
- **用户头像**: `profileAvatar`
  - 当前 URL: `https://lh3.googleusercontent.com/...`
  - 建议文件名: `ic_user_avatar.png`
  - **需修改文件**: [`app/src/main/java/com/example/fitness/MainActivity.java`](e:\Andriod\fitness\app\src\main\java\com\example\fitness\MainActivity.java) (约第 80 行 `ImageLoader.loadCircleImage`)
- **开始训练背景图**: `startWorkoutBackground`
  - 当前 URL: `https://images.unsplash.com/photo-1517836357463-d25dfeac3438...`
  - 建议文件名: `bg_start_workout.jpg`
  - **需修改文件**: [`app/src/main/java/com/example/fitness/MainActivity.java`](e:\Andriod\fitness\app\src\main\java\com\example\fitness\MainActivity.java) (约第 87 行 `ImageLoader.loadImage`)

## 2. 计划页 (PlanActivity)
- **推荐计划封面**: `featuredWorkoutImage`
  - 当前 URL: `https://images.unsplash.com/photo-1534258936925-c58bed479fcb...`
  - 建议文件名: `bg_featured_plan.jpg`
  - **需修改文件**: [`app/src/main/java/com/example/fitness/PlanActivity.java`](e:\Andriod\fitness\app\src\main\java\com\example\fitness\PlanActivity.java) (约第 75 行 `ImageLoader.loadImage`)

## 3. 训练详情页 (WorkoutActivity)
- **训练主视觉图**: `heroImage`
  - 当前 URL: `https://images.unsplash.com/photo-1576678927484-cc907957088c...`
  - 建议文件名: `bg_workout_hero.jpg`
  - **需修改文件**: [`app/src/main/java/com/example/fitness/WorkoutActivity.java`](e:\Andriod\fitness\app\src\main\java\com\example\fitness\WorkoutActivity.java) (约第 62 行 `Glide.with(this).load(...)`)

## 4. 自定义计划动作列表 (CustomPlanActivity)
以下图片在 `setupExercises()` 方法中定义：
- **深蹲**: `https://picsum.photos/seed/squats/300` -> `ic_exercise_squats.png`
- **俯卧撑**: `https://picsum.photos/seed/pushups/300` -> `ic_exercise_pushups.png`
- **平板支撑**: `https://picsum.photos/seed/plank/300` -> `ic_exercise_plank.png`
- **哑铃弯举**: `https://picsum.photos/seed/bicep/300` -> `ic_exercise_bicep.png`
- **弓步蹲**: `https://picsum.photos/seed/lunges/300` -> `ic_exercise_lunges.png`
- **登山跑**: `https://picsum.photos/seed/mountain/300` -> `ic_exercise_mountain.png`
- **需修改文件**: [`app/src/main/java/com/example/fitness/CustomPlanActivity.java`](e:\Andriod\fitness\app\src\main\java\com\example\fitness\CustomPlanActivity.java) (约第 45-50 行)

---

### 操作指南：
1. **准备图片**：将上述建议文件名的图片保存到 `app/src/main/res/drawable/`。
2. **修改代码**：打开对应的 Java 文件，将 `ImageLoader.loadImage(...)` 或 `Glide.load(...)` 中的 URL 字符串替换为 `R.drawable.your_image_name`。
   - 例如：将 `"https://..."` 改为 `String.valueOf(R.drawable.ic_user_avatar)` 或直接使用 Glide 的 `load(R.drawable.ic_user_avatar)`。
3. **验证**：重新编译运行项目，确保图片显示正常且无崩溃。
