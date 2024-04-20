package com.example.offlineupi;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;

public class useOfSecurityQuestion extends AppCompatActivity {

    private Spinner spinnerQuestion1, spinnerQuestion2, spinnerQuestion3;
    private EditText editTextAnswer1, editTextAnswer2, editTextAnswer3;
    private Button btnSubmit;

    private String[] securityQuestions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_question);

        // Initialize security questions
        securityQuestions = getResources().getStringArray(R.array.security_questions_array);

        // Initialize views
        spinnerQuestion1 = findViewById(R.id.spinnerQuestion1);
        spinnerQuestion2 = findViewById(R.id.spinnerQuestion2);
        spinnerQuestion3 = findViewById(R.id.spinnerQuestion3);
        editTextAnswer1 = findViewById(R.id.editTextAnswer1);
        editTextAnswer2 = findViewById(R.id.editTextAnswer2);
        editTextAnswer3 = findViewById(R.id.editTextAnswer3);
        btnSubmit = findViewById(R.id.btnSubmit);

        // Set up spinners
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, securityQuestions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerQuestion1.setAdapter(adapter);
        spinnerQuestion2.setAdapter(adapter);
        spinnerQuestion3.setAdapter(adapter);

        // Set up button click listener
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Validate answers
                validateAnswers();
            }
        });
    }

    private void validateAnswers() {
        // Retrieve JSON string containing security question-answer pairs from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("security_data", Context.MODE_PRIVATE);
        String json = sharedPreferences.getString("security_questions", "");

        // Convert JSON string to a Map using Gson
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, String>>(){}.getType();
        Map<String, String> questionAnswerMap = gson.fromJson(json, type);

        // Validate answers
        String question1 = spinnerQuestion1.getSelectedItem().toString();
        String question2 = spinnerQuestion2.getSelectedItem().toString();
        String question3 = spinnerQuestion3.getSelectedItem().toString();

        String answer1 = editTextAnswer1.getText().toString().trim();
        String answer2 = editTextAnswer2.getText().toString().trim();
        String answer3 = editTextAnswer3.getText().toString().trim();

        boolean allAnswersValid = true;

        if (questionAnswerMap.containsKey(question1)) {
            String correctAnswer1 = questionAnswerMap.get(question1);
            if (!answer1.equals(correctAnswer1)) {
                allAnswersValid = false;
                Log.d("Validation", "Answer for question 1 is invalid");
            }
        } else {
            allAnswersValid = false;
            Log.d("Validation", "Question 1 is not found in the map");
        }

        if (questionAnswerMap.containsKey(question2)) {
            String correctAnswer2 = questionAnswerMap.get(question2);
            if (!answer2.equals(correctAnswer2)) {
                allAnswersValid = false;
                Log.d("Validation", "Answer for question 2 is invalid");
            }
        } else {
            allAnswersValid = false;
            Log.d("Validation", "Question 2 is not found in the map");
        }

        if (questionAnswerMap.containsKey(question3)) {
            String correctAnswer3 = questionAnswerMap.get(question3);
            if (!answer3.equals(correctAnswer3)) {
                allAnswersValid = false;
                Log.d("Validation", "Answer for question 3 is invalid");
            }
        } else {
            allAnswersValid = false;
            Log.d("Validation", "Question 3 is not found in the map");
        }

        if (allAnswersValid) {
            Toast.makeText(this, " answers are valid!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(useOfSecurityQuestion.this, SetPinActivity.class));
        }else {
            Toast.makeText(this, "One or more answers are invalid!", Toast.LENGTH_SHORT).show();
        }
    }
}