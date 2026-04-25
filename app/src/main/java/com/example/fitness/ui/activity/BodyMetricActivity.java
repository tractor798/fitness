package com.example.fitness.ui.activity;

import com.example.fitness.R;
import com.example.fitness.BaseActivity;
import com.example.fitness.ServiceLocator;
import com.example.fitness.ui.adapter.BodyMetricAdapter;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitness.config.AppConfig;
import com.example.fitness.domain.model.BodyMetric;

import java.util.List;

/**
 * 身体指标记录页。
 */
public class BodyMetricActivity extends BaseActivity {

    private TextView latestWeightText;
    private TextView latestBodyFatText;
    private TextView latestBmiText;
    private TextView weightChangeText;
    private RecyclerView bodyMetricsRecyclerView;
    private BodyMetricAdapter bodyMetricAdapter;

    private ServiceLocator serviceLocator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_body_metric);

        serviceLocator = ServiceLocator.getInstance(this);
        initializeViews();
        loadBodyMetrics();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadBodyMetrics();
    }

    private void initializeViews() {
        latestWeightText = findViewById(R.id.latestWeightText);
        latestBodyFatText = findViewById(R.id.latestBodyFatText);
        latestBmiText = findViewById(R.id.latestBmiText);
        weightChangeText = findViewById(R.id.weightChangeText);
        bodyMetricsRecyclerView = findViewById(R.id.bodyMetricsRecyclerView);
        Button addMetricButton = findViewById(R.id.addMetricButton);

        if (bodyMetricsRecyclerView != null) {
            bodyMetricsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        }
        if (addMetricButton != null) {
            addMetricButton.setOnClickListener(v -> showAddMetricDialog());
        }
    }

    private void loadBodyMetrics() {
        BodyMetric latest = serviceLocator.getBodyMetricRepository().getLatestMetric();
        if (latest != null) {
            if (latestWeightText != null) {
                latestWeightText.setText(latest.getWeightDisplay());
            }
            if (latestBodyFatText != null) {
                latestBodyFatText.setText(latest.getBodyFatDisplay());
            }
            if (latestBmiText != null) {
                latestBmiText.setText(latest.getBmiDisplay());
            }

            double change = serviceLocator.getBodyMetricAnalysisService()
                    .getWeightChangeInDays(AppConfig.WEIGHT_TREND_DAYS);
            if (weightChangeText != null) {
                if (change < 0) {
                    weightChangeText.setText(String.format("↓ %.1f kg", Math.abs(change)));
                    weightChangeText.setTextColor(getColor(android.R.color.holo_green_dark));
                } else if (change > 0) {
                    weightChangeText.setText(String.format("↑ %.1f kg", change));
                    weightChangeText.setTextColor(getColor(android.R.color.holo_red_dark));
                } else {
                    weightChangeText.setText("→ 0 kg");
                    weightChangeText.setTextColor(getColor(android.R.color.darker_gray));
                }
            }
        } else {
            if (latestWeightText != null) {
                latestWeightText.setText("--");
            }
            if (latestBodyFatText != null) {
                latestBodyFatText.setText("--");
            }
            if (latestBmiText != null) {
                latestBmiText.setText("--");
            }
            if (weightChangeText != null) {
                weightChangeText.setText("暂无数据");
                weightChangeText.setTextColor(getColor(android.R.color.darker_gray));
            }
        }

        List<BodyMetric> metrics = serviceLocator.getBodyMetricRepository()
                .getMetricsForDays(AppConfig.BODY_METRIC_HISTORY_DAYS);
        if (bodyMetricAdapter == null) {
            bodyMetricAdapter = new BodyMetricAdapter(metrics, this::onMetricDelete);
            if (bodyMetricsRecyclerView != null) {
                bodyMetricsRecyclerView.setAdapter(bodyMetricAdapter);
            }
        } else {
            bodyMetricAdapter.updateMetrics(metrics);
        }
    }

    private void showAddMetricDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("记录身体指标");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 16, 32, 8);

        EditText weightInput = createInput("体重 (kg)", true);
        EditText bodyFatInput = createInput("体脂率 (%)", true);
        EditText muscleMassInput = createInput("肌肉量 (kg)", true);
        EditText bmiInput = createInput("BMI（可选）", true);
        EditText notesInput = createInput("备注（可选）", false);

        layout.addView(weightInput);
        layout.addView(bodyFatInput);
        layout.addView(muscleMassInput);
        layout.addView(bmiInput);
        layout.addView(notesInput);

        builder.setView(layout);
        builder.setPositiveButton("保存", (dialog, which) -> {
            if (saveMetricFromInputs(weightInput, bodyFatInput, muscleMassInput, bmiInput, notesInput)) {
                Toast.makeText(this, "记录已保存", Toast.LENGTH_SHORT).show();
                loadBodyMetrics();
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    private EditText createInput(String hint, boolean numeric) {
        EditText input = new EditText(this);
        input.setHint(hint);
        input.setInputType(numeric
                ? (InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL)
                : InputType.TYPE_CLASS_TEXT);
        return input;
    }

    private boolean saveMetricFromInputs(
            EditText weightInput,
            EditText bodyFatInput,
            EditText muscleMassInput,
            EditText bmiInput,
            EditText notesInput
    ) {
        try {
            String weightStr = weightInput.getText().toString().trim();
            if (weightStr.isEmpty()) {
                Toast.makeText(this, "请输入体重", Toast.LENGTH_SHORT).show();
                return false;
            }

            BodyMetric metric = new BodyMetric();
            metric.setWeight(Double.parseDouble(weightStr));

            String bodyFatStr = bodyFatInput.getText().toString().trim();
            if (!bodyFatStr.isEmpty()) {
                metric.setBodyFat(Double.parseDouble(bodyFatStr));
            }

            String muscleStr = muscleMassInput.getText().toString().trim();
            if (!muscleStr.isEmpty()) {
                metric.setMuscleMass(Double.parseDouble(muscleStr));
            }

            String bmiStr = bmiInput.getText().toString().trim();
            if (!bmiStr.isEmpty()) {
                metric.setBmi(Double.parseDouble(bmiStr));
            } else {
                double height = serviceLocator.getUserPreferences().getUserHeight();
                if (height <= 0) {
                    height = AppConfig.DEFAULT_HEIGHT_FOR_BMI;
                }
                metric.calculateBMI(height);
            }

            String notesStr = notesInput.getText().toString().trim();
            if (!notesStr.isEmpty()) {
                metric.setNotes(notesStr);
            }

            serviceLocator.getBodyMetricRepository().saveMetric(metric);
            return true;
        } catch (NumberFormatException e) {
            Toast.makeText(this, "输入格式有误", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void onMetricDelete(String date) {
        new AlertDialog.Builder(this)
                .setTitle("删除记录")
                .setMessage("确认删除这条记录吗？")
                .setPositiveButton("删除", (dialog, which) -> {
                    serviceLocator.getBodyMetricRepository().deleteMetric(date);
                    Toast.makeText(this, "记录已删除", Toast.LENGTH_SHORT).show();
                    loadBodyMetrics();
                })
                .setNegativeButton("取消", null)
                .show();
    }
}
