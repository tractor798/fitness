# 健身管理系统 (Zen Kinetic) 开发指导与 Java Prompt 指南

**!! 重要声明：本指南已根据您的最新要求，全面调整为 Java 语言及原生 Activity 开发模式。 !!**

---

## 1. 技术栈要求 (Java Native)
*   **开发语言**：Java (JDK 8 或更高版本)。
*   **核心架构**：以 **Activity** 为基础的传统安卓开发模式。
*   **本地存储**：
    *   使用 **SQLite (推荐原生 SQLiteOpenHelper 或 Room with Java)**。
    *   使用 **SharedPreferences** 存储用户偏好和基础配置。
*   **UI 实现**：使用 XML 布局文件，遵循 Material Design 设计规范。

---

## 2. 核心逻辑与 Java 数据模型 (Data Model)
在 Java 中，请确保实体类包含正确的 Constructor 和 Getter/Setter 方法：
*   **User.java**: 用户信息类。
*   **Exercise.java**: 动作实体类。
*   **WorkoutPlan.java**: 训练计划类（需处理与动作的关联）。
*   **WorkoutLog.java**: 训练记录类（关联日期、时长、消耗）。
*   **BodyMetric.java**: 身体指标历史。

---

## 3. 针对 Java 开发 AI 的 Prompt 建议

### A. 初始化 Java 数据库架构
> "我正在开发一个名为 'Zen Kinetic' 的原生 Android 健身管理 App。**要求：必须使用 Java 语言，以 Activity 模式开发，禁止使用 Kotlin 或 Compose。** 应用为纯本地存储。请帮我设计 SQLite 数据库架构及对应的 `SQLiteOpenHelper` 类，包含：用户表、动作表、自定义计划表、以及训练历史记录表。请提供 Java 实体类代码。"

### B. Java Activity 与 XML 布局还原
> "请参考我提供的 HTML/CSS 设计稿，帮我将其转换为 Android **XML 布局代码**。同时，请编写对应的 **Java Activity 类**。要求：
> 1. 使用 Java 语言实现点击事件处理。
> 2. 颜色代码：主色 #FF5247（禅意红），背景 #F8F9FA。
> 3. 组件圆角统一为 24dp。
> 4. 使用 `findViewById` 或 `ViewBinding` (Java 版) 进行控件初始化。
> 5. 实现从本地 SQLite 数据库读取数据并更新 UI 的逻辑。"

### C. 实现 Java 核心交互 (以创建计划为例)
> "请帮我实现 [创建自定义计划] 的 Java 逻辑。包含：
> 1. `CreatePlanActivity.java`：处理输入框文本读取、RecyclerVIew 列表展示。
> 2. 跳转逻辑使用 `Intent`。
> 3. 数据保存逻辑：调用 DAO 类将 Java 对象持久化到 SQLite。"

---

## 4. 答辩关键建议 (Java 原生)
1.  **工程结构**：向评委展示 `src/main/java` 下清晰的包结构（如 `.activity`, `.adapter`, `.db`, `.model`）。
2.  **生命周期控制**：在答辩时可以重点提到在 Activity 的 `onResume()` 中刷新本地数据，以体现对安卓原生生命周期的理解。
3.  **适配器逻辑**：展示您如何编写自定义的 `BaseAdapter` 或 `RecyclerView.Adapter` (Java 实现) 来承载本地数据。

---

## 5. 开发建议
*   **不要混用语言**：在项目中完全避免 `.kt` 文件，确保 `build.gradle` 仅配置 Java 支持。
*   **静态引用**：由于是 Java，注意内存泄漏问题，尤其是在 Activity 内部使用 Handler 或静态变量时。
*   **预览代码**：点击设计稿的 **</> View Code**，将 HTML 结构喂给 AI 时，再次强调“Translate this to Android XML and Java Activity code”。

# End Update