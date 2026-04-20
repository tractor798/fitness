package com.example.fitness;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PlanAdapter extends RecyclerView.Adapter<PlanAdapter.PlanViewHolder> {

    private List<PlanItem> plans;
    private OnPlanClickListener clickListener;

    public interface OnPlanClickListener {
        void onPlanClick(PlanItem planItem);
    }

    public PlanAdapter(List<PlanItem> plans, OnPlanClickListener listener) {
        this.plans = plans;
        this.clickListener = listener;
    }

    @NonNull
    @Override
    public PlanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_plan, parent, false);
        return new PlanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlanViewHolder holder, int position) {
        PlanItem plan = plans.get(position);
        holder.bind(plan);
    }

    @Override
    public int getItemCount() {
        return plans.size();
    }

    public void updatePlans(List<PlanItem> newPlans) {
        this.plans = newPlans;
        notifyDataSetChanged();
    }

    class PlanViewHolder extends RecyclerView.ViewHolder {
        private TextView planNameText;
        private TextView planDescriptionText;
        private TextView planDurationText;
        private TextView planCaloriesText;
        private Button startWorkoutButton;

        public PlanViewHolder(@NonNull View itemView) {
            super(itemView);
            planNameText = itemView.findViewById(R.id.planNameText);
            planDescriptionText = itemView.findViewById(R.id.planDescriptionText);
            planDurationText = itemView.findViewById(R.id.planDurationText);
            planCaloriesText = itemView.findViewById(R.id.planCaloriesText);
            startWorkoutButton = itemView.findViewById(R.id.startWorkoutButton);
        }

        public void bind(PlanItem plan) {
            planNameText.setText(plan.getName());
            planDescriptionText.setText(plan.getDescription());
            planDurationText.setText(plan.getFormattedDuration());
            planCaloriesText.setText(plan.getFormattedCalories());

            startWorkoutButton.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onPlanClick(plan);
                }
            });
        }
    }
}