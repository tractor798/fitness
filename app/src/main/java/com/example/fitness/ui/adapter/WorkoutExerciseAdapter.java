package com.example.fitness.ui.adapter;

import com.example.fitness.R;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fitness.domain.model.Exercise;

import java.util.List;

public class WorkoutExerciseAdapter extends RecyclerView.Adapter<WorkoutExerciseAdapter.ExerciseViewHolder> {

    private final List<Exercise> exercises;

    public WorkoutExerciseAdapter(List<Exercise> exercises) {
        this.exercises = exercises;
        setHasStableIds(true); // 启用稳定ID
    }

    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_workout_exercise_item, parent, false);
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

    @Override
    public long getItemId(int position) {
        return exercises.get(position).getName().hashCode();
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    static class ExerciseViewHolder extends RecyclerView.ViewHolder {
        private final ImageView exerciseImage;
        private final TextView exerciseNameText;
        private final TextView exerciseDetailsText;

        public ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            exerciseImage = itemView.findViewById(R.id.exerciseImage);
            exerciseNameText = itemView.findViewById(R.id.exerciseNameText);
            exerciseDetailsText = itemView.findViewById(R.id.exerciseDetailsText);
        }

        public void bind(Exercise exercise) {
            exerciseNameText.setText(exercise.getName());
            exerciseDetailsText.setText(exercise.getDescription());
            
            // 优先使用本地资源ID
            if (exercise.getImageResId() != 0) {
                Glide.with(itemView.getContext())
                        .load(exercise.getImageResId())
                        .placeholder(R.drawable.bg_exercise_thumbnail)
                        .error(R.drawable.bg_exercise_thumbnail)
                        .into(exerciseImage);
            } else {
                String url = exercise.getImageUrl();
                if (url != null && !url.isEmpty()) {
                    Glide.with(itemView.getContext())
                            .load(url)
                            .placeholder(R.drawable.bg_exercise_thumbnail)
                            .error(R.drawable.bg_exercise_thumbnail)
                            .into(exerciseImage);
                } else {
                    exerciseImage.setImageResource(R.drawable.bg_exercise_thumbnail);
                }
            }
        }
    }
}
