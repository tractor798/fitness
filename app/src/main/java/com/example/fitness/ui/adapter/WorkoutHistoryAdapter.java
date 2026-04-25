package com.example.fitness.ui.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fitness.domain.model.WorkoutSession;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WorkoutHistoryAdapter extends RecyclerView.Adapter<WorkoutHistoryAdapter.HistoryViewHolder> {

    private List<WorkoutSession> workoutHistory;
    private final SimpleDateFormat dateTextFormat;
    private final SimpleDateFormat timeTextFormat;

    public WorkoutHistoryAdapter(List<WorkoutSession> workoutHistory) {
        this.workoutHistory = workoutHistory;
        this.dateTextFormat = new SimpleDateFormat("MM月dd日", Locale.getDefault());
        this.timeTextFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_workout_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        holder.bind(workoutHistory.get(position));
    }

    @Override
    public int getItemCount() {
        return workoutHistory == null ? 0 : workoutHistory.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateHistory(List<WorkoutSession> newHistory) {
        this.workoutHistory = newHistory;
        notifyDataSetChanged();
    }

    class HistoryViewHolder extends RecyclerView.ViewHolder {
        private final ImageView workoutImage;
        private final TextView workoutNameText;
        private final TextView workoutSubtitleText;
        private final TextView workoutTimeText;
        private final TextView workoutDateText;
        private final TextView workoutDurationText;
        private final TextView workoutCaloriesText;

        HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            workoutImage = itemView.findViewById(R.id.workoutImage);
            workoutNameText = itemView.findViewById(R.id.workoutNameText);
            workoutSubtitleText = itemView.findViewById(R.id.workoutSubtitleText);
            workoutTimeText = itemView.findViewById(R.id.workoutTimeText);
            workoutDateText = itemView.findViewById(R.id.workoutDateText);
            workoutDurationText = itemView.findViewById(R.id.workoutDurationText);
            workoutCaloriesText = itemView.findViewById(R.id.workoutCaloriesText);
        }

        @SuppressLint("SetTextI18n")
        void bind(WorkoutSession session) {
            long timestamp = session.getEndTime() > 0 ? session.getEndTime() : session.getStartTime();
            Date sessionDate = new Date(timestamp);
            String planName = session.getPlanName() == null ? "训练计划" : session.getPlanName();

            workoutNameText.setText(planName);
            workoutSubtitleText.setText(getSubtitle(planName));
            workoutTimeText.setText(timeTextFormat.format(sessionDate));
            workoutDateText.setText(dateTextFormat.format(sessionDate));
            workoutDurationText.setText(getDurationDisplay(session));
            workoutCaloriesText.setText(session.getCalories() + " kcal");

            Glide.with(workoutImage.getContext())
                    .load(getImageResId(planName))
                    .placeholder(R.drawable.bg_exercise_thumbnail)
                    .error(R.drawable.bg_exercise_thumbnail)
                    .into(workoutImage);
        }

        private String getDurationDisplay(WorkoutSession session) {
            long minutes = Math.max(1, session.getDuration() / (1000 * 60));
            return minutes + " 分钟";
        }

        private String getSubtitle(String planName) {
            String name = planName.toLowerCase(Locale.ROOT);
            if (containsAny(name, "腿", "深蹲", "squat", "leg")) {
                return "Leg Day Intensity";
            }
            if (containsAny(name, "hiit", "燃脂", "有氧", "cardio")) {
                return "Cardio & Core";
            }
            if (containsAny(name, "拉伸", "恢复", "stretch", "recovery")) {
                return "Mindful Recovery";
            }
            if (containsAny(name, "上肢", "胸", "背", "push", "pull", "upper")) {
                return "Upper Body Session";
            }
            return "Personal Training";
        }

        private String getImageUrl(String planName) {
            String name = planName.toLowerCase(Locale.ROOT);
            if (containsAny(name, "腿", "深蹲", "squat", "leg")) {
                return "https://images.unsplash.com/photo-1434608519344-49d77a699e1d?auto=format&fit=crop&w=500&q=80";
            }
            if (containsAny(name, "hiit", "燃脂", "有氧", "cardio")) {
                return "https://images.unsplash.com/photo-1549476464-37392f717541?auto=format&fit=crop&w=500&q=80";
            }
            if (containsAny(name, "拉伸", "恢复", "stretch", "recovery")) {
                return "https://images.unsplash.com/photo-1544367567-0f2fcb009e0b?auto=format&fit=crop&w=500&q=80";
            }
            return "https://images.unsplash.com/photo-1517836357463-d25dfeac3438?auto=format&fit=crop&w=500&q=80";
        }

        private int getImageResId(String planName) {
            String name = planName.toLowerCase(Locale.ROOT);
            if (containsAny(name, "腿", "深蹲", "squat", "leg")) {
                return R.drawable.img_ex_squats;
            }
            if (containsAny(name, "hiit", "燃脂", "有氧", "cardio")) {
                return R.drawable.cover_fat_burn;
            }
            if (containsAny(name, "拉伸", "恢复", "stretch", "recovery")) {
                return R.drawable.cover_pilates;
            }
            return R.drawable.bg_start_workout;
        }

        private boolean containsAny(String source, String... keys) {
            for (String key : keys) {
                if (source.contains(key)) {
                    return true;
                }
            }
            return false;
        }
    }
}
