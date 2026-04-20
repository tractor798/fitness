package com.example.fitness;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class WorkoutExerciseAdapter extends RecyclerView.Adapter<WorkoutExerciseAdapter.ExerciseViewHolder> {

    private List<Exercise> exercises;

    public WorkoutExerciseAdapter(List<Exercise> exercises) {
        this.exercises = exercises;
    }

    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_workout_exercise, parent, false);
        return new ExerciseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
        Exercise exercise = exercises.get(position);
        holder.bind(exercise, position + 1);
    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }

    static class ExerciseViewHolder extends RecyclerView.ViewHolder {
        private TextView exerciseNumberText;
        private TextView exerciseNameText;
        private TextView exerciseDetailsText;

        public ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            exerciseNumberText = itemView.findViewById(R.id.exerciseNumberText);
            exerciseNameText = itemView.findViewById(R.id.exerciseNameText);
            exerciseDetailsText = itemView.findViewById(R.id.exerciseDetailsText);
        }

        public void bind(Exercise exercise, int position) {
            exerciseNumberText.setText(String.valueOf(position));
            exerciseNameText.setText(exercise.getName());
            exerciseDetailsText.setText(String.format("%d组 × %d次",
                    exercise.getSets(), exercise.getReps()));
        }
    }
}