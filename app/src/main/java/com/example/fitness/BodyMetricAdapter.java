package com.example.fitness;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BodyMetricAdapter extends RecyclerView.Adapter<BodyMetricAdapter.BodyMetricViewHolder> {

    private List<BodyMetric> bodyMetrics;
    private final OnMetricDeleteListener deleteListener;

    public interface OnMetricDeleteListener {
        void onDelete(String date);
    }

    public BodyMetricAdapter(List<BodyMetric> bodyMetrics, OnMetricDeleteListener deleteListener) {
        this.bodyMetrics = bodyMetrics;
        this.deleteListener = deleteListener;
        setHasStableIds(true); // 启用稳定ID
    }

    @NonNull
    @Override
    public BodyMetricViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_body_metric, parent, false);
        return new BodyMetricViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BodyMetricViewHolder holder, int position) {
        BodyMetric metric = bodyMetrics.get(position);
        holder.bind(metric);
    }

    @Override
    public int getItemCount() {
        return bodyMetrics.size();
    }

    @Override
    public long getItemId(int position) {
        // 使用日期作为唯一ID
        return bodyMetrics.get(position).getDate().hashCode();
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    public void updateMetrics(List<BodyMetric> newMetrics) {
        this.bodyMetrics = newMetrics;
        notifyDataSetChanged();
    }

    class BodyMetricViewHolder extends RecyclerView.ViewHolder {
        private final TextView dateText;
        private final TextView weightText;
        private final TextView bodyFatText;
        private final TextView muscleMassText;
        private final TextView bmiText;
        private final TextView notesText;
        private final ImageView deleteButton;

        BodyMetricViewHolder(View itemView) {
            super(itemView);
            dateText = itemView.findViewById(R.id.metricDateText);
            weightText = itemView.findViewById(R.id.metricWeightText);
            bodyFatText = itemView.findViewById(R.id.metricBodyFatText);
            muscleMassText = itemView.findViewById(R.id.metricMuscleMassText);
            bmiText = itemView.findViewById(R.id.metricBmiText);
            notesText = itemView.findViewById(R.id.metricNotesText);
            deleteButton = itemView.findViewById(R.id.deleteMetricButton);
        }

        void bind(BodyMetric metric) {
            dateText.setText(metric.getFormattedDate());
            weightText.setText("体重: " + metric.getWeightDisplay());

            if (metric.getBodyFat() > 0) {
                bodyFatText.setText("体脂率: " + metric.getBodyFatDisplay());
                bodyFatText.setVisibility(View.VISIBLE);
            } else {
                bodyFatText.setVisibility(View.GONE);
            }

            if (metric.getMuscleMass() > 0) {
                muscleMassText.setText("肌肉: " + metric.getMuscleMassDisplay());
                muscleMassText.setVisibility(View.VISIBLE);
            } else {
                muscleMassText.setVisibility(View.GONE);
            }

            if (metric.getBmi() > 0) {
                bmiText.setText("BMI: " + metric.getBmiDisplay());
                bmiText.setVisibility(View.VISIBLE);
            } else {
                bmiText.setVisibility(View.GONE);
            }

            if (metric.getNotes() != null && !metric.getNotes().isEmpty()) {
                notesText.setText("备注: " + metric.getNotes());
                notesText.setVisibility(View.VISIBLE);
            } else {
                notesText.setVisibility(View.GONE);
            }

            deleteButton.setOnClickListener(v -> {
                if (deleteListener != null) {
                    deleteListener.onDelete(metric.getDate());
                }
            });
        }
    }
}
