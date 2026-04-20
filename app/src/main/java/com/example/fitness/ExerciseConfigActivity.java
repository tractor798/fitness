package com.example.fitness;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ExerciseConfigActivity extends BaseActivity {

    private TextView exerciseNameText;
    private TextView exerciseDescriptionText;
    private EditText setsEdit;
    private EditText repsEdit;
    private EditText durationEdit;
    private EditText caloriesEdit;
    private Button saveButton;
    private Exercise currentExercise;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_config);
        setupBottomNavigation(1);

        initializeViews();
        loadExerciseData();
        setupListeners();
    }

    private void initializeViews() {
        exerciseNameText = findViewById(R.id.exerciseNameText);
        exerciseDescriptionText = findViewById(R.id.exerciseDescriptionText);
        setsEdit = findViewById(R.id.setsEdit);
        repsEdit = findViewById(R.id.repsEdit);
        durationEdit = findViewById(R.id.durationEdit);
        caloriesEdit = findViewById(R.id.caloriesEdit);
        saveButton = findViewById(R.id.saveButton);
    }

    private void loadExerciseData() {
        currentExercise = (Exercise) getIntent().getSerializableExtra("exercise");
        if (currentExercise != null) {
            exerciseNameText.setText(currentExercise.getName());
            exerciseDescriptionText.setText(currentExercise.getDescription());
            setsEdit.setText(String.valueOf(currentExercise.getSets()));
            repsEdit.setText(String.valueOf(currentExercise.getReps()));
            durationEdit.setText(String.valueOf(currentExercise.getDuration()));
            caloriesEdit.setText(String.valueOf(currentExercise.getCalories()));
        }
    }

    private void setupListeners() {
        saveButton.setOnClickListener(v -> saveExerciseConfig());
    }

    private void saveExerciseConfig() {
        try {
            int sets = Integer.parseInt(setsEdit.getText().toString().trim());
            int reps = Integer.parseInt(repsEdit.getText().toString().trim());
            int duration = Integer.parseInt(durationEdit.getText().toString().trim());
            int calories = Integer.parseInt(caloriesEdit.getText().toString().trim());

            if (sets <= 0 || reps <= 0 || duration <= 0 || calories <= 0) {
                Toast.makeText(this, "所有数值必须大于0", Toast.LENGTH_SHORT).show();
                return;
            }

            currentExercise.setSets(sets);
            currentExercise.setReps(reps);
            currentExercise.setDuration(duration);
            currentExercise.setCalories(calories);

            Intent resultIntent = new Intent();
            resultIntent.putExtra("exercise", currentExercise);
            setResult(RESULT_OK, resultIntent);
            finish();

        } catch (NumberFormatException e) {
            Toast.makeText(this, "请输入有效的数字", Toast.LENGTH_SHORT).show();
        }
    }
}