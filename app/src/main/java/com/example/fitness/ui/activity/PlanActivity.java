package com.example.fitness.ui.activity;

import com.example.fitness.R;
import com.example.fitness.BaseActivity;
import com.example.fitness.ServiceLocator;
import com.example.fitness.ui.adapter.PlanAdapter;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitness.domain.model.CustomPlan;
import com.example.fitness.domain.model.Exercise;
import com.example.fitness.domain.model.PlanItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

/**
 * 训练计划页，展示计划列表并支持按目标过滤。
 */
public class PlanActivity extends BaseActivity {

    private static final String FILTER_ALL = "全部";
    private static final String FILTER_FAT_LOSS = "减脂";
    private static final String FILTER_MUSCLE = "增肌";
    private static final String FILTER_SHAPING = "塑形";
    private static final String FILTER_RECOVERY = "恢复";

    private TextView filterAll;
    private TextView filterFatLoss;
    private TextView filterMuscle;
    private TextView filterShaping;
    private TextView filterRecovery;
    private RecyclerView plansRecyclerView;
    private ImageView startFeaturedWorkout;
    private ImageView featuredWorkoutImage;
    private FloatingActionButton createPlanFab;
    private LinearLayout paginationLayout;
    private Button prevPageButton;
    private Button nextPageButton;
    private TextView pageIndicatorText;

