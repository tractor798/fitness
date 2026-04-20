package com.example.fitness;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

/**
 * 训练历史回顾页，包含月历、当日总热量与训练列表。
 */
@RequiresApi(api = Build.VERSION_CODES.O)
public class WorkoutHistoryActivity extends BaseActivity {

    private TextView monthTitleText;
    private TextView dailyTotalCaloriesText;
    private TextView emptyText;
    private GridLayout calendarGrid;
    private RecyclerView historyRecyclerView;
    private WorkoutHistoryAdapter historyAdapter;

    private ServiceLocator serviceLocator;
    private List<WorkoutSession> allSessions = new ArrayList<>();
    private LocalDate selectedDate = LocalDate.now();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_history);

        serviceLocator = ServiceLocator.getInstance(this);
        initializeViews();
        loadHistoryData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadHistoryData();
    }

    private void initializeViews() {
        monthTitleText = findViewById(R.id.monthTitleText);
        dailyTotalCaloriesText = findViewById(R.id.dailyTotalCaloriesText);
        emptyText = findViewById(R.id.emptyText);
        calendarGrid = findViewById(R.id.calendarGrid);
        historyRecyclerView = findViewById(R.id.historyRecyclerView);

        if (historyRecyclerView != null) {
            historyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        }
    }

    private void loadHistoryData() {
        List<WorkoutSession> sessions = serviceLocator.getWorkoutRepository().getAllSessions();
        allSessions = sessions != null ? sessions : new ArrayList<>();

        if (!allSessions.isEmpty()) {
            WorkoutSession latest = allSessions.get(0);
            long timestamp = latest.getEndTime() > 0 ? latest.getEndTime() : latest.getStartTime();
            selectedDate = Instant.ofEpochMilli(timestamp)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
        }

        renderCalendar();
        updateSessionsForSelectedDate();
    }

    @SuppressLint("SetTextI18n")
    private void renderCalendar() {
        if (calendarGrid == null) {
            return;
        }

        YearMonth month = YearMonth.from(selectedDate);
        if (monthTitleText != null) {
            monthTitleText.setText(month.getYear() + "年" + month.getMonthValue() + "月");
        }

        calendarGrid.removeAllViews();
        calendarGrid.setColumnCount(7);

        LocalDate firstDay = month.atDay(1);
        int dayOffset = convertToMondayFirst(firstDay.getDayOfWeek());
        int daysInMonth = month.lengthOfMonth();

        for (int i = 0; i < dayOffset; i++) {
            calendarGrid.addView(createEmptyCalendarCell());
        }

        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = month.atDay(day);
            TextView dayView = createDayCell(day, date);
            calendarGrid.addView(dayView);
        }
    }

    private TextView createDayCell(int day, LocalDate date) {
        TextView tv = new TextView(this);
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = dpToPx(44);
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        params.setMargins(dpToPx(2), dpToPx(4), dpToPx(2), dpToPx(4));
        tv.setLayoutParams(params);
        tv.setGravity(Gravity.CENTER);
        tv.setText(String.valueOf(day));
        tv.setTextSize(16);
        tv.setBackgroundResource(android.R.color.transparent);

        boolean isSelected = date.equals(selectedDate);
        boolean hasWorkout = hasWorkoutOnDate(date);

        if (isSelected) {
            tv.setBackgroundResource(R.drawable.bg_fab_stats);
            tv.setTextColor(getColor(android.R.color.white));
            tv.setTypeface(tv.getTypeface(), android.graphics.Typeface.BOLD);
        } else if (hasWorkout) {
            tv.setBackgroundResource(R.drawable.bg_day_dot_inactive);
            tv.setTextColor(getColor(R.color.onSurface));
            tv.setTypeface(tv.getTypeface(), android.graphics.Typeface.BOLD);
        } else {
            tv.setTextColor(getColor(R.color.onSurfaceVariant));
        }

        tv.setOnClickListener(v -> {
            selectedDate = date;
            renderCalendar();
            updateSessionsForSelectedDate();
        });
        return tv;
    }

    private View createEmptyCalendarCell() {
        View view = new View(this);
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = dpToPx(44);
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        params.setMargins(dpToPx(2), dpToPx(4), dpToPx(2), dpToPx(4));
        view.setLayoutParams(params);
        return view;
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    private void updateSessionsForSelectedDate() {
        List<WorkoutSession> filtered = new ArrayList<>();
        int totalCalories = 0;

        for (WorkoutSession session : allSessions) {
            LocalDate sessionDate = toLocalDate(session);
            if (selectedDate.equals(sessionDate)) {
                filtered.add(session);
                totalCalories += Math.max(0, session.getCalories());
            }
        }

        if (dailyTotalCaloriesText != null) {
            dailyTotalCaloriesText.setText(String.format("%,d", totalCalories));
        }

        boolean hasData = !filtered.isEmpty();
        if (emptyText != null) {
            emptyText.setVisibility(hasData ? View.GONE : View.VISIBLE);
            if (!hasData) {
                emptyText.setText(selectedDate.getMonthValue() + "月" + selectedDate.getDayOfMonth() + "日 暂无训练记录");
            }
        }
        if (historyRecyclerView != null) {
            historyRecyclerView.setVisibility(hasData ? View.VISIBLE : View.GONE);
        }

        if (historyAdapter == null) {
            historyAdapter = new WorkoutHistoryAdapter(filtered);
            if (historyRecyclerView != null) {
                historyRecyclerView.setAdapter(historyAdapter);
            }
        } else {
            historyAdapter.updateHistory(filtered);
        }
    }

    private boolean hasWorkoutOnDate(LocalDate date) {
        for (WorkoutSession session : allSessions) {
            if (date.equals(toLocalDate(session))) {
                return true;
            }
        }
        return false;
    }

    private LocalDate toLocalDate(WorkoutSession session) {
        long timestamp = session.getEndTime() > 0 ? session.getEndTime() : session.getStartTime();
        return java.time.Instant.ofEpochMilli(timestamp)
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDate();
    }

    private int convertToMondayFirst(DayOfWeek dayOfWeek) {
        int value = dayOfWeek.getValue();
        return value - 1;
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
}
