package com.example.fitness;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BodyMetricActivity extends BaseActivity {

    private LinearLayout quickRecordCard;
    private TextView latestWeightText;
    private TextView latestBodyFatText;
    private TextView latestBmiText;
    private TextView weightChangeText;
    private RecyclerView bodyMetricsRecyclerView;
    private BodyMetricAdapter bodyMetricAdapter;
    private Button addMetricButton;

    private PlanDataManager dataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_body_metric);
        setupBottomNavigation(2); // 数据导航项
        setupBodyMetricNavigation();

        dataManager = PlanDataManager.getInstance(this);
        initializeViews();
        loadBodyMetrics();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadBodyMetrics();
    }

    private void initializeViews() {
        quickRecordCard = findViewById(R.id.quickRecordCard);
        latestWeightText = findViewById(R.id.latestWeightText);
        latestBodyFatText = findViewById(R.id.latestBodyFatText);
        latestBmiText = findViewById(R.id.latestBmiText);
        weightChangeText = findViewById(R.id.weightChangeText);
        bodyMetricsRecyclerView = findViewById(R.id.bodyMetricsRecyclerView);
        addMetricButton = findViewById(R.id.addMetricButton);

        if (bodyMetricsRecyclerView != null) {
            bodyMetricsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        }

        if (addMetricButton != null) {
            addMetricButton.setOnClickListener(v -> showAddMetricDialog());
        }
    }

    private void setupBodyMetricNavigation() {
        // 如果需要在导航栏附近添加其他功能，可在此处添加
        // 例如：一个按钮可以切换到StatsActivity
    }

    private void loadBodyMetrics() {
        BodyMetric latest = dataManager.getLatestBodyMetric();

        if (latest != null) {
            // 更新最新数据显示
            if (latestWeightText != null) {
                latestWeightText.setText(latest.getWeightDisplay());
            }
            if (latestBodyFatText != null) {
                latestBodyFatText.setText(latest.getBodyFatDisplay());
            }
            if (latestBmiText != null) {
                latestBmiText.setText(latest.getBmiDisplay());
            }

            // 计算体重变化
            double weightChange = dataManager.getWeightChangeInDays(30);
            if (weightChangeText != null) {
                String changeText = String.format(Locale.getDefault(), "%.1f kg", weightChange);
                if (weightChange < 0) {
                    weightChangeText.setText("↓ " + Math.abs(weightChange) + " kg");
                    weightChangeText.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                } else if (weightChange > 0) {
                    weightChangeText.setText("↑ " + weightChange + " kg");
                    weightChangeText.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                } else {
                    weightChangeText.setText("→ 0 kg");
                    weightChangeText.setTextColor(getResources().getColor(android.R.color.darker_gray));
                }
            }
        } else {
            // 没有数据时显示提示
            if (latestWeightText != null) latestWeightText.setText("-- kg");
            if (latestBodyFatText != null) latestBodyFatText.setText("--%");
            if (latestBmiText != null) latestBmiText.setText("--");
            if (weightChangeText != null) {
                weightChangeText.setText("暂无数据");
                weightChangeText.setTextColor(getResources().getColor(android.R.color.darker_gray));
            }
        }

        // 加载历史记录
        List<BodyMetric> metrics = dataManager.getBodyMetricsForDays(90); // 显示最近90天
        if (bodyMetricAdapter == null) {
            bodyMetricAdapter = new BodyMetricAdapter(metrics, this::onMetricDelete);
            bodyMetricsRecyclerView.setAdapter(bodyMetricAdapter);
        } else {
            bodyMetricAdapter.updateMetrics(metrics);
        }
    }

    private void showAddMetricDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("记录身体指标");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(20, 20, 20, 20);

        // 体重输入框
        EditText weightInput = new EditText(this);
        weightInput.setHint("体重 (kg)");
        weightInput.setInputType(android.text.InputType.TYPE_CLASS_NUMBER |
                android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        layout.addView(weightInput);

        // 体脂率输入框
        EditText bodyFatInput = new EditText(this);
        bodyFatInput.setHint("体脂率 (%)");
        bodyFatInput.setInputType(android.text.InputType.TYPE_CLASS_NUMBER |
                android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        layout.addView(bodyFatInput);

        // 肌肉质量输入框
        EditText muscleMassInput = new EditText(this);
        muscleMassInput.setHint("肌肉质量 (kg)");
        muscleMassInput.setInputType(android.text.InputType.TYPE_CLASS_NUMBER |
                android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        layout.addView(muscleMassInput);

        // BMI输入框
        EditText bmiInput = new EditText(this);
        bmiInput.setHint("BMI值 (可选)");
        bmiInput.setInputType(android.text.InputType.TYPE_CLASS_NUMBER |
                android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        layout.addView(bmiInput);

        // 备注输入框
        EditText notesInput = new EditText(this);
        notesInput.setHint("备注 (可选)");
        layout.addView(notesInput);

        builder.setView(layout);
        builder.setPositiveButton("保存", (dialog, which) -> {
            try {
                String weightStr = weightInput.getText().toString().trim();
                if (weightStr.isEmpty()) {
                    Toast.makeText(this, "请输入体重", Toast.LENGTH_SHORT).show();
                    return;
                }

                BodyMetric metric = new BodyMetric();
                metric.setWeight(Double.parseDouble(weightStr));

                if (!bodyFatInput.getText().toString().isEmpty()) {
                    metric.setBodyFat(Double.parseDouble(bodyFatInput.getText().toString()));
                }
                if (!muscleMassInput.getText().toString().isEmpty()) {
                    metric.setMuscleMass(Double.parseDouble(muscleMassInput.getText().toString()));
                }
                if (!bmiInput.getText().toString().isEmpty()) {
                    metric.setBmi(Double.parseDouble(bmiInput.getText().toString()));
                } else {
                    // 使用用户身高计算BMI
                    double height = dataManager.getUserHeight();
                    metric.calculateBMI(height);
                }
                if (!notesInput.getText().toString().isEmpty()) {
                    metric.setNotes(notesInput.getText().toString());
                }

                dataManager.saveBodyMetric(metric);
                Toast.makeText(this, "记录保存成功", Toast.LENGTH_SHORT).show();
                loadBodyMetrics();
            } catch (NumberFormatException e) {
                Toast.makeText(this, "输入格式错误", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    private void onMetricDelete(String date) {
        new AlertDialog.Builder(this)
                .setTitle("删除记录")
                .setMessage("确定要删除这条记录吗？")
                .setPositiveButton("删除", (dialog, which) -> {
                    dataManager.deleteBodyMetric(date);
                    Toast.makeText(this, "记录已删除", Toast.LENGTH_SHORT).show();
                    loadBodyMetrics();
                })
                .setNegativeButton("取消", null)
                .show();
    }
}
