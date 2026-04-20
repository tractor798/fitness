package com.example.fitness;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WorkoutHistoryAdapter extends RecyclerView.Adapter<WorkoutHistoryAdapter.HistoryViewHolder> {

    private List<WorkoutSession> workoutHistory;
    private SimpleDateFormat dateFormat;

    public WorkoutHistoryAdapter(List<WorkoutSession> workoutHistory) {
        this.workoutHistory = workoutHistory;
        this.dateFormat = new SimpleDateFormat("MM/dd HH:mm", Locale.getDefault());
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_workout_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        WorkoutSession session = workoutHistory.get(position);
        holder.bind(session);
    }

    @Override
    public int getItemCount() {
        return workoutHistory.size();
    }

    public void updateHistory(List<WorkoutSession> newHistory) {
        this.workoutHistory = newHistory;
        notifyDataSetChanged();
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        private TextView workoutNameText;
        private TextView workoutDateText;
        private TextView workoutDurationText;
        private TextView workoutCaloriesText;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            workoutNameText = itemView.findViewById(R.id.workoutNameText);
            workoutDateText = itemView.findViewById(R.id.workoutDateText);
            workoutDurationText = itemView.findViewById(R.id.workoutDurationText);
            workoutCaloriesText = itemView.findViewById(R.id.workoutCaloriesText);
        }

        public void bind(WorkoutSession session) {
            workoutNameText.setText(session.getPlanName());

            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd HH:mm", Locale.getDefault());
            workoutDateText.setText(dateFormat.format(new Date(session.getEndTime())));

            workoutDurationText.setText(session.getFormattedDuration());
            workoutCaloriesText.setText(session.getCalories() + " kcal");
        }
    }
}