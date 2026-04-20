package com.example.fitness;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder> {

    private final List<Exercise> exercises;
    private final OnExerciseConfigClickListener configClickListener;

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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exercise, parent, false);
        return new ExerciseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
        holder.bind(exercises.get(position));
    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }

    class ExerciseViewHolder extends RecyclerView.ViewHolder {
        private final TextView exerciseNameText;
        private final TextView exerciseDetailsText;
        private final ImageView exerciseThumb;
        private final ImageView deleteButton;

        ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            exerciseNameText = itemView.findViewById(R.id.exerciseNameText);
            exerciseDetailsText = itemView.findViewById(R.id.exerciseDetailsText);
            exerciseThumb = itemView.findViewById(R.id.exerciseThumb);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }

        @SuppressLint("DefaultLocale")
        void bind(Exercise exercise) {
            exerciseNameText.setText(exercise.getName());
            exerciseDetailsText.setText(String.format("%d组 × %d次", exercise.getSets(), exercise.getReps()));

            // 优先使用本地资源ID
            if (exercise.getImageResId() != 0) {
                Glide.with(exerciseThumb.getContext())
                        .load(exercise.getImageResId())
                        .apply(new RequestOptions().centerCrop().placeholder(R.drawable.bg_exercise_thumbnail))
                        .into(exerciseThumb);
            } else {
                String url = exercise.getImageUrl();
                if (url != null && !url.isEmpty()) {
                    Glide.with(exerciseThumb.getContext())
                            .load(url)
                            .apply(new RequestOptions().centerCrop().placeholder(R.drawable.bg_exercise_thumbnail))
                            .into(exerciseThumb);
                } else {
                    exerciseThumb.setImageResource(R.drawable.bg_exercise_thumbnail);
                }
            }

            updateSelectedState(exercise);

            itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos < 0 || pos >= exercises.size()) {
                    return;
                }
                Exercise ex = exercises.get(pos);
                ex.setSelected(!ex.isSelected());
                notifyItemChanged(pos);
            });

            itemView.setOnLongClickListener(v -> {
                if (configClickListener != null) {
                    configClickListener.onExerciseConfigClick(exercise);
                    return true;
                }
                return false;
            });

            deleteButton.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos >= 0 && pos < exercises.size()) {
                    exercises.remove(pos);
                    notifyItemRemoved(pos);
                }
            });
        }

        private void updateSelectedState(Exercise exercise) {
            if (!(itemView instanceof MaterialCardView card)) {
                return;
            }
            if (exercise.isSelected()) {
                card.setCardBackgroundColor(itemView.getResources().getColor(R.color.primary));
                exerciseNameText.setTextColor(itemView.getResources().getColor(R.color.onPrimary));
                exerciseDetailsText.setTextColor(itemView.getResources().getColor(R.color.onPrimary));
                exerciseDetailsText.setAlpha(0.88f);
                deleteButton.setColorFilter(itemView.getResources().getColor(R.color.onPrimary));
            } else {
                card.setCardBackgroundColor(itemView.getResources().getColor(R.color.surfaceContainerHighest));
                exerciseNameText.setTextColor(itemView.getResources().getColor(R.color.onSurface));
                exerciseDetailsText.setTextColor(itemView.getResources().getColor(R.color.onSurfaceVariant));
                exerciseDetailsText.setAlpha(1f);
                deleteButton.setColorFilter(itemView.getResources().getColor(R.color.surfaceContainerLow));
            }
        }
    }
}