    private ServiceLocator serviceLocator;
    private String currentFilter = FILTER_ALL;
    private final List<PlanItem> allPlanItems = new ArrayList<>();
    private int currentPage = 0;
    private static final int ITEMS_PER_PAGE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);
        setupBottomNavigation(1);

        serviceLocator = ServiceLocator.getInstance(this);
        initializeViews();
        setupFilters();
        setupListeners();
        loadImages();
        loadPlans();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPlans();
    }

    private void initializeViews() {
        filterAll = findViewById(R.id.filterAll);
        filterFatLoss = findViewById(R.id.filterFatLoss);
        filterMuscle = findViewById(R.id.filterMuscle);
        filterShaping = findViewById(R.id.filterShaping);
        filterRecovery = findViewById(R.id.filterRecovery);
        plansRecyclerView = findViewById(R.id.plansRecyclerView);
        startFeaturedWorkout = findViewById(R.id.startFeaturedWorkout);
        featuredWorkoutImage = findViewById(R.id.featuredWorkoutImage);
        createPlanFab = findViewById(R.id.createPlanFab);
        paginationLayout = findViewById(R.id.paginationLayout);
        prevPageButton = findViewById(R.id.prevPageButton);
        nextPageButton = findViewById(R.id.nextPageButton);
        pageIndicatorText = findViewById(R.id.pageIndicatorText);

        if (plansRecyclerView != null) {
            plansRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        }

        if (prevPageButton != null) {
            prevPageButton.setOnClickListener(v -> goToPage(currentPage - 1));
        }
        if (nextPageButton != null) {
            nextPageButton.setOnClickListener(v -> goToPage(currentPage + 1));
        }
    }

    private void loadImages() {
        if (featuredWorkoutImage != null) {
            featuredWorkoutImage.setImageResource(R.drawable.cover_fat_burn);
        }
    }

    private void setupFilters() {
        View.OnClickListener listener = v -> {
            resetAllFilters();
            TextView selectedFilter = (TextView) v;
            selectedFilter.setBackgroundResource(R.drawable.bg_filter_active);
            selectedFilter.setTextColor(getColor(R.color.onPrimary));

            if (selectedFilter == filterFatLoss) {
                currentFilter = FILTER_FAT_LOSS;
            } else if (selectedFilter == filterMuscle) {
                currentFilter = FILTER_MUSCLE;
            } else if (selectedFilter == filterShaping) {
                currentFilter = FILTER_SHAPING;
            } else if (selectedFilter == filterRecovery) {
                currentFilter = FILTER_RECOVERY;
            } else {
                currentFilter = FILTER_ALL;
            }
            loadPlans();
        };

        if (filterAll != null) {
            filterAll.setOnClickListener(listener);
        }
        if (filterFatLoss != null) {
            filterFatLoss.setOnClickListener(listener);
        }
        if (filterMuscle != null) {
            filterMuscle.setOnClickListener(listener);
        }
        if (filterShaping != null) {
            filterShaping.setOnClickListener(listener);
        }
        if (filterRecovery != null) {
            filterRecovery.setOnClickListener(listener);
        }
    }

    private void resetAllFilters() {
        TextView[] filters = {filterAll, filterFatLoss, filterMuscle, filterShaping, filterRecovery};
        for (TextView filter : filters) {
            if (filter != null) {
                filter.setBackgroundResource(R.drawable.bg_filter_inactive);
                filter.setTextColor(getColor(R.color.onSurfaceVariant));
            }
        }
    }

    private void loadPlans() {
        allPlanItems.clear();

        // 添加精选计划（固定显示在第一位）
        CustomPlan featuredPlan = createFeaturedPlan();
        PlanItem featuredItem = new PlanItem();
        featuredItem.setName(featuredPlan.getName());
        featuredItem.setDescription(featuredPlan.getDescription());
        featuredItem.setDuration(featuredPlan.getTotalDuration());
        featuredItem.setCalories(featuredPlan.getTotalCalories());
        featuredItem.setType(PlanItem.TYPE_PRESET);
        featuredItem.setCustomPlan(featuredPlan);
        allPlanItems.add(featuredItem);

        // 添加自定义计划
        List<CustomPlan> allPlans = serviceLocator.getPlanRepository().getAllPlans();
        for (CustomPlan customPlan : allPlans) {
            if (!FILTER_ALL.equals(currentFilter) && !matchesFilter(customPlan, currentFilter)) {
                continue;
            }
            PlanItem item = new PlanItem();
            item.setName(customPlan.getName());
            item.setDescription(customPlan.getDescription());
            item.setDuration(customPlan.getTotalDuration());
            item.setCalories(customPlan.getTotalCalories());
            item.setType(PlanItem.TYPE_CUSTOM);
            item.setCustomPlan(customPlan);
            allPlanItems.add(item);
        }

        // 重置到第一页
        currentPage = 0;
        displayCurrentPage();
    }

    @SuppressLint("SetTextI18n")
    private void displayCurrentPage() {
        int totalPages = (int) Math.ceil((double) allPlanItems.size() / ITEMS_PER_PAGE);
        
        if (totalPages <= 1) {
            // 只有一页，隐藏分页控件
            if (paginationLayout != null) {
                paginationLayout.setVisibility(View.GONE);
            }
            // 显示所有项目
            updateAdapter(allPlanItems);
        } else {
            // 多页，显示分页控件
            if (paginationLayout != null) {
                paginationLayout.setVisibility(View.VISIBLE);
            }
            
            // 计算当前页的数据范围
            int startIndex = currentPage * ITEMS_PER_PAGE;
            int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, allPlanItems.size());
            List<PlanItem> currentPageItems = allPlanItems.subList(startIndex, endIndex);
            
            updateAdapter(currentPageItems);
            
            // 更新页码指示器
            if (pageIndicatorText != null) {
                pageIndicatorText.setText((currentPage + 1) + " / " + totalPages);
            }
            
            // 更新按钮状态
            if (prevPageButton != null) {
                prevPageButton.setEnabled(currentPage > 0);
                prevPageButton.setAlpha(currentPage > 0 ? 1.0f : 0.3f);
            }
            if (nextPageButton != null) {
                nextPageButton.setEnabled(currentPage < totalPages - 1);
                nextPageButton.setAlpha(currentPage < totalPages - 1 ? 1.0f : 0.3f);
            }
        }
    }

    private void goToPage(int page) {
        int totalPages = (int) Math.ceil((double) allPlanItems.size() / ITEMS_PER_PAGE);
        if (page >= 0 && page < totalPages) {
            currentPage = page;
            displayCurrentPage();
        }
    }

    private void updateAdapter(List<PlanItem> items) {
        PlanAdapter adapter = new PlanAdapter(items, this::openWorkoutFromPlan, this::deletePlan);
        if (plansRecyclerView != null) {
            plansRecyclerView.setAdapter(adapter);
        }
    }

    private boolean matchesFilter(CustomPlan plan, String filter) {
        // 使用分类字段进行匹配
        String category = plan.getCategory();
        if (category != null && !category.isEmpty()) {
            return switch (filter) {
                case FILTER_FAT_LOSS -> category.contains("减脂");
                case FILTER_MUSCLE -> category.contains("增肌");
                case FILTER_SHAPING -> category.contains("塑形");
                case FILTER_RECOVERY -> category.contains("耐力");
                default -> true;
            };
        }
        
        // 兼容旧数据，使用名称和描述匹配
        String text = (plan.getName() + " " + plan.getDescription()).toLowerCase();
        return switch (filter) {
            case FILTER_FAT_LOSS -> text.contains("减脂") || text.contains("燃脂");
            case FILTER_MUSCLE -> text.contains("增肌") || text.contains("力量");
            case FILTER_SHAPING -> text.contains("塑形") || text.contains("核心");
            case FILTER_RECOVERY -> text.contains("恢复") || text.contains("拉伸");
            default -> true;
        };
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupListeners() {
        if (startFeaturedWorkout != null) {
            startFeaturedWorkout.setOnClickListener(v -> openFeaturedWorkout());
        }

        if (createPlanFab != null) {
            createPlanFab.setOnClickListener(v ->
                    startActivity(new Intent(this, CustomPlanActivity.class))
            );
            
            // 添加FAB拖动功能
            createPlanFab.setOnTouchListener(new View.OnTouchListener() {
                private float startX, startY;
                private boolean isDragging = false;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            startX = event.getRawX();
                            startY = event.getRawY();
                            isDragging = false;
                            return true;
                        case MotionEvent.ACTION_MOVE:
                            float deltaX = event.getRawX() - startX;
                            float deltaY = event.getRawY() - startY;
                            
                            if (Math.abs(deltaX) > 10 || Math.abs(deltaY) > 10) {
                                isDragging = true;
                                
                                // 更新FAB位置
                                float newX = v.getX() + deltaX;
                                float newY = v.getY() + deltaY;
                                
                                // 限制在屏幕范围内
                                newX = Math.max(0, Math.min(newX, getWindowManager().getDefaultDisplay().getWidth() - v.getWidth()));
                                newY = Math.max(0, Math.min(newY, getWindowManager().getDefaultDisplay().getHeight() - v.getHeight() - 200)); // 避开底部导航栏区域
                                
                                v.setX(newX);
                                v.setY(newY);
                                
                                startX = event.getRawX();
                                startY = event.getRawY();
                            }
                            return true;
                        case MotionEvent.ACTION_UP:
                            if (!isDragging) {
                                // 如果没有拖动，就是点击事件
                                createPlanFab.performClick();
                            }
                            return true;
                    }
                    return false;
                }
            });
        }
    }

    private void openWorkoutFromPlan(PlanItem planItem) {
        Intent intent = new Intent(this, WorkoutActivity.class);
        intent.putExtra("workout_name", planItem.getName());
        intent.putExtra("plan_type", planItem.getType());
        if (planItem.getCustomPlan() != null) {
            intent.putExtra("custom_plan", planItem.getCustomPlan());
        }
        startActivity(intent);
    }

    private void openFeaturedWorkout() {
        CustomPlan featuredPlan = createFeaturedPlan();
        Intent intent = new Intent(this, WorkoutActivity.class);
        intent.putExtra("workout_name", featuredPlan.getName());
        intent.putExtra("plan_type", PlanItem.TYPE_CUSTOM);
        intent.putExtra("custom_plan", featuredPlan);
        startActivity(intent);
        Toast.makeText(this, "已加载今日推荐训练", Toast.LENGTH_SHORT).show();
    }

    private CustomPlan createFeaturedPlan() {
        CustomPlan plan = new CustomPlan(
                "全身燃脂挑战",
                "结合 HIIT 与抗阻训练，在 45 分钟内激活全身。"
        );
        List<Exercise> exercises = new ArrayList<>();

        Exercise e1 = new Exercise(
                "杠铃深蹲",
                "腿部 · 爆发力",
                R.drawable.img_ex_squats
        );
        e1.setSelected(true);
        e1.setSets(4);
        e1.setReps(12);
        e1.setDuration(15);
        e1.setCalories(160);

        Exercise e2 = new Exercise(
                "俯卧撑",
                "胸肩 · 自重训练",
                R.drawable.img_ex_pushups
        );
        e2.setSelected(true);
        e2.setSets(3);
        e2.setReps(15);
        e2.setDuration(12);
        e2.setCalories(120);

        Exercise e3 = new Exercise(
                "平板支撑",
                "核心 · 稳定控制",
                R.drawable.img_ex_squats
        );
        e3.setSelected(true);
        e3.setSets(3);
        e3.setReps(1);
        e3.setDuration(18);
        e3.setCalories(140);

        exercises.add(e1);
        exercises.add(e2);
        exercises.add(e3);
        plan.setExercises(exercises);
        plan.calculateTotals();
        return plan;
    }

    private void deletePlan(PlanItem planItem) {
        new AlertDialog.Builder(this)
                .setTitle("确认删除")
                .setMessage("确定要删除计划「" + planItem.getName() + "」吗？")
                .setPositiveButton("删除", (dialog, which) -> {
                    serviceLocator.getPlanRepository().deletePlan(planItem.getName());
                    loadPlans();
                    Toast.makeText(this, "计划已删除", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("取消", null)
                .show();
    }
}
