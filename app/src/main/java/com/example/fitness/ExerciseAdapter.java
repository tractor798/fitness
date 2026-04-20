package com.example.fitness;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder> {

    private List<Exercise> exercises;
    private OnExerciseConfigClickListener configClickListener;

    public interface OnExerciseConfigClickListener {
        void onExerciseConfigClick(Exercise exercise);
    }

    public ExerciseAdapter(List<Exercise> exercises, OnExerciseConfigClickListener listener) {
        this.exercises = exercises;
        this.configClickListener = listener;
    }

    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exercise, parent, false);
        return new ExerciseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
        Exercise exercise = exercises.get(position);
        holder.bind(exercise);
    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }

    class ExerciseViewHolder extends RecyclerView.ViewHolder {
        private CheckBox exerciseCheckBox;
        private TextView exerciseNameText;
        private TextView exerciseDescriptionText;
        private TextView exerciseDetailsText;
        private ImageView configButton;

        public ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            exerciseCheckBox = itemView.findViewById(R.id.exerciseCheckBox);
            exerciseNameText = itemView.findViewById(R.id.exerciseNameText);
            exerciseDescriptionText = itemView.findViewById(R.id.exerciseDescriptionText);
            exerciseDetailsText = itemView.findViewById(R.id.exerciseDetailsText);
            configButton = itemView.findViewById(R.id.configButton);
        }

        public void bind(Exercise exercise) {
            exerciseNameText.setText(exercise.getName());
            exerciseDescriptionText.setText(exercise.getDescription());
            exerciseDetailsText.setText(String.format("%d组 × %d次 | %d分钟 | %d kcal",
                    exercise.getSets(), exercise.getReps(),
                    exercise.getDuration(), exercise.getCalories()));

            exerciseCheckBox.setChecked(exercise.isSelected());
            exerciseCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                exercise.setSelected(isChecked);
            });

            configButton.setOnClickListener(v -> {
                if (configClickListener != null) {
                    configClickListener.onExerciseConfigClick(exercise);
                }
            });
        }
    }
}