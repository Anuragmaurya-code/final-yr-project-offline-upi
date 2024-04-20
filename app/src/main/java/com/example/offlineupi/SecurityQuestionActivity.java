package com.example.offlineupi;

import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class SecurityQuestionActivity extends AppCompatActivity {

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
                submitAnswers();
            }
        });
    }

    private void submitAnswers() {
        String answer1 = editTextAnswer1.getText().toString().trim();
        String answer2 = editTextAnswer2.getText().toString().trim();
        String answer3 = editTextAnswer3.getText().toString().trim();

        // Check if any answer is empty
        if (answer1.isEmpty() || answer2.isEmpty() || answer3.isEmpty()) {
            Toast.makeText(this, "Please answer all security questions", Toast.LENGTH_SHORT).show();
        } else {
            String question1 = spinnerQuestion1.getSelectedItem().toString();
            String question2 = spinnerQuestion2.getSelectedItem().toString();
            String question3 = spinnerQuestion3.getSelectedItem().toString();

            // Store the question-answer pairs in a HashMap
            Map<String, String> questionAnswerMap = new HashMap<>();
            questionAnswerMap.put(question1, answer1);
            questionAnswerMap.put(question2, answer2);
            questionAnswerMap.put(question3, answer3);

            // Convert the map to a JSON string using Gson
            Gson gson = new Gson();
            String json = gson.toJson(questionAnswerMap);

            // Store the JSON data in SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences("security_data", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("security_questions", json);
            editor.apply();

            // Inform the user that answers have been converted to JSON and stored successfully
            Toast.makeText(this, "Answers converted to JSON and stored successfully!", Toast.LENGTH_SHORT).show();

            // Start MainActivity
//            startActivity(new Intent(SecurityQuestionsActivity.this, MainActivity.class));
//            finish(); // Finish the activity after submitting answers
            startActivity(new Intent(SecurityQuestionActivity.this, EnterPinActivity.class));
        }
    }

}

