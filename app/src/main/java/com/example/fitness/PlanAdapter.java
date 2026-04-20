package com.example.fitness;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PlanAdapter extends RecyclerView.Adapter<PlanAdapter.PlanViewHolder> {

    private List<PlanItem> plans;
    private final OnPlanClickListener clickListener;
    private final OnPlanDeleteListener deleteListener;

    public interface OnPlanClickListener {
        void onPlanClick(PlanItem planItem);
    }

    public interface OnPlanDeleteListener {
        void onPlanDelete(PlanItem planItem);
    }

    public PlanAdapter(List<PlanItem> plans, OnPlanClickListener listener, OnPlanDeleteListener deleteListener) {
        this.plans = plans;
        this.clickListener = listener;
        this.deleteListener = deleteListener;
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public PlanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_plan, parent, false);
        return new PlanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlanViewHolder holder, int position) {
        holder.bind(plans.get(position));
    }

    @Override
    public int getItemCount() {
        return plans == null ? 0 : plans.size();
    }

    @Override
    public long getItemId(int position) {
        PlanItem item = plans.get(position);
        return (item.getName() + item.getDuration() + item.getCalories()).hashCode();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updatePlans(List<PlanItem> newPlans) {
        this.plans = newPlans;
        notifyDataSetChanged();
    }

    class PlanViewHolder extends RecyclerView.ViewHolder {
        private final TextView planNameText;
        private final TextView planDescriptionText;
        private final TextView planDurationText;
        private final TextView planCaloriesText;
        private final Button startWorkoutButton;
        private final ImageView deleteButton;

        PlanViewHolder(@NonNull View itemView) {
            super(itemView);
            planNameText = itemView.findViewById(R.id.planNameText);
            planDescriptionText = itemView.findViewById(R.id.planDescriptionText);
            planDurationText = itemView.findViewById(R.id.planDurationText);
            planCaloriesText = itemView.findViewById(R.id.planCaloriesText);
            startWorkoutButton = itemView.findViewById(R.id.startWorkoutButton);
            deleteButton = itemView.findViewById(R.id.deletePlanButton);
        }

        void bind(PlanItem plan) {
            planNameText.setText(plan.getName());
            planDescriptionText.setText(plan.getDescription());
            planDurationText.setText(plan.getFormattedDuration());
            planCaloriesText.setText(plan.getFormattedCalories());
            
            // 只显示自定义计划的删除按钮
            if (deleteButton != null) {
                deleteButton.setVisibility(plan.getType() == PlanItem.TYPE_CUSTOM ? View.VISIBLE : View.GONE);
                deleteButton.setOnClickListener(v -> {
                    if (deleteListener != null) {
                        deleteListener.onPlanDelete(plan);
                    }
                });
            }
            
            startWorkoutButton.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onPlanClick(plan);
                }
            });
        }
    }
}
